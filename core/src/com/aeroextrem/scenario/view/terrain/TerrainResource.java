package com.aeroextrem.scenario.view.terrain;

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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.collision.btStaticPlaneShape;
import com.badlogic.gdx.utils.ArrayMap;
import org.jetbrains.annotations.NotNull;

import static com.badlogic.gdx.graphics.VertexAttributes.Usage.*;

/** Ressource für den Boden */
public class TerrainResource implements PhysicsResource {

	// IDs
	public static final String PART_GROUND =	"ground";
	public static final String VISUAL_GROUND =	"ground";
	public static final String PHYS_GROUND 	=	"ground";
	
	private ArrayMap<String, PhysicsInfo> physicsNodes;
	private Model model;
	private Pixmap pixmap;

	public static final float SIZE = 20000f;
	
	@Override
	public void load() {
		physicsNodes = new ArrayMap<>(1);

		pixmap = new Pixmap(Gdx.files.internal("texture/ground.jpg"));
		physicsNodes.put(PHYS_GROUND, loadPhysicsGround());
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
		mb.node().id = VISUAL_GROUND;
		mb.manage(texture);
		int attributes = Position | Normal | TextureCoordinates;
		MeshPartBuilder mpb = mb.part(PART_GROUND, GL20.GL_TRIANGLES, attributes, material);
		mpb.rect(
				+SIZE/2, 1f, +SIZE/2,
				+SIZE/2, 1f, -SIZE/2,
				-SIZE/2, 1f, -SIZE/2,
				-SIZE/2, 1f, +SIZE/2,
				0f, 1f, 0f
		);
		model = mb.end();
	}

	private PhysicsInfo loadPhysicsGround() {
		btCompoundShape shape = new btCompoundShape(false);
		shape.addChildShape(newMatrixAt(0f,		0f,		0f),	 new btStaticPlaneShape(new Vector3( 0f, +1f,  0f), 1f));
		shape.addChildShape(newMatrixAt(0f,		+SIZE,	0f),	 new btStaticPlaneShape(new Vector3( 0f, -1f,  0f), 1f));
		shape.addChildShape(newMatrixAt(-SIZE/2,0f,		0f),	 new btStaticPlaneShape(new Vector3(+1f,  0f,  0f), 1f));
		shape.addChildShape(newMatrixAt(+SIZE/2,0f,		0f),	 new btStaticPlaneShape(new Vector3(-1f,  0f,  0f), 1f));
		shape.addChildShape(newMatrixAt(0f,		0f,		-SIZE/2),new btStaticPlaneShape(new Vector3( 0f,  0f, +1f), 1f));
		shape.addChildShape(newMatrixAt(0f,		0f,		+SIZE/2),new btStaticPlaneShape(new Vector3( 1f,  0f, -1f), 1f));

		return new PhysicsInfo(shape, 0, PhysicsInfo.GROUP_WORLD, PhysicsInfo.GROUP_ALL);
	}

	@Override
	public @NotNull Model getModel() {
		return model;
	}

	@Override
	public @NotNull ArrayMap<String, PhysicsInfo> getPhysicsNodes() {
		return physicsNodes;
	}

	@Override
	public void dispose() {
		model.dispose();
	}
	
	/** Bequemlichkeits-Methode, die eine Matrix mit angegeben Koordinaten erstellt. */
	private static Matrix4 newMatrixAt(float x, float y, float z) {
		return new Matrix4().translate(x, y, z);
	}
	
}
