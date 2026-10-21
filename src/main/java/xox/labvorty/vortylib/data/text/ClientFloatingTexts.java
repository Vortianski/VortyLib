package xox.labvorty.vortylib.data.text;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
        int[] counts = new int[lines.size()];
        for (int i = 0; i < widths.length; i++) {
            widths[i] = font.width(lines.get(i));
            counts[i] = countChars(lines.get(i));
        }

        if (ACTIVE.size() >= MAX_ACTIVE) {
            ACTIVE.remove(0);
        }

        Active active = new Active(packet, lines, widths, counts);
        active.track(1.0f);
        ACTIVE.add(active);
    }

    public static void remove(FloatingTextPacket packet) {
        ACTIVE.removeIf(active -> active.packet == packet);
    }

    private static int countChars(FormattedCharSequence sequence) {
        int[] count = {0};
        sequence.accept((index, style, codePoint) -> {
            count[0]++;
            return true;
        });
        return count[0];
    }

    private static FormattedCharSequence truncate(FormattedCharSequence sequence, int limit) {
        return sink -> {
            int[] count = {0};
            return sequence.accept((index, style, codePoint) -> {
                if (count[0] >= limit) {
                    return false;
                }
                count[0]++;
                return sink.accept(index, style, codePoint);
            });
        };
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
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL || ACTIVE.isEmpty()) {
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
        active.track(partialTick);
        Quaternionf delta = active.followDelta();
        Vec3 base = active.basePosition(delta);
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
        Quaternionf rotation = new Quaternionf(delta).mul(
                new Quaternionf().rotationYXZ(
                        angles.y * Mth.DEG_TO_RAD,
                        angles.x * Mth.DEG_TO_RAD,
                        angles.z * Mth.DEG_TO_RAD
                )
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

        boolean typewriter = packet.typewriterSpeed() > 0.0f;
        int revealed = typewriter ? (int) (progress * packet.typewriterSpeed()) : Integer.MAX_VALUE;

        poseStack.pushPose();
        if (applyView) {
            poseStack.mulPose(view);
        }
        poseStack.translate(dx, dy, dz);
        poseStack.mulPose(rotation);
        poseStack.scale(-scale, -scale, -scale);

        Matrix4f matrix = poseStack.last().pose();
        int lineHeight = font.lineHeight;
        float startY = -(active.lines.size() * lineHeight) / 2.0f;

        for (int i = 0; i < active.lines.size(); i++) {
            FormattedCharSequence line = active.lines.get(i);

            if (typewriter) {
                if (revealed <= 0) {
                    break;
                }
                int visible = Math.min(revealed, active.counts[i]);
                revealed -= visible;
                if (visible < active.counts[i]) {
                    line = truncate(line, visible);
                }
            }

            font.drawInBatch(
                    line,
                    -active.widths[i] / 2.0f,
                    startY + i * lineHeight,
                    color,
                    packet.dropShadow(),
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
        final int[] counts;
        int age;
        boolean captured;
        Vec3 spawnPivot = Vec3.ZERO;
        float spawnYaw;
        float spawnPitch;
        float eyeHeight;
        Vec3 pivot = Vec3.ZERO;
        float yaw;
        float pitch;
        boolean stageSeen;
        boolean drawn;
        String lastSkip;
        int lastLoggedAge = -1;

        Active(FloatingTextPacket packet, List<FormattedCharSequence> lines, int[] widths, int[] counts) {
            this.packet = packet;
            this.lines = lines;
            this.widths = widths;
            this.counts = counts;
        }

        void track(float partialTick) {
            if (packet.followEntityId() < 0) {
                return;
            }

            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) {
                return;
            }

            Entity entity = level.getEntity(packet.followEntityId());
            if (entity == null) {
                return;
            }

            if (!captured) {
                spawnPivot = entity.position();
                spawnYaw = 0.0f;
                spawnPitch = 0.0f;
                eyeHeight = entity.getEyeHeight();
                captured = true;
            }

            pivot = packet.followPosition() ? entity.getPosition(partialTick) : spawnPivot;
            yaw = packet.followXRotation() ? entity.getViewYRot(partialTick) : spawnYaw;
            pitch = packet.followYRotation() ? entity.getViewXRot(partialTick) : spawnPitch;
        }

        Quaternionf followDelta() {
            if (!captured) {
                return new Quaternionf();
            }

            Quaternionf current = new Quaternionf().rotationYXZ(
                    -yaw * Mth.DEG_TO_RAD,
                    pitch * Mth.DEG_TO_RAD,
                    0.0f
            );
            Quaternionf origin = new Quaternionf().rotationYXZ(
                    -spawnYaw * Mth.DEG_TO_RAD,
                    spawnPitch * Mth.DEG_TO_RAD,
                    0.0f
            ).invert();

            return current.mul(origin);
        }

        Vec3 basePosition(Quaternionf delta) {
            if (packet.followEntityId() < 0) {
                return packet.position();
            }

            if (!captured) {
                return null;
            }

            Vec3 offset = packet.position();
            Vector3f relative = new Vector3f(
                    (float) offset.x,
                    (float) (offset.y - eyeHeight),
                    (float) offset.z
            );
            delta.transform(relative);

            return pivot.add(relative.x, relative.y + eyeHeight, relative.z);
        }
    }
}