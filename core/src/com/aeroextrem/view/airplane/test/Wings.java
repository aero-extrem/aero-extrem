package com.aeroextrem.view.airplane.test;

import com.aeroextrem.engine.common3d.resource.PhysicsInfo;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import org.jetbrains.annotations.NotNull;

import static com.aeroextrem.view.airplane.test.TestPlaneMaterials.*;

/** Zwei Flügel */
class Wings implements PartVisual, PartPhysics {

	private static final String PART_NAME = "wings";
	private static final float MASS = 10f;

	@NotNull
	@Override
	public PhysicsInfo loadPhysicsPart() {
		btCompoundShape wings = new btCompoundShape(false);
		btCollisionShape leftWing = new btBoxShape(new Vector3(0.75f, 0.15f, 2f));
		btCollisionShape rightWing = new btBoxShape(new Vector3(0.75f, 0.15f, 2f));
		wings.addChildShape(new Matrix4().translate(0f, 0f, -1.5f), leftWing);
		wings.addChildShape(new Matrix4().translate(0f, 0f, 1.5f), rightWing);

		return new PhysicsInfo(wings, MASS, PhysicsInfo.GROUP_AIRPLANE, PhysicsInfo.GROUP_WORLD);
	}

	@Override
	public void loadNode(@NotNull ModelBuilder mb, @NotNull Node node) {
		MeshPartBuilder mpb;

		mpb = mb.part(PART_NAME, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, PLANE_STATIC);

		// Wing left
		mpb.setVertexTransform(new Matrix4().translate(0f, 0f, -1.5f));
		BoxShapeBuilder.build(mpb, 0.75f, 0.15f, 2f);

		// Wing right
		mpb.setVertexTransform(new Matrix4().translate(0f, 0f, 1.5f));
		BoxShapeBuilder.build(mpb, 0.75f, 0.15f, 2f);

		node.globalTransform.translate(0, -1f, 0);
	}

}
