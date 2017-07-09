package com.aeroextrem.engine;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
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
	public Core(@NotNull Scenario firstScenario) {
		scenario = firstScenario;
	}

	/** Momentan geladenes Szenario */
	@Nullable
	private Scenario scenario = null;
	/** Lädt jetziges Szenario noch? */
	private boolean isLoading = false;
	/** Ist jetziges Szenario gerade fertig geworden mit Laden? */
	private boolean changesToApply = false;

	/** Lade-Bildschirm */
	private ApplicationListener loadingScreen;

	/** Hintergrund Threads */
	private final ExecutorService threadPool = new ThreadPoolExecutor(
			BACKGROUND_THREADS, BACKGROUND_THREADS,
			10, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>());

	// Singleton-Pattern
	private static Core instance;
	@NotNull
	public static Core getInstance() {
		return instance;
	}

	/** Nach Initialisierung der Engine */
	@Override
	public void create() {
		System.out.println("Aero EXTREM Engine startet.");

		loadingScreen = new LoadingScreen();
		loadingScreen.create();

		instance = this;

		assert scenario != null;
		scenario.create();
		scenario.load();

		System.out.printf("Gestartet mit Szenario %s\n", scenario);
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

		if(changesToApply) {
			changesToApply = false;
			assert scenario != null;
			scenario.lateLoad();
			scenario.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}

		if(scenario != null && !isLoading) {
			scenario.render();
		} else {
			// Lade-Bildschirm
			loadingScreen.render();
		}
	}

	/** Falls die Fenstergröße verändert wird. */
	@Override
	public void resize(int width, int height) {
		if(scenario != null && !isLoading && !changesToApply)
			scenario.resize(width, height);
		loadingScreen.resize(width, height);
	}

	/** Pausiert die Anzeige des Szenarios. */
	@Override
	public void pause() {
		if(scenario != null && !isLoading && !changesToApply)
			scenario.pause();
	}

	/** Fährt die Anzeige des Szenarios fort. */
	@Override
	public void resume() {
		if(scenario != null && !isLoading && !changesToApply)
			scenario.resume();
	}

	/** Fährt die Engine herunter */
	public void shutdown() {
		if(!isLoading && scenario != null) {
			isLoading = true;
			scenario.dispose();
		}

		Gdx.app.exit();
	}

	/** Vor Ausschalten der Engine */
	@Override
	public void dispose() {
		loadingScreen.dispose();
		System.out.println("Aero EXRTEM Engine fährt herunter.");
	}

	/** Setzt das Szenario
	 *
	 * Lädt das Szenario und setzt es in den Vordergrund.<br>
	 * Das alte Szenario wird entfernt und freigegeben.<br>
	 * Während der Ladezeit wird ein Ladebildschirm angezeigt.
	 *
	 * @param listener Neues, uninitialisiertes Szenario */
	public void setScenario(@Nullable Scenario listener) {
		if(scenario != null)
			scenario.dispose();

		if(listener == this || listener == null) {
			scenario = null;
			return;
		}

		scenario = listener;
		isLoading = true;
		// Lädt das Szenario parallel
		// Zeigt einen Ladebildschirm während create() läuft

		scenario.create();
		asyncExec(() -> {
			scenario.load();
			isLoading = false;
			changesToApply = true;

			System.out.printf("Szenario geladen: %s\n", scenario);
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
