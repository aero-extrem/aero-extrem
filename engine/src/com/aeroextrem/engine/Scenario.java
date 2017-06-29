package com.aeroextrem.engine;

import com.badlogic.gdx.ApplicationListener;

public interface Scenario extends ApplicationListener {

	/** Erstellen des Szenarios mit OpenGL Kontext. Wird vor load() aufgerufen. */
	@Override void create();

	/** Laden von externen Ressourcen. Wird asynchron ausgeführt. */
	void load();

	/** Laden von externen Ressourcen. Wird mit OpenGL-Kontext nach load() ausgeführt. */
	void lateLoad();

	/** Ausgeben eines Frames */
	@Override void render();

	/** Bildschirmgröße ändern */
	@Override void resize(int width, int height);

	/** Anwendung herunterfahren, Ressourcen freigeben. */
	@Override void dispose();
}
