package com.aeroextrem.view.ui;

import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.jetbrains.annotations.NotNull;

public class IngameMenu extends ApplicationAdapter {

	private Stage stage;
	private Background background;

	/** Fenster im Menü */
	public final ArrayMap<String, Window> windows = new ArrayMap<>();

	/** Erstellt ein Ingame-Menü */
	@Override
	public void create() {
		stage = new Stage(new ScreenViewport());
		background = new Background();

		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	/** Aktualisiert die verfügbaren Fenster */
	public void updateWindowList() {
		// Alle bisherigen löschen
		stage.getActors().clear();
		// Alle neu hinzufügen (Bricht ab bei 0 Fenstern!)
		stage.addActor(background);
		//stage.getActors().addAll(windows.values, 0, windows.size-1);
		for(Window w : windows.values())
			stage.addActor(w);
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	/** Setzt die Sichtbarkeit eines Fensters
	 *
	 * @param targetWindowID ID des Fensters */
	public void setVisibleWindow(@NotNull String targetWindowID) {
		for(ObjectMap.Entry<String, Window> entry : windows.entries()) {
			String windowID = entry.key;
			Window window = entry.value;

			window.setVisible(targetWindowID.equals(windowID));
		}
	}

	/** Zeichnet das Pause-Menü */
	@Override
	public void render() {
		stage.act();
		stage.draw();
	}

	/** Ändert die Größe des Menüs und alle Fenster */
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);

		for(Window window : windows.values()) {
			window.setBounds(
				(1f/4f) * stage.getWidth(),
				(1f/4f) * stage.getHeight(),
				stage.getWidth() / 2,
				stage.getHeight() / 2
			);
		}
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@NotNull
	public Stage getStage() {
		return stage;
	}

}
