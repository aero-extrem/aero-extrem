package com.aeroextrem.scenario.menu;

import com.aeroextrem.database.Recording;
import com.aeroextrem.engine.Core;
import com.aeroextrem.engine.ScenarioAdapter;
import com.aeroextrem.scenario.simulation.PlaybackSimulation;
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

import java.sql.SQLException;

import static com.aeroextrem.util.AeroExtrem.db;
import static com.aeroextrem.util.AeroExtrem.skin;

/** Hauptmenü-Szenario nach dem MVC-Pattern.
 *
 * Dies ist der Controller. */
public class Menu extends ScenarioAdapter {

	// Views (Main)
	private Stage stage;
	private Window window;
	private TextButton quit;

	private int page;
	private static final int NUM_PAGE_ITEMS = 10;

	// Views (Recordings)
	private Window recordingsWindow;
	private List<Recording> recordList;
	private TextButton pageLeft;
	private TextButton pageRight;
	private ScrollPane pain;

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

		createMainWindow();
		createRecordingWindow();

		music.play();
	}

	private void createMainWindow() {
		// Window
		window = new Window("", skin);
		window.setMovable(false);
		window.setFillParent(true);

		// Title
		Label title = new Label("Aero EXTREM Main Menu", skin);

		Table actions = new Table(skin);
		TextButton simulation = new TextButton("Simulation >", skin);
		TextButton recordings = new TextButton("Aufnahmen >", skin);
		quit = new TextButton("Beenden", skin);

		actions.add(simulation);
		actions.row();
		actions.add(recordings);
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

		recordings.addListener(new SingleClickListener() {
			@Override public boolean singleClicked(float x, float y) {
				recordingsWindow.setVisible(true);
				window.setVisible(false);
				return true;
			}
		});

		window.add(title);
		window.row();
		window.add(actions);
		window.row();
		window.add(quit);

		window.pack();
		stage.addActor(window);
	}

	private void createRecordingWindow() {
		recordingsWindow = new Window("Recordings", skin);
		recordingsWindow.setVisible(false);
		recordingsWindow.setMovable(false);
		recordingsWindow.setFillParent(true);

		pageLeft = new TextButton("<", skin);
		pageRight = new TextButton(">", skin);
		TextButton recordingsBack = new TextButton("Zurück", skin);

		recordList = new List<>(skin);

		pain = new ScrollPane(recordList);
		pain.setSmoothScrolling(false);
		pain.setSize(stage.getWidth(), stage.getHeight() - 200);

		try {
			recordList.setItems(db.listRecordings(0, NUM_PAGE_ITEMS));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		recordingsBack.addListener(new SingleClickListener() {
			@Override public boolean singleClicked(float x, float y) {
				recordingsWindow.setVisible(false);
				window.setVisible(true);
				return true;
			}
		});

		pageLeft.addListener(new SingleClickListener() {
			@Override public boolean singleClicked(float x, float y) {
				if(page < 1)
					return false;

				try {
					page--;
					int offset = NUM_PAGE_ITEMS * page;
					recordList.setItems(db.listRecordings(offset, NUM_PAGE_ITEMS));
					recordingsWindow.layout();
				} catch(SQLException e) {
					e.printStackTrace();
				}

				return true;
			}
		});

		pageRight.addListener(new SingleClickListener() {
			@Override public boolean singleClicked(float x, float y) {
				try {
					page++;
					int offset = NUM_PAGE_ITEMS * page;
					recordList.setItems(db.listRecordings(offset, NUM_PAGE_ITEMS));
					recordingsWindow.layout();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				return true;
			}
		});

		TextButton delete = new TextButton("Entfernen", skin);
		delete.addListener(new SingleClickListener() {
			@Override public boolean singleClicked(float x, float y) {
				Recording r = recordList.getSelected();
				if(r != null) {
					try {
						db.deleteRecording(r.ID);

						int offset = NUM_PAGE_ITEMS * page;
						recordList.setItems(db.listRecordings(offset, NUM_PAGE_ITEMS));
						pageRight.setLayoutEnabled(recordList.getItems().size != 0);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				return true;
			}
		});

		TextButton start = new TextButton("Abspielen", skin);
		start.addListener(new SingleClickListener() {
			@Override
			public boolean singleClicked(float x, float y) {
				Recording r = recordList.getSelected();
				if(r != null)
					Core.getInstance().setScenario(new PlaybackSimulation(r.ID));
				return true;
			}
		});

		recordingsWindow.add(pain);
		recordingsWindow.row();
		Table t = new Table();
		t.add(pageLeft);
		t.add(pageRight);
		t.row();
		t.add(delete);
		t.add(start);
		recordingsWindow.add(t);
		recordingsWindow.row();
		recordingsWindow.add(recordingsBack);
		recordingsWindow.layout();

		stage.addActor(recordingsWindow);
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

		if(recordingsWindow != null) {
			window.setPosition(recordingsWindow.getX()/2, recordingsWindow.getY()/2);
			pain.setSize(stage.getWidth(), stage.getHeight() - 200);
		}
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
