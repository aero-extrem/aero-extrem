package com.aeroextrem.tests;

import com.aeroextrem.database.DBConnection;
import com.badlogic.gdx.files.FileHandle;
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

		DBConnection db = new DBConnection(handle);
		db.open();
		int recording = db.createRecording();
		db.deleteRecording(recording);
		db.close();
	}

}
