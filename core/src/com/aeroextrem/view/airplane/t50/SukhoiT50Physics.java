package com.aeroextrem.view.airplane.t50;

import com.aeroextrem.engine.common3d.behaviour.BehaviourPhysics;
import com.aeroextrem.engine.common3d.resource.PhysicsInstance;
import com.aeroextrem.engine.resource.GameResource;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btFixedConstraint;
import com.badlogic.gdx.physics.bullet.dynamics.btHingeConstraint;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import org.jetbrains.annotations.NotNull;

import static com.aeroextrem.view.airplane.t50.SukhoiT50.*;

/** Physikalische Eigenschaften */
public class SukhoiT50Physics implements BehaviourPhysics {

	// Liste der belegten Resourcen
	private Array<Disposable> despos;

	private PhysicsInstance instance;
	private btDynamicsWorld world;

	@Override
	public void onCreate(@NotNull GameResource resource) {
		despos = new Array<>();
	}

	/** Verbindungen zwischen Teilen erstellen */
	@Override
	public void onBindPhysics(@NotNull btDynamicsWorld world, @NotNull PhysicsInstance instance) {
		this.world = world;
		this.instance = instance;

		// Connect fuselage -> parts
		connectToBody(AILERON_LEFT, OFFSET_AILERON_LEFT);
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
		connectWheel(GEAR_WHEEL_BACKRIGHT, OFFSET_GEAR_WHEEL_BACKRIGHT);
	}

	private void connectToBody(String node, Vector3 pos) {
		Matrix4 conn = new Matrix4().set(new Vector3(1f, 1f, 1f), new Quaternion());
		Matrix4 conn2 = new Matrix4().set(pos, new Quaternion());
		btFixedConstraint constraint = new btFixedConstraint(
			instance.partMap.get(BODY).rb,
			instance.partMap.get(node).rb,
			conn, conn2
		);
		despos.add(constraint);
		world.addConstraint(constraint);
	}

	private void connectWheel(String node, Vector3 pos) {
		btHingeConstraint constraint = new btHingeConstraint(
			instance.partMap.get(BODY).rb,
			instance.partMap.get(node).rb,
			pos, Vector3.Zero, Vector3.Z, Vector3.Z
		);
		despos.add(constraint);
		instance.world.addConstraint(constraint);
	}

	@Override
	public void physicsTick(float deltaTime) {
		// TODO
	}

	@Override
	public void dispose() {
		despos.forEach(Disposable::dispose);
	}

}
