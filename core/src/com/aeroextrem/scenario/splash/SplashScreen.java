package com.aeroextrem.scenario.splash;

import com.aeroextrem.database.DBConnection;
import com.aeroextrem.engine.Core;
import com.aeroextrem.engine.ScenarioAdapter;
import com.aeroextrem.scenario.menu.Menu;
import com.aeroextrem.util.AeroExtrem;
import com.aeroextrem.util.OpaqueBackground;
import com.aeroextrem.view.ui.Background;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import org.jetbrains.annotations.Nullable;

/** Willkommens-Bildschirm */
@SuppressWarnings("unused") // Wird per Reflect-API geladen
public class SplashScreen extends ScenarioAdapter {

	private Stage stage;
	private Group scrTitle, scrTeam, scrEngine;

	private Background overlay;

	private boolean active = true;
	private boolean skipPressedLastFrame = false;

	private Texture textureFullLogo, textureLibGDX, textureLWJGL;

	@Override
	public void create() {
		this.stage = new Stage();

		float dim = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		float format = 1f;

		// TITLE
		this.scrTitle = new Group();
		scrTitle.addActor(new Background(0f, 0f, 0f, 1f));

		Image logo = new Image(textureFullLogo = new Texture("texture/logo4096.png"));
		format = textureFullLogo.getWidth() / textureFullLogo.getHeight();
		logo.setSize(dim * 0.8f * format, dim * 0.8f);
		logo.setPosition(stage.getWidth() / 2 - logo.getWidth() / 2,stage.getHeight() / 2 - logo.getHeight() / 2);
		scrTitle.addActor(logo);

		// TEAM
		this.scrTeam = new Group();
		scrTeam.addActor(new Background(1f, 1f, 1f, 1f));

		Image lwjgl = new Image(textureLWJGL = new Texture("texture/logoLWJGL.png"));
		format = textureLWJGL.getWidth() / textureLWJGL.getHeight();
		lwjgl.setSize(dim * 0.5f * format, dim * 0.5f);
		lwjgl.setPosition(stage.getWidth() / 2 - lwjgl.getWidth() / 2,stage.getHeight() / 2 - lwjgl.getHeight() / 2);
		scrTeam.addActor(lwjgl);

		// ENGINE
		this.scrEngine = new Group();
		scrEngine.addActor(new Background(1f, 1f, 1f, 1f));

		Image libGDX = new Image(textureLibGDX = new Texture("texture/logoGDX.jpg"));
		format = textureLibGDX.getWidth() / textureLibGDX.getHeight();
		libGDX.setSize(dim * 0.7f * format, dim * 0.7f);
		libGDX.setPosition(stage.getWidth() / 2 - libGDX.getWidth() / 2,stage.getHeight() / 2 - libGDX.getHeight() / 2);
		scrEngine.addActor(libGDX);

		stage.addActor(scrTitle);
		stage.addActor(scrTeam);
		stage.addActor(scrEngine);

		stage.addActor(overlay = new Background(0f, 0f, 0f, 1f));

		setScreen(SCREEN_TITLE);
	}

	@Override
	public void dispose() {
		stage.dispose();
		textureFullLogo.dispose();
		textureLibGDX.dispose();
		textureLWJGL.dispose();
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
		// FIXME Woanders aufrufen und besser machen
		AeroExtrem.db = new DBConnection(Gdx.files.external("aero.db"));
		try {
			AeroExtrem.db.open();
		} catch (Exception e) {}
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

		boolean skipPressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);

		// Benutzer hat Skip-Knopf erstmals gedrückt: Registrieren
		if(skipPressed && !skipPressedLastFrame) {
			skipPressedLastFrame = true;
		}
		// Benutzer hat Skip-Knopf losgelassen: Nächster Frame
		else if(!skipPressed && skipPressedLastFrame) {
			skipPressedLastFrame = false;
			nextScreen();
			render();
			return;
		}

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
