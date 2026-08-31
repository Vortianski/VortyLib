package xox.labvorty.vortylib.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.GuiGraphics;

public class ConfettiEffect {
    private static final int PARTICLES_PER_TRIGGER = 60;
    private static final float GRAVITY_PX_PER_SEC2 = 380f;
    private static final float PARTICLE_LIFETIME_SECONDS = 1.6f;
    private static final int[] COLORS = {0xFFEF476F, 0xFFFFD166, 0xFF06D6A0, 0xFF118AB2, 0xFF073B4C};

    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();
    private long lastTickTime = -1;

    public void trigger(int originX, int originY) {
        for (int i = 0; i < PARTICLES_PER_TRIGGER; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 80 + random.nextDouble() * 160;
            float vx = (float) (Math.cos(angle) * speed);
            float vy = (float) (Math.sin(angle) * speed) - 120f;
            int color = COLORS[random.nextInt(COLORS.length)];
            int size = 2 + random.nextInt(3);
            particles.add(new Particle(originX, originY, vx, vy, color, size));
        }
    }

    public void tick() {
        long now = System.currentTimeMillis();
        if (lastTickTime < 0) {
            lastTickTime = now;
            return;
        }
        float dt = (now - lastTickTime) / 1000f;
        lastTickTime = now;

        for (Particle p : particles) {
            p.vy += GRAVITY_PX_PER_SEC2 * dt;
            p.x += p.vx * dt;
            p.y += p.vy * dt;
            p.life -= dt;
        }
        particles.removeIf(p -> p.life <= 0);
    }

    public void render(GuiGraphics guiGraphics) {
        for (Particle p : particles) {
            int alpha = (int) Math.max(0, Math.min(255, 255 * (p.life / PARTICLE_LIFETIME_SECONDS)));
            int argb = (alpha << 24) | (p.color & 0xFFFFFF);
            int half = p.size / 2;
            guiGraphics.fill((int) p.x - half, (int) p.y - half, (int) p.x + half, (int) p.y + half, argb);
        }
    }

    public boolean isActive() {
        return !particles.isEmpty();
    }

    private static class Particle {
        float x, y, vx, vy;
        final int color;
        final int size;
        float life = PARTICLE_LIFETIME_SECONDS;

        Particle(float x, float y, float vx, float vy, int color, int size) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
            this.size = size;
        }
    }
}