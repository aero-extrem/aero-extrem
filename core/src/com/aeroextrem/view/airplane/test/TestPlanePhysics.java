package com.aeroextrem.view.airplane.test;

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

import static com.aeroextrem.view.airplane.test.TestPlaneResource.*;

/** Physikalisches Verhalten vom TestPlane */
public class TestPlanePhysics implements BehaviourPhysics {

	// Liste der belegten Resourcen
	private Array<Disposable> despos;

	private PhysicsInstance instance;
	
	private final TestPlaneData data;
	
	public TestPlanePhysics(TestPlaneData data) {
		this.data = data;
	}
	
	@Override
	public void onCreate(@NotNull GameResource resource) {
		despos = new Array<>();
	}

	/** Verbindungen zwischen Teilen erstellen */
	@Override
	public void onBindPhysics(@NotNull btDynamicsWorld world, @NotNull PhysicsInstance instance) {

		this.instance = instance;
		// Connect fuselage -> wings
		Matrix4 conn = new Matrix4().set(new Vector3(1f, 1f, 1f), new Quaternion());
		btFixedConstraint constrFuselageWings = new btFixedConstraint(
				instance.partMap.get(NODE_FUSELAGE).rb,
				instance.partMap.get(NODE_WINGS).rb,
				conn, conn
		);
		despos.add(constrFuselageWings);
		world.addConstraint(constrFuselageWings);

		// Connect fuselage -> wheels
		connectWheel(instance, NODE_FRONTWHEEL,	new Vector3(1, -1f, 0));
		connectWheel(instance, NODE_BACKWHEEL_L,	new Vector3(-1f, -1f, -0.3f));
		connectWheel(instance, NODE_BACKWHEEL_R,	new Vector3(-1f, -1f, 0.3f));
	}

	/** Assoziiert ein Rad mit dem Flugzeug.
	 * 
	 * Erstellt eine 2-Winkel-Restriktion zwischen Flugzeug und Rad
	 *
	 * @param wheelPart ID des rotierenden starren Körpers (Rad)
	 * @param relativePos Vektor Flugzeug -> Rad  */
	private void connectWheel(
			@NotNull PhysicsInstance instance,
			@NotNull String wheelPart,
			@NotNull Vector3 relativePos
	) {
		/* Erklärung
		 * Das erstellt eine Bewegungseinschränkung zwischen statischen Flugzeugteilen und dessen Räder.
		 * Das Flugzeug rotiert (theoretisch) entlang der Z-Achse um das Rad -> relativePos als pivotInA
		 * Effektiv bedeutet das, das sich das Rad frei entlang der Z-Achse drehen kann, jedoch die Winkel an
		 *   X- und Y-Achse konstant bleiben.
		 * Wichtig ist dass der Rotationspunkt im Reifen liegt -> Nullvektor als pivotInB 
		 * Die Distanz zwischen Rad und Flugzeug ist relativePos. */
		btHingeConstraint constraint = new btHingeConstraint(
				instance.partMap.get(NODE_FUSELAGE).rb,
				instance.partMap.get(wheelPart).rb,
				relativePos, Vector3.Zero, Vector3.Z, Vector3.Z);
		despos.add(constraint);
		instance.world.addConstraint(constraint);
	}

	/** Aerodynamische Kräfte errechnen */
	@Override
	public void physicsTick(float deltaTime) {
		Vector3 vel = instance.partMap.get(NODE_FUSELAGE).rb.getLinearVelocity();
		instance.partMap.get(NODE_FUSELAGE).rb.applyForce(vel.scl(Math.min(vel.len2() * 0.02f, 0.8f)), Vector3.Zero);

		instance.partMap.get(NODE_FUSELAGE).rb.applyForce(new Vector3(0f, data.pitch, 0f).mul(instance.partMap.get(NODE_FUSELAGE).rb.getOrientation()).scl(300f), Vector3.Y);
		instance.partMap.get(NODE_FUSELAGE).rb.applyForce(new Vector3(0f, -data.pitch, 0f).mul(instance.partMap.get(NODE_FUSELAGE).rb.getOrientation()).scl(3000f), new Vector3(0f, -1 ,0f));

		instance.partMap.get(NODE_FUSELAGE).rb.applyForce(new Vector3(0f, data.roll, 0f).mul(instance.partMap.get(NODE_FUSELAGE).rb.getOrientation()).scl(30f), Vector3.Z);
		instance.partMap.get(NODE_FUSELAGE).rb.applyForce(new Vector3(0f, -data.roll, 0f).mul(instance.partMap.get(NODE_FUSELAGE).rb.getOrientation()).scl(30f), new Vector3(0f, 0f, -1f));

		instance.partMap.get(NODE_FUSELAGE).rb.applyForce(new Vector3(0f, 0f, data.yaw).mul(instance.partMap.get(NODE_FUSELAGE).rb.getOrientation()).scl(30f), Vector3.X);
		instance.partMap.get(NODE_FUSELAGE).rb.applyForce(new Vector3(0f, 0f, data.yaw).mul(instance.partMap.get(NODE_FUSELAGE).rb.getOrientation()).scl(30f), new Vector3(-1f, 0f, 0f));


		instance.partMap.get(NODE_FUSELAGE).rb.applyForce(new Vector3(data.thrust, 0f, 0f).mul(instance.partMap.get(NODE_FUSELAGE).rb.getOrientation()).scl(1000f), new Vector3(-1f, 0f, 0f));

		if(data.brakes) {
			instance.partMap.get(NODE_FRONTWHEEL).rb.setAngularFactor(.1f);
		}
	}

	/** Verbindungen zwischen Teilen löschen */
	@Override
	public void dispose() {
		for(Disposable d : despos)
			d.dispose();
	}

	
}
