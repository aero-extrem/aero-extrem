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

	//  Rotation of chased object
	private final	Quaternion	chasedRot	= new Quaternion();

	/*  Distance (Radius) between camera and chased object */
	public			float		radius		= 5f;

	/** Inclination of camera (like latitude on globe)
	 *
	 * Takes values from 0 to π */
	public			float		theta		= 0f;

	/** Azimuth of camera
	 *
	 * Takes values from 0 to 2π */
	public			float		phi			= 0f;

	/** How many radians the camera moves each frame with key press */
	public 			float		velocity	= .05f;

	/** How many radians the camera moves each pixel dragged */
	public			float		radPerPixel	= 0.005f;


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

	private void handleKeys() {
		if(keys.containsKey(UP))
			theta += velocity;
		if(keys.containsKey(DOWN))
			theta -= velocity;

		if(theta < 0)
			theta = 0;
		else if(theta > PI)
			theta = PI;

		if(keys.containsKey(LEFT))
			phi = (phi + velocity) % (2*PI);
		if(keys.containsKey(RIGHT))
			phi = (phi - velocity) % (2*PI);

		if(phi < 0)
			phi = 2*PI - Math.abs(phi);
	}

	/** React to user input and update the camera position */
	public void update() {
		handleKeys();

		chased.getTranslation(chasedPos);
		chased.getRotation   (chasedRot);

		// https://en.wikipedia.org/wiki/Spherical_coordinate_system
		float dX = MathUtils.sin(theta) * MathUtils.cos(phi);
		float dY = MathUtils.cos(theta);
		float dZ = MathUtils.sin(theta) * MathUtils.sin(phi);

		unit  .set(dX, dY, dZ).mul(chasedRot);
		relPos.set(unit).scl(radius);

		camera.position .set(chasedPos);
		camera.position .add(relPos);
		camera.direction.set(unit).scl(-1);

		// Normal (never rolling)
		// TODO: Fix weird behaviour @ theta=PI/2
		float upX = MathUtils.sin(theta - PI) * MathUtils.cos(phi);
		float upY = MathUtils.cos(theta - PI);
		float upZ = MathUtils.sin(theta - PI) * MathUtils.sin(phi);

		camera.up.set(upX, upY, upZ).mul(chasedRot);

		camera.update(true);
	}

}
