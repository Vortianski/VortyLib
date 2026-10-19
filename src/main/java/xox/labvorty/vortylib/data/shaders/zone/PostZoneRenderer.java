package xox.labvorty.vortylib.data.shaders.zone;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import xox.labvorty.vortylib.init.VortyLibShaders;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(value = Dist.CLIENT)
public final class PostZoneRenderer {
    private static final double RANGE = 256.0;
    public static final int MAX_ZONES = 16;
    private static RenderTarget scratch;
    public static int effectMode = 0;
    public static float strength = 1.0f;
    private static final Map<Object, AABB> ZONES = new LinkedHashMap<>();

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;

        ShaderInstance shader = VortyLibShaders.POST_ZONE_TEST;
        if (shader == null || isEmpty()) return;

        Vec3 cam = event.getCamera().getPosition();
        List<AABB> zones = nearest(cam, RANGE, MAX_ZONES);
        if (zones.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        RenderTarget main = mc.getMainRenderTarget();
        int w = main.width, h = main.height;

        if (scratch == null) {
            scratch = new TextureTarget(w, h, true, Minecraft.ON_OSX);
        } else if (scratch.width != w || scratch.height != h) {
            scratch.resize(w, h, Minecraft.ON_OSX);
        }
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, main.frameBufferId);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, scratch.frameBufferId);
        GL30.glBlitFramebuffer(0, 0, w, h, 0, 0, w, h,
                GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);

        Matrix4f invViewProj = new Matrix4f(event.getProjectionMatrix())
                .mul(event.getModelViewMatrix())
                .invert();

        float[] data = new float[MAX_ZONES * 6];
        for (int i = 0; i < zones.size(); i++) {
            AABB b = zones.get(i);
            data[i * 6]     = (float) (b.minX - cam.x);
            data[i * 6 + 1] = (float) (b.minY - cam.y);
            data[i * 6 + 2] = (float) (b.minZ - cam.z);
            data[i * 6 + 3] = (float) (b.maxX - cam.x);
            data[i * 6 + 4] = (float) (b.maxY - cam.y);
            data[i * 6 + 5] = (float) (b.maxZ - cam.z);
        }

        shader.safeGetUniform("InvViewProj").set(invViewProj);
        shader.safeGetUniform("ZoneCount").set(zones.size());
        shader.safeGetUniform("Zones").set(data);
        shader.safeGetUniform("Time").set((Util.getMillis() % 100000L) / 1000.0f);
        shader.safeGetUniform("EffectMode").set(effectMode);
        shader.safeGetUniform("Strength").set(strength);

        main.bindWrite(true);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.disableBlend();
        RenderSystem.setShaderTexture(0, scratch.getColorTextureId());
        RenderSystem.setShaderTexture(1, scratch.getDepthTextureId());
        RenderSystem.setShader(() -> shader);

        BufferBuilder buf = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        buf.addVertex(-1f, -1f, 0f);
        buf.addVertex( 1f, -1f, 0f);
        buf.addVertex( 1f,  1f, 0f);
        buf.addVertex(-1f,  1f, 0f);
        BufferUploader.drawWithShader(buf.buildOrThrow());

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        if (scratch != null) {
            scratch.destroyBuffers();
            scratch = null;
        }

        clear();
    }

    public static void put(Object key, AABB box) {
        ZONES.put(key, box);
    }

    public static void remove(Object key) {
        ZONES.remove(key);
    }

    public static void clear() {
        ZONES.clear();
    }

    public static boolean isEmpty() {
        return ZONES.isEmpty();
    }

    public static List<AABB> nearest(Vec3 cam, double range, int max) {
        double r2 = range * range;
        List<AABB> out = new ArrayList<>();
        for (AABB b : ZONES.values()) {
            if (b.distanceToSqr(cam) <= r2) out.add(b);
        }
        out.sort((a, b) -> Double.compare(a.distanceToSqr(cam), b.distanceToSqr(cam)));
        return out.size() > max ? out.subList(0, max) : out;
    }
}