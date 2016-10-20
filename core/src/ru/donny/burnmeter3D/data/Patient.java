package ru.donny.burnmeter3D.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class Patient {
	public static final int NO_ID = -1;

	private int id;
	private String medicalHistoryNumber;
	private String firstName;
	private String secondName;
	private String thirdName;
	private Burns burns;
	private GregorianCalendar date;

	private SimpleDateFormat dateFormat;

	public Patient(String medicalHistoryNumber, String firstName, String secondName, String thirdName, Burns burns,
			GregorianCalendar date, int id) {
		super();
		this.medicalHistoryNumber = medicalHistoryNumber;
		this.firstName = firstName;
		this.secondName = secondName;
		this.thirdName = thirdName;
		this.burns = burns;
		this.date = date;
		this.id = id;
	}

	public Patient(String medicalHistoryNumber, String firstName, String secondName, String thirdName, Burns burns,
			GregorianCalendar date) {
		this(medicalHistoryNumber, firstName, secondName, thirdName, burns, date, NO_ID);
	}

	public String getShortNameRepresentation() {
		String shortName = secondName;

		if ((firstName != null) && (firstName.length() > 0))
			shortName += " " + firstName.charAt(0) + ".";

		if ((thirdName != null) && (thirdName.length() > 0))
			shortName += " " + thirdName.charAt(0) + ". ";

		return shortName;
	}

	public String getFullNameRepresentation() {
		return secondName + " " + firstName + " " + thirdName;
	}

	public String getMedicalHistoryNumber() {
		return medicalHistoryNumber;
	}

	public void setMedicalHistoryNumber(String medicalHistoryNumber) {
		this.medicalHistoryNumber = medicalHistoryNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getThirdName() {
		return thirdName;
	}

	public void setThirdName(String thirdName) {
		this.thirdName = thirdName;
	}

	public Burns getBurns() {
		return burns;
	}

	public void setBurns(Burns burns) {
		this.burns = burns;
	}

	public GregorianCalendar getDate() {
		return date;
	}

	public String getDateRepresentation() {
		if (dateFormat == null)
			dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy");

		return dateFormat.format(date.getTime());
	}

	public void setDate(GregorianCalendar date) {
		this.date = date;
	}

	public void parseFullName(String fullName) throws ParseException {
		if (fullName.length() == 0) {
			throw new ParseException(fullName, 0);
		} else {
			Scanner in = new Scanner(fullName);
			secondName = in.next();
			if (in.hasNext())
				firstName = in.next();
			if (in.hasNext())
				thirdName = in.next();
			in.close();
		}

	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDeteFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
