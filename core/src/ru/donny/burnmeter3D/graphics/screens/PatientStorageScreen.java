package ru.donny.burnmeter3D.graphics.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ru.donny.burnmeter3D.data.Patient;
import ru.donny.burnmeter3D.data.PatientStorage;
import ru.donny.burnmeter3D.graphics.screens.PatientModificationScreen.Action;
import ru.donny.burnmeter3D.resources.Resources;
import ru.donny.burnmeter3D.resources.TextResources;

public class PatientStorageScreen extends StandartScreen implements Screen {

	private static float patient_name_width = Gdx.graphics.getWidth() * 0.25f;
	private static float medical_history_width = Gdx.graphics.getWidth() * 0.3f;
	private static float date_width = Gdx.graphics.getWidth() * 0.25f;
	private static float modificate_button_width = Gdx.graphics.getWidth() * 0.2f;

	ScreenChangedListener screenChanger;

	private PatientStorage patientStorage;
	private ArrayList<Patient> patients;

	private Table rootTable;
	private Table patientTable;

	public PatientStorageScreen(PatientStorage modelStorage, ScreenChangedListener screenChanger)
			throws NullPointerException {
		if (modelStorage == null)
			throw new NullPointerException();
		else
			this.patientStorage = modelStorage;

		this.screenChanger = screenChanger;
	}

	@Override
	public void show() {
		super.show();

		initizlizeDimens();
		initializePatientTable();
		rootTable = new Table();
		rootTable.setFillParent(true);
		rootTable.add(createHead());
		rootTable.row();

		ScrollPane scrollPane = new ScrollPane(patientTable);
		scrollPane.setOverscroll(false, false);
		rootTable.add(scrollPane);

		mainStage.addActor(rootTable);
	}

	private void initizlizeDimens() {
		patient_name_width = Gdx.graphics.getWidth() * 0.25f;
		medical_history_width = Gdx.graphics.getWidth() * 0.3f;
		date_width = Gdx.graphics.getWidth() * 0.25f;
		modificate_button_width = Gdx.graphics.getWidth() * 0.2f;
	}

	private void initializePatientTable() {
		patientTable = new Table();
		patients = patientStorage.getPatients();
		
		if (patients.size() == 0)
			addNoSaves();
		else
			for (int i = 0; i < patients.size(); i++)
				addPatient(patients.get(i));

		if(patients.size() < 22){
			patientTable.setFillParent(true);
			finishFillingTable();
		}
	}

	private void finishFillingTable() {
		patientTable.add().expand();
	}

	private void addPatient(Patient patient) {
		Label patientName = createLabel(patient.getShortNameRepresentation());
		patientTable.add(patientName).width(patient_name_width);

		Label date = createLabel(patient.getDateRepresentation());
		patientTable.add(date).width(date_width);

		Label medicalHistory = createLabel(patient.getMedicalHistoryNumber());
		patientTable.add(medicalHistory).width(medical_history_width);

		final TextButton modify = createTextButton(Resources.getTextResources().getString(TextResources.StringResource.modify));
		modify.setName(Integer.toString(patient.getId()));
		modify.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				Patient modificating = null;

				for (Patient i : patients)
					if (i.getId() == Integer.parseInt(modify.getName()))
						modificating = i;

				if (modificating != null) {
					PatientModificationScreen newScreen = new PatientModificationScreen(modificating, patientStorage,
							screenChanger, Action.update, PatientStorageScreen.class);
					screenChanger.changeScreenTo(newScreen);
				}
			}
		});

		patientTable.add(modify).width(modificate_button_width);
		patientTable.row();
	}

	private void addNoSaves() {
		Color notificationColor = new Color(1f, 0f, 74.0f / 255.0f, 1f);
		Label notification = createLabel(Resources.getTextResources().getString(TextResources.StringResource.notification), notificationColor);
		patientTable.add(notification);
	}

	private Table createHead() {
		Table head = new Table();
		// Color headColor = new Color(1f, 0f, 74.0f / 255.0f, 1f);

		Label patientName = createLabel(Resources.getTextResources().getString(TextResources.StringResource.name));
		// patientName.getStyle().fontColor = headColor;
		head.add(patientName).width(patient_name_width);

		Label date = createLabel(Resources.getTextResources().getString(TextResources.StringResource.date));
		// patientName.getStyle().fontColor = headColor;
		head.add(date).width(date_width);

		Label medicalHistory = createLabel(Resources.getTextResources().getString(TextResources.StringResource.medicalHistory));
		// patientName.getStyle().fontColor = headColor;
		head.add(medicalHistory).width(medical_history_width);

		TextButton exit = createTextButton(Resources.getTextResources().getString(TextResources.StringResource.exit));
		exit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);

				screenChanger.changeScreen(MainMenuScreen.class);
			}
		});

		head.add(exit).width(modificate_button_width);

		return head;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}
}
