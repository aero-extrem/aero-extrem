package com.aeroextrem.scenario.simulation;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

import static com.aeroextrem.util.AeroExtrem.skin;

public class PlaybackPauseWindow extends PauseWindow {

	public PlaybackPauseWindow() {
		Label notice = new Label("PLAYBACK ZUENDE", skin);
		add(notice);
		row();

		create();
	}

}
