#version 150

#moj_import <fog.glsl>

#define SPIRAL_ARMS 3.0
#define SPIRAL_TIGHTNESS 7.0
#define WARP_SCALE 7.0
#define WARP_AMOUNT 0.12
#define FLOW_CYCLES 120.0
#define MAX_COLORS 8

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

uniform float GameTime;

uniform int PaletteSize;
uniform vec3 PaletteColor0;
uniform vec3 PaletteColor1;
uniform vec3 PaletteColor2;
uniform vec3 PaletteColor3;
uniform vec3 PaletteColor4;
uniform vec3 PaletteColor5;
uniform vec3 PaletteColor6;
uniform vec3 PaletteColor7;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;

out vec4 fragColor;

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(vec2 p) {
    float v = 0.0;
    float amp = 0.5;
    for (int i = 0; i < 3; i++) {
        v += amp * noise(p);
        p *= 2.0;
        amp *= 0.5;
    }
    return v;
}

vec3 palette(float t) {
    vec3 colors[MAX_COLORS] = vec3[](
    PaletteColor0, PaletteColor1, PaletteColor2, PaletteColor3,
    PaletteColor4, PaletteColor5, PaletteColor6, PaletteColor7
    );
    int size = clamp(PaletteSize, 2, MAX_COLORS);
    float s = fract(t) * float(size);
    int i = int(floor(s));
    float f = smoothstep(0.25, 0.75, fract(s));
    return mix(colors[i % size], colors[(i + 1) % size], f);
}

void main() {
    vec4 base = texture(Sampler0, texCoord0);

    if (base.a < 0.1) {
        discard;
    }

    vec2 p = texCoord0 - vec2(0.5);

    vec2 warp = vec2(
    fbm(p * WARP_SCALE),
    fbm(p * WARP_SCALE + 17.3)
    ) - 0.5;

    vec2 q = p + warp * WARP_AMOUNT;
    float r = length(q);
    float a = atan(q.y, q.x);

    float spiral = a * SPIRAL_ARMS / 6.2831853 + r * SPIRAL_TIGHTNESS - GameTime * FLOW_CYCLES;
    vec3 dye = palette(spiral);

    dye *= 0.9 + 0.2 * noise(texCoord0 * 80.0);

    vec4 color = vec4(base.rgb * dye, base.a);

    color *= vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}