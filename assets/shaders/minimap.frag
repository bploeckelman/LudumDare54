#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_time;

varying vec4 v_color;
varying vec2 v_texCoord;


void main() {
    vec4 maskColor = texture2D(u_texture, v_texCoord);
    vec4 finalColor = vec4(1. -maskColor.r);
    gl_FragColor = finalColor * v_color;
}
