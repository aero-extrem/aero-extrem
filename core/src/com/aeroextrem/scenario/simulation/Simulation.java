package com.aeroextrem.scenario.simulation;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

/** Platzhalter für die Simulation */
public class Simulation extends ApplicationAdapter {

	@Override
	public void create() {
		// Simuliert eine Ladezeit
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			System.err.println("Schlaf unterbrochen :(");
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
}
