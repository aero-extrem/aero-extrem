package com.aeroextrem.scenario.splash;

import com.aeroextrem.engine.Core;
import com.aeroextrem.engine.ScenarioAdapter;
import com.aeroextrem.scenario.menu.Menu;
import com.aeroextrem.util.OpaqueBackground;
import com.aeroextrem.view.ui.Background;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.jetbrains.annotations.Nullable;

/** Willkommens-Bildschirm */
@SuppressWarnings("unused") // Wird per Reflect-API geladen
public class SplashScreen extends ScenarioAdapter {

	private Stage stage;
	private Group scrTitle, scrTeam, scrEngine;

	private Background overlay;

	private boolean active = true;

	@Override
	public void create() {
		this.stage = new Stage();

		this.scrTitle = new Group();
		scrTitle.addActor(new OpaqueBackground(1f, 0f, 0f));
		this.scrTeam = new Group();
		scrTeam.addActor(new OpaqueBackground(0f, 1f, 0f));
		this.scrEngine = new Group();
		scrEngine.addActor(new OpaqueBackground(0f, 0f, 1f));

		stage.addActor(scrTitle);
		stage.addActor(scrTeam);
		stage.addActor(scrEngine);

		stage.addActor(overlay = new Background(0f, 0f, 0f, 1f));

		setScreen(SCREEN_TITLE);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	/** Derzeitiges Logo */
	private byte screen = 0;

	/** Derzeitiges Logo */
	public byte getScreenID() {
		return screen;
	}

	@Nullable
	public Group getScreen() {
		switch(screen) {
			case SCREEN_TITLE:	return scrTitle;
			case SCREEN_TEAM:	return scrTeam;
			case SCREEN_ENGINE:	return scrEngine;

			default:				return null;
		}
	}

	public void setScreen(byte screen) {
		mode = MODE_ATTACK;
		frame = 0;
		this.screen = screen;

		scrTitle .setVisible(screen == SCREEN_TITLE);
		scrTeam  .setVisible(screen == SCREEN_TEAM);
		scrEngine.setVisible(screen == SCREEN_ENGINE);
	}

	public void nextScreen() {
		if(++screen <= SCREEN_ENGINE) {
			setScreen(screen);
		} else {
			Core.getInstance().setScenario(new Menu());
			active = false;
		}
	}

	public static final byte SCREEN_TITLE = 0;
	public static final byte SCREEN_TEAM = 1;
	public static final byte SCREEN_ENGINE = 2;
	
	
	/** Anzeige Modus */
	private byte mode = 0;

	/** Anzeige Modus */
	public byte getMode() {
		return mode;
	}

	public void setMode(byte mode) {
		this.mode = mode;
		this.frame = 0L;
	}

	public static final byte MODE_ATTACK = -1;
	public static final byte MODE_SUSTAIN = 0;
	public static final byte MODE_RELEASE = 1;

	/** Fortschritt in diesem Modus */
	public long frame = 0L;

	/** Wie lange FADEIN dauert */
	public static final int TIME_ATTACK = 75;
	/** Wie lange das Logo angezeigt wird */
	public static final int TIME_SUSTAIN = 120;
	/** Wie lange FADEOUT dauert */
	public static final int TIME_RELEASE = 60;

	/** Disk I/O */
	@Override
	public void load() {
		super.load();
	}

	/** Scene2d Baum aufbauen */
	@Override
	public void lateLoad() {
		screen = SCREEN_TITLE;
		mode = MODE_ATTACK;
		frame = 0L;
	}

	@Override
	public void render() {
		if(!active)
			return;

		float alpha = 0f;
		switch (mode) {
			case MODE_ATTACK:
				alpha = Math.min((float) frame / TIME_ATTACK, 1f);

				if(frame > TIME_ATTACK) {
					setMode(MODE_SUSTAIN);
					render();
					return;
				}
				break;
			case MODE_SUSTAIN:
				alpha = 1f;

				if(frame > TIME_SUSTAIN) {
					setMode(MODE_RELEASE);
					render();
					return;
				}
				break;
			case MODE_RELEASE:
				alpha = 1 - Math.min((float) frame / TIME_RELEASE, 1f);

				if(frame > TIME_RELEASE) {
					nextScreen();
					render();
					return;
				}
				break;
		}

		overlay.colorA = 1-alpha;
		stage.draw();

		++frame;
	}

}
