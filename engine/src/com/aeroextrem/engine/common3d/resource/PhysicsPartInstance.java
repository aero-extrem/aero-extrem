package com.aeroextrem.engine.common3d.resource;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;

/** Instanz einer physikalischen Komponente */
public class PhysicsPartInstance implements Disposable {

	public PhysicsPartInstance(btRigidBody.btRigidBodyConstructionInfo rbci, MotionState ms, btRigidBody rb) {
		this.rbci = rbci;
		this.ms = ms;
		this.rb = rb;
	}

	/** Trägheit etc. */
	public final btRigidBody.btRigidBodyConstructionInfo rbci;
	/** Synchronisation visuelle Position – physikalische Position */
	public final MotionState ms;
	/** Starrer Körper / Objekt aus der Physiksimulation */
	public final btRigidBody rb;

	/** Verschiebt eine ganze Instanz, indem alle ihre Nodes/Komponenten verschoben werden.
	 *
	 * @param model ModelInstance, die verschoben werden soll.
	 * @param translation Vektor, um den verschoben wird.*/
	public static void moveObject(ModelInstance model, Vector3 translation) {
		for (Node node : model.nodes) {
			node.globalTransform.translate(translation);
		}
	}

	/** Gibt den Speicher frei und verwirft alle Objekte */
	@Override
	public void dispose() {
		rbci.dispose();
		rb.dispose();
		ms.dispose();
	}

}
