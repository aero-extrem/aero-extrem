package com.aeroextrem.engine.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

import static com.badlogic.gdx.Input.Keys.UP;
import static com.badlogic.gdx.Input.Keys.DOWN;
import static com.badlogic.gdx.Input.Keys.LEFT;
import static com.badlogic.gdx.Input.Keys.RIGHT;
import static com.badlogic.gdx.math.MathUtils.FLOAT_ROUNDING_ERROR;
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
// This file is licensed under MIT, not GPLv3.
/** Third-person 3D camera controller.
 *
 * Use arrows or drag mouse to look around.
 *
 * @author Richard Patel */
public class ChaseCameraController extends InputAdapter {

	// The camera
	private	final	Camera		camera;

	// The keys currently pressed
	private	final	IntIntMap	keys		= new IntIntMap();

	/** Camera position on  */
	public	final	Vector3		unit 		= new Vector3();

	/** Camera position relative to chased object */
	public	final	Vector3		relPos 		= new Vector3();

	/** Reference to chased object */
	public	final	Matrix4		chased;

	//  Translation of chased object
	private final	Vector3		chasedPos	= new Vector3();

	private final Vector3 lastChasedPos = new Vector3();

	//  Rotation of chased object
	private final	Quaternion	chasedRot	= new Quaternion();

	public			float		defaultZoom	= 300f;

	/** Distance (Radius) between camera and chased object */
	public			float		radius		= defaultZoom;


	/** Zoom speed (Exponent) */
	public			float		zoomExp		= 1.005f;

	/** Nearest allowed */
	public 			float		minZoom		= 10f;

	/** Furthest allowed */
	public 			float		maxZoom		= 3000f;

	/** Inclination of camera (like latitude on globe)
	 *
	 * Takes values from 0 to π */
	public			float		theta		= 0f;

	/** Azimuth of camera
	 *
	 * Takes values from 0 to 2π */
	public			float		phi			= 0f;

	/** How many radians the camera moves each frame with key press */
	public 			float		velocity		= .05f;

	/** How many radians the camera moves each pixel dragged */
	public			float		radPerPixel	= 0.005f;

	/** Should the camera rotate with the followed object? */
	public 			boolean		fixAngle		= false;

	/** Smooth camera movement */
	public boolean flow = false;
	public float flowSpeed = 0.001f;
	private float deltaTheta = 0f;
	private float deltaPhi = 0f;

	/** Start chasing an object
	 *
	 * @param camera Camera to modify
	 * @param chased Transform of object to chase */
	public ChaseCameraController (Camera camera, Matrix4 chased) {
		this.camera = camera;
		this.chased = chased;
	}

	@Override
	public boolean keyDown (int keycode) {
		keys.put(keycode, keycode);
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		switch (character) {
			case 'm':
			case 'M':
				fixAngle = !fixAngle;
				break;
			case 'o':
			case 'O':
				deltaTheta = 0f;
				deltaPhi = 0f;
				flow = !flow;
				break;
		}
		return false;
	}

	@Override
	public boolean keyUp (int keycode) {
		keys.remove(keycode, 0);
		return false;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		float deltaX = Gdx.input.getDeltaX() * radPerPixel;
		float deltaY = Gdx.input.getDeltaY() * radPerPixel;

		if(deltaY > 0)
			theta = Math.min(theta + deltaY, PI);
		else if(deltaY < 0)
			theta = Math.max(theta + deltaY, 0);

		phi = (phi + deltaX) % (2*PI);
		if(phi < 0)
			phi = 2*PI - Math.abs(phi);

		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		float lastRadius = radius;
		radius = (float) Math.pow((radius + amount * 0.2f), zoomExp);
		if(amount < 0)
			radius = lastRadius - (radius - lastRadius);

		if(radius < minZoom)
			radius = minZoom;
		else if(radius > maxZoom)
			radius = maxZoom;

		return false;
	}

	private void handleKeys() {
		if (flow) {
			handleFlowKeys();
		} else {
			handleInstantKeys();
		}
	}

	private void handleInstantKeys() {
		if(keys.containsKey(UP))
			theta += velocity;
		if(keys.containsKey(DOWN))
			theta -= velocity;

		if(theta < 0)
			theta = 0;
		else if(theta > PI)
			theta = PI - FLOAT_ROUNDING_ERROR;

		if(keys.containsKey(LEFT))
			phi = (phi + velocity) % (2*PI);
		if(keys.containsKey(RIGHT))
			phi = (phi - velocity) % (2*PI);

		if(phi < 0)
			phi = 2*PI - Math.abs(phi);
	}

	private void handleFlowKeys() {
		if(keys.containsKey(UP))
			deltaTheta += flowSpeed;
		else if(keys.containsKey(DOWN))
			deltaTheta -= flowSpeed;
		else
			deltaTheta = MathUtils.lerp(deltaTheta, 0, flowSpeed * 2);

		if(keys.containsKey(LEFT))
			deltaPhi += flowSpeed;
		if(keys.containsKey(RIGHT))
			deltaPhi -= flowSpeed;
		else
			deltaPhi = MathUtils.lerp(deltaPhi, 0, flowSpeed * 2);

		theta = (theta + deltaTheta) % (2*PI);
		phi = (phi + deltaPhi) % (2*PI);

		if(phi < 0)
			phi = 2*PI - Math.abs(phi);

		if(theta < 0)
			theta = 0;
		else if(theta > PI)
			theta = PI - FLOAT_ROUNDING_ERROR;
	}

	/** React to user input and update the camera position */
	public void update() {
		handleKeys();

		chased.getTranslation(chasedPos);
		chased.getRotation   (chasedRot);

		chasedRot.nor();

		// https://en.wikipedia.org/wiki/Spherical_coordinate_system
		float dX = MathUtils.sin(theta) * MathUtils.cos(phi);
		float dY = MathUtils.cos(theta);
		float dZ = MathUtils.sin(theta) * MathUtils.sin(phi);

		unit.set(dX, dY, dZ);
		if(fixAngle)
			unit.mul(chasedRot);
		relPos.set(unit).scl(radius);

		camera.position .set(chasedPos);
		camera.position .add(relPos);
		camera.direction.set(unit).scl(-1);

		// Normal (never rolling)
		float upX = MathUtils.sin(theta - (PI/2)) * MathUtils.cos(phi);
		float upY = MathUtils.cos(theta - (PI/2));
		float upZ = MathUtils.sin(theta - (PI/2)) * MathUtils.sin(phi);

		camera.up.set(upX, upY, upZ);
		if(fixAngle)
			camera.up.mul(chasedRot);

		camera.update(false);

		lastChasedPos.set(chasedPos);
	}

}
