package ru.donny.burnmeter3D.engine.objects.geometry;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import ru.donny.burnmeter3D.engine.objects.geometry.point.Point3D;

public class Line {

	/**
	 * First plane of the straight (straight - intersection of two planes).
	 */
	private Plane plane1;

	/**
	 * First plane of the straight (straight - intersection of two planes).
	 */
	private Plane plane2;

	private Point3D start;

	private Point3D end;

	public Line(Plane plane1, Plane plane2) {
		this.setPlane1(plane1);
		this.setPlane2(plane2);
	}

	public Line(Vector3 point1, Vector3 point2) {
		this(new Point3D(point1), new Point3D(point2));
	}
	
	
	/**
	 * Constructs new {@link Line}. If point1 = point2 then it is a {@link Point3D} therefore sets planes equal to <code>null</code>.
	 * @param point1 one of the point belongs the {@link Line}.
	 * @param point2 one of the point belongs the {@link Line}.
	 */
	public Line(Point3D point1, Point3D point2) {
		start = point1;
		end = point2;

		if ((point1.x - point2.x) != 0) {
			plane1 = calculatePlane(false, true, false);
			plane2 = calculatePlane(false, false, true);
		} else if ((point1.y - point2.y) != 0) {
			plane1 = calculatePlane(true, false, false);
			plane2 = calculatePlane(false, false, true);
		} else if ((point1.z - point2.z) != 0) {
			plane1 = calculatePlane(true, false, false);
			plane2 = calculatePlane(false, true, false);
		} else {
			plane1 = null;
			plane2 = null;
		}
	}

	/**
	 * Constructs {@link Plane} with A/B/C = 0. Only 1 argument should be = 0.
	 * 
	 * @param isAEqualsZero
	 *            is A = 0.
	 * @param isBEqualsZero
	 *            is B = 0.
	 * @param isCEqualsZero
	 *            is C = 0.
	 * @return constructed {@link Plane} or null if parameters are invalid (see
	 *         method description).
	 */
	private Plane calculatePlane(boolean isAEqualsZero, boolean isBEqualsZero, boolean isCEqualsZero) {
		int expressedFieldId = 0;
		int expressedThroughFieldId = 0;

		if (isAEqualsZero) {
			expressedFieldId = Point3D.Y_ID;
			expressedThroughFieldId = Point3D.Z_ID;
		} else if (isBEqualsZero) {
			expressedFieldId = Point3D.X_ID;
			expressedThroughFieldId = Point3D.Z_ID;
		} else if (isCEqualsZero) {
			expressedFieldId = Point3D.X_ID;
			expressedThroughFieldId = Point3D.Y_ID;
		}

		if (expressedFieldId == expressedThroughFieldId)
			return null;

		float expressedValue = end.get(expressedThroughFieldId) - start.get(expressedThroughFieldId);
		float expressedThroughValue = start.get(expressedFieldId) - end.get(expressedFieldId);
		float d = end.get(expressedFieldId) * start.get(expressedThroughFieldId)
				- start.get(expressedFieldId) * end.get(expressedThroughFieldId);

		if (isAEqualsZero)
			return new Plane(0, expressedValue, expressedThroughValue, d);
		else if (isBEqualsZero)
			return new Plane(expressedValue, 0, expressedThroughValue, d);
		else if (isCEqualsZero)
			return new Plane(expressedValue, expressedThroughValue, 0, d);
		else
			return null;
	}

	public float getLength() {
		return new Point3D(end.x - start.x, end.y - start.y, end.z - start.z).len();
	}

	public Plane getPlane2() {
		return plane2;
	}

	public void setPlane2(Plane plane2) {
		this.plane2 = plane2;
	}

	public Plane getPlane1() {
		return plane1;
	}

	public void setPlane1(Plane plane1) {
		this.plane1 = plane1;
	}

	public Point3D getStart() {
		return start;
	}

	public void setStart(Point3D start) {
		this.start = start;
	}

	public Point3D getEnd() {
		return end;
	}

	public void setEnd(Point3D end) {
		this.end = end;
	}

	public BoundingBox getBoundingBox() {
		return new BoundingBox(start, end);
	}
}
