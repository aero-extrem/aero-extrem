package com.aeroextrem.engine.common3d.behaviour;

import com.aeroextrem.engine.common3d.resource.PhysicsInstance;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;

/** Physikalisches Verhalten */
public interface BehaviourPhysics extends BehaviourBase {

	/** Wird ausgeführt, sobald das Behaviour in die Physikengine geladen wird
	 *
	 * @param world Physiksimulation
	 * @param physInstance Zuweisung Anzeige zu Physikkomponente */
	void onBindPhysics(btDynamicsWorld world, PhysicsInstance physInstance);

	/** Verhalten bei einzelnem Physik-Schritt
	 *
	 * @param deltaTime Zeit seit letztem Physikschritt */
	void physicsTick(float deltaTime);

}
