package com.aeroextrem.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.jetbrains.annotations.NotNull;

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

	public Background(float colorR, float colorG, float colorB, float colorA) {
		this.colorR = colorR;
		this.colorG = colorG;
		this.colorB = colorB;
		this.colorA = colorA;
	}

	@Override
	public void draw(@NotNull Batch batch, float parentAlpha) {
		// Kontextwechsel SpriteBatch
		batch.end();
		// Transparenz
		Gdx.gl20.glEnable(GL_BLEND);
		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		// Form zeichnen
		shaper.begin(Filled);
			shaper.setColor(colorR, colorG, colorB, colorA * parentAlpha);
			shaper.rect(0, 0, getStage().getWidth(), getStage().getHeight());
		shaper.end();

		// Änderungen rückgängig machen
		Gdx.gl20.glDisable(GL_BLEND);
		batch.begin();
	}
}
