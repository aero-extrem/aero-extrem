package com.aeroextrem.desktop;

import com.aeroextrem.engine.Core;
import com.aeroextrem.scenario.menu.Menu;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/** Main-Klasse für den Desktop */
public class DesktopLauncher {

	/** Startet LWJGL und die Engine
	 *
	 * @param arg Kommandozeilenargumente */
	public static void main (String[] arg) {
		// Startet die LWJGL
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		Core engine = new Core(new Menu());

		new LwjglApplication(engine, config);
	}

}
