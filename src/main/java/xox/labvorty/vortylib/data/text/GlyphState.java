package xox.labvorty.vortylib.data.text;

public final class GlyphState {
    public static final float TAU = (float) (Math.PI * 2.0);
    private static final float CHAR_WIDTH = 6f;
    public float time;
    public float x, y;
    public float advance;
    public int index;
    public int codePoint;
    public boolean shadow;
    public float dx, dy;
    public float rotation;
    public float scale = 1f;
    public float alpha = 1f;
    public int color = -1;

    public void begin(float time, float x, float y, float advance, int index, int codePoint, boolean shadow) {
        this.time = time;
        this.x = x;
        this.y = y;
        this.advance = advance;
        this.index = index;
        this.codePoint = codePoint;
        this.shadow = shadow;
        this.dx = 0f;
        this.dy = 0f;
        this.rotation = 0f;
        this.scale = 1f;
        this.alpha = 1f;
        this.color = -1;
    }

    public float phase(float perChar) {
        return x / CHAR_WIDTH * perChar;
    }

    public static float frac(float v) {
        return v - (float) Math.floor(v);
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static float noise(float x, float y, float time, float rate, int salt) {
        int h = ((int) Math.floor(x) * 73856093)
                ^ ((int) Math.floor(y) * 19349663)
                ^ ((int) Math.floor(time * rate) * 83492791)
                ^ (salt * 668265263);
        h ^= h >>> 13;
        h *= 0x5bd1e995;
        h ^= h >>> 15;
        return ((h & 0xFFFF) / 65535f - 0.5f) * 2f;
    }
}