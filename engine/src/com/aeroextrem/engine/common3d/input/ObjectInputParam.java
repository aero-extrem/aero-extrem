package com.aeroextrem.engine.common3d.input;

/** MVC Modell: Abstrakte Benutzereingabe */
public class ObjectInputParam<T> implements InputParam<T> {

	/** Benutzereingabe als Objekt */
	public final T object;

	public ObjectInputParam(T object) {
		this.object = object;
	}

	/** Gibt den Wert aus
	 *
	 * @return Wert */
	@Override
	public T get() {
		return object;
	}

	/** Darf nicht aufgerufen werden
	 *
	 * Undefiniertes Verhalten; Objekte können nicht neu zugewiesen werden.
	 *
	 * @param val ??? */
	@Override
	public void set(T val) {
		// Sollte nie aufgerufen werden
		assert false;
	}

}
