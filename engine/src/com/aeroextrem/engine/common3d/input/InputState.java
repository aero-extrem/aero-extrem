package com.aeroextrem.engine.common3d.input;

import org.jetbrains.annotations.Nullable;

/** MVC Modell: Inputstatus */
public interface InputState {

	InputParam[] getParams();

	// Nützliche statische Methoden

	/** Gibt einen int Param aus.
	 *
	 * @param i Index
	 * @return IntInputParam oder null
	 * @throws ClassCastException falls an der Stelle ein anderer Param ist. */
	@Nullable
	default IntInputParam getIntParam(int i) {
		return (IntInputParam) getParams()[i];
	}

	/** Gibt eine Zahl aus einem int Param aus.
	 *
	 * @param i Index
	 * @return int aus dem IntInputParam
	 * @throws NullPointerException falls kein Param vorhanden ist.
	 * @throws ClassCastException falls an der Stelle ein anderer Param ist. */
	default int getInt(int i) {
		return getIntParam(i)._get();
	}

	/** Gibt einen Object Param aus.
	 *
	 * @param i Index
	 * @return BooleanInputParam oder null
	 * @throws ClassCastException falls an der Stelle ein anderer Param ist. */
	default ObjectInputParam getObjectParam(int i) {
		return (ObjectInputParam) getParams()[i];
	}

	/** Gibt einen boolean Param aus
	 *
	 * @param i Index
	 * @return boolean aus dem BooleanInputParam
	 * @throws ClassCastException falls an der Stelle ein anderer param ist. */
	default BooleanInputParam getBooleanParam(int i) {
		return (BooleanInputParam) getParams()[i];
	}

	/** Gibt einen boolean aus einem boolean Param aus.
	 *
	 * @param i Index
	 * @return boolean aus dem BooleanInputParam
	 * @throws NullPointerException falls kein Param vorhanden ist.
	 * @throws ClassCastException falls an der Stelle ein anderer param ist. */
	default boolean isBool(int i) {
		return getBooleanParam(i).value;
	}

}
