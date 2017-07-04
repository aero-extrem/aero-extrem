package com.aeroextrem.scenario.simulation;

import com.aeroextrem.view.ui.IngameMenu;

/** Erstellt ein Pause-Menü für die Simulation */
class MenuController {

	private static final String MAIN = "Pause";
	private static final String OPTIONS = "Options";

	private IngameMenu menu;

	MenuController(IngameMenu menu) {
		this.menu = menu;
	}

	void createMenu() {
		PauseWindow main = new PauseWindow();

		menu.windows.put(MAIN, main);

		menu.updateWindowList();
		menu.setVisibleWindow(MAIN);
	}

}
