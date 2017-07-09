package com.aeroextrem.scenario.menu;

import com.aeroextrem.engine.Core;
import com.aeroextrem.engine.ScenarioAdapter;
import com.aeroextrem.scenario.simulation.Simulation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import static com.aeroextrem.util.AeroExtrem.skin;

/** Hauptmenü-Szenario nach dem MVC-Pattern.
 *
 * Dies ist der Controller. */
public class Menu extends ScenarioAdapter {

	// Views
	private Stage stage;
	private Window window;
	private TextButton quit, simulation, options;

	// Audio
	private Music music;

	/** Erstellt das Hauptmenü */
	@Override
	public void create() {
		// Erstelle Views.
		stage = new Stage(new ScreenViewport());
	}

	@Override
	public void load() {
		Gdx.input.setInputProcessor(stage);
		skin = new Skin();

		music = Gdx.audio.newMusic(Gdx.files.internal("sounds/musicTitle.mp3"));
		music.setVolume(0.5f);
		music.setLooping(true);
	}

	@Override
	public void lateLoad() {
		// FIXME: Disk I/O in render
		FileHandle skinFile = Gdx.files.internal("skin/neon-ui.json");
		FileHandle atlasFile = skinFile.sibling(skinFile.nameWithoutExtension() + ".atlas");
		if (atlasFile.exists()) {
			// FIXME: Atlas nie freigegeben
			TextureAtlas atlas = new TextureAtlas(atlasFile);
			skin.addRegions(atlas);
		}
		skin.load(skinFile);

		// Window
		window = new Window("", skin);
		window.setMovable(false);
		window.setFillParent(true);

		// Title
		Label title = new Label("Aero EXTREM Main Menu", skin);

		// TODO: Ersetzen durch Inhalt
		Label nothingHere = new Label("Die Simulation ist noch nicht ganz fertig.", skin);

		Table actions = new Table(skin);
		simulation = new TextButton("Simulation >", skin);
		options = new TextButton("Optionen >", skin);
		quit = new TextButton("Beenden", skin);

		actions.add(simulation);
		actions.add(options);
		actions.row();
		actions.add(quit);
		actions.pack();

		quit.addListener(new SingleClickListener() {
			@Override public boolean singleClicked(float x, float y) {
				Core.getInstance().shutdown();
				return true;
			}
		});

		simulation.addListener(new SingleClickListener() {
			@Override public boolean singleClicked(float x, float y) {
				Core.getInstance().setScenario(new Simulation());
				return true;
			}
		});

		window.add(title);
		window.row();
		window.add(nothingHere);
		window.row();
		window.add(actions);
		window.row();
		window.add(quit);

		window.pack();
		stage.addActor(window);

		music.play();
	}

	/** Zeigt das Hauptmenü an */
	@Override
	public void render() {
		// Schwarzer Hintergrund
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(window != null)
			handleUI();
	}

	private void handleUI() {
		stage.act();
		stage.draw();

		if(quit.isPressed())
			Core.getInstance().dispose();
	}

	/** Ändert die Größe des Fensters */
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, false);
		if(window != null)
			window.setPosition(window.getX()/2, window.getY()/2);
	}

	/** Schließt das Hauptmenü */
	@Override
	public void dispose() {
		stage.dispose();
		music.stop();
		music.dispose();
	}

	private static class SingleClickListener extends ClickListener {
		public boolean singleClicked(float x, float y) {
			return false;
		}

		boolean singleClick = false;

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			if(!singleClick) {
				singleClick = true;
				return singleClicked(x, y);
			}

			return false;
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			singleClick = false;
		}
	}

}
