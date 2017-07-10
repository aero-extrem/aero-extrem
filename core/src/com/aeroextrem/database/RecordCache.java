package com.aeroextrem.database;

import com.aeroextrem.engine.Core;

import java.sql.SQLException;

/** Implementierung von einem blockierenden Ring-Buffer
 *
 * Es können unendlich viele Zeilen in den RecordCache geschrieben werden,
 * der Cache braucht aber trotzdem nur konstant großen Arbeitsspeicher.<p>
 * Wenn die Anzahl Zeilen einen bestimmten Wert (WRITE_THRESHOLD)
 * übersteigt, werden neue Zeilen eingelesen.<p>
 * Außerdem wird die Datenbank entlastet, da nur gelegentlich große Queries
 * stattfinden anstatt viele kleine.  */
public class RecordCache {

	/** Array unter dem Ring buffer */
	private RecordRow[] cache = new RecordRow[CACHE_SIZE];

	/** Neueste Zeile im Cache */
	private int headPtr = 0;
	
	/** Älteste ungespeicherte Zeile im Cache */
	private int tailPtr = 0;

	/** Datenbank unter dem Ring buffer */
	private DBConnection db;
	private final int RecordingID;

	/** Absoluter (DB) Index */
	private int step = 0;

	/** Nicht-blockierender Mutex / Semaphore
	 *
	 * Verhindert einen Schreibzugriff auf die DB falls schon einer läuft */
	private boolean isWriting = false;

	/** Blockierender Mutex
	 *
	 * Unterbricht Ausführung des jetzigen Threads, falls der Ring buffer voll ist*/
	private final Object mutex = new Object();

	/** Falls die Datenbank nicht schreiben konnte */
	public Exception currentError;

	/** Niedrigere Zahl verursacht aggressiveres Schreiben */
	public static final int WRITE_THRESHOLD = 400;

	/** Höhrere Zahl => Bessere Zuverlässigkeit */
	public static final int CACHE_SIZE = 1000;

	public RecordCache(DBConnection db, int RecordingID) {
		this.db = db;
		this.RecordingID = RecordingID;

		for(int i = 0; i < CACHE_SIZE; ++i) {
			cache[i] = new RecordRow();
		}
	}
	
	public void addRow(
		float FrameDelta,
		float PosX,
		float PosY,
		float PosZ,
		float RotX,
		float RotY, 
		float RotZ, 
		float RotW,
		float DeflectPitch, 
		float DeflectYaw, 
		float DelfectRoll,
		float Thrust
	) {
		RecordRow r = cache[headPtr];
		r.Step = ++step;
		r.FrameDelta = FrameDelta;
		r.PosX = PosX;
		r.PosY = PosY;
		r.PosZ = PosZ;
		r.RotX = RotX;
		r.RotY = RotY;
		r.RotZ = RotZ;
		r.RotW = RotW;
		r.DeflectPitch = DeflectPitch;
		r.DeflectYaw = DeflectYaw;
		r.DelfectRoll = DelfectRoll;
		r.Thrust = Thrust;


		int writeLag;
		if(headPtr >= tailPtr)
			writeLag = headPtr - tailPtr;
		else
			writeLag = (headPtr + CACHE_SIZE) - tailPtr;

		if(writeLag > WRITE_THRESHOLD && !isWriting) {
			commit();
		}

		// Falls der Cache schneller gefüllt wird als geschrieben werden kann
		if((headPtr + 1) % CACHE_SIZE == tailPtr) {
			// Warten bis die Datenbank fertig geschrieben hat
			// TODO Anstatt zu blocken, Frames überspringen?
			synchronized (mutex) {
				System.out.println("Achtung: RecordCache Buffer Overflow, blocke.");
				try {
					mutex.wait();
				} catch (InterruptedException e) { System.err.println("Fehler: RecordCache unterbrochen"); }
			}
		}

		headPtr = (headPtr + 1) % CACHE_SIZE;
	}

	public void commit() {
		commit(false);
	}

	public void commit(boolean now) {
		int writeLag;
		if(headPtr > tailPtr)
			writeLag = headPtr - tailPtr;
		else
			writeLag = (headPtr + CACHE_SIZE) - tailPtr;

		isWriting = true;
		Runnable r = () -> {
			try {
				db.writeRecordsBatch(RecordingID, cache, tailPtr, writeLag-1);

				// Mutex entsperren: Neue Daten geschrieben
				synchronized (mutex) {
					tailPtr = (tailPtr + writeLag-1) % CACHE_SIZE;
					mutex.notify();
				}
				isWriting = false;
			} catch (SQLException e) {
				currentError = e;
				System.err.printf("Error at ptr: %d, lastWrite: %d", headPtr, tailPtr);
				e.printStackTrace();
				assert false;
				// FIXME Useful error code
			}
		};

		if(now)
			r.run();
		else
			Core.getInstance().asyncExec(r);
	}

}
