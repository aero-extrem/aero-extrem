package com.aeroextrem.scenario.menu;

import com.aeroextrem.engine.Core;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/** Hauptmenü-Szenario nach dem MVC-Pattern.
 *
 * Dies ist der Controller. */
public class Menu extends ApplicationAdapter {

	// Views
	private Stage stage;
	private Skin skin;
	private Window window;
	private TextButton quitBtn;

	/** Erstellt das Hauptmenü */
	@Override
	public void create() {
		// Erstelle Views.
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		skin = new Skin(Gdx.files.internal("skin/neon-ui.json"));

		// Window
		window = new Window("", skin);
		window.setMovable(false);
		window.setFillParent(true);

		// Title
		Label title = new Label("Aero EXTREM Main Menu", skin);

		// TODO: Ersetzen durch Inhalt
		Label nothingHere = new Label("Die Simulation ist noch nicht ganz fertig.", skin);

		// Quit Button
		quitBtn = new TextButton("Quit", skin);
		quitBtn.addListener(new ClickListener() {
			@Override public void clicked(InputEvent event, float x, float y) {
				Core.getInstance().shutdown();
			}
		});

		window.add(title);
		window.row();
		window.add(nothingHere);
		window.row();
		window.add(quitBtn);

		window.pack();
		stage.addActor(window);
	}

	/** Zeigt das Hauptmenü an */
	@Override
	public void render() {
		// Schwarzer Hintergrund
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act();
		stage.draw();

		if(quitBtn.isPressed())
			Core.getInstance().dispose();
	}

	/** Ändert die Größe des Fensters */
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		window.setPosition(window.getX()/2, window.getY()/2);
	}

	/** Schließt das Hauptmenü */
	@Override
	public void dispose() {
		skin.dispose();
		stage.dispose();
	}

}
