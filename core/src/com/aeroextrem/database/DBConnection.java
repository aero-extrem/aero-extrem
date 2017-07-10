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

	public Recording[] listRecordings(int start, int max) throws SQLException {
		// READ 1: Alle Tabellen
		PreparedStatement stmt = null;
		ResultSet rset = null;
		Recording[] recordingList = new Recording[max];

		int i = 0;
		try {
			stmt = conn.prepareStatement("SELECT * FROM main.recordings LIMIT ? OFFSET ?;");
			stmt.setInt(1, max);
			stmt.setInt(2, start);

			rset = stmt.executeQuery();
			while(rset.next()) {
				Recording r = new Recording(rset.getInt(1), rset.getLong(2));

				recordingList[i++] = r;
			}
		} finally {
			if(stmt != null) stmt.close();
			if(rset != null) rset.close();
		}

		Recording[] a = new Recording[i];
		System.arraycopy(recordingList, 0, a, 0, i);
		return a;
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

	/** Schreibt mehrere aufgenommene Zeilen
	 *
	 * @param recordingID ID der Aufnahme
	 * @param cache RecordRow-Cache
	 * @param start Startindex
	 * @param len Anzahl Zeilen */
	public void writeRecordsBatch(int recordingID, RecordRow[] cache, int start, int len) throws SQLException {
		PreparedStatement stmt = null;

		// WRITE 1: Eintrag in recordings_X
		try {
			stmt = conn.prepareStatement("INSERT INTO recording_" + recordingID + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			for(int i = start; i < len; ++i) {
				RecordRow r = cache[(i % cache.length)];
				stmt.setInt(1, r.Step);
				stmt.setFloat(2, r.FrameDelta);
				stmt.setFloat(3, r.PosX);
				stmt.setFloat(4, r.PosY);
				stmt.setFloat(5, r.PosZ);
				stmt.setFloat(6, r.RotX);
				stmt.setFloat(7, r.RotY);
				stmt.setFloat(8, r.RotZ);
				stmt.setFloat(9, r.RotW);
				stmt.setFloat(10, r.DelfectRoll);
				stmt.setFloat(11, r.DeflectYaw);
				stmt.setFloat(12, r.DeflectPitch);
				stmt.setFloat(13, r.Thrust);
				stmt.addBatch();
			}

			stmt.executeBatch();

			conn.commit();
		} finally {
			if(stmt != null) stmt.close();
		}
	}

	/** Liest mehrere aufgenommene Zeilen ein
	 *
	 * @param recordingID ID der Aufnahme
	 * @param cache Cache, in den eingelesen werden soll
	 * @param start Index, ab dem geschrieben werden soll
	 * @param length Anzahl Einträge */
	public void readRecordsBatch(int recordingID, RecordRow[] cache, int start, int length) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;

		// READ 1: Eintrag in recordings_X
		try {
			stmt = conn.prepareStatement("SELECT * FROM recording_" + recordingID + " ORDER BY Step");
			rset = stmt.executeQuery();

			for(int i = start; i < length && rset.next(); ++i) {
				RecordRow r = cache[(i % cache.length)];

				r.Step = rset.getInt(1);
				r.FrameDelta = rset.getFloat(2);
				r.PosX = rset.getFloat(3);
				r.PosY = rset.getFloat(4);
				r.PosZ = rset.getFloat(5);
				r.RotX = rset.getFloat(6);
				r.RotY = rset.getFloat(7);
				r.RotZ = rset.getFloat(8);
				r.RotW = rset.getFloat(9);
				r.DelfectRoll = rset.getFloat(10);
				r.DeflectYaw = rset.getFloat(11);
				r.DeflectPitch = rset.getFloat(12);
				r.Thrust = rset.getFloat(13);
			}
		} finally {
			if(stmt != null) stmt.close();
			if(rset != null) rset.close();
		}
	}

	/** Gibt die Größe eines Recordings zurück.
	 *
	 * @param recordingID ID der Aufnahme
	 * @return -1, falls nicht gefunden, andernfalls die Anzahl der Zeilen */
	public int getRecordingSize(int recordingID) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rset = null;

		// READ 1: Anzahl Zeilen
		try {
			stmt = conn.prepareStatement("SELECT COUNT(1) FROM recording_" + recordingID);
			rset = stmt.executeQuery();

			if(rset.next())
				return rset.getInt(1);
			else
				return -1;
		} finally {
			if(stmt != null) stmt.close();
			if(rset != null) rset.close();
		}
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
		"  PosX REAL NOT NULL,\n" +
		"  PosY REAL NOT NULL,\n" +
		"  PosZ REAL NOT NULL,\n" +
		"  RotX REAL NOT NULL,\n" +
		"  RotY REAL NOT NULL,\n" +
		"  RotZ REAL NOT NULL,\n" +
		"  RotW REAL NOT NULL,\n" +
		"  DeflectRoll  REAL NOT NULL,\n" +
		"  DeflectYaw   REAL NOT NULL,\n" +
		"  DeflectPitch REAL NOT NULL,\n" +
		"  Thrust       REAL NOT NULL\n" +
		");";

}
