package ru.donny.burnmeter3D.data;

import java.util.HashSet;

import com.badlogic.gdx.graphics.Color;

import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;
import ru.donny.burnmeter3D.engine.objects.model.ModelAbstraction;
import ru.donny.burnmeter3D.graphics.renderable.BurnInstance;
import ru.donny.burnmeter3D.graphics.renderable.TriangledArea;

public class Burn extends HashSet<Integer> {

	private BurnType type;
	private float percentage;

	private static final long serialVersionUID = -6410807530504573862L;

	public enum BurnType {
		I, II, III, undefined;

		private static final Color I_COLOR = Color.GREEN;/*
															 * new Color(237f /
															 * 255f, 1f, 0f,
															 * 1f);
															 */
		private static final Color II_COLOR = new Color(1f, 99f / 255f, 0f, 1f);
		private static final Color III_COLOR = Color.RED;
		private static final int I_ID = 0;
		private static final int II_ID = 1;
		private static final int III_ID = 2;

		public static BurnType createFromColor(Color typeColor) {
			if (typeColor.equals(I_COLOR))
				return I;
			else if (typeColor.equals(II_COLOR))
				return II;
			else if (typeColor.equals(III_COLOR))
				return III;
			return undefined;
		}

		public Color getColor() {
			switch (this) {
			case I:
				return I_COLOR;
			case II:
				return II_COLOR;
			case III:
				return III_COLOR;
			default:
				return new Color();
			}
		}

		public int getId() {
			switch (this) {
			case I:
				return I_ID;
			case II:
				return II_ID;
			case III:
				return III_ID;
			default:
				return -1;
			}
		}
	}

	public Burn(BurnType type) {
		this.type = type;
	}

	public Burn(TriangledArea area) {
		super();

		for (Triangle i : area.getRenderedTriangles())
			add(i.getId());

		setType(BurnType.createFromColor(area.getColor()));
	}

	public Burn(BurnType type, float percentage) {
		this.percentage = percentage;
		this.type = type;
	}

	public HashSet<Integer> getContaied(BurnInstance area) {
		HashSet<Integer> contained = new HashSet<Integer>();

		for (Triangle i : area.getBurnTriangles())
			if (contains(i.getId()))
				contained.add(i.getId());

		return contained;
	}

	public void calculateSquare(ModelAbstraction model) {
		float burnSquare = 0;

		for (Integer i : this) {
			Triangle tmp = model.getTriangle(i);
			burnSquare += tmp.getSquare();
		}

		setPercentage(burnSquare / model.getSquare() * 100f);
	}

	public BurnType getType() {
		return type;
	}

	public void setType(BurnType type) {
		this.type = type;
	}

	public float getPercentage() {
		return percentage;
	}

	public void setPercentage(float percentage) {
		this.percentage = percentage;
	}

}
