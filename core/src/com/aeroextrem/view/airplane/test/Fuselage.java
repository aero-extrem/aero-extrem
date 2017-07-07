package com.aeroextrem.view.airplane.test;

import com.aeroextrem.engine.common3d.resource.PhysicsInfo;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShapeX;

import static com.aeroextrem.view.airplane.test.TestPlaneMaterials.*;

/** Tank und Cockpit des Flugzeugs */
public class Fuselage implements PartVisual, PartPhysics {

	private static final String PART_NAME_1 = "fuselage";
	private static final String PART_NAME_2 = "back";
	private static final String PART_NAME_3 = "front";
	private static final float MASS = 50f;
	
	@Override
	public PhysicsInfo loadPhysicsPart() {
		return new PhysicsInfo(
			new btCapsuleShapeX(1f, 4.5f),
			MASS,
			PhysicsInfo.GROUP_AIRPLANE, PhysicsInfo.GROUP_WORLD
		);
	}

	@Override
	public void loadNode(ModelBuilder mb, Node node) {
		MeshPartBuilder mpb;

		// Plane Tank
		mpb = mb.part(PART_NAME_1, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, PLANE_STATIC);
		mpb.setVertexTransform(new Matrix4().rotate(Vector3.Z, 90));
		CylinderShapeBuilder.build(mpb, 1f, 3f, 1f, 10);

		// Plane Tail
		mpb = mb.part(PART_NAME_2, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, PLANE_STATIC);
		mpb.setVertexTransform(new Matrix4().translate(-1.75f, 0f, 0f).rotate(Vector3.Z,90));
		ConeShapeBuilder.build(mpb, 1f, 1f, 1f, 10);

		// Plane front
		mpb = mb.part(PART_NAME_3, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, PLANE_STATIC);
		mpb.setVertexTransform(new Matrix4().translate(1.5f, 0f, 0f).rotate(Vector3.Y, 90));
		SphereShapeBuilder.build(mpb, 1f, 1f, 1f, 10, 10, 0f, 180f, 0f, 180f);
	}
}
