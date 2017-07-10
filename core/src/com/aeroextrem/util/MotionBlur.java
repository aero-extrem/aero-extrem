package com.aeroextrem.util;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import org.intellij.lang.annotations.Language;

public class MotionBlur implements Disposable {

	private float alpha = 0.3f;
	private Texture lastFrame;

	private ShaderProgram prog1, prog2;


	private Texture inTexture1 = null;
	private Texture inTexture2 = null;
	private Texture inLast2 = null;

	private Mesh quad1;
	private Mesh quad2;

	@Language("GLSL")
	private String vShader1 = "#ifdef GL_ES\n" +
			"\t#define PRECISION mediump\n" +
			"\tprecision PRECISION float;\n" +
			"#else\n" +
			"\t#define PRECISION\n" +
			"#endif\n" +
			"\n" +
			"attribute vec4 a_position;\n" +
			"attribute vec2 a_texCoord0;\n" +
			"varying vec2 v_texCoords;\n" +
			"\n" +
			"void main()\n" +
			"{\n" +
			"\tv_texCoords = a_texCoord0;\n" +
			"\tgl_Position = a_position;\n" +
			"}";

	@Language("GLSL")
	private String fShader1 = "#ifdef GL_ES\n" +
			"\t#define PRECISION mediump\n" +
			"\tprecision PRECISION float;\n" +
			"#else\n" +
			"\t#define PRECISION\n" +
			"#endif\n" +
			"\n" +
			"uniform sampler2D u_texture0;\n" +
			"varying vec2 v_texCoords;\n" +
			"\n" +
			"void main(void)\n" +
			"{\n" +
			"\tvec3 col = texture2D(u_texture0,v_texCoords).xyz;\n" +
			"\tgl_FragColor = vec4(col,1.0);\n" +
			"}";

	@Language("GLSL")
	private String vShader2 = "#ifdef GL_ES\n" +
			"\t#define PRECISION mediump\n" +
			"\tprecision PRECISION float;\n" +
			"#else\n" +
			"\t#define PRECISION\n" +
			"#endif\n" +
			"\n" +
			"attribute vec4 a_position;\n" +
			"attribute vec2 a_texCoord0;\n" +
			"varying vec2 v_texCoords;\n" +
			"\n" +
			"void main()\n" +
			"{\n" +
			"\tv_texCoords = a_texCoord0;\n" +
			"\tgl_Position = a_position;\n" +
			"}";

	@Language("GLSL")
	private String fShader2 = "#ifdef GL_ES\n" +
			"precision mediump float;\n" +
			"precision mediump int;\n" +
			"#endif\n" +
			"\n" +
			"// Unprocessed image\n" +
			"uniform sampler2D u_texture0;\n" +
			"// Last frame\n" +
			"uniform sampler2D u_texture1;\n" +
			"// Last frame alpha\n" +
			"uniform float u_blurOpacity;\n" +
			"\n" +
			"varying vec2 v_texCoords;\n" +
			"\n" +
			"void main() {\n" +
			"    gl_FragColor = max(texture2D(u_texture0, v_texCoords), texture2D(u_texture1, v_texCoords) * u_blurOpacity);\n" +
			"}";

	private static float[] verts = new float[16];
	public MotionBlur() {
		// vertex coord
		verts[0] = -1;
		verts[1] = -1;
		verts[2] = 1;
		verts[3] = -1;
		verts[4] = 1;
		verts[5] = 1;
		verts[6] = -1;
		verts[7] = 1;
		// tex coords
		verts[8] = 0f;
		verts[9] = 0f;
		verts[10] = 1f;
		verts[11] = 0f;
		verts[12] = 1f;
		verts[13] = 1f;
		verts[14] = 0f;
		verts[15] = 1f;
		quad1 = new Mesh(Mesh.VertexDataType.VertexArray, true, 4, 0, new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"), new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0"));
		quad2 = new Mesh(Mesh.VertexDataType.VertexArray, true, 4, 0, new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"), new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0"));
		quad1.setVertices(verts);
		quad2.setVertices(verts);

		prog1 = new ShaderProgram(vShader1, fShader1);
		prog1.begin();
		bind1();
		prog1.end();

		prog2 = new ShaderProgram(vShader2, fShader2);
		bind2();
	}

	@Override
	public void dispose() {

	}

	private void bind1() {
		prog1.setUniformf("u_texture0", 0);
	}

	private void bind2() {
		prog2.setUniformf("u_texture0", 0);
		prog2.setUniformf("u_blurOpacity", this.alpha);

		if(inLast2 != null)
			prog2.setUniformf("u_texture1", 1);
	}

	public void render(FrameBuffer src, FrameBuffer dest) {
		inTexture1.bind(0);
		prog1.begin();
		quad1.render(prog1, GL20.GL_TRIANGLE_FAN, 0, 4);
		prog1.end();

		inTexture2.bind(0);
		prog2.begin();
		quad2.render(prog2, GL20.GL_TRIANGLE_FAN, 0, 4);
		prog2.end();
	}

}
