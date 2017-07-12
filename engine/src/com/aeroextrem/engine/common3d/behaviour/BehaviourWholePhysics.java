package com.aeroextrem.engine.common3d.behaviour;

import com.aeroextrem.engine.common3d.resource.WholePhysicsInstance;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import org.jetbrains.annotations.NotNull;

/** Einfaches physikalisches Verhalten */
public interface BehaviourWholePhysics extends BehaviourBase {

	/** Wird ausgeführt, sobald das Behaviour in die Physikengine geladen wird
	 *
	 * @param world Physiksimulation
	 * @param physInstance Zuweisung Anzeige zu Physikkomponente */
	void onBindPhysics(@NotNull btDynamicsWorld world, @NotNull WholePhysicsInstance physInstance);

	/** Verhalten bei einzelnem Physik-Schritt
	 *
	 * @param deltaTime Zeit seit letztem Physikschritt */
	void physicsTick(float deltaTime);

}
