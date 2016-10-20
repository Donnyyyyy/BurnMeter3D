package ru.donny.burnmeter3D.data;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;
import ru.donny.burnmeter3D.engine.objects.model.BodyPart;
import ru.donny.burnmeter3D.engine.objects.model.ModelAbstraction;
import ru.donny.burnmeter3D.engine.objects.model.ModelSeparationBuilder;

public class ModelSeparationStorage {

	public static final String PREFIX = "res/separation/";

	public static HashMap<BodyPart, ArrayList<Triangle>> load(ModelAbstraction model) {
		FileHandle file = Gdx.files.local(PREFIX + model.getModelName());
		if (!file.exists()) {
			System.out.println("model separation for " + model.getModelName() + " does not exists");
			return null;
		}
		
		Parser in = new Parser(file.read());

		ModelSeparationBuilder builder = new ModelSeparationBuilder();
		for (int i = 0; i < BodyPart.getAllParts().size(); i++) {
			builder.addPart(BodyPart.getPartByName(in.nextString()), model.getTriangles(in.intArrayList()));
		}

		return builder.build();
	}

	public static void store(Map<BodyPart, ArrayList<Triangle>> modelSeparation, String modelName) {
		PrintStream out = new PrintStream(Gdx.files.local(PREFIX + modelName).write(false));

		for (BodyPart i : BodyPart.getAllParts()) {
			out.print(i.toString());

			for (Triangle j : modelSeparation.get(i)) {
				out.print(" " + j.getId());
			}

			out.println();
		}
	}
}
