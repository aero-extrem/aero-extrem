package com.aeroextrem.engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

/** Core hat bis jetzt keine Funktionen. */
public class Core extends ApplicationAdapter {

	@Override
	public void create() {
		System.out.println("Core startet…");
	}

	@Override
	public void render() {
		// Background color
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void dispose() {
		System.out.println("Core fährt herunter…");
	}

}
