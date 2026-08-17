package xox.labvorty.vortylib.data.shaders;

/**
 * Single shared clock for all post-processing shaders. Ticked exactly
 * once per frame (see GameRendererMixin), never per-layer, so multiple
 * shaders reading Time all stay in sync and nothing double-accumulates.
 *
 * Uses real wall-clock deltas rather than game tick deltas, since tick
 * state can jump/reset (pausing, tick rate changes, frame skips) in ways
 * that make an accumulated "totalTime" jerk backward or stutter.
 */
public final class ShaderTime {
    public static final ShaderTime INSTANCE = new ShaderTime();

    // Clamp any single frame's delta - protects against a huge jump after
    // alt-tabbing, a stutter, or the world loading, which would otherwise
    // cause shader animations to "teleport" forward once.
    private static final double MAX_FRAME_DELTA_SECONDS = 0.25;

    private double totalSeconds = 0.0;
    private long lastNanoTime = -1L;

    private ShaderTime() {}

    /** Call exactly once per rendered frame. */
    public void tick() {
        long now = System.nanoTime();
        if (lastNanoTime < 0) {
            lastNanoTime = now;
            return;
        }

        double delta = (now - lastNanoTime) / 1_000_000_000.0;
        lastNanoTime = now;

        if (delta > MAX_FRAME_DELTA_SECONDS) {
            delta = MAX_FRAME_DELTA_SECONDS;
        }

        totalSeconds += delta;
    }

    /** Ever-increasing seconds since this clock started - no 0-1 wrap. */
    public float getTime() {
        return (float) totalSeconds;
    }

    /** Useful if you ever want to reset the clock (e.g. on world join). */
    public void reset() {
        totalSeconds = 0.0;
        lastNanoTime = -1L;
    }
}