package com.aeroextrem.database;

import java.util.Date;

/**
 * Created by richie on 10.07.17.
 */
public class Recording {

	int ID;
	long time;

	@Override
	public String toString() {
		return String.format("%d:\t%s", ID, new Date(time));
	}
}
