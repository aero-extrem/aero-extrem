package com.aeroextrem.engine;

/** Scenario mit leeren Methoden für Bequemlichkeit */
public class ScenarioAdapter implements Scenario {

	/** Laden von externen Ressourcen. Wird mit OpenGL-Kontext nach load() ausgeführt. */
	@Override public void lateLoad() {}

	/** Laden von externen Ressourcen. Wird asynchron ausgeführt. */
	@Override public void load() {}

	/** Erstellen des Szenarios mit OpenGL Kontext. Wird vor load() aufgerufen. */
	@Override public void create() {}

	/** Ausgeben eines Frames */
	@Override public void render() {}

	/** Bildschirmgröße ändern */
	@Override public void resize(int width, int height) {}

	/** Anwendung herunterfahren, Ressourcen freigeben. */
	@Override public void dispose() {}

	@Override public void pause() {}
	@Override public void resume() {}
}
