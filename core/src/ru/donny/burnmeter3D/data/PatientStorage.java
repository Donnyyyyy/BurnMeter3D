package ru.donny.burnmeter3D.data;

import java.util.ArrayList;

public interface PatientStorage {

	public Patient getPatient(int id);

	public ArrayList<Patient> getPatients();

	public void updatePatient(Patient patient);
	
	public void setPatient(Patient patient);

	public Burns getBurns(int patientId);
	
	public void deletePatient(int patientId);
}
