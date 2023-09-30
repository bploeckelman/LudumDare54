#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_texureSize;
uniform vec2 u_screenBounds;

varying vec4 v_color;
varying vec2 v_texCoord;

void main() {
    vec4 finalColor;
    if (v_color.b > .5) {
        // draw Circle
        float radius = v_color.g * 1000.;
        float shapeDim = radius/100.;
        float dist =  distance(v_texCoord, vec2(.5)) * 2.;
        float leftEdge = 1. - (.01 /shapeDim);
        float rightEdge = 1. - (.15 / shapeDim);
        float filled = smoothstep(leftEdge, rightEdge, dist) * v_color.a;
        finalColor = vec4(filled, filled, filled, filled);
    } else {
        // draw Square
    }
    gl_FragColor = finalColor;

}
