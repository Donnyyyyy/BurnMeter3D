package ru.donny.burnmeter3D.engine.objects.model;

import java.util.ArrayList;

public enum BodyPart {

	head, arms, legs, torso;

	public static final String HEAD = "head";
	public static final String TORSO = "torso";
	public static final String LEGS = "legs";
	public static final String ARMS = "arms";

	public String toString() {
		if (this == head)
			return HEAD;
		else if (this == arms)
			return ARMS;
		else if (this == legs)
			return LEGS;
		else if (this == torso)
			return TORSO;
		else
			return "undefined";
	}

	public static ArrayList<BodyPart> getAllParts() {
		ArrayList<BodyPart> parts = new ArrayList<BodyPart>();
		parts.add(head);
		parts.add(arms);
		parts.add(legs);
		parts.add(torso);

		return parts;
	}

	public static BodyPart getPartByName(String name) {
		if (name.equals(HEAD))
			return head;
		else if (name.equals(ARMS))
			return arms;
		else if (name.equals(LEGS))
			return legs;
		else if (name.equals(TORSO))
			return torso;
		else
			return null;
	}
}