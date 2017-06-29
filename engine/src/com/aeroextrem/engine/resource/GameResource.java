package com.aeroextrem.engine.resource;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Disposable;
import org.jetbrains.annotations.NotNull;

/** Ein 3D-Modell, das geladen werden kann
 *
 * Der Ladevorgang soll nicht im Konstruktur geschehen, sondern in der load()-Methode */
public interface GameResource extends Disposable {

	/** Lädt die benötigten Objekte in den Speicher. */
	void load();

	/** Wird nach load() aufgerufen, mit OpenGL Kontext */
	default void commit() {}

	/** Gibt ein libgdx 3D-Modell mit den Resourcen aus.
	 *
	 * @return Modell */
	@NotNull
	Model getModel();

	/** Gibt den Speicher der Resource wieder frei. */
	@Override void dispose();

}
