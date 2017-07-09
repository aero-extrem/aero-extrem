package com.aeroextrem.engine;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/** Main-Klasse für den Desktop */
public class Launcher {

	/** Startet LWJGL und die Engine
	 *
	 * @param arg Kommandozeilenargumente */
	public static void main (String[] arg) throws Exception {
		Class startScenarioClass;
		try {
			startScenarioClass = Class.forName(arg[0]);
		} catch (ClassNotFoundException e) {
			System.err.println("Engine: Start scenario not found. Check your classpath.");
			System.exit(-420);
			throw e;
		}

		if(!Scenario.class.isAssignableFrom(startScenarioClass)) {
			System.err.println("Engine: Start class is not a scenario.");
			System.exit(-421);
			throw new IllegalStateException();
		}

		Scenario startScenario;
		try {
			startScenario = (Scenario) startScenarioClass.newInstance();
		} catch (InstantiationException|IllegalAccessException e) {
			System.err.println("Engine: Could not create scenario.");
			System.exit(-422);
			throw e;
		}

		// Startet die LWJGL
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.addIcon("texture/logo16.png", Files.FileType.Internal);
		config.addIcon("texture/logo32.png", Files.FileType.Internal);
		config.addIcon("texture/logo256.png", Files.FileType.Internal);
		config.addIcon("texture/logo2048.png", Files.FileType.Internal);
		config.backgroundFPS = 20;
		config.useHDPI = true;
		config.vSyncEnabled = true;

		Core engine = new Core(startScenario);

		new LwjglApplication(engine, config);
	}

}
