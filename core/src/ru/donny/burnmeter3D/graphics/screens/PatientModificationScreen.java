package ru.donny.burnmeter3D.graphics.screens;

import java.text.ParseException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ru.donny.burnmeter3D.data.Burn;
import ru.donny.burnmeter3D.data.Patient;
import ru.donny.burnmeter3D.data.PatientStorage;
import ru.donny.burnmeter3D.resources.Resources;
import ru.donny.burnmeter3D.resources.TextResources;

public class PatientModificationScreen extends StandartScreen {

	private ScreenChangedListener screenChanger;

	private final Patient patient;
	private PatientStorage patientStorage;
	private Action action;
	@SuppressWarnings("rawtypes")
	private Class parentScreen;

	private static final int COLUMN_COUNT = 2;
	private static final float FIELD_WIDTH = Gdx.graphics.getWidth() * 0.4f;
	private static final float VALUE_WIDTH = Gdx.graphics.getWidth() * 0.6f;
	private static final float NAME_PADDING = Gdx.graphics.getHeight() * 0.1f;

	private Table rootTable;

	public enum Action {
		insert, update;
	}

	public PatientModificationScreen(Patient patient, PatientStorage patientStorage,
			ScreenChangedListener screenChanger, Action action, @SuppressWarnings("rawtypes") Class parentScreen) {
		this.screenChanger = screenChanger;
		this.patient = patient;
		this.patientStorage = patientStorage;
		this.action = action;
		this.parentScreen = parentScreen;
	}

	@Override
	public void show() {
		super.show();

		initializeRootTable();
		mainStage.addActor(rootTable);
	}

	private void initializeRootTable() {
		rootTable = new Table();
		rootTable.setFillParent(true);

		addName();
		addChangableField(Resources.getTextResources().getString(TextResources.StringResource.date), patient.getDateRepresentation());
		addChangableField(Resources.getTextResources().getString(TextResources.StringResource.medicalHistory), patient.getMedicalHistoryNumber());
		addBurns();
		addManageButtons();
	}

	private void addBurns() {
		Burn degreeI = getBurn(Burn.BurnType.I);
		Float degreeIPercentage = 0f;
		if (degreeI != null)
			degreeIPercentage = degreeI.getPercentage();

		addNonchangableField(Resources.getTextResources().getString(TextResources.StringResource.burnI), Float.toString(degreeIPercentage));

		Burn degreeII = getBurn(Burn.BurnType.II);
		Float degreeIIPercentage = 0f;
		if (degreeII != null)
			degreeIIPercentage = degreeII.getPercentage();

		addNonchangableField(Resources.getTextResources().getString(TextResources.StringResource.burnII), Float.toString(degreeIIPercentage));

		Burn degreeIII = getBurn(Burn.BurnType.III);
		Float degreeIIIPercentage = 0f;
		if (degreeIII != null)
			degreeIIIPercentage = degreeIII.getPercentage();

		addNonchangableField(Resources.getTextResources().getString(TextResources.StringResource.burnIII), Float.toString(degreeIIIPercentage));
	}

	private Burn getBurn(Burn.BurnType type) {
		for (Burn i : patient.getBurns())
			if (i.getType() == type)
				return i;

		return null;
	}

	private void addName() {
		final TextField nameField = createTextField(patient.getFullNameRepresentation());
		nameField.setName(Resources.getTextResources().getString(TextResources.StringResource.fullName));
		rootTable.add(nameField).colspan(2).width(FIELD_WIDTH + VALUE_WIDTH);

		rootTable.row();

		rootTable.add().height(NAME_PADDING).expandX();
		rootTable.row();
	}

	private void addNonchangableField(String field, String value) {
		Label nameLabel = createLabel(field);
		rootTable.add(nameLabel).width(FIELD_WIDTH);

		final Label valueLabel = createLabel(value);
		rootTable.add(valueLabel).width(VALUE_WIDTH);

		rootTable.row();
	}

	private void addChangableField(String field, String value) {
		Label nameLabel = createLabel(field);
		rootTable.add(nameLabel).width(FIELD_WIDTH);

		final TextField valueLabel = createTextField(value);
		valueLabel.setName(field);
		rootTable.add(valueLabel).width(VALUE_WIDTH);

		rootTable.row();
	}

	private void addManageButtons() {
		Table manageTable = new Table();
		manageTable.add(createBackButton());
		if (action == Action.update)
			manageTable.add(createDeleteButton());
		manageTable.add(createSaveButton());

		rootTable.add(manageTable).colspan(COLUMN_COUNT).expand().bottom().right();
	}

	private Button createDeleteButton() {
		TextButton delete = createTextButton(Resources.getTextResources().getString(TextResources.StringResource.remove));
		delete.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				
				patientStorage.deletePatient(patient.getId());
				screenChanger.changeScreen(parentScreen);
			}
		});
		return delete;
	}

	private Button createBackButton() {
		TextButton back = createTextButton("Back");
		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				screenChanger.changeScreen(parentScreen);
			}
		});

		return back;
	}

	private Button createSaveButton() {
		TextButton save = createTextButton(Resources.getTextResources().getString(TextResources.StringResource.save));
		save.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				boolean parseSuccessfull = true;

				for (Actor i : rootTable.getChildren()) {
					if (i.getName() != null) {
						if (i.getName().equals(Resources.getTextResources().getString(TextResources.StringResource.fullName))) {
							try {
								patient.parseFullName(((TextField) i).getText().toString());
							} catch (ParseException e) {
								showErrorDialog(Resources.getTextResources().getString(TextResources.StringResource.cantParseName));
								parseSuccessfull = false;
							} finally {
								((TextField) i).setText(patient.getFullNameRepresentation());
							}
						} else if (i.getName().equals(Resources.getTextResources().getString(TextResources.StringResource.date))) {
							try {
								patient.getDateFormat().parse(((TextField) i).getText().toString());
							} catch (ParseException e) {
								showErrorDialog(Resources.getTextResources().getString(TextResources.StringResource.cantParseName));
								parseSuccessfull = false;
							} finally {
								((TextField) i).setText(patient.getDateRepresentation());
							}
						} else if (i.getName().equals(Resources.getTextResources().getString(TextResources.StringResource.medicalHistory))) {
							String medicalHistoryNumber = ((TextField) i).getText().toString();
							if (medicalHistoryNumber.length() > 0)
								patient.setMedicalHistoryNumber(medicalHistoryNumber);
							else {
								showErrorDialog(Resources.getTextResources().getString(TextResources.StringResource.medicalHistoryIsNotSet));
								((TextField) i).setText(patient.getMedicalHistoryNumber());
								parseSuccessfull = false;
							}
						}
					}
				}

				if (parseSuccessfull) {
					if (action == Action.update)
						patientStorage.updatePatient(patient);
					else if (action == Action.insert)
						patientStorage.setPatient(patient);

					screenChanger.changeScreen(parentScreen);
				}
			}
		});

		return save;
	}
}
