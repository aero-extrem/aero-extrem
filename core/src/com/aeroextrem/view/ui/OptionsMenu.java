package com.aeroextrem.view.ui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static com.aeroextrem.util.AeroExtrem.skin;

/** Spieleinstellungen */
public class OptionsMenu extends ApplicationAdapter {

	private Stage stage;
	private Window window;

	/** Erstellt das Optionen-Menü */
	@Override
	public void create() {
		stage = new Stage(new ScreenViewport());

		// Window
		window = new Window("", skin);
		window.setMovable(false);
		window.setKeepWithinStage(false);

		// Title
		Label title = new Label("Optionen", skin);
		window.add(title);

		Label placeHolder = new Label("Hier werden später Einstellungen angezeigt", skin);
		window.add(placeHolder);

		stage.addActor(new Background());
		stage.addActor(window);

		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void render() {
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		window.setBounds((1f/4f) * stage.getWidth(), (1f/4f) * stage.getHeight(), stage.getWidth() / 2, stage.getHeight() / 2);
	}

	@Override
	public void dispose() {
		skin.dispose();
		stage.dispose();
	}

	public Stage getStage() {
		return stage;
	}
}
