package com.aeroextrem.engine.common3d.behaviour;

import com.aeroextrem.engine.resource.GameResource;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import org.jetbrains.annotations.NotNull;

/** Basis für jedes Behaviour */
public interface BehaviourBase extends Disposable {

	/** Wird beim Erstellen des Behaviours aufgerufen
	 *
	 * @param resource Asoziierte Resource */
	void onCreate(@NotNull GameResource resource);

	@Override default void dispose() {}
}
