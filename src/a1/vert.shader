#version 430

uniform float horiz_offset;
uniform float vert_offset;
uniform float size;
uniform float colorFlag;
uniform float moveFlag;
out vec4 initialColor;

void main(void)
{ if (gl_VertexID == 0) gl_Position = vec4( size+ horiz_offset,-size + vert_offset, 0.0, 1.0);
  else if (gl_VertexID == 1) gl_Position = vec4(-size+ horiz_offset,-size + vert_offset, 0.0, 1.0);
  else gl_Position = vec4( size+ horiz_offset, size + vert_offset, 0.0, 1.0);

  if(colorFlag == 0) {
	//RED
    if (gl_VertexID == 0) {
      initialColor = vec4(1.0, 0.0, 0.0, 1.0);
    } else if (gl_VertexID == 1) {
        initialColor = vec4(1.0, 0.0, 0.0, 1.0);
    } else {
        initialColor = vec4(1.0, 0.0, 0.0, 1.0);
    }
  } else if (colorFlag == 1) {
	//GREEN
    if (gl_VertexID == 0) {
      initialColor = vec4(0.0, 1.0, 0.0, 1.0);
    } else if (gl_VertexID == 1) {
        initialColor = vec4(0.0, 1.0, 0.0, 1.0);
    } else {
        initialColor = vec4(0.0, 1.0, 0.0, 1.0);
    }
  } else if (colorFlag == 2) {
	//RED
    if (gl_VertexID == 0) {
      initialColor = vec4(0.0, 0.0, 1.0, 1.0);
    } else if (gl_VertexID == 1) {
        initialColor = vec4(0.0, 0.0, 1.0, 1.0);
    } else {
        initialColor = vec4(0.0, 0.0, 1.0, 1.0);
    }
  } else if (colorFlag == 3) {
	//RGB PRISM
    if (gl_VertexID == 0) {
      initialColor = vec4(1.0, 0.0, 0.0, 1.0);
    } else if (gl_VertexID == 1) {
        initialColor = vec4(0.0, 1.0, 0.0, 1.0);
    } else {
        initialColor = vec4(0.0, 0.0, 1.0, 1.0);
    }
  }
}