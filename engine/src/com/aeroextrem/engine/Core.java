package com.aeroextrem.engine;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** Core ist die Wurzel der Engine. */
public class Core implements ApplicationListener {

	/** Anzahl der Hintergrund Threads für Berechnungen, die in render() ungeeignet sind. */
	private static final int BACKGROUND_THREADS = 1;

	/** Erstellt die Engine
	 *
	 * @param firstScenario Erstes Szenario der Engine */
	public Core(@NotNull ApplicationListener firstScenario) {
		scenario = firstScenario;
	}

	/** Momentan geladenes Szenario */
	@Nullable
	private ApplicationListener scenario = null;
	/** Lädt jetziges Szenario noch? */
	private boolean isLoading = false;

	/** Hintergrund Threads */
	private final ExecutorService threadPool = new ThreadPoolExecutor(
			BACKGROUND_THREADS, BACKGROUND_THREADS,
			10, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>());

	// Singleton-Pattern
	private static Core instance;
	public static Core getInstance() {
		return instance;
	}

	/** Nach Initialisierung der Engine */
	@Override
	public void create() {
		System.out.println("Aero EXTREM Engine startet.");

		instance = this;

		assert scenario != null;
		scenario.create();
	}

	/** Gibt ein Bild aus. Wird bestenfalls 60 mal in der Sekunde aufgerufen. */
	@Override
	public void render() {
		/* Damit die render() Funktion schnell genug ausgeführt wird,
		 * sind nur Read/Writes und spezialisierte Methoden erlaubt:
		 *  - Lesen/Schreiben von Referenzen und Primitiven
		 *  - Aufruf von Funktionen mit vorhersehbarer Laufzeit
		 *  - GL-Funktionen
		 *
		 * Achtung vor:
		 *  - Generell Speicherzuweisungen (create, new …) / Speicherbefreiung (dispose)
		 *  - Übermäßiges Erstellen von Objekten (new …)
		 *  - Diese könnten den langsamen Garbage Collector triggern, der Lags verursacht.
		 *  - Stattdessen sollen bereits in create() benötigte Arrays erstellt werden.
		 *
		 * Zu vermeiden:
		 *  - Buffered I/O: System.out, Files.
		 *  - Jegliche Aufrufe, die Buffered I/O ausführen könnten.
		 *  - Darunter fällt auch ResourceManager.
		 */

		if(scenario != null && !isLoading) {
			scenario.render();
		} else {
			// Grauer Bildschirm
			Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		}
	}

	/** Falls die Fenstergröße verändert wird. */
	@Override
	public void resize(int width, int height) {
		if(scenario != null)
			scenario.resize(width, height);
	}

	/** Pausiert die Anzeige des Szenarios. */
	@Override
	public void pause() {
		if(scenario != null)
			scenario.pause();
	}

	/** Fährt die Anzeige des Szenarios fort. */
	@Override
	public void resume() {
		if(scenario != null)
			scenario.resume();
	}

	/** Vor Ausschalten der Engine */
	@Override
	public void dispose() {
		System.out.println("Aero EXRTEM Engine fährt herunter.");
	}

	/** Setzt das Szenario
	 *
	 * @param listener Neues Szenario */
	public void setScenario(@Nullable ApplicationListener listener) {
		if(scenario != null)
			scenario.dispose();

		if(listener == this || listener == null) {
			scenario = null;
			return;
		}

		scenario = listener;
		isLoading = true;
		// Lädt das Szenario parallel
		asyncExec(() -> {
			scenario.create();
			isLoading = false;
		});
	}

	/** Gibt das jetzige Szenario aus.
	 *
	 * @return Momentan geladenes Szenario oder null, falls keins geladen ist. */
	@Nullable
	public ApplicationListener getCurrentScenario() {
		if(!isLoading)
			return scenario;
		else
			return null;
	}

	/** Führt eine Aufgabe parallel aus
	 *
	 * @param task Aufgabe */
	public void asyncExec(@NotNull Runnable task) {
		/* Diese Art, nebenläufige Aufgaben auszuführen,
		 * ist sehr viel effizienter als das Erstellen
		 * neuer Threads.
		 * (vgl. goroutines in der Programmiersprache Go) */
		threadPool.execute(task);
	}

}