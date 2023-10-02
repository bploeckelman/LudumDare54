#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_time;

varying vec4 v_color;
varying vec2 v_texCoord;


void main()
{
    // normalized coordinates (-1 to 1 vertically)
    vec2 p = v_texCoord * 2. - 1.;
    p.x *= (1280./720.);
    p.x += sin(u_time) * .11;
    p.y += cos(u_time * 1.3) * .11;

    // angle of each pixel to the center of the screen
    float a = atan(p.y,p.x);

    // cylindrical tunnel
    float r = length(p);

    // index texture by (animated inverse) radius and angle
    vec2 uv = vec2( 0.3/r + 0.2*u_time, a/3.14 );

    vec2 uv2 = vec2( uv.x, atan(p.y,abs(p.x))/3.14 );

    vec3 col = texture2D(u_texture, uv).xyz;

    // darken at the center
    col = col*r;

    gl_FragColor = vec4( col, 1.0 );
}
