package com.aeroextrem.engine.common3d.behaviour;

import com.badlogic.gdx.InputProcessor;

/** Für Reaktionen auf Benutzereingabe */
public interface BehaviourInput extends BehaviourBase {

	/** Wird ausgeführt, sobald das Behaviour geladen wird
	 *
	 * @return Objekt, das auf Inputs reagiert */
	InputProcessor onBindInput();

}
