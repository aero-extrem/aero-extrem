package com.aeroextrem.engine.common3d.resource;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.utils.Disposable;
import org.jetbrains.annotations.NotNull;

/** Instanz eines Modells mit physikalischen Eigenschaften
 *
 * Hier wird das ganze Model transformiert anstatt die einzelnen Knoten */
public class WholePhysicsInstance extends ModelInstance implements Disposable {

	public final PhysicsPartInstance part;

	/** Hiermit sollten nur Constraints erstellt werden. */
	public final btDynamicsWorld world;

	private static final Vector3 localInertia = new Vector3();
	public WholePhysicsInstance(@NotNull WholePhysicsResource res, @NotNull btDynamicsWorld world) {
		super(res.getModel());

		this.world = world;

		PhysicsInfo phys = res.getPhysicsInfo();

		// Create Rigid body
		btCollisionShape hitbox = phys.hitbox;

		hitbox.calculateLocalInertia(phys.mass, localInertia);
		btRigidBodyConstructionInfo rbci =
				new btRigidBodyConstructionInfo(phys.mass, null, hitbox, localInertia);

		btRigidBody rb = new btRigidBody(rbci);

		// Sync to model (MotionState)
		Matrix4 visualPos = transform;
		MotionState ms = new MotionState();
		ms.transform = visualPos;
		rb.setMotionState(ms);

		part = new PhysicsPartInstance(rbci, ms, rb);

		world.addRigidBody(part.rb, phys.group, phys.mask);
	}

	@Override
	public void dispose() {
		world.removeRigidBody(part.rb);
		part.dispose();
	}

}
