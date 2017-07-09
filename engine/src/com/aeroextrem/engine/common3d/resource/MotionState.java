package com.aeroextrem.engine.common3d.resource;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import org.jetbrains.annotations.NotNull;

/** Synchronisiert die Position aus der Physiksimulation und Darstellung */
public class MotionState extends btMotionState {

	/** Visuelle Position */
	public Matrix4 transform;

	/** Setzt die Position aus der Physiksimulation auf die der Darstellung */
	@Override
	public void getWorldTransform (@NotNull Matrix4 worldTrans) {
		worldTrans.set(transform);
	}

	/** Setzt die Position aus der Darstellung auf die der Physiksimulation */
	@Override
	public void setWorldTransform (@NotNull Matrix4 worldTrans) {
		transform.set(worldTrans);
	}

}
