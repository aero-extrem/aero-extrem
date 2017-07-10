package com.aeroextrem.tests;

import com.aeroextrem.database.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/** Testet alle Funktionen der Datenbank */
public class Database {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void runTest() throws IOException, ClassNotFoundException, SQLException {
		File dbFile = folder.newFile("aero.db");
		FileHandle handle = new FileHandle(dbFile);

		// Mit der Datenbank verbinden
		DBConnection db = new DBConnection(handle);
		db.open();

		// Neue Aufnahme erstellen
		int recording = db.createRecording();

		// Neue Aufnahme 2 erstellen
		int recording2 = db.createRecording();

		Recording[] recordings = db.listRecordings(0, 3);
		Assert.assertEquals("Anzahl eingetragener Recordings ist falsch", 2, recordings.length);

		// Fake Daten erstellen
		RecordCache recordCache = new RecordCache(db, recording);
		for(int i = 0; i < 9000; ++i) {
			recordCache.addRow(
				MathUtils.random(1),
				MathUtils.random(1),
				MathUtils.random(1),
				MathUtils.random(1),
				MathUtils.random(1),
				MathUtils.random(1),
				MathUtils.random(1),
				MathUtils.random(1),
				MathUtils.random(1),
				MathUtils.random(1),
				MathUtils.random(1),
				MathUtils.random(1)
			);
		}

		// Fake Daten lesen und Integrität prüfen
		PlaybackCache playCache = new PlaybackCache(db, recording);
		RecordRow row;
		int step = 1;
		while((row = playCache.getNextRow()) != null) {
			Assert.assertEquals("Integrität der Aufnahmen konnte nicht sichergestellt werden.", step++, row.Step);
		}

		// Länge der Fake Daten überprüfen
		int rows = db.getRecordingSize(recording);
		Assert.assertEquals("Falsche Anzahl an Zeilen", 9000, rows);

		Assert.assertNull("Error occured", playCache.currentError);
		// TODO Integrität testen

		// Löschen testen
		db.deleteRecording(recording);
		db.deleteRecording(recording2);
		
		db.close();
	}

}
