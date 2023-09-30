#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_time;

varying vec4 v_color;
varying vec2 v_texCoord;



void main() {
    vec4 purple = vec4(.1, .3, .6, 1.);
    vec4 dark = vec4(.0, .0, .1, 1.);
    vec2 sampleCoords1 = vec2(v_texCoord * .7 + .3);
    sampleCoords1.x += u_time * .02;
    sampleCoords1.y -= u_time * .02;

    vec2 sampleCoords2 = vec2(v_texCoord * .7 + .2);
    sampleCoords2.x -= u_time * .02;
    sampleCoords2.y += u_time * .02;

    vec2 sampleCoords3 = vec2(v_texCoord * .7 + .5);
    sampleCoords3.x += u_time * .02;
    sampleCoords3.y += u_time * .02;

    vec2 sampleCoords4 = vec2(v_texCoord * .7 + .1);
    sampleCoords4.x -= u_time * .02;
    sampleCoords4.y -= u_time * .02;


    vec4 noise1 = texture2D(u_texture, sampleCoords1);
    vec4 noise2 = texture2D(u_texture, sampleCoords2);
    vec4 noise3 = texture2D(u_texture, sampleCoords3);
    vec4 noise4 = texture2D(u_texture, sampleCoords4);

    float noiseVal =  noise1.r * .125;
    noiseVal +=       noise1.b * .125;
    noiseVal +=       noise2.r * .125;
    noiseVal +=       noise2.b * .125;
    noiseVal +=       noise3.r * .125;
    noiseVal +=       noise3.b * .125;
    noiseVal +=       noise4.r * .125;
    noiseVal +=       noise4.b * .125;

    vec4 finalColor = mix(purple, dark, noiseVal);
    finalColor.a = .9;
    gl_FragColor = finalColor;
}
