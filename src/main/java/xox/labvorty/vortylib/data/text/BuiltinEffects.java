package xox.labvorty.vortylib.data.text;

import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.Function;

public final class BuiltinEffects {
    public static void registerAll() {
        register("wave", false, p -> {
            float a = 2.5f * p.f("a", 1f), f = 4f * p.f("f", 1f), w = 0.6f * p.f("w", 1f);
            return g -> g.dy += Mth.sin(g.time * f - g.phase(w)) * a;
        });

        register("rainbow", true, p -> {
            float f = 0.4f * p.f("f", 1f), w = 0.07f * p.f("w", 1f), s = p.f("s", 0.7f);
            return g -> g.color = Mth.hsvToRgb(GlyphState.frac(g.time * f - g.phase(w)), s, 1f);
        });

        register("shake", false, p -> {
            float a = 0.8f * p.f("a", 1f), rate = 20f * p.f("f", 1f);
            return g -> {
                g.dx += GlyphState.noise(g.x, g.y, g.time, rate, 1) * a;
                g.dy += GlyphState.noise(g.x, g.y, g.time, rate, 2) * a;
            };
        });

        register("spin", false, p -> {
            float f = 0.75f * p.f("f", 1f) * GlyphState.TAU, w = 0.9f * p.f("w", 1f);
            return g -> g.rotation += g.time * f - g.phase(w);
        });

        register("swing", false, p -> {
            float a = (float) Math.toRadians(25.0) * p.f("a", 1f), f = 5f * p.f("f", 1f), w = 0.7f * p.f("w", 1f);
            return g -> g.rotation += Mth.sin(g.time * f - g.phase(w)) * a;
        });

        register("bounce", false, p -> {
            float a = 3f * p.f("a", 1f), f = 6f * p.f("f", 1f), w = 0.9f * p.f("w", 1f);
            return g -> g.dy -= Math.abs(Mth.sin(g.time * f - g.phase(w))) * a;
        });

        register("pulse", false, p -> {
            float lo = 1f - 0.2f * p.f("a", 1f), hi = 1f + 0.3f * p.f("a", 1f);
            float f = 5f * p.f("f", 1f), w = 0.9f * p.f("w", 1f);
            return g -> g.scale *= GlyphState.lerp(lo, hi, 0.5f + 0.5f * Mth.sin(g.time * f - g.phase(w)));
        });

        register("orbit", false, p -> {
            float r = 1.5f * p.f("a", 1f), f = 6f * p.f("f", 1f), w = 1.2f * p.f("w", 1f);
            return g -> {
                float angle = g.time * f - g.phase(w);
                g.dx += Mth.cos(angle) * r;
                g.dy += Mth.sin(angle) * r;
            };
        });

        register("twitch", false, p -> {
            float a = (float) Math.toRadians(14.0) * p.f("a", 1f), rate = 15f * p.f("f", 1f);
            return g -> g.rotation += GlyphState.noise(g.x, g.y, g.time, rate, 3) * a;
        });

        register("glitch", true, p -> {
            float a = 3f * p.f("a", 1f), rate = 8f * p.f("f", 1f);
            float threshold = 1f - 2f * p.f("c", 0.25f);
            return g -> {
                if (GlyphState.noise(0f, g.y, g.time, rate, 7) <= threshold) return;
                g.dx += GlyphState.noise(g.x, g.y, g.time, rate * 2f, 8) * a;
                g.color = GlyphState.noise(g.x, g.y, g.time, rate * 2f, 9) > 0f ? 0x00FFFF : 0xFF00FF;
            };
        });

        register("fire", true, p -> {
            float lick = 1.2f * p.f("a", 1f), f = p.f("f", 1f);
            return g -> {
                g.dy -= (0.5f + 0.5f * Mth.sin(g.time * 11f * f + g.x * 1.3f)) * lick;
                g.color = Mth.hsvToRgb(0.02f + 0.08f * (0.5f + 0.5f * Mth.sin(g.time * 9f * f - g.x * 0.7f)), 1f, 1f);
            };
        });

        register("fade", false, p -> {
            float min = p.f("a", 0.3f), f = 2f * p.f("f", 1f), w = 0.5f * p.f("w", 0f);
            return g -> g.alpha *= GlyphState.lerp(min, 1f, 0.5f + 0.5f * Mth.sin(g.time * f - g.phase(w)));
        });

        register("grad", true, p -> {
            int from = p.color("from", 0xFF5555), to = p.color("to", 0x5555FF);
            float f = 2f * p.f("f", 1f), w = 0.5f * p.f("w", 1f);
            return g -> g.color = lerpColor(from, to, 0.5f + 0.5f * Mth.sin(g.time * f - g.phase(w)));
        });
    }

    private static void register(String name, boolean colorful, Function<EffectParams, Consumer<GlyphState>> factory) {
        TextEffects.register(TextEffects.id(name), colorful, factory);
    }

    private static int lerpColor(int from, int to, float t) {
        int r = (int) GlyphState.lerp((from >> 16) & 0xFF, (to >> 16) & 0xFF, t);
        int g = (int) GlyphState.lerp((from >> 8) & 0xFF, (to >> 8) & 0xFF, t);
        int b = (int) GlyphState.lerp(from & 0xFF, to & 0xFF, t);
        return (r << 16) | (g << 8) | b;
    }
}