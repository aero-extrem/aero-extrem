package com.aeroextrem.view.airplane.t50;

import com.aeroextrem.engine.common3d.behaviour.BehaviourWholePhysics;
import com.aeroextrem.engine.common3d.resource.WholePhysicsInstance;
import com.aeroextrem.engine.resource.GameResource;
import com.aeroextrem.view.airplane.test.TestPlaneData;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import org.jetbrains.annotations.NotNull;

/** Physikalische Eigenschaften */
public class SukhoiT50Physics implements BehaviourWholePhysics {

	// Liste der belegten Resourcen
	private Array<Disposable> despos;

	private WholePhysicsInstance instance;
	private btDynamicsWorld world;

	private final TestPlaneData data;

	public SukhoiT50Physics(TestPlaneData data) {
		this.data = data;
	}

	@Override
	public void onCreate(@NotNull GameResource resource) {
		despos = new Array<>();
	}

	/** Verbindungen zwischen Teilen erstellen */
	@Override
	public void onBindPhysics(@NotNull btDynamicsWorld world, @NotNull WholePhysicsInstance instance) {
		this.world = world;
		this.instance = instance;

		instance.part.rb.setFriction(0);

		// Connect fuselage -> parts
		/*connectToBody(AILERON_LEFT, OFFSET_AILERON_LEFT);
		connectToBody(AILERON_RIGHT, OFFSET_AILERON_RIGHT);
		connectToBody(FLAPS_LEFT, OFFSET_FLAPS_LEFT);
		connectToBody(FLAPS_RIGHT, OFFSET_FLAPS_RIGHT);
		connectToBody(LEAD_FLAPS_LEFT, OFFSET_LEAD_FLAPS_LEFT);
		connectToBody(LEAD_FLAPS_RIGHT, OFFSET_LEAD_FLAPS_RIGHT);
		connectToBody(RUDDER_LEFT, OFFSET_RUDDER_LEFT);
		connectToBody(RUDDER_RIGHT, OFFSET_RUDDER_RIGHT);
		connectToBody(GEAR_ROD_FRONT, OFFSET_GEAR_ROD_FRONT);
		connectToBody(GEAR_ROD_BACKLEFT, OFFSET_GEAR_ROD_BACKLEFT);
		connectToBody(GEAR_ROD_BACKRIGHT, OFFSET_GEAR_ROD_BACKRIGHT);

		connectWheel(GEAR_WHEEL_FRONT, OFFSET_GEAR_WHEEL_FRONT);
		connectWheel(GEAR_WHEEL_BACKLEFT, OFFSET_GEAR_WHEEL_BACKLEFT);
		connectWheel(GEAR_WHEEL_BACKRIGHT, OFFSET_GEAR_WHEEL_BACKRIGHT);*/
	}

	/*private void connectToBody(String node, Vector3 pos) {
		Matrix4 conn = new Matrix4().set(new Vector3(1f, 1f, 1f), new Quaternion());
		Matrix4 conn2 = new Matrix4().set(pos, new Quaternion());
		btFixedConstraint constraint = new btFixedConstraint(
			part.rb,
			instance.partMap.get(node).rb,
			conn, conn2
		);
		despos.add(constraint);
		world.addConstraint(constraint);
	}

	private void connectWheel(String node, Vector3 pos) {
		btHingeConstraint constraint = new btHingeConstraint(
			part.rb,
			instance.partMap.get(node).rb,
			pos, Vector3.Zero, Vector3.Z, Vector3.Z
		);
		despos.add(constraint);
		instance.world.addConstraint(constraint);
	}*/

	@Override
	public void physicsTick(float deltaTime) {
		Vector3 vel = instance.part.rb.getLinearVelocity();
		instance.part.rb.applyForce(vel.scl(-Math.min(vel.len2() * 0.02f, 0.8f)), Vector3.Zero);
		instance.part.rb.setAngularVelocity(instance.part.rb.getAngularVelocity().scl(0.2f));

		forceOnPlane(calcForce(0f, 300 * data.pitch, 0f), 2f, 0f, 0f);
		forceOnPlane(calcForce(0f, -300 * data.pitch, 0f), -2f, 0f, 0f);

		forceOnPlane(calcForce(0f, 30 * data.roll, 0f), 0f, 0f, 2f);
		forceOnPlane(calcForce(0f, -30 * data.roll, 0f), 0f, 0f, -2f);

		forceOnPlane(calcForce(0f, 0f, 30 * data.yaw), 2f, 0f, 0f);
		forceOnPlane(calcForce(0f, 0f, -30 * data.yaw), -2f, 0f, 0f);

		forceOnPlane(calcForce(100f * data.thrust, 0f, 0f), -1f, 0f, 0f);
	}

	private final Vector3 helper2 = new Vector3();
	private void forceOnPlane(Vector3 force, float posX, float posY, float posZ) {
		instance.part.rb.applyForce(force, helper2.set(posX, posY, posZ));
	}

	private final Vector3 helper = new Vector3();
	private Vector3 calcForce(float x, float y, float z) {
		helper.set(x, y, z);
		float len = helper.len();
		helper.nor();

		Quaternion quat = instance.part.rb.getOrientation();
		helper.mul(quat);

		helper.scl(len);
		return helper;
	}

	@Override
	public void dispose() {
		despos.forEach(Disposable::dispose);
	}

}
