package ru.donny.burnmeter3D.engine.objects.model;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

import ru.donny.burnmeter3D.data.Burns;
import ru.donny.burnmeter3D.engine.objects.burns.BurnsStorage;
import ru.donny.burnmeter3D.engine.objects.burns.BurnsWrapper;
import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;
import ru.donny.burnmeter3D.graphics.gui.widgets.DataInterface;
import ru.donny.burnmeter3D.graphics.renderable.BurnInstance;

public class PatientModelInstance extends ModelInstance implements BurnsStorage {

	private String name;
	private BurnsWrapper burns;

	public PatientModelInstance(Model model, String name, BurnsWrapper burns) {
		super(model);
		this.setName(name);
		this.setBurns(burns);
	}

	@Override
	public boolean removeLast() {
		return getBurns().removeLast();
	}

	@Override
	public void clear() {
		getBurns().clear();
	}

	@Override
	public boolean add(BurnInstance burn) {
		return getBurns().add(burn);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public ArrayList<Triangle> getUniqueTriangles() {
		return getBurns().getUniqueTriangles();
	}

	@Override
	public Burns toBurns(ModelAbstraction modelAbstraction) {
		return getBurns().toBurns(modelAbstraction);
	}

	@Override
	public void setResultDisplayer(DataInterface dataInterface) {
		getBurns().setResultDisplayer(dataInterface);
	}

	public BurnsWrapper getBurns() {
		return burns;
	}

	public void setBurns(BurnsWrapper burns) {
		this.burns = burns;
	}

}
