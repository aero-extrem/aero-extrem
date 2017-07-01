package com.aeroextrem.scenario.simulation;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class SimulationInput extends InputAdapter {

	private Simulation sim;

	public SimulationInput(Simulation sim) {
		this.sim = sim;
	}

	@Override
	public boolean keyUp (int keycode) {
		switch (keycode) {
			case Keys.ESCAPE:
				sim.showPauseMenu = sim.pauseMenuInput.enabled = !sim.showPauseMenu;
				return true;
			default:
				return false;
		}
	}

}
