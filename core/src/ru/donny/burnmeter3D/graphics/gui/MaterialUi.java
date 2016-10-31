package ru.donny.burnmeter3D.graphics.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class MaterialUi extends Stage {

    private Table rootTable;
    private BitmapFont russianFont = RussianFont.get();
    private Skin skin = new Skin(Gdx.files.internal("data/skins/uiskin.json"));

    private ButtonGroup<CheckBox> tmpRadioGroup;
    private boolean creatingRadioGroup = false;

    public MaterialUi() {
        rootTable = new Table();
        rootTable.setFillParent(true);
        addActor(rootTable);

        magicInit();
    }

    protected void magicInit() {
        addTextButton("Magic goes somewhere here", null);
        addCheckbox("And here", null);
        addIcon("logo.png", null);

        rootTable.clearChildren();
    }

    public Cell<ImageButton> addIcon(String iconName, ClickListener clickListener) {
        ImageButton imageButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("data/drawable/" + iconName))));

        if (clickListener != null)
            imageButton.addListener(clickListener);

        return rootTable.add(imageButton).width(64).height(64).pad(14);
    }

    public Cell<TextButton> addTextButton(String text, ClickListener clickListener) {
        TextButton button = new TextButton(text != null ? text : "", skin);
        button.getStyle().font = russianFont;
        button.getStyle().fontColor = Color.WHITE;

        if (clickListener != null)
            button.addListener(clickListener);

        return rootTable.add(button).width(240).height(42).pad(14);
    }

    public Cell<CheckBox> addCheckbox(String text, ClickListener clickListener) {
        CheckBox checkBox = new CheckBox(text != null ? text : "", skin);
        checkBox.getStyle().font = russianFont;
        checkBox.getStyle().fontColor = Color.WHITE;

        if (clickListener != null) {
            checkBox.addListener(clickListener);
        }

        if (tmpRadioGroup != null && creatingRadioGroup)
            tmpRadioGroup.add(checkBox);

        return rootTable.add(checkBox).width(220).height(42).pad(14);
    }

    public void startRadioGroup() {
        tmpRadioGroup = new ButtonGroup<CheckBox>();
        creatingRadioGroup = true;
    }

    public ButtonGroup<CheckBox> finishRadioGroup() {
        creatingRadioGroup = false;
        return tmpRadioGroup;
    }

    public Table getRootTable() {
        return rootTable;
    }

    public Actor getLast() {
        if (!rootTable.hasChildren()) {
            return null;
        } else {
            Actor[] children = rootTable.getChildren().items;

            return children[children.length - 1];
        }
    }
}
