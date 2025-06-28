#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform vec4 Roundness;
uniform vec2 widthHeight;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

float sdRoundBoxOutline(vec2 p, vec2 b, vec4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x  = (p.y > 0.0) ? r.x  : r.y;
    vec2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
}

void main() {
    float distance = sdRoundBoxOutline(texCoord0 - widthHeight / 2, widthHeight / 2, Roundness);
    float fw = fwidth(distance);
    // This is what allows the rectangle to support the original color's alpha.
    float alpha = smoothstep(-fw, fw, -distance) * ColorModulator.a * vertexColor.a;
    vec4 color = ColorModulator * vertexColor * vec4(1, 1, 1, alpha);
    if (color.a < 0.01) discard;
    fragColor = color;
}