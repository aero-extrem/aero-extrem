package com.aeroextrem.view.airplane.test;

import com.aeroextrem.engine.common3d.resource.PhysicsInfo;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShapeZ;
import org.jetbrains.annotations.NotNull;

import static com.aeroextrem.view.airplane.test.TestPlaneMaterials.*;

/** Ein Rad */
class Wheel implements PartVisual, PartPhysics {

	private static final String PART_NAME = "wheel_front";
	private static final float MASS = 1f;
	private static final float RADIUS = 0.3f;
	private static final float LENGTH = 0.3f;

	@Override
	@NotNull
	public PhysicsInfo loadPhysicsPart() {
		return new PhysicsInfo(
			new btCylinderShapeZ(new Vector3(RADIUS, RADIUS, LENGTH)),
			MASS,
			PhysicsInfo.GROUP_AIRPLANE, PhysicsInfo.GROUP_WORLD
		);
	}

	@Override
	public void loadNode(@NotNull ModelBuilder mb, @NotNull Node node) {
		MeshPartBuilder mpb;

		mpb = mb.part(PART_NAME, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, PLANE_DYNAMIC);

		mpb.setVertexTransform(new Matrix4().rotate(Vector3.X, 90));
		CylinderShapeBuilder.build(mpb, RADIUS, LENGTH, RADIUS, 5);
	}
}
