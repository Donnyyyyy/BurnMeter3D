package ru.donny.burnmeter3D.controllers;

import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Logger {

	private static PrintStream mainInstance = System.out;

	public static void printTimeMessage(String message) {

		mainInstance.println(getTime() + " " + message);
	}

	private static String getTime() {
		GregorianCalendar date = new GregorianCalendar();

		String time = "";

		if (date.get(Calendar.HOUR_OF_DAY) < 10)
			time += "0" + date.get(Calendar.HOUR_OF_DAY);
		else
			time += date.get(Calendar.HOUR_OF_DAY);
		time += ":";

		if (date.get(Calendar.MINUTE) < 10)
			time += "0" + date.get(Calendar.MINUTE);
		else
			time += date.get(Calendar.MINUTE);
		time += ":";

		if (date.get(Calendar.SECOND) < 10)
			time += "0" + date.get(Calendar.SECOND);
		else
			time += date.get(Calendar.SECOND);
		time += ":";

		if (date.get(Calendar.MILLISECOND) < 100) {
			String millis = new String("000" + date.get(Calendar.MILLISECOND));
			time += millis.substring(millis.length() - 3);
		} else
			time += date.get(Calendar.MILLISECOND);

		return time;
	}
}
