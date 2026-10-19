#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform mat4 InvViewProj;
uniform int ZoneCount;
uniform float Zones[96];
uniform float Time;
uniform int EffectMode;
uniform float Strength;

in vec2 texCoord;
out vec4 fragColor;

vec3 unproject(vec2 uv, float ndcZ) {
    vec4 p = InvViewProj * vec4(uv * 2.0 - 1.0, ndcZ, 1.0);
    return p.xyz / p.w;
}

bool intersectBox(vec3 ro, vec3 rd, vec3 bmin, vec3 bmax, out float tNear, out float tFar) {
    rd += vec3(lessThan(abs(rd), vec3(1e-6))) * 1e-6;
    vec3 inv = 1.0 / rd;
    vec3 t0 = (bmin - ro) * inv;
    vec3 t1 = (bmax - ro) * inv;
    vec3 tmin = min(t0, t1);
    vec3 tmax = max(t0, t1);
    tNear = max(max(tmin.x, tmin.y), tmin.z);
    tFar  = min(min(tmax.x, tmax.y), tmax.z);
    return tFar > max(tNear, 0.0);
}

vec3 sceneAt(vec2 uv) {
    return texture(Sampler0, clamp(uv, vec2(0.001), vec2(0.999))).rgb;
}

vec3 applyEffect(vec2 uv) {
    float k = Strength;
    if (EffectMode == 1) {
        return 1.0 - sceneAt(uv);
    }
    if (EffectMode == 2) {
        vec3 c = sceneAt(uv);
        float g = dot(c, vec3(0.299, 0.587, 0.114));
        return mix(c, vec3(g) * vec3(1.0, 0.9, 0.75), clamp(k, 0.0, 1.0));
    }
    if (EffectMode == 3) {
        vec2 res = vec2(textureSize(Sampler0, 0));
        float px = 8.0;
        vec2 p = (floor(uv * res / px) + 0.5) * px / res;
        return sceneAt(p);
    }

    vec2 wave = vec2(sin(uv.y * 40.0 + Time * 2.0), cos(uv.x * 40.0 + Time * 2.0)) * 0.004 * k;
    vec2 ca = (uv - 0.5) * 0.01 * k;
    return vec3(sceneAt(uv + wave + ca).r,
    sceneAt(uv + wave).g,
    sceneAt(uv + wave - ca).b);
}

void main() {
    float depth = texture(Sampler1, texCoord).r;

    vec3 ro = unproject(texCoord, -1.0);
    vec3 rd = normalize(unproject(texCoord, 1.0) - ro);

    float sceneT = 1e9;
    if (depth < 0.99999) {
        sceneT = dot(unproject(texCoord, depth * 2.0 - 1.0) - ro, rd);
    }

    float bias = 0.02 + 0.002 * min(sceneT, 1000.0);

    bool hit = false;
    float thickness = 0.0;
    float edge = 0.0;

    for (int i = 0; i < ZoneCount; i++) {
        vec3 bmin = vec3(Zones[i * 6],     Zones[i * 6 + 1], Zones[i * 6 + 2]);
        vec3 bmax = vec3(Zones[i * 6 + 3], Zones[i * 6 + 4], Zones[i * 6 + 5]);
        float tNear, tFar;
        if (!intersectBox(ro, rd, bmin, bmax, tNear, tFar)) continue;

        float enter = max(tNear, 0.0);
        if (enter > sceneT + bias) continue;

        hit = true;
        thickness += max(min(tFar, sceneT) - enter, 0.0);

        if (tNear > 0.0) {
            vec3 hp = ro + rd * enter;
            vec3 d = min(hp - bmin, bmax - hp);
            float mx = max(d.x, max(d.y, d.z));
            float mn = min(d.x, min(d.y, d.z));
            float mid = d.x + d.y + d.z - mx - mn;
            edge = max(edge, 1.0 - smoothstep(0.0, 0.04, mid));
        }
    }

    if (!hit) discard;

    vec3 color = applyEffect(texCoord);
    color = mix(color, vec3(0.55, 0.75, 1.0), 1.0 - exp(-thickness * 0.02));
    color = mix(color, vec3(0.85, 0.95, 1.0), edge * 0.7);
    fragColor = vec4(color, 1.0);
}