package xox.labvorty.vortylib.data.shaders;

public final class ShaderTime {
    public static final ShaderTime INSTANCE = new ShaderTime();

    private static final double MAX_FRAME_DELTA_SECONDS = 0.25;

    private double totalSeconds = 0.0;
    private long lastNanoTime = -1L;

    private ShaderTime() {}

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

    public float getTime() {
        return (float) totalSeconds;
    }

    public void reset() {
        totalSeconds = 0.0;
        lastNanoTime = -1L;
    }
}