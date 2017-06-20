package com.aeroextrem.engine.common3d.behaviour;

import com.aeroextrem.engine.common3d.input.InputState;

/** Für Reaktionen auf Benutzereingabe */
public interface BehaviourInput extends BehaviourBase {

	/** Wird ausgeführt, sobald das Behaviour geladen wird
	 *
	 * @param state Benutzereingaben */
	void onBindInput(InputState state);

}
