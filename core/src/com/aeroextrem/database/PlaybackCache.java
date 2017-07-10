package com.aeroextrem.database;

import com.aeroextrem.engine.Core;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

/** Implementierung von einem blockierenden Ring-Buffer
 *
 * Es können (theoretisch) unendlich viele Zeilen aus dem PlaybackCache werden,
 * der Cache braucht aber trotzdem nur konstant großen Arbeitsspeicher.<p>
 * Wenn die Anzahl Zeilen unter einem bestimmten Wert (READ_THRESHOLD)
 * liegt, werden neue Daten eingelesen.
 * <p>Außerdem wird die Datenbank entlastet, da nur gelegentlich große Queries
 * stattfinden anstatt viele kleine. */
public class PlaybackCache {

	/** Array unter dem Ring buffer */
	private RecordRow[] cache = new RecordRow[CACHE_SIZE];

	/** Neueste Zeile im Cache
	 *
	 * Dieser Index bewegt sich, sobald die Datenbank neue Daten hat */
	private int headPtr = 0;

	/** Älteste ungelesene Zeile im Cache
	 *
	 * Dieser Index bewegt sich jeden erfolgreichen Aufruf von getNextRow() */
	private int tailPtr = 0;

	/** Verhindert einen Mutex-Lock beim ersten Ausführen */
	private boolean firstRun = true;

	/** Datenbank unter dem Ring buffer */
	private DBConnection db;
	private final int RecordingID;

	/** Absoluter (DB) Index */
	private int step = 0;

	private final int totalSteps;

	/** Nicht-blockierender Mutex / Semaphore
	 *
	 * Verhindert einen Leseaufruf auf die DB falls schon einer läuft */
	private boolean isReading = false;

	/** Blockierender Mutex
	 *
	 * Unterbricht Ausführung des jetzigen Threads, falls der Ring buffer voll ist*/
	private final Object mutex = new Object();

	/** Falls die Datenbank nicht schreiben konnte */
	public Exception currentError;

	/** Niedrigere Zahl verursacht aggressiveres Lesen */
	public static final int READ_THRESHOLD = 400;

	/** Höhrere Zahl => Bessere Zuverlässigkeit */
	public static final int CACHE_SIZE = 1000;

	public PlaybackCache(DBConnection db, int RecordingID) {
		this.db = db;
		this.RecordingID = RecordingID;

		int _totalSteps;
		try {
			_totalSteps = db.getRecordingSize(RecordingID);
		} catch (SQLException e) {
			// TODO Besseres Error-Handling
			_totalSteps = -1;
			assert false;
		}
		this.totalSteps = _totalSteps;

		for(int i = 0; i < CACHE_SIZE; ++i) {
			cache[i] = new RecordRow();
		}
	}

	@Nullable
	public RecordRow getNextRow() {
		// Ist das Ende erreicht worden?
		if(step >= totalSteps)
			return null;

		int bufferLeft;
		if(headPtr >= tailPtr)
			bufferLeft = headPtr - tailPtr;
		else
			bufferLeft = (headPtr + CACHE_SIZE) - tailPtr;

		// Weniger Daten als gewünscht, es muss gelesen werden
		if(bufferLeft < READ_THRESHOLD && !isReading)
			fillBuffer();

		// Falls mehr gelesen wird als verfügbar ist
		if(headPtr % CACHE_SIZE == tailPtr) {
			// Warten bis die Datenbank neues gelesen hat
			// TODO Anstatt zu blocken, Frames überspringen?
			synchronized (mutex) {
				System.out.println("Achtung: PlaybackCache Buffer Underflow, blocke.");
				try {
					mutex.wait();
				} catch (InterruptedException e) {
					System.err.println("Fehler: PlaybackCache unterbrochen");
				}
			}
		}

		firstRun = false;

		RecordRow row = cache[tailPtr];
		tailPtr = (tailPtr + 1) % CACHE_SIZE;
		return row;
	}

	private void fillBuffer() {
		int bufferLeft;
		if(headPtr >= tailPtr)
			bufferLeft = headPtr - tailPtr;
		else
			bufferLeft = (headPtr + CACHE_SIZE) - tailPtr;

		isReading = true;
		Core.getInstance().asyncExec(() -> {
			try {
				int newDataCount = CACHE_SIZE - bufferLeft - 2;
				db.readRecordsBatch(RecordingID, cache, headPtr, newDataCount);

				// Mutex entsperren: Neue Daten gelesen
				synchronized (mutex) {
					headPtr = (headPtr + newDataCount) % CACHE_SIZE;
					mutex.notify();
				}
			} catch (SQLException e) {
				currentError = e;
				System.err.printf("Error at ptr: %d, data until: %d", tailPtr, headPtr);
				e.printStackTrace();
				assert false;
				// TODO Useful error code
			}
		});
		isReading = false;
	}

}
