#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform sampler2D u_texture1;

uniform vec3 u_pos;
uniform float u_nebula;

uniform float u_time;

varying vec4 v_color;
varying vec2 v_texCoord;


vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}


void main() {
    // Sky Background Color
    vec4 vColor = vec4(vec3( 0.1, 0.1, 0.3 ) * (1. - v_texCoord.y), 1.);

    vec2 sampleCoords1 = vec2(v_texCoord * 1.9 + .3);
    sampleCoords1.x += u_time * .02;
    sampleCoords1.y -= u_time * .02;

    vec2 sampleCoords2 = vec2(v_texCoord * 1.9 + .2);
    sampleCoords2.x -= u_time * .02;
    sampleCoords2.y += u_time * .02;

    vec2 staticCoords = vec2(v_texCoord * .5 + .2);


    vec4 noise1 = texture2D(u_texture1, sampleCoords1);
    vec4 noise2 = texture2D(u_texture1, sampleCoords2);

    vec4 nebula1 = texture2D(u_texture1, staticCoords);
    vec4 nebula2 = texture2D(u_texture1, staticCoords + vec2(.005, -.005) );
    vec4 nebula3 = texture2D(u_texture1, staticCoords + vec2(.005, +.005) );

    float noiseVal =  noise1.b * .5;
    noiseVal += noise2.b * .5;
    noiseVal = smoothstep(.5, .8, noiseVal);
    noiseVal *= noiseVal;


    vec4 stars1 = texture2D(u_texture, v_texCoord * 1.1);
    vColor += stars1 * (stars1.a * (.3 + 1.9 * noiseVal));

    vec2 offsetStars = vec2(u_pos.x /(1280.* 80.), -u_pos.y / (720.* 80.));
    vec4 stars2 = texture2D(u_texture, v_texCoord * .8 + offsetStars);

    vColor += stars2 * (stars2.a * (.2 + 1.9 * noiseVal));

    vec4 nebulaColor1 = vec4 (hsv2rgb(vec3(.5+.5*sin(u_time*.1), 0.5, .25)), 0.);
    vec4 nebulaColor2 = vec4 (hsv2rgb(vec3(.5+.5*sin(u_time*.21), 1., .25)), 0.);
    vec4 nebulaColor3 = vec4 (hsv2rgb(vec3(.5+.5*sin(u_time*.11), .8, .25)), 0.);


    vec4 finalNebulaColor = nebulaColor1 * .25 * smoothstep(.55, .8, nebula1.g) ;
    finalNebulaColor += nebulaColor2 * .9 * smoothstep(.6, .95, nebula2.g);
    finalNebulaColor += nebulaColor3 * .5 * smoothstep(.6, .8, nebula3.g);

    finalNebulaColor = mix (vec4(0), finalNebulaColor, u_nebula);
    gl_FragColor = vec4(vColor + finalNebulaColor);
}
