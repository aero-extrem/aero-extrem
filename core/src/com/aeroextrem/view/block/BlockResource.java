package com.aeroextrem.view.block;

import com.aeroextrem.engine.common3d.resource.PhysicsInfo;
import com.aeroextrem.engine.common3d.resource.PhysicsResource;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.utils.ArrayMap;
import org.jetbrains.annotations.NotNull;

import static com.badlogic.gdx.graphics.VertexAttributes.Usage.*;

/** Ressource für den Boden */
public class BlockResource implements PhysicsResource {

	public static final float SIZE = 1f;

	// IDs
	public static final String PART = "block";

	private ArrayMap<String, PhysicsInfo> physicsNodes;
	private Model model;
	private Pixmap pixmap;


	@Override
	public void load() {
		physicsNodes = new ArrayMap<>(1);

		pixmap = new Pixmap(Gdx.files.internal("texture/ground.jpg"));
		physicsNodes.put(PART, loadPhysics());
	}

	@Override
	public void commit() {
		// Texturen
		Texture texture = new Texture(pixmap);
		Material material = new Material();
		material.set(TextureAttribute.createDiffuse(texture));

		// Modell
		ModelBuilder mb = new ModelBuilder();
		mb.begin();
		mb.node().id = PART;
		mb.node().id = PART;
		mb.manage(texture);
		int attributes = Position | Normal | TextureCoordinates;
		MeshPartBuilder mpb = mb.part(PART, GL20.GL_TRIANGLES, attributes, material);
		BoxShapeBuilder.build(mpb, SIZE, SIZE, SIZE);
		model = mb.end();
	}

	@NotNull
	private PhysicsInfo loadPhysics() {
		btBoxShape shape = new btBoxShape(new Vector3(SIZE, SIZE, SIZE));
		
		return new PhysicsInfo(shape, 0, PhysicsInfo.GROUP_WORLD, PhysicsInfo.GROUP_ALL);
	}

	@Override
	@NotNull
	public Model getModel() {
		return model;
	}

	@Override
	@NotNull
	public ArrayMap<String, PhysicsInfo> getPhysicsNodes() {
		return physicsNodes;
	}

	@Override
	public void dispose() {
		model.dispose();
	}

}
