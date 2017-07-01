package com.aeroextrem.engine.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

import static com.badlogic.gdx.graphics.GL20.*;
import static com.badlogic.gdx.Gdx.gl20;

// https://stackoverflow.com/a/22777350/2639234
public class EnvironmentCubemap implements Disposable {

	public static final String FRAGMENT_SHADER = 
		"#ifdef GL_ES" +							"\n" +
		"precision mediump float;" +				"\n" +
		"#endif" +									"\n" +
		"uniform samplerCube u_environmentCubemap;"+"\n" +
		"varying vec2 v_texCoord0;" +				"\n" +
		"varying vec3 v_cubeMapUV;" +				"\n" +
													"\n" +
		"void main() {" +							"\n" +
		"    gl_FragColor = vec4(textureCube(u_environmentCubemap, v_cubeMapUV).rgb, 1.0);" + "\n" +
		"}";
	
	public static final String VERTEX_SHADER =
		"attribute vec3 a_position;" +					"\n" +
		"attribute vec3 a_normal;" +					"\n" +
		"attribute vec2 a_texCoord0;" +					"\n" +
		"uniform mat4 u_worldTrans;" + 					"\n" +
		"varying vec2 v_texCoord0;" + 					"\n" +
		"varying vec3 v_cubeMapUV;" + 					"\n" +
														"\n" +
		"void main() {" +								"\n" +
		"    v_texCoord0 = a_texCoord0;" +				"\n" +
		"    vec4 g_position = u_worldTrans * vec4(a_position, 1.0);" + "\n" +
		"    v_cubeMapUV = normalize(g_position.xyz);" +"\n" +
		"    gl_Position = vec4(a_position, 1.0);" + 	"\n" +
		"}";
	
	protected final Pixmap[] textures = new Pixmap[6];
	protected ShaderProgram shader;

	protected int u_worldTrans;
	protected Mesh quad;

	private Matrix4 worldTrans;
	private Quaternion cameraRot;
	
	/** Build environment cubemap out of a single texture */
	public EnvironmentCubemap(Pixmap sixmap) {
		int w = sixmap.getWidth();
		int h = sixmap.getHeight();
		for(int i = 0; i < 6; i++) textures[i] = new Pixmap(w/4, h/3, Pixmap.Format.RGB888);
		for(int x = 0; x < w; x++)
			for(int y = 0; y < h; y++){
				// -X
				if(x >= 0		&& x <= w/4		&& y >= h/3		&& y <= h*2/3	) 
					textures[1].drawPixel(x, y-h/3, sixmap.getPixel(x, y));
				// +Y
				if(x >= w/4		&& x <= w/2		&& y >= 0		&& y <= h/3		)
					textures[2].drawPixel(x-w/4, y, sixmap.getPixel(x, y));
				// +Z
				if(x >= w/4		&& x <= w/2		&& y >= h/3		&& y <= h*2/3	) 
					textures[4].drawPixel(x-w/4, y-h/3, sixmap.getPixel(x, y));
				// -Y
				if(x >= w/4		&& x <= w/2		&& y >= h*2/3	&& y <= h		) 
					textures[3].drawPixel(x-w/4, y-h*2/3, sixmap.getPixel(x, y));
				// +X
				if(x >= w/2		&& x <= w*3/4	&& y >= h/3		&& y <= h*2/3	)
					textures[0].drawPixel(x-w/2, y-h/3, sixmap.getPixel(x, y));
				// -Z
				if(x >= w*3/4	&& x <= w		&& y >= h/3		&& y <= h*2/3	) 
					textures[5].drawPixel(x-w*3/4, y-h/3, sixmap.getPixel(x, y));
			}
		sixmap.dispose();
		
		init();
	}
	
	/** Build environment cubemap out of multiple textures */
	public EnvironmentCubemap(Pixmap posX, Pixmap negX, Pixmap posY, Pixmap negY, Pixmap posZ, Pixmap negZ) {
		textures[0] = posX;
		textures[1] = negX;
		textures[2] = posY;
		textures[3] = negY;
		textures[4] = posZ;
		textures[5] = negZ;
		
		init();
	}
	
	private void init() {
		shader = new ShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);
		if (!shader.isCompiled())
			throw new GdxRuntimeException(shader.getLog());
		
		u_worldTrans = shader.getUniformLocation("u_worldTrans");
		
		quad = createQuad();
		worldTrans = new Matrix4();
		cameraRot = new Quaternion();
		
		initCubemap();
	}
	
	private void initCubemap() {
		//bind cubemap
		gl20.glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
		gl20.glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GL_RGB, textures[0].getWidth(), textures[0].getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, textures[0].getPixels());
		gl20.glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GL_RGB, textures[1].getWidth(), textures[1].getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, textures[1].getPixels());

		gl20.glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GL_RGB, textures[2].getWidth(), textures[2].getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, textures[2].getPixels());
		gl20.glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GL_RGB, textures[3].getWidth(), textures[3].getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, textures[3].getPixels());

		gl20.glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GL_RGB, textures[4].getWidth(), textures[4].getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, textures[4].getPixels());
		gl20.glTexImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GL_RGB, textures[5].getWidth(), textures[5].getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, textures[5].getPixels());

		//gl20.glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
		//gl20.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

		gl20.glTexParameteri ( GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER,GL_LINEAR_MIPMAP_LINEAR );
		gl20.glTexParameteri ( GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER,GL_LINEAR );
		gl20.glTexParameteri ( GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE );
		gl20.glTexParameteri ( GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE );

		gl20.glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
	}

	public void render(Camera camera) {
		camera.view.getRotation(cameraRot, true);
		cameraRot.conjugate();
		worldTrans.idt();
		worldTrans.rotate(cameraRot);

		shader.begin();
		shader.setUniformMatrix(u_worldTrans, worldTrans.translate(0, 0, -1));
		quad.render(shader, GL_TRIANGLES);
		shader.end();
	}

	public Mesh createQuad() {
		Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.TexCoords(0));
		mesh.setVertices(new float[] {
			-1, -1,  0,  1,  1,  1,  1,  0,  1,
			 1, -1,  0,  1,  1,  1,  1,  1,  1,
			 1,  1,  0,  1,  1,  1,  1,  1,  0,
			-1,  1,  0,  1,  1,  1,  1,  0,  0
		});
		mesh.setIndices(new short[] {0, 1, 2, 2, 3, 0});
		return mesh;
	}

	@Override
	public void dispose() {
		shader.dispose();
		quad.dispose();
		for(Pixmap texture : textures)
			texture.dispose();
	}
}
