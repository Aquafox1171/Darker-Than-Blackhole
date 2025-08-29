#version 150

uniform sampler2D DiffuseSampler;
uniform float uStrength;   // 0..1
uniform float uGamma;      // >=1
uniform float uThreshold;  // 0..1
uniform float uSoftness;   // 0..1

in vec2 texCoord;
out vec4 fragColor;

float luma(vec3 c){ return dot(c, vec3(0.2126, 0.7152, 0.0722)); }

void main() {
    vec4 col = texture(DiffuseSampler, texCoord);
    float L = luma(col.rgb);

    // Soft threshold: t=0 above threshold, t->1 deeper into darkness
    float t = clamp((uThreshold - L) / max(uSoftness, 1e-5), 0.0, 1.0);

    float dark   = pow(1.0 - clamp(uStrength, 0.0, 1.0), max(1.0, uGamma));
    float factor = mix(1.0, dark, t);

    fragColor = vec4(col.rgb * factor, col.a);
}
