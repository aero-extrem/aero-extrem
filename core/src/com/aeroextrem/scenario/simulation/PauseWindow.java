package com.aeroextrem.scenario.simulation;

import com.aeroextrem.view.ui.IngameWindow;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import static com.aeroextrem.util.AeroExtrem.skin;

/** Ein Pause Menü Fenster */
public class PauseWindow extends IngameWindow {

	public PauseWindow() {
		// Title
		Label title = new Label("Pause", skin);
		add(title);
	}


}
