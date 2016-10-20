package ru.donny.burnmeter3D.engine.objects.geometry;

import java.util.ArrayList;

import ru.donny.burnmeter3D.engine.objects.geometry.objects2d.Line2D;
import ru.donny.burnmeter3D.engine.objects.geometry.point.AbstractPoint;

public class ConvexPolygon extends ArrayList<AbstractPoint> {

	private static final long serialVersionUID = -1863936296054321685L;

	@SuppressWarnings("unused")
	public boolean isBelongs(AbstractPoint verifiable) {
		if ((size() == 0) || (verifiable.getClass() != get(0).getClass()))
			return false;

		int insideCount = 0;
		Line2D line;
		int previousPointSignum;
		/*
		 * for (int i = 0; i <= size(); i++) { if (i == 0) { line = new
		 * Line2D(get(i), get(i + 1)); previousPointSignum =
		 * line.isBelongs(start); } else if (i == dragPoints.size() - 1) { line
		 * = new Line2D(dragPoints.get(i), end); previousPointSignum =
		 * line.isBelongs(dragPoints.get(i - 1)); } else if (i ==
		 * dragPoints.size()) { line = new Line2D(end, start);
		 * previousPointSignum = line.isBelongs(dragPoints.get(i - 1)); } else {
		 * line = new Line2D(dragPoints.get(i), dragPoints.get(i + 1));
		 * previousPointSignum = line.isBelongs(dragPoints.get(i - 1)); }
		 * 
		 * if (line.isBelongs(verifiable) == previousPointSignum) insideCount++;
		 * else if (previousPointSignum == 0) { insideCount++; } }
		 */
		return false;
	}
}
