package com.aeroextrem.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled;

/** Hintergrund mit Farbe 0x00000080 (Durchsichtiges Schwarz) */
public class OpaqueBackground extends Actor {

	public float colorR, colorG, colorB;

	private final ShapeRenderer shaper = new ShapeRenderer();

	public OpaqueBackground() {
		colorR = colorG = colorB = 0f;
	}

	public OpaqueBackground(float colorR, float colorG, float colorB) {
		this.colorR = colorR;
		this.colorG = colorG;
		this.colorB = colorB;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		// Form zeichnen
		shaper.begin(Filled);
		shaper.setColor(colorR, colorG, colorB, 1f);
		shaper.rect(0, 0, getStage().getWidth(), getStage().getHeight());
		shaper.end();
	}
}
