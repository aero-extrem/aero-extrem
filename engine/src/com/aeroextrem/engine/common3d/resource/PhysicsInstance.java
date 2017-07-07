package com.aeroextrem.engine.common3d.resource;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;

/** Instanz eines Modells mit physikalischen Eigenschaften */
public class PhysicsInstance extends ModelInstance implements Disposable {

	public final ArrayMap<String, PhysicsPartInstance> partMap;

	/** Hiermit sollten nur Constraints erstellt werden. */
	public final btDynamicsWorld world;

	private static final Vector3 localInertia = new Vector3();
	public PhysicsInstance(PhysicsResource res, btDynamicsWorld world) {
		super(res.getModel());

		this.world = world;
		this.partMap = new ArrayMap<>(res.getPhysicsNodes().size);

		for(String node : res.getPhysicsNodes().keys()) {
			PhysicsInfo phys = res.getPhysicsNodes().get(node);

			// Create Rigid body
			btCollisionShape hitbox = phys.hitbox;

			hitbox.calculateLocalInertia(phys.mass, localInertia);
			btRigidBodyConstructionInfo rbci =
					new btRigidBodyConstructionInfo(phys.mass, null, hitbox, localInertia);

			btRigidBody rb = new btRigidBody(rbci);

			// Sync to model (MotionState)
			Matrix4 visualPos = getNode(node).globalTransform;
			MotionState ms = new MotionState();
			ms.transform = visualPos;
			rb.setMotionState(ms);

			PhysicsPartInstance part = new PhysicsPartInstance(rbci, ms, rb);

			// Write
			partMap.put(node, part);

			world.addRigidBody(part.rb, phys.group, phys.mask);
		}
	}

	@Override
	public void dispose() {
		for(PhysicsPartInstance part : partMap.values()) {
			world.removeRigidBody(part.rb);
			part.dispose();
		}
	}

}
