package com.aeroextrem.scenario.simulation;

import com.aeroextrem.util.InputSwitch;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

class SimulationInput extends InputAdapter {

	private Simulation sim;
	private InputSwitch inputSwitch;

	SimulationInput(Simulation sim, InputSwitch inputSwitch) {
		this.sim = sim;
		this.inputSwitch = inputSwitch;
	}

	@Override
	public boolean keyUp (int keycode) {
		switch (keycode) {
			case Keys.ESCAPE:
				sim.showPauseMenu = inputSwitch.enabled = !sim.showPauseMenu;
				return true;
			default:
				return false;
		}
	}

}
