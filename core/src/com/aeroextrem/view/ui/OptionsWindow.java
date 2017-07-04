package com.aeroextrem.view.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import static com.aeroextrem.util.AeroExtrem.skin;

/** Spieleinstellungen */
public class OptionsWindow extends IngameWindow {

	public final Button exit;

	public OptionsWindow() {
		Label title = new Label("Optionen", skin);
		add(title);

		row();

		Label placeHolder = new Label("Hier werden später Einstellungen angezeigt", skin);
		add(placeHolder);

		row();

		exit = new TextButton("Zurück", skin);
		add(exit);
	}

}
