package ru.donny.burnmeter3D.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ru.donny.burnmeter3D.controllers.Preferences;
import ru.donny.burnmeter3D.graphics.gui.RussianFont;

public abstract class StandartScreen implements Screen {

	protected BitmapFont russianFont;
	protected Skin defaultSkin = new Skin(Gdx.files.internal("data/skins/uiskin.json"));
	protected Stage mainStage;

	public StandartScreen() {
		mainStage = new Stage();
		russianFont = RussianFont.get();
	}

	@Override
	public void show() {
		initializeWidgets();
		Gdx.input.setInputProcessor(mainStage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//		Gdx.gl.glClearColor(233f / 255f, 255f / 255f, 133f / 255f, 1f);
		mainStage.act();
		mainStage.draw();
	}

	protected void showErrorDialog(String title) {
		final Dialog alert = createDialog(title);

		TextButton confirm = createTextButton("Confirm");

		confirm.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);

				alert.hide();
				alert.cancel();
				alert.remove();
			}
		});

		alert.button(confirm);
		alert.show(mainStage);
	}

	protected void showConfirmationDialog(String title, final Boolean reply) {
		final Dialog confirmation = new Dialog(title, defaultSkin);
		confirmation.getStyle().titleFont = russianFont;
		confirmation.getStyle().titleFontColor = Preferences.FONT_COLOR;

		TextButton accept = createTextButton("Ok");
		accept.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				confirmation.hide();
				confirmation.cancel();
				confirmation.remove();
			}
		});
		confirmation.button(accept, true);

		TextButton refuse = createTextButton("No");
		refuse.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);

				confirmation.hide();
				confirmation.cancel();
				confirmation.remove();
			}
		});
		confirmation.button(refuse, false);

		confirmation.show(mainStage);
	}

	protected TextButton createTextButton(String text) {
		TextButton button = new TextButton(text, defaultSkin);
		button.getStyle().font = russianFont;
		button.getStyle().fontColor = Preferences.FONT_COLOR;

		return button;
	}

	protected TextButton createTextButton(String text, Color textColor) {
		TextButton button = new TextButton(text, defaultSkin);
		button.getStyle().font = russianFont;
		button.getStyle().fontColor = textColor;

		return button;
	}

	protected TextField createTextField(String text) {
		TextField textField = new TextField(text, defaultSkin);
		textField.getStyle().font = russianFont;
		textField.getStyle().fontColor = Preferences.FONT_COLOR;

		return textField;
	}

	protected TextField createTextField(String text, Color textColor) {
		TextField textField = new TextField(text, defaultSkin);
		textField.getStyle().font = russianFont;
		textField.getStyle().fontColor = textColor;

		return textField;
	}

	protected Label createLabel(String text) {
		Label label = new Label(text, defaultSkin);
		label.getStyle().font = russianFont;
		label.getStyle().fontColor = Preferences.FONT_COLOR;
		label.setWrap(true);

		return label;
	}

	protected Label createLabel(String text, Color textColor) {
		Label label = new Label(text, defaultSkin);
		label.getStyle().font = russianFont;
		label.getStyle().fontColor = textColor;
		label.setWrap(true);

		return label;
	}

	protected Dialog createDialog(String title) {
		Dialog dialog = new Dialog(title, defaultSkin);
		dialog.getStyle().titleFont = russianFont;
		dialog.getStyle().titleFontColor = Preferences.FONT_COLOR;

		return dialog;
	}

	@Override
	public void resize(int width, int height) {
		mainStage.getViewport().update(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	public void dispose() {
		russianFont.dispose();
		defaultSkin.dispose();
		mainStage.dispose();
	}

	@SuppressWarnings("unused")
	protected void initializeWidgets() {
		Label trashLabel = createLabel("Trash");
		TextButton trashButton = createTextButton("Trash");
		TextField trashField = createTextField("Trash");
		Dialog alert = createDialog("Trash");
		alert.getStyle().titleFont = russianFont;
	}
}
