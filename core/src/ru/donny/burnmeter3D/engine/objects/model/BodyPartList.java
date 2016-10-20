package ru.donny.burnmeter3D.engine.objects.model;

import java.util.ArrayList;
import java.util.Collection;

import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;

public class BodyPartList extends ArrayList<Triangle> {

	private static final long serialVersionUID = 5171465616927319701L;

	private float totalSquare;

	public BodyPartList(Collection<? extends Triangle> parent) {
		super(parent);
		computeTotalSquare();
	}

	public void computeTotalSquare() {
		setTotalSquare(0);

		for (Triangle i : this)
			setTotalSquare(getTotalSquare() + i.getSquare());
	}

	public float getTotalSquare() {
		return totalSquare;
	}

	public void setTotalSquare(float totalSquare) {
		this.totalSquare = totalSquare;
	}

}
