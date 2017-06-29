package com.aeroextrem.engine;

import com.badlogic.gdx.ApplicationListener;

public interface Scenario extends ApplicationListener {

	/** Laden von externen Ressourcen. Wird asynchron ausgeführt. */
	void load();

	/** Erstellen des Szenarios mit OpenGL Kontext. Wird vor load() aufgerufen. */
	@Override void create();

	/** Ausgeben eines Frames */
	@Override void render();

	/** Bildschirmgröße ändern */
	@Override void resize(int width, int height);

	/** Anwendung herunterfahren, Ressourcen freigeben. */
	@Override void dispose();
}
