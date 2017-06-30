package com.aeroextrem.engine.util;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

import static com.badlogic.gdx.Input.Keys.UP;
import static com.badlogic.gdx.Input.Keys.DOWN;
import static com.badlogic.gdx.Input.Keys.LEFT;
import static com.badlogic.gdx.Input.Keys.RIGHT;
import static com.badlogic.gdx.math.MathUtils.PI;

/*
 * MIT License
 *
 * Copyright (c) 2017 Richard Patel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */
public class ChaseCameraController extends InputAdapter {

	private final Camera camera;
	private final IntIntMap keys = new IntIntMap();
	private float velocity = .1f;
	private float degreesPerPixel = 0.5f;
	private final Vector3 tmpPos = new Vector3();
	private Vector3 chased;

	// Kreiskoordinaten

	// 0 … ∞
	// Radius
	private float r = 5f;

	// 0 … π
	// Inclination
	private float theta = 0f;

	// 0 … 2π
	// Azimuth
	private float phi = 0f;

	public ChaseCameraController (Camera camera, Vector3 chased) {
		this.camera = camera;
		this.chased = chased;
	}

	@Override
	public boolean keyDown (int keycode) {
		keys.put(keycode, keycode);
		return true;
	}

	@Override
	public boolean keyUp (int keycode) {
		keys.remove(keycode, 0);
		return true;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		return true;
	}


	public void update() {
		if(keys.containsKey(UP)) {
			theta = (theta + velocity) % PI;
		} else if(keys.containsKey(DOWN)) {
			theta = (theta - velocity) % PI;
		}

		if(keys.containsKey(LEFT)) {
			phi = (phi - velocity) % (2*PI);
		} else if(keys.containsKey(RIGHT)) {
			phi = (phi + velocity) % (2*PI);
		}

		// https://en.wikipedia.org/wiki/Spherical_coordinate_system
		float dX = r * MathUtils.sin(theta) * MathUtils.cos(phi);
		float dY = r * MathUtils.sin(theta) * MathUtils.sin(phi);
		float dZ = r * MathUtils.cos(theta);

		camera.position.set(chased);
		camera.position.add(dX, dY, dZ);
		camera.lookAt(chased);
		camera.normalizeUp();

		camera.update(true);
	}

}
