package xox.labvorty.vortylib.data.text;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import xox.labvorty.vortylib.data.network.FloatingTextPacket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT)
public final class ClientFloatingTexts {
    private static final int MAX_ACTIVE = 256;
    private static final double MAX_RENDER_DISTANCE_SQR = 64.0 * 64.0;
    private static final float MATRIX_EPSILON = 1.0E-5f;
    private static final List<Active> ACTIVE = new ArrayList<>();
    private static ClientLevel lastLevel;

    public static void add(FloatingTextPacket packet) {
        Font font = Minecraft.getInstance().font;
        List<FormattedCharSequence> lines = font.split(packet.text(), packet.wrapWidth());

        if (lines.isEmpty()) {
            return;
        }

        int[] widths = new int[lines.size()];
        for (int i = 0; i < widths.length; i++) {
            widths[i] = font.width(lines.get(i));
        }

        if (ACTIVE.size() >= MAX_ACTIVE) {
            ACTIVE.remove(0);
        }

        ACTIVE.add(new Active(packet, lines, widths));
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level != lastLevel) {
            ACTIVE.clear();
            lastLevel = minecraft.level;
        }

        if (minecraft.isPaused() || ACTIVE.isEmpty()) {
            return;
        }

        Iterator<Active> iterator = ACTIVE.iterator();
        while (iterator.hasNext()) {
            Active active = iterator.next();
            active.age++;
            if (active.age >= active.packet.lifetime()) {
                iterator.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ACTIVE.clear();
        lastLevel = null;
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS || ACTIVE.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        Camera camera = event.getCamera();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();

        Matrix4f view = event.getModelViewMatrix();
        Matrix4f projection = event.getProjectionMatrix();
        Matrix4f identity = new Matrix4f();
        boolean renderSystemIdentity = RenderSystem.getModelViewMatrix().equals(identity, MATRIX_EPSILON);
        boolean poseIdentity = poseStack.last().pose().equals(identity, MATRIX_EPSILON);
        boolean viewIdentity = view.equals(identity, MATRIX_EPSILON);
        boolean applyView = renderSystemIdentity && poseIdentity && !viewIdentity;

        for (Active active : ACTIVE) {
            if (!active.stageSeen) {
                active.stageSeen = true;
            }
            render(active, minecraft, camera, partialTick, poseStack, buffers, view, projection, applyView);
        }

        buffers.endBatch();
    }

    private static void skip(Active active, String reason) {
        if (!reason.equals(active.lastSkip)) {
            active.lastSkip = reason;
        }
    }

    private static void render(
            Active active,
            Minecraft minecraft,
            Camera camera,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource.BufferSource buffers,
            Matrix4f view,
            Matrix4f projection,
            boolean applyView
    ) {
        FloatingTextPacket packet = active.packet;
        Vec3 base = active.basePosition(partialTick);
        if (base == null) {
            skip(active, "base position is null");
            return;
        }

        float progress = active.age + partialTick;

        float alphaMultiplier = 1.0f;
        if (packet.fadeIn() > 0 && progress < packet.fadeIn()) {
            alphaMultiplier = progress / packet.fadeIn();
        }
        float remaining = packet.lifetime() - progress;
        if (packet.fadeOut() > 0 && remaining < packet.fadeOut()) {
            alphaMultiplier = Math.min(alphaMultiplier, Math.max(0.0f, remaining / packet.fadeOut()));
        }

        int alpha = (int) (255.0f * alphaMultiplier);
        if (alpha < 4) {
            skip(active, "alpha " + alpha);
            return;
        }

        Vec3 cameraPosition = camera.getPosition();
        double x = base.x;
        double y = base.y + packet.riseSpeed() * progress;
        double z = base.z;

        double dx = x - cameraPosition.x;
        double dy = y - cameraPosition.y;
        double dz = z - cameraPosition.z;
        double distanceSqr = dx * dx + dy * dy + dz * dz;
        if (distanceSqr > MAX_RENDER_DISTANCE_SQR) {
            skip(active, "too far, distance " + Math.sqrt(distanceSqr));
            return;
        }

        if (active.age % 20 == 0 && active.age != active.lastLoggedAge) {
            active.lastLoggedAge = active.age;
        }

        float scale = packet.scale();
        if (packet.popIn() && packet.fadeIn() > 0 && progress < packet.fadeIn()) {
            float t = progress / packet.fadeIn();
            float eased = 1.0f - (1.0f - t) * (1.0f - t);
            scale *= Mth.lerp(eased, 0.6f, 1.0f);
        }

        Vector3f angles = packet.rotations();
        Quaternionf rotation = new Quaternionf().rotationYXZ(
                angles.y * Mth.DEG_TO_RAD,
                angles.x * Mth.DEG_TO_RAD,
                angles.z * Mth.DEG_TO_RAD
        );

        Vector3f frontNormal = rotation.transform(new Vector3f(0.0f, 0.0f, -1.0f));
        Vector3f toCamera = new Vector3f((float) -dx, (float) -dy, (float) -dz);
        if (frontNormal.dot(toCamera) < 0.0f) {
            rotation.mul(new Quaternionf().rotationY(Mth.PI));
        }

        int light = packet.fullBright()
                ? LightTexture.FULL_BRIGHT
                : LevelRenderer.getLightColor(minecraft.level, BlockPos.containing(x, y, z));

        int color = (alpha << 24) | 0xFFFFFF;

        Font font = minecraft.font;
        Font.DisplayMode mode = packet.seeThrough() ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL;

        if (!active.drawn) {
            active.drawn = true;
        }

        poseStack.pushPose();
        if (applyView) {
            poseStack.mulPose(view);
        }
        poseStack.translate(dx, dy, dz);
        poseStack.mulPose(rotation);
        poseStack.scale(-scale, -scale, scale);

        Matrix4f matrix = poseStack.last().pose();
        int lineHeight = font.lineHeight;
        float startY = -(active.lines.size() * lineHeight) / 2.0f;

        for (int i = 0; i < active.lines.size(); i++) {
            font.drawInBatch(
                    active.lines.get(i),
                    -active.widths[i] / 2.0f,
                    startY + i * lineHeight,
                    color,
                    false,
                    matrix,
                    buffers,
                    mode,
                    0,
                    light
            );
        }

        poseStack.popPose();
    }

    private static final class Active {
        final FloatingTextPacket packet;
        final List<FormattedCharSequence> lines;
        final int[] widths;
        int age;
        Vec3 lastBase;
        boolean stageSeen;
        boolean drawn;
        String lastSkip;
        int lastLoggedAge = -1;

        Active(FloatingTextPacket packet, List<FormattedCharSequence> lines, int[] widths) {
            this.packet = packet;
            this.lines = lines;
            this.widths = widths;
        }

        Vec3 basePosition(float partialTick) {
            if (packet.followEntityId() < 0) {
                return packet.position();
            }

            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                Entity entity = level.getEntity(packet.followEntityId());
                if (entity != null) {
                    lastBase = entity.getPosition(partialTick).add(packet.position());
                }
            }

            return lastBase;
        }
    }
}