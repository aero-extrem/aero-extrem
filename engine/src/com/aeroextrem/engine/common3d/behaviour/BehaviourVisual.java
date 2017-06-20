package com.aeroextrem.engine.common3d.behaviour;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

/** Visuelles Verhalten */
public interface BehaviourVisual extends BehaviourBase {

	/** Wird ausgeführt, sobald das Behaviour in die Anzeigeengine geladen wird.
	 *
	 * @param instance Assoziiertes Model */
	void onCreateVisuals(ModelInstance instance);

	/** Wird jeden Frame aufgerufen.
	 *
	 * Hier soll das visuelle Verhalten gezeichnet werden.
	 *
	 * @param batch 3D-Batch zum Zeichnen */
	void render(ModelBatch batch);

}
