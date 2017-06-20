package com.aeroextrem.engine.common3d.input;

/** MVC Modell: Boolean */
public class BooleanInputParam implements InputParam<Boolean> {

	/** Schnellerer Zugriff auf den Wert */
	public boolean value = false;

	@Override
	public void set(Boolean val) {
		value = val;
	}

	@Override
	public Boolean get() {
		return value;
	}

	public boolean toggle() {
		return value = !value;
	}

}
