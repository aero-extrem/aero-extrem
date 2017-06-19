package com.aeroextrem.desktop;

import com.aeroextrem.engine.Core;
import com.aeroextrem.scenario.Test;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		// Startet die LWJGL
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		Core engine = new Core();
		engine.setScenario(new Test());

		new LwjglApplication(engine, config);
	}
}
