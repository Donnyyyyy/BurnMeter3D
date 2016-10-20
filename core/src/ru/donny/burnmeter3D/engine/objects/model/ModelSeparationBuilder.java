package ru.donny.burnmeter3D.engine.objects.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;

public class ModelSeparationBuilder {

	private HashMap<BodyPart, ArrayList<Triangle>> modelSeparation = new HashMap<BodyPart, ArrayList<Triangle>>();

	/**
	 * @param bodyPart
	 *            {@link BodyPart} that is key in {@link Map}. If
	 *            modelSeparation already contains such key, value will be
	 *            replaced.
	 * @return whether building modelSeparation already has such bodyPart.
	 */
	public boolean addPart(BodyPart bodyPart, ArrayList<Triangle> partTriangles) {
		return modelSeparation.put(bodyPart, partTriangles) != null;
	}

	public boolean isFilled() {
		return BodyPart.getAllParts().size() == modelSeparation.size();
	}

	public HashMap<BodyPart, ArrayList<Triangle>> build() {
		for (BodyPart i : BodyPart.getAllParts()) {
			if (!modelSeparation.containsKey(i)) {
				modelSeparation.put(i, new ArrayList<Triangle>());
			}
		}

		return modelSeparation;
	}
}
