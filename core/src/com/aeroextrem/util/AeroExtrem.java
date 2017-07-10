package com.aeroextrem.util;

import com.aeroextrem.database.DBConnection;
import com.aeroextrem.database.RecordCache;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/** Statische Eigenschaften */
public class AeroExtrem {

	public static Skin skin;

	public static float musicVol = 0.5f;

	public static boolean isRecording = false;

	public static DBConnection db;

	public static RecordCache recorder;

}
