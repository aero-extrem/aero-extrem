package com.aeroextrem.scenario.simulation;

import com.aeroextrem.view.ui.IngameWindow;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import static com.aeroextrem.util.AeroExtrem.skin;

/** Ein Pause Menü Fenster */
public class PauseWindow extends IngameWindow {

	Button options;
	Button quit;
	Button record;
	Label recordStatus;

	final String recordOn = "AN";
	final String recordOff = "AUS";

	public PauseWindow() {
		// Title
		Label title = new Label("Pause", skin);
		add(title);

		row();

		// Optionen
		options = new TextButton("Optionen", skin);
		add(options);

		row();

		// Zurück zum Menü
		quit = new TextButton("Beenden", skin);
		add(quit);

		row();

		// Aufnahme starten/stoppen
		record = new TextButton("Aufnahme: ", skin);
		add(record);
		recordStatus = new Label(recordOff, skin);
		add(recordStatus);
	}


}
