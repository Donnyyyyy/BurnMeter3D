package ru.donny.burnmeter3D.engine.objects.model;

import java.util.ArrayList;

import ru.donny.burnmeter3D.objects.Pair;

public class HumanInfo {

	private Float age;
	private ArrayList<Pair<BodyPart, Float>> proportions;

	public HumanInfo() {
		proportions = new ArrayList<Pair<BodyPart, Float>>();
	}

	public HumanInfo(Float age, ArrayList<Pair<BodyPart, Float>> proportions) {
		super();
		this.setAge(age);
		this.setProportions(proportions);
	}

	public ArrayList<Pair<BodyPart, Float>> getProportions() {
		return proportions;
	}

	public void setProportions(ArrayList<Pair<BodyPart, Float>> proportions) {
		this.proportions = proportions;
	}

	public Float getAge() {
		return age;
	}

	public void setAge(Float age) {
		this.age = age;
	}

}
