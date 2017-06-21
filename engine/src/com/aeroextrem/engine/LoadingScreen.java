package com.aeroextrem.engine;

import com.aeroextrem.engine.util.GifDecoder;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.*;

/** Ein Ladebildschirm zum Anzeigen während dem Laden von Szenarien.
 *
 * Achtung: LoadingScreen benötigt die Animation "assets/loading.gif", die nicht in der Engine vorhanden ist. */
public class LoadingScreen extends ApplicationAdapter {

	private static final String MESSAGE = "Loading ...";
	private static final String GIF = "loading.gif";

	/** Maximale Größe der Ladeanimation */
	private static final int MAX_GIF_SIZE = 200;

	/** 2D Anzeige */
	private SpriteBatch sprites;

	/** Font */
	private BitmapFont font;

	/** TextInfo */
	private GlyphLayout glyphLayout;

	/** Lade-Animation */
	private Animation<TextureRegion> animation;

	/** Fortschritt der Lade-Animation */
	private float elapsed;

	/** Höhe des Textes */
	private float textHeight;

	/** Breite des Textes */
	private float textWidth;

	/** Größe der Ladeanimation */
	private float animSize;

	/** Lädt die Lade-Animation und bereitet den Rest vor. */
	@Override
	public void create() {
		sprites = new SpriteBatch();
		animation = GifDecoder.loadGifAnimation(
				Animation.PlayMode.LOOP,
				Gdx.files.internal(GIF).read()
		);
		font = new BitmapFont();
		glyphLayout = new GlyphLayout();
	}

	/** Zeigt einen Frame des Ladebildschirm an */
	@Override
	public void render() {
		elapsed += Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		TextureRegion keyFrame = animation.getKeyFrame(elapsed);
		sprites.begin();
		sprites.setColor(1f, 1f, 1f, Math.min(elapsed / 2f, 1f));
		sprites.draw(
				keyFrame,
				(Gdx.graphics.getWidth() - animSize) / 2,
				Gdx.graphics.getHeight() / 2 + 10,
				animSize, animSize
		);
		font.draw(
				sprites,
				"LOADING…",
				(Gdx.graphics.getWidth() - textWidth) / 2,
				(Gdx.graphics.getHeight() - textHeight) / 2 - 10
		);
		sprites.end();
	}

	/** Falls die Fenstergröße verändert wird. */
	@Override
	public void resize(int width, int height) {
		sprites.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		glyphLayout.setText(font, MESSAGE);
		textWidth = glyphLayout.width;
		textHeight = glyphLayout.height;

		animSize = Math.min(Gdx.graphics.getWidth() / 3, MAX_GIF_SIZE);
	}

	/** Vor Ausschalten der Engine */
	@Override
	public void dispose() {
		sprites.dispose();
		font.dispose();
	}

}
