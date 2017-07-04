package com.aeroextrem.view.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Window;

import static com.aeroextrem.util.AeroExtrem.skin;

/** Ein leeres Fenster fürs Ingame-Menü */
public class IngameWindow extends Window {

	/** Erstellt ein Fenster ohne Titel */
	public IngameWindow() {
		// Window
		super("", skin);
		setMovable(false);
		setKeepWithinStage(false);
	}

}
