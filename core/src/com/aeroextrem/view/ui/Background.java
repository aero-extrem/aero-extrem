package com.aeroextrem.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled;

/** Hintergrund mit Farbe 0x00000080 (Durchsichtiges Schwarz) */
public class Background extends Actor {

	public float colorR, colorG, colorB, colorA;

	private final ShapeRenderer shaper = new ShapeRenderer();

	public Background() {
		colorR = colorG = colorB = 0f;
		colorA = 0.5f;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		Gdx.gl20.glEnable(GL_BLEND);
		shaper.begin(Filled);
		shaper.setColor(colorR, colorG, colorB, colorA * parentAlpha);
		shaper.rect(0, 0, getStage().getWidth(), getStage().getHeight());
		shaper.end();
		Gdx.gl20.glDisable(GL_BLEND);
		batch.begin();
	}
}
