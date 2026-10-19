package xox.labvorty.vortylib.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;

public class RadialMenuScreen extends Screen {
    public record Entry(Component label, ItemStack icon, Runnable action) {}
    private final float innerRadius;
    private final float outerRadius;
    private final float hoverGrow;
    private final float gapDegrees;
    private final int colorNormal;
    private final int colorHover;
    private final int openTicks;
    private final int growTicks;
    private final List<Entry> entries;
    private int hovered = -1;
    private int ticksOpen = 0;
    private float progress;
    private float[] hoverPrev;
    private float[] hoverCur;

    public RadialMenuScreen(List<Entry> entries) {
        this(Component.empty(), entries);
    }

    public RadialMenuScreen(Component component, List<Entry> entries) {
        this(
                component,
                entries,
                0x99101018,
                0xCC4A6FA5,
                40f,
                100f,
                0f,
                0f,
                5,
                5
        );
    }

    public RadialMenuScreen(Component component, List<Entry> entries, int normalColor, int hoverColor, float innerRadius, float outerRadius, float hoverGrow, float gapDegrees) {
        this(
                component,
                entries,
                normalColor,
                hoverColor,
                innerRadius,
                outerRadius,
                hoverGrow,
                gapDegrees,
                5,
                5
        );
    }

    public RadialMenuScreen(Component component, List<Entry> entries, int normalColor, int hoverColor, float innerRadius, float outerRadius, float hoverGrow, float gapDegrees, int openTicks, int growTicks) {
        super(component);
        this.entries = entries;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.hoverGrow = hoverGrow;
        this.gapDegrees = gapDegrees;
        this.colorNormal = normalColor;
        this.colorHover = hoverColor;
        this.openTicks = Math.max(0, openTicks);
        this.growTicks = Math.max(0, growTicks);
        this.progress = this.openTicks == 0 ? 1f : 0f;
        this.hoverPrev = new float[entries.size()];
        this.hoverCur = new float[entries.size()];
    }

    @Override
    public void tick() {
        super.tick();
        ticksOpen++;
        syncState();

        float delta = growTicks == 0 ? 1f : 1f / growTicks;
        for (int i = 0; i < hoverCur.length; i++) {
            hoverPrev[i] = hoverCur[i];
            float target = i == hovered ? 1f : 0f;
            float current = hoverCur[i];
            if (current < target) {
                current = Math.min(target, current + delta);
            } else if (current > target) {
                current = Math.max(target, current - delta);
            }
            hoverCur[i] = current;
        }
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);

        hovered = -1;
        progress = computeProgress(partialTick);
        syncState();

        int n = entries.size();
        if (n == 0 || progress <= 0f) return;

        float cx = this.width / 2f;
        float cy = this.height / 2f;
        hovered = getHoveredIndex(mouseX, mouseY, cx, cy);

        float step = 360f / n;
        float inner = innerRadius * progress;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Matrix4f matrix = g.pose().last().pose();
        for (int i = 0; i < n; i++) {
            float amount = hoverAmount(i, partialTick);
            float center = i * step;
            float a0 = center - step / 2f + gapDegrees / 2f;
            float a1 = center + step / 2f - gapDegrees / 2f;
            float outer = (outerRadius + hoverGrow * amount) * progress;
            int color = withAlphaFactor(blend(colorNormal, colorHover, amount), progress);
            drawSector(matrix, cx, cy, inner, outer, a0, a1, color);
        }

        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        if (progress > 0.05f) {
            PoseStack pose = g.pose();
            for (int i = 0; i < n; i++) {
                float amount = hoverAmount(i, partialTick);
                float midRadius = (innerRadius + outerRadius + hoverGrow * amount) / 2f * progress;
                double rad = Math.toRadians(i * step);
                float px = cx + (float) Math.sin(rad) * midRadius;
                float py = cy - (float) Math.cos(rad) * midRadius;
                pose.pushPose();
                pose.translate(px, py, 0f);
                pose.scale(progress, progress, 1f);
                g.renderItem(entries.get(i).icon(), -8, -8);
                pose.popPose();
            }
        }

        int textAlpha = (int) (255 * progress);
        if (textAlpha > 8) {
            Component text = hovered >= 0 ? entries.get(hovered).label() : this.title;
            g.drawCenteredString(this.font, text, (int) cx, (int) cy - this.font.lineHeight / 2, (textAlpha << 24) | 0xFFFFFF);
        }
    }

    private void syncState() {
        int n = entries.size();
        if (hoverCur.length != n) {
            hoverPrev = new float[n];
            hoverCur = new float[n];
        }
    }

    private float hoverAmount(int index, float partialTick) {
        float t = Mth.lerp(partialTick, hoverPrev[index], hoverCur[index]);
        return t * t * (3f - 2f * t);
    }

    private float computeProgress(float partialTick) {
        if (openTicks == 0) return 1f;
        float t = Mth.clamp((ticksOpen + partialTick) / openTicks, 0f, 1f);
        float inv = 1f - t;
        return 1f - inv * inv * inv;
    }

    private static int blend(int from, int to, float t) {
        int a = (int) Mth.lerp(t, (from >> 24) & 0xFF, (to >> 24) & 0xFF);
        int r = (int) Mth.lerp(t, (from >> 16) & 0xFF, (to >> 16) & 0xFF);
        int g = (int) Mth.lerp(t, (from >> 8) & 0xFF, (to >> 8) & 0xFF);
        int b = (int) Mth.lerp(t, from & 0xFF, to & 0xFF);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int withAlphaFactor(int argb, float factor) {
        int alpha = Math.round(((argb >> 24) & 0xFF) * factor);
        return (alpha << 24) | (argb & 0xFFFFFF);
    }

    private static void drawSector(Matrix4f matrix, float cx, float cy, float rInner, float rOuter, float a0Deg, float a1Deg, int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int gr = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;

        int segments = Math.max(2, Mth.ceil((a1Deg - a0Deg) / 3f));

        BufferBuilder buf = Tesselator.getInstance()
                .begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        for (int j = 0; j <= segments; j++) {
            double rad = Math.toRadians(Mth.lerp(j / (float) segments, a0Deg, a1Deg));
            float sin = (float) Math.sin(rad);
            float cos = (float) Math.cos(rad);

            buf.addVertex(matrix, cx + sin * rOuter, cy - cos * rOuter, 0f).setColor(r, gr, b, a);
            buf.addVertex(matrix, cx + sin * rInner, cy - cos * rInner, 0f).setColor(r, gr, b, a);
        }

        BufferUploader.drawWithShader(buf.buildOrThrow());
    }

    private int getHoveredIndex(double mouseX, double mouseY, float cx, float cy) {
        double dx = mouseX - cx;
        double dy = mouseY - cy;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < innerRadius * progress || dist > (outerRadius + hoverGrow) * progress) return -1;

        int n = entries.size();
        double step = 360.0 / n;

        double angle = Math.toDegrees(Math.atan2(dx, -dy));
        if (angle < 0) angle += 360.0;

        return (int) (((angle + step / 2.0) % 360.0) / step);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && hovered >= 0) {
            Entry entry = entries.get(hovered);
            this.onClose();
            entry.action().run();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, this.width, this.height, 0x44000000);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}