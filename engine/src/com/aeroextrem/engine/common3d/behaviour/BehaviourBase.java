package com.aeroextrem.engine.common3d.behaviour;

import com.aeroextrem.engine.resource.GameResource;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/** Basis für jedes Behaviour */
public interface BehaviourBase {

	/** Wird beim Erstellen des Behaviours aufgerufen
	 *
	 * @param resource Asoziierte Resource
	 * @param despos Platz für Ressourcen, die explizit freigegeben werden müssen */
	void onCreate(GameResource resource, Array<Disposable> despos);

}
