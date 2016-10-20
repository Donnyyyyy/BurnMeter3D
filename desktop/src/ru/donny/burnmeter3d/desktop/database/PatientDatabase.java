package ru.donny.burnmeter3d.desktop.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.donny.burnmeter3D.data.Burn;
import ru.donny.burnmeter3D.data.Burns;
import ru.donny.burnmeter3D.data.Patient;
import ru.donny.burnmeter3D.data.PatientStorage;

public class PatientDatabase implements PatientStorage {

	private static final String FILE_NAME = "patients.s3db";

	private static final String PATIENT_TABLE_NAME = "'patientTable'";
	private static final String BURNS_TABLE_NAME = "'burnsTable'";

	private static final String PATIENT_ID = "id";
	private static final String TRIANGLE_ID = "triangleId";
	private static final String MEDICAL_HISTORY_NUMBER = "medicalHistory";
	private static final String FIRST_NAME = "firstName";
	private static final String SECOND_NAME = "secondName";
	private static final String THIRD_NAME = "thirdName";
	private static final String DATE = "date";
	private static final String MODEL_NAME = "modelName";
	private static final String I_BURN_PERCENTAGE = "IburnPercentage";
	private static final String II_BURN_PERCENTAGE = "IIburnPercentage";
	private static final String III_BURN_PERCENTAGE = "IIIburnPercentage";
	private static final String BURN_TYPE = "burnType";

	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;

	public PatientDatabase(String databaseName) {
		try {
			connect(databaseName);
			create();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void connect(String databaseName) throws ClassNotFoundException, SQLException {
		connection = null;
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + FILE_NAME);

		System.out.println("Connected to DB");
	}

	private void create() throws ClassNotFoundException, SQLException {

		statement = connection.createStatement();

		try {
			statement.executeQuery("SELECT * FROM " + PATIENT_TABLE_NAME);
		} catch (SQLException e) {

			statement.execute("CREATE TABLE if not exists " + PATIENT_TABLE_NAME //
					+ " ('" //
					+ PATIENT_ID + "' INTEGER PRIMARY KEY AUTOINCREMENT, '" //
					+ FIRST_NAME + "' TEXT, '" //
					+ MEDICAL_HISTORY_NUMBER + "' TEXT, '" //
					+ SECOND_NAME + "' TEXT, '" //
					+ THIRD_NAME + "' TEXT, '" //
					+ MODEL_NAME + "' TEXT,'" //
					+ DATE + "' BIGINT,'" //
					+ I_BURN_PERCENTAGE + "' REAL, '" //
					+ II_BURN_PERCENTAGE + "' REAL, '" //
					+ III_BURN_PERCENTAGE + "' REAL" //
					+ ");" //
			);

			statement.execute("CREATE TABLE if not exists " + BURNS_TABLE_NAME //
					+ " (" //
					+ PATIENT_ID + " INTEGER KEY, " //
					+ TRIANGLE_ID + " INTEGER, " //
					+ BURN_TYPE + " INTEGER " + ");" //
			);

//			fill();
		} finally {
			System.out.println("Tables created (or have been already created)");
		}
	}

	@SuppressWarnings("unused")
	private void fill() throws SQLException {
		Date day = new Date(1624398612);
		GregorianCalendar datt = new GregorianCalendar();
		datt.setTime(day);

		for (int i = 1; i <= 30; i++) {
			GregorianCalendar date = new GregorianCalendar();
			date.setTime(new Date(System.currentTimeMillis() - (int) ((Math.random() - 0.5) * 1000000)));

			setPatient(new Patient(Integer.toString((int) (Math.random() * 7624434)), "AAA", "SomeSecondName" + i, "AnotherName",
					new Burns(), date));
		}

		System.out.println("Table filled");
	}

	private void close() throws ClassNotFoundException, SQLException {
		statement.close();
		connection.close();
		resultSet.close();

		System.out.println("Table closed");
	}

	@Override
	public Burns getBurns(int patientId) {
		try {
			resultSet = statement
					.executeQuery("SELECT * FROM " + BURNS_TABLE_NAME + " WHERE " + PATIENT_ID + " == " + patientId);

			Burns burns = new Burns();

			int tmpTriangleId;
			int tmpBurnType;
			while (resultSet.next()) {
				tmpTriangleId = resultSet.getInt(TRIANGLE_ID);
				tmpBurnType = resultSet.getInt(BURN_TYPE);
				burns.add(tmpTriangleId, tmpBurnType);
			}

			return burns;
		} catch (SQLException e) {
			System.out
					.println("An error (" + e.getMessage() + ") occurred during getting burns of the patient with id = "
							+ patientId + " from database, returned empty Burns.");
			return new Burns();
		}
	}

	@Override
	public ArrayList<Patient> getPatients() {
		try {
			resultSet = statement.executeQuery("SELECT * FROM " + PATIENT_TABLE_NAME + " ORDER BY " + DATE + " DESC");

			ArrayList<Patient> patients = new ArrayList<Patient>();
			while (resultSet.next())
				patients.add(parsePatient(resultSet));

			return patients;
		} catch (SQLException e) {
			System.out.println("An error (" + e.getMessage()
					+ ") occurred during getting patients from database, returned empty list.");
			e.printStackTrace();
			return new ArrayList<Patient>();
		}
	}

	@Override
	public Patient getPatient(int id) {
		try {
			resultSet = statement
					.executeQuery("SELECT * FROM " + PATIENT_TABLE_NAME + " WHERE " + PATIENT_ID + " == " + id);

			if (resultSet.first()) {
				return parsePatient(resultSet);
			} else
				return null;
		} catch (SQLException e) {
			System.out.println("An error (" + e.getMessage() + ") occurred during getting patient with id = " + id
					+ " from database, returned null.");
			return null;
		}
	}

	private Patient parsePatient(ResultSet resultSet) throws SQLException {
		int id = resultSet.getInt(PATIENT_ID);
		String medicalHistoryNumber = resultSet.getString(MEDICAL_HISTORY_NUMBER);
		String firstName = resultSet.getString(FIRST_NAME);
		String secondName = resultSet.getString(SECOND_NAME);
		String thirdName = resultSet.getString(THIRD_NAME);

		long dateMillis = resultSet.getLong(DATE);
		GregorianCalendar date = new GregorianCalendar();
		date.setTimeInMillis(dateMillis);

		Burns burns = new Burns();
		float IburnsPercntage = resultSet.getFloat(I_BURN_PERCENTAGE);
		burns.add(new Burn(Burn.BurnType.I, IburnsPercntage));
		float IIburnsPercntage = resultSet.getFloat(II_BURN_PERCENTAGE);
		burns.add(new Burn(Burn.BurnType.II, IIburnsPercntage));
		float IIIburnsPercntage = resultSet.getFloat(III_BURN_PERCENTAGE);
		burns.add(new Burn(Burn.BurnType.III, IIIburnsPercntage));

		return new Patient(medicalHistoryNumber, firstName, secondName, thirdName, burns, date, id);
	}

	@Override
	public void setPatient(Patient patient) {
		try {
			if (patient == null)
				throw new NullPointerException("Error setting null-pointer patient");

			PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + PATIENT_TABLE_NAME + " (" //
					+ MEDICAL_HISTORY_NUMBER + ", " //
					+ FIRST_NAME + ", " //
					+ SECOND_NAME + ", " //
					+ THIRD_NAME + ", " //
					+ DATE + ", " //
					+ I_BURN_PERCENTAGE + ", " //
					+ II_BURN_PERCENTAGE + ", " //
					+ III_BURN_PERCENTAGE //
					+ ") " //
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

			preparedStatement.setString(1, patient.getMedicalHistoryNumber());
			preparedStatement.setString(2, patient.getFirstName());
			preparedStatement.setString(3, patient.getSecondName());
			preparedStatement.setString(4, patient.getThirdName());
			preparedStatement.setLong(5, patient.getDate().getTimeInMillis());
			preparedStatement.setFloat(6, patient.getBurns().getPercentage(Burn.BurnType.I));
			preparedStatement.setFloat(7, patient.getBurns().getPercentage(Burn.BurnType.II));
			preparedStatement.setFloat(8, patient.getBurns().getPercentage(Burn.BurnType.III));
			preparedStatement.executeUpdate();

			resultSet = preparedStatement.getGeneratedKeys();
			if (resultSet.next())
				setBurns(patient.getBurns(), resultSet.getLong(1));
			else
				throw new SQLException("Can't get patient id.");

			preparedStatement.close();
			System.out.println("Patient inserted successful");
		} catch (SQLException e) {
			System.out.println("An error (" + e.getMessage()
					+ ") occurred during setting the patient to the database. Patient didn't set.");
			e.printStackTrace();
			return;
		}
	}

	@Override
	public void updatePatient(Patient patient) {
		try {
			if (patient == null)
				throw new NullPointerException("Error updating null-pointer patient");

			statement.execute("UPDATE " + PATIENT_TABLE_NAME + " SET " + getPatientUpdateRepresentation(patient)
					+ " WHERE " + PATIENT_ID + " == " + patient.getId());
			setBurns(patient.getBurns(), patient.getId());

			System.out.println("Patient updated successful");
		} catch (SQLException e) {
			System.out.println("An error (" + e.getMessage()
					+ ") occurred during updating the patient in the database. Patient didn't set.");
			e.printStackTrace();
			return;
		}
	}

	private String getPatientUpdateRepresentation(Patient patient) {
		return PATIENT_ID + " = " + patient.getId() + ", "//
				+ MEDICAL_HISTORY_NUMBER + " = '" + patient.getMedicalHistoryNumber() + "', " //
				+ FIRST_NAME + " = '" + patient.getFirstName() + "', " //
				+ SECOND_NAME + " = '" + patient.getSecondName() + "', " //
				+ THIRD_NAME + " = '" + patient.getThirdName() + "', " //
				+ DATE + " = " + patient.getDate().getTimeInMillis() + ", "//
				+ I_BURN_PERCENTAGE + " = " + patient.getBurns().getPercentage(Burn.BurnType.I) + ", "//
				+ II_BURN_PERCENTAGE + " = " + patient.getBurns().getPercentage(Burn.BurnType.II) + ", "//
				+ III_BURN_PERCENTAGE + " = " + patient.getBurns().getPercentage(Burn.BurnType.III); //
	}

	public void setBurns(Burns burns, long id) throws SQLException {
		statement.execute("DELETE FROM " + BURNS_TABLE_NAME + " WHERE " + PATIENT_ID + " == " + id);

		for (Burn i : burns)
			for (int j : i)
				statement.execute("INSERT INTO " + BURNS_TABLE_NAME + " (" //
						+ PATIENT_ID + "," //
						+ TRIANGLE_ID + "," //
						+ BURN_TYPE //
						+ ") " //
						+ " VALUES (" //
						+ id + "," //
						+ j + "," //
						+ i.getType().getId() //
						+ ");" //
				);

	}

	@Override
	public void deletePatient(int patientId) {
		try {
			statement = connection.createStatement();
			statement.execute("DELETE FROM " + PATIENT_TABLE_NAME + " WHERE " + PATIENT_ID + " == " + patientId);
			System.out.println("Patient with id " + patientId + " deleted seccussful");
		} catch (SQLException e) {
			System.out.println("Can't delete patient with id " + patientId + ", error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		try {
			close();
		} catch (Exception e) {
			System.out.println("Patient storage cant be closed!");
		}
	}
}
