package com.aeroextrem.scenario.view.skybox;

import com.aeroextrem.engine.resource.GameResource;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import org.jetbrains.annotations.NotNull;

import static com.badlogic.gdx.graphics.VertexAttributes.Usage.Normal;
import static com.badlogic.gdx.graphics.VertexAttributes.Usage.Position;
import static com.badlogic.gdx.graphics.VertexAttributes.Usage.TextureCoordinates;

// Himmel
public class SkyboxResource implements GameResource {

	// IDs
	public static final String PART_SKYBOX =	"skybox";
	public static final String VISUAL_SKYBOX =	"skybox";

	private Pixmap pixmap;
	private Model model;
	private Texture texture;

	@Override
	public void load() {
		pixmap = new Pixmap(Gdx.files.internal("texture/ground.jpg"));
	}

	@Override
	public void commit() {
		Material material = new Material();
		texture = new Texture(pixmap);
		material.set(ColorAttribute.createSpecular(Color.CORAL));
		material.set(TextureAttribute.createDiffuse(texture));

		ModelBuilder mb = new ModelBuilder();
		mb.begin();
		mb.node().id = VISUAL_SKYBOX;
		int attributes = Position | Normal | TextureCoordinates;
		MeshPartBuilder mpb = mb.part(PART_SKYBOX, GL20.GL_TRIANGLES, attributes, material);
		BoxShapeBuilder.build(mpb, 2f, 2f, 2f);
		model = mb.end();
	}

	@Override
	public @NotNull Model getModel() {
		return model;
	}

	@Override
	public void dispose() {
		pixmap.dispose();
		texture.dispose();
	}

}
