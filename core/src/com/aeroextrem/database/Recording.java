package com.aeroextrem.database;

import java.util.Date;

/** Aufnahme */
public class Recording {

	public final int ID;
	public final long time;

	public Recording(int ID, long time) {
		this.ID = ID;
		this.time = time;
	}

	@Override
	public String toString() {
		return String.format("%d:\t%s", ID, new Date(time));
	}
}
