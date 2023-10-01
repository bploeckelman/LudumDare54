#ifdef GL_ES
precision mediump float;
#endif



uniform sampler2D u_texture;
uniform sampler2D u_texture1;
uniform sampler2D u_texture2;

uniform float u_time;
uniform vec2 u_screenSize;
uniform float u_showFog;

varying vec4 v_color;
varying vec2 v_texCoord;


float cubicPulse( float c, float w, float x )
{
    x = abs(x - c);
    if( x>w ) return 0.0;
    x /= w;
    return 1.0 - x*x*(3.0-2.0*x);
}

vec4 permute(vec4 x){return mod(((x*34.0)+1.0)*x, 289.0);}
vec2 fade(vec2 t) {return t*t*t*(t*(t*6.0-15.0)+10.0);}

float cnoise(vec2 P){
    vec4 Pi = floor(P.xyxy) + vec4(0.0, 0.0, 1.0, 1.0);
    vec4 Pf = fract(P.xyxy) - vec4(0.0, 0.0, 1.0, 1.0);
    Pi = mod(Pi, 289.0); // To avoid truncation effects in permutation
    vec4 ix = Pi.xzxz;
    vec4 iy = Pi.yyww;
    vec4 fx = Pf.xzxz;
    vec4 fy = Pf.yyww;
    vec4 i = permute(permute(ix) + iy);
    vec4 gx = 2.0 * fract(i * 0.0243902439) - 1.0; // 1/41 = 0.024...
    vec4 gy = abs(gx) - 0.5;
    vec4 tx = floor(gx + 0.5);
    gx = gx - tx;
    vec2 g00 = vec2(gx.x,gy.x);
    vec2 g10 = vec2(gx.y,gy.y);
    vec2 g01 = vec2(gx.z,gy.z);
    vec2 g11 = vec2(gx.w,gy.w);
    vec4 norm = 1.79284291400159 - 0.85373472095314 * vec4(dot(g00, g00), dot(g01, g01), dot(g10, g10), dot(g11, g11));
    g00 *= norm.x;
    g01 *= norm.y;
    g10 *= norm.z;
    g11 *= norm.w;
    float n00 = dot(g00, vec2(fx.x, fy.x));
    float n10 = dot(g10, vec2(fx.y, fy.y));
    float n01 = dot(g01, vec2(fx.z, fy.z));
    float n11 = dot(g11, vec2(fx.w, fy.w));
    vec2 fade_xy = fade(Pf.xy);
    vec2 n_x = mix(vec2(n00, n01), vec2(n10, n11), fade_xy.x);
    float n_xy = mix(n_x.x, n_x.y, fade_xy.y);
    return 2.3 * n_xy;
}

void main() {
    float wiggleSpeedMulti = .3;
    vec2 wiggle = 10.0 *(1.0 / u_screenSize);
    vec4 exploredColor = texture2D(u_texture, vec2(v_texCoord.x, v_texCoord.y));
    vec4 unexploredColor = texture2D(u_texture1, vec2(v_texCoord.x, v_texCoord.y));

    vec2 maskVec = vec2(v_texCoord.x + wiggle.x * cnoise(v_texCoord * 2. + u_time * wiggleSpeedMulti), v_texCoord.y + wiggle.y * cnoise(v_texCoord * 2. - u_time * wiggleSpeedMulti));

    vec4 mask = texture2D(u_texture2, maskVec);

    // sample around for hard edge
    vec2 nextPixel = 1.0 / u_screenSize;
    vec4 mask1 = texture2D(u_texture2, maskVec + vec2(nextPixel.x, 0));
    vec4 mask2 = texture2D(u_texture2, maskVec + vec2(-nextPixel.x, 0));
    vec4 mask3 = texture2D(u_texture2, maskVec + vec2(0, nextPixel.y));
    vec4 mask4 = texture2D(u_texture2, maskVec + vec2(0, -nextPixel.y));
    float maskSmooth = (mask.r + mask1.r + mask2.r + mask3.r + mask4.r) / 5.;
    float edge = cubicPulse(.031, .02, maskSmooth);

    vec4 finalColor = mix(exploredColor, unexploredColor, 1. - mask.r);
    finalColor = mix(exploredColor, finalColor, u_showFog);
    finalColor = mix(finalColor, vec4(.2, .2, .5, 1.), edge);
    gl_FragColor = finalColor;

}
