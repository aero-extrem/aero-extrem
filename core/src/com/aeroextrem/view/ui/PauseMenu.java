package com.aeroextrem.view.ui;

import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static com.aeroextrem.util.AeroExtrem.skin;

public class PauseMenu extends ApplicationAdapter {

	private Stage stage;
	private Window window;

	/** Erstellt das Pause-Menü */
	@Override
	public void create() {
		stage = new Stage(new ScreenViewport());

		// Window
		window = new Window("", skin);
		window.setMovable(false);
		window.setKeepWithinStage(false);

		// Title
		Label title = new Label("Pause", skin);
		window.add(title);

		stage.addActor(new Background());
		stage.addActor(window);

		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	/** Zeichnet das Pause-Menü */
	@Override
	public void render() {
		stage.act();
		stage.draw();
	}

	/** Ändert die Größe des Fensters */
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		window.setBounds((1f/4f) * stage.getWidth(), (1f/4f) * stage.getHeight(), stage.getWidth() / 2, stage.getHeight() / 2);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	public Stage getStage() {
		return stage;
	}

}
