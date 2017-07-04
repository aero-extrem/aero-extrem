package com.aeroextrem.scenario.simulation;

import com.aeroextrem.view.ui.IngameWindow;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import static com.aeroextrem.util.AeroExtrem.skin;

/** Ein Pause Menü Fenster */
public class PauseWindow extends IngameWindow {

	Button options;

	public PauseWindow() {
		// Title
		Label title = new Label("Pause", skin);
		add(title);

		row();

		// Optionen
		options = new TextButton("Optionen", skin);
		add(options);
	}


}
