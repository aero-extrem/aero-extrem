package com.aeroextrem.engine.common3d.resource;

import com.aeroextrem.engine.common3d.behaviour.BehaviourBase;
import com.badlogic.gdx.utils.ArrayMap;
import org.jetbrains.annotations.NotNull;

/** Instance + Behaviour */
public class BehavingInstance<T> {

	public BehavingInstance(@NotNull T instance) {
		this.instance = instance;
		this.behaviours = new ArrayMap<>(2);
	}

	public final ArrayMap<String, BehaviourBase> behaviours;
	public final T instance;

}
