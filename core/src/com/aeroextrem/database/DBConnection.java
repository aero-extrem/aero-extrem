package com.aeroextrem.database;

import com.badlogic.gdx.files.FileHandle;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

/** @author terorie */
public class DBConnection {

	/** Relativer Pfad der Datenbank */
	private final String relPath;

	private Connection conn;

	public DBConnection(@NotNull FileHandle dbFile) {
		relPath = dbFile.path();
	}

	/** Öffnet die Verbindung zur SQLite-Datenbank
	 *
	 * @throws ClassNotFoundException falls der Treiber nicht gefunden wurde.
	 * @throws SQLException falls der Treiber die Datenbank nicht öffnen konnte. */
	public void open() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		// TODO Path injections?
		conn = DriverManager.getConnection("jdbc:sqlite:" + relPath);
		conn.setAutoCommit(false);

		createTables();
	}

	/** Schließt die Verbindung zur SQLite-Datenbank
	 *
	 * @throws SQLException falls der Treiber die Datenbank nicht schließen konnte. */
	public void close() throws SQLException {
		conn.close();
	}

	public void createTables() throws SQLException {
		// WRITE 1: Alle Tabellen
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.execute(STATIC_TABLES);
		} finally {
			if(stmt != null) stmt.close();
		}

		conn.commit();
	}

	/** Erstellt eine neue, leere Aufnahme
	 *
	 * @return ID der Aufnahme */
	public int createRecording() throws SQLException {
		Statement stmt = null;
		ResultSet rset = null;

		// ID des neuen Recordings
		int ID;

		// WRITE 1: Eintrag in recordings
		try {
			// Versuchen, eine Zeile einzufügen
			PreparedStatement ptmt;
			stmt = ptmt = conn.prepareStatement(
					"INSERT OR FAIL INTO recordings (Time) VALUES (?);",
					Statement.RETURN_GENERATED_KEYS
			);
			ptmt.setLong(1, System.currentTimeMillis());
			ptmt.executeUpdate();

			// Letzte Insert ID holen
			rset = ptmt.getGeneratedKeys();
			rset.next();
			ID = rset.getInt(1);
		} finally {
			if(stmt != null) stmt.close();
			if(rset != null) rset.close();
		}

		// WRITE 2: Tabelle erstellen
		try {
			stmt = conn.createStatement();
			stmt.execute(String.format(DYN_TABLE_RECORDING, ID));
		} finally {
			if(stmt != null) stmt.close();
		}

		conn.commit();

		return ID;
	}

	/** Löscht eine Aufnahme
	 *
	 * @param ID ID der aufnahme
	 * @return Erfolgreich? */
	public boolean deleteRecording(int ID) throws SQLException {
		Statement stmt = null;

		// WRITE 1: Eintrag in recordings
		try {
			// Versuchen, eine Zeile zu löschen
			PreparedStatement ptmt;
			stmt = ptmt = conn.prepareStatement("DELETE FROM recordings WHERE RecordingID = ?;");
			ptmt.setInt(1, ID);

			// Anzahl gelöschter Zeilen holen
			int deleted = ptmt.executeUpdate();
			if(deleted == 0) {
				conn.rollback();
				return false;
			} else if(deleted != 1) {
				conn.rollback();
				assert false;
			}
			// (Ausführung wird fortgesetzt, falls erfolgreich gelöscht
		} finally {
			if(stmt != null) stmt.close();
		}

		// WRITE 2: Tabelle löschen
		try {
			stmt = conn.createStatement();
			//language=SQLite
			stmt.execute(String.format("DROP TABLE recording_%d; VACUUM;", ID));
		} finally {
			if(stmt != null) stmt.close();
		}

		conn.commit();
		return true;
	}

	@Language("SQLite")
	private static final String STATIC_TABLES =
		"CREATE TABLE IF NOT EXISTS recordings (\n" +
		"  RecordingID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
		"  Time INTEGER(8) NOT NULL \n" +
		");";

	@Language("SQLite")
	private static final String DYN_TABLE_RECORDING =
		"CREATE TABLE IF NOT EXISTS recording_%d (\n" +
		"  Step INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
		"  FrameDelta REAL NOT NULL,\n" +
		"  PosX INTEGER NOT NULL,\n" +
		"  PosY INTEGER NOT NULL,\n" +
		"  PosZ INTEGER NOT NULL,\n" +
		"  RotX INTEGER NOT NULL,\n" +
		"  RotY INTEGER NOT NULL,\n" +
		"  RotZ INTEGER NOT NULL,\n" +
		"  RotW INTEGER NOT NULL,\n" +
		"  DeflectRoll  REAL NOT NULL,\n" +
		"  DeflectYaw   REAL NOT NULL,\n" +
		"  DeflectPitch REAL NOT NULL\n" +
		");";

}
