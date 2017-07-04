package com.aeroextrem.scenario.simulation;

import com.aeroextrem.view.ui.IngameMenu;
import com.aeroextrem.view.ui.OptionsWindow;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/** Erstellt ein Pause-Menü für die Simulation */
class MenuController {

	private static final String MAIN = "Pause";
	private static final String OPTIONS = "Options";

	private IngameMenu menu;

	MenuController(IngameMenu menu) {
		this.menu = menu;
	}

	void createMenu() {
		// Hauptfenster
		PauseWindow main = new PauseWindow();
		main.options.addListener(new WindowSwitchClickListener(OPTIONS));

		// Optionen-Fenster
		OptionsWindow options = new OptionsWindow();
		options.exit.addListener(new WindowSwitchClickListener(MAIN));

		menu.windows.put(MAIN, main);
		menu.windows.put(OPTIONS, options);

		menu.updateWindowList();
		menu.setVisibleWindow(MAIN);
	}

	/** Listener, der bei Click das Fenster wechselt */
	private final class WindowSwitchClickListener extends ClickListener {
		private final String targetWindow;

		public WindowSwitchClickListener(String targetWindow) {
			this.targetWindow = targetWindow;
		}

		@Override
		public void clicked(InputEvent event, float x, float y) {
			menu.setVisibleWindow(targetWindow);
		}
	}

}
