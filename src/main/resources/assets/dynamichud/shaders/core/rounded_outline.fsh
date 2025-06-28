#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform vec4 Roundness;
uniform vec4 Thickness;
uniform vec2 widthHeight;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

float sdRoundBoxOutline(vec2 p, vec2 b, vec4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x  = (p.y > 0.0) ? r.x  : r.y;
    vec2 q = abs(p) - b + r.x;
    float distance = min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
    return distance + Thickness.x * 0.5;  // Shift outline boundary
}

void main() {
    float distance = sdRoundBoxOutline(texCoord0 - widthHeight / 2, widthHeight / 2, Roundness);
    float fw = fwidth(distance);

    // Define strict outline boundaries
    float innerEdge = smoothstep(-Thickness.x, -Thickness.x + fw, distance);
    float outerEdge = smoothstep(0, fw, distance);

    // This is what allows the rectangle to support the original color's alpha uniformly thoughout.
    float outlineAlpha = (innerEdge - outerEdge) * ColorModulator.a * vertexColor.a;

    vec4 color = ColorModulator * vertexColor * vec4(1, 1, 1, outlineAlpha);

    if (color.a < 0.01) discard;
    fragColor = color;
}