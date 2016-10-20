package ru.donny.burnmeter3d.android.database;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import ru.donny.burnmeter3D.data.Burns;
import ru.donny.burnmeter3D.data.Patient;
import ru.donny.burnmeter3D.data.PatientStorage;

public class PatientDatabase extends SQLiteOpenHelper implements PatientStorage {

	public static final int VERSION = 1;

	private static final String PATIENT_TABLE_NAME = "patientTableName";
	private static final String BURNS_TABLE_NAME = "patientTableName";

	private static final String PATIENT_ID = "id";
	private static final String TRIANGLE_ID = "triangleId";
	private static final String FIRST_NAME = "firstName";
	private static final String SECOND_NAME = "secondName";
	private static final String THIRD_NAME = "thirdName";
	private static final String MODEL_NAME = "modelName";
	private static final String I_BURN_PERCENTAGE = "IburnPercentage";
	private static final String II_BURN_PERCENTAGE = "IIburnPercentage";
	private static final String III_BURN_PERCENTAGE = "IIIburnPercentage";

	public PatientDatabase(Context context, String name) {
		super(context, name, null, VERSION);
	}

	public PatientDatabase(Context context, String name, CursorFactory factory, int version,
			DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE if not exists " + PATIENT_TABLE_NAME //
				+ " (" //
				+ PATIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " //
				+ FIRST_NAME + " TEXT, " //
				+ SECOND_NAME + " TEXT, " //
				+ THIRD_NAME + " TEXT, " //
				+ MODEL_NAME + " TEXT," //
				+ I_BURN_PERCENTAGE + " REAL, " //
				+ II_BURN_PERCENTAGE + " REAL, " //
				+ III_BURN_PERCENTAGE + " REAL" //
				+ ");" //
		);

		db.execSQL("CREATE TABLE if not exists " + BURNS_TABLE_NAME //
				+ " (" //
				+ PATIENT_ID + " INTEGER KEY, " //
				+ TRIANGLE_ID + " INTEGER " //
				+ ");" //
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	@Override
	public Patient getPatient(int id) {
		return new Patient("404", "Noname", "Noname", "Noname", new Burns(), new GregorianCalendar(), id);
	}

	@Override
	public ArrayList<Patient> getPatients() {
		return new ArrayList<Patient>();
	}

	@Override
	public void setPatient(Patient patient) {

	}

	@Override
	public void updatePatient(Patient patient) {
	}

	@Override
	public Burns getBurns(int patientId) {
		return new Burns();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

	@Override
	public void deletePatient(int patientId) {
		// TODO Auto-generated method stub

	}
}
