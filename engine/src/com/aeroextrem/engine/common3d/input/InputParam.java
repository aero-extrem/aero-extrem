package com.aeroextrem.engine.common3d.input;

/** Basis für Model im MVC */
public interface InputParam<T> {

	/** Gibt den Wert aus
	 *
	 * @return Wert */
	T get();

	/** Setzt den Wert
	 *
	 * @param val Wert */
	void set(T val);

}
