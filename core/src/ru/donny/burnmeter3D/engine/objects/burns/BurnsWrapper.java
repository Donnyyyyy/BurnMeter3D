package ru.donny.burnmeter3D.engine.objects.burns;

import java.util.ArrayList;
import java.util.HashSet;

import ru.donny.burnmeter3D.controllers.Logger;
import ru.donny.burnmeter3D.controllers.Preferences;
import ru.donny.burnmeter3D.data.Burn;
import ru.donny.burnmeter3D.data.Burns;
import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;
import ru.donny.burnmeter3D.engine.objects.model.ModelAbstraction;
import ru.donny.burnmeter3D.graphics.gui.widgets.DataInterface;
import ru.donny.burnmeter3D.graphics.renderable.BurnInstance;

public class BurnsWrapper extends ArrayList<BurnInstance> implements BurnsStorage {

	private static final long serialVersionUID = 8686521159169238696L;
	public static final String SQUARE_RESULT_TAG = "square";
	public static final String THERAPY_RESULT_TAG = "therapy";
	private Float totalSquare = new Float(0);
	private DataInterface resultDisplayer;
	private float bodySquare;

	public BurnsWrapper(DataInterface resultDisplayer, float bodySquare) {
		super();
		this.setResultDisplayer(resultDisplayer);
		this.bodySquare = bodySquare;
	}

	@Override
	public boolean add(BurnInstance e) {
		boolean isAdded = super.add(e);
		calculateSquare();
		updateInfo();
		return isAdded;
	}

	@Override
	public void clear() {
		super.clear();
		calculateSquare();
		updateInfo();
	}

	public ArrayList<BurnInstance> asArray(){
		return this;
	}

	public ArrayList<Triangle> getUniqueTriangles() {
		HashSet<Triangle> unique = new HashSet<Triangle>();

		for (BurnInstance i : this)
			for (Triangle j : i.getBurnTriangles())
				unique.add(j);

		return new ArrayList<Triangle>(unique);
	}

	private void calculateSquare() {
		totalSquare = new Float(0);

		for (Triangle i : getUniqueTriangles())
			totalSquare += i.getSquare();

		totalSquare = totalSquare / bodySquare * 100f;
		return;
	}

	private void updateInfo() {
		showSquare(getTotalSquare());
		showInfusionAmount(getTotalSquare(), getResultDisplayer().getWeight());
	}

	private void showSquare(float square) {
		square = (int) (square * Math.pow(10, Preferences.SQUARE_ACCURACY))
				/ (float) Math.pow(10, Preferences.SQUARE_ACCURACY);
		getResultDisplayer().setResult(SQUARE_RESULT_TAG, square);
		Logger.printTimeMessage("square: " + square + "%");
	}

	private void showInfusionAmount(Float totalSquare, float weight) {
		float infusionAmount;
		if (totalSquare == 0)
			infusionAmount = 0;
		else
			infusionAmount = (2000f + (totalSquare * weight)) * 2f;

		getResultDisplayer().setResult(THERAPY_RESULT_TAG, infusionAmount);
	}

	public boolean removeLast() {
		if (size() >= 1) {
			remove(size() - 1);
			calculateSquare();
			return true;
		}
		return false;
	}

	public Float getTotalSquare() {
		return totalSquare;
	}

	public void setTotalSquare(Float totalSquare) {
		this.totalSquare = totalSquare;
	}

	public DataInterface getResultDisplayer() {
		return resultDisplayer;
	}

	public void setResultDisplayer(DataInterface resultDisplayer) {
		this.resultDisplayer = resultDisplayer;
	}

	@Override
	public Burns toBurns(ModelAbstraction modelAbstraction) {
		Burns burns = new Burns();
		for (int i = size() - 1; i >= 0; i--)
			burns.add(get(i));

		for (Burn i : burns)
			i.calculateSquare(modelAbstraction);

		return burns;
	}
}
