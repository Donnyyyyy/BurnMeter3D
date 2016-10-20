package ru.donny.burnmeter3D.engine.objects.geometry;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import ru.donny.burnmeter3D.engine.MathEngine;
import ru.donny.burnmeter3D.engine.MathEngine.LowAccuracyException;
import ru.donny.burnmeter3D.engine.objects.geometry.point.Point3D;
import ru.donny.burnmeter3D.objects.Pair;

public class Triangle extends Plane {

	public static final int TRIANGLE_NUM_VERTICES = 3;

	private Point3D pointA;
	private Point3D pointB;
	private Point3D pointC;
	private BoundingBox boundingBox;
	private Float square = null;
	private Vector3 normal;

	public Triangle(Point3D aPoint, Point3D bPoint, Point3D cPoint, int id) {
		super(aPoint, bPoint, cPoint, id);
		pointA = aPoint;
		pointB = bPoint;
		pointC = cPoint;
	}

	public void precalculateAll() {
		calculateNormal();
		calculateSquare();
		calculateBoundingBox();
	}

	/**
	 * @param point
	 *            point to check.
	 * @return <code>true</code> if the point belongs to the triangle or if
	 *         bounds haven't been set.<br>
	 *         <code>false</code> in another cases.
	 */
	public boolean isBelong(Vector3 point) {
		if (square == null)
			calculateSquare();

		try {
			float particleSquare1 = MathEngine.calculateTriangleSquare(pointA, pointB, point);
			float particleSquare2 = MathEngine.calculateTriangleSquare(pointA, pointC, point);
			float particleSquare3 = MathEngine.calculateTriangleSquare(pointC, pointB, point);

			float verificationTriangleSquare = particleSquare1 + particleSquare2 + particleSquare3;

			return MathEngine.compare(square, verificationTriangleSquare,
					MathEngine.ACCURACY_MEDIUM) == MathEngine.COMPARE_EQUAL;
		} catch (LowAccuracyException e) {
			return false;
		}

	}

	public boolean hasSameTwoPoints(Triangle verifiable) {
		int equal = 0;

		for (int i = 0; i < Triangle.TRIANGLE_NUM_VERTICES; i++)
			for (int j = 0; j < Triangle.TRIANGLE_NUM_VERTICES; j++)
				if (verifiable.getPoint(i).getId() == getPoint(j).getId())
					equal++;

		if (equal >= 2)
			return true;
		else
			return false;
	}

	public boolean hasAdjacentTwoPoints(Triangle verifiable) {
		int equal = 0;

		for (int i = 0; i < Triangle.TRIANGLE_NUM_VERTICES; i++)
			for (int j = 0; j < Triangle.TRIANGLE_NUM_VERTICES; j++)
				if (verifiable.getPoint(i).equals(getPoint(j)))
					equal++;

		if (equal >= 2)
			return true;
		else
			return false;
	}

	/**
	 * Get the adjacent {@link Line} between triangles.
	 * 
	 * @param neighbour
	 * @return Line with the start/end at the mutual line's point.
	 *         <code>null</code> if this triangles hasn't such line.
	 */
	public Line getAdjacentLine(Triangle neighbour) {
		Pair<Point3D, Point3D> adjacentPoints = getAdjacentPoints(neighbour);

		return new Line(adjacentPoints.getA(), adjacentPoints.getB());
	}

	/**
	 * Get the adjacent {@link Point3D}s between triangles.
	 * 
	 * @param neighbour
	 * @return Pair<Point, Point> with the start/end at the adjacent line's
	 *         points. <code>null</code> if this triangles hasn't such points.
	 */
	public Pair<Point3D, Point3D> getAdjacentPoints(Triangle neighbour) {
		Point3D adjacentPoint1 = null;
		Point3D adjacentPoint2 = null;

		for (int i = 0; i < TRIANGLE_NUM_VERTICES; i++)
			for (int j = 0; j < TRIANGLE_NUM_VERTICES; j++)
				if (MathEngine.compare(getPoint(i), neighbour.getPoint(j),
						MathEngine.ACCURACY_HIGH) == MathEngine.COMPARE_EQUAL)
					adjacentPoint1 = new Point3D(getPoint(i));

		for (int i = 0; i < TRIANGLE_NUM_VERTICES; i++)
			for (int j = 0; j < TRIANGLE_NUM_VERTICES; j++)
				if ((getPoint(i) != adjacentPoint1) && (MathEngine.compare(getPoint(i), neighbour.getPoint(j),
						MathEngine.ACCURACY_HIGH) == MathEngine.COMPARE_EQUAL))
					adjacentPoint2 = new Point3D(getPoint(i));

		if ((adjacentPoint1 != null) && (adjacentPoint2 != null))
			return new Pair<Point3D, Point3D>(adjacentPoint1, adjacentPoint2);
		else
			return null;
	}

	public Point3D getPoint(int index) {
		switch (index) {
		case 0:
			return pointA;
		case 1:
			return pointB;
		case 2:
			return pointC;
		default:
			return null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			Triangle verifiable = (Triangle) obj;
			MathEngine comparator = new MathEngine();
			comparator.setAccuracy(MathEngine.ACCURACY_HIGH);

			return (Math.abs(comparator.compare(pointA, verifiable.getPointA()))
					+ Math.abs(comparator.compare(pointB, verifiable.getPointB()))
					+ Math.abs(comparator.compare(pointC, verifiable.getPointC()))) == MathEngine.COMPARE_EQUAL;
		} else
			return false;
	}

	public void setSquare(Float square) {
		this.square = square;
	}

	public float getSquare() {
		if (square == null)
			calculateSquare();
		return square;
	}

	private void calculateSquare() {
		try {
			square = MathEngine.calculateTriangleSquare(this);
		} catch (LowAccuracyException e) {
			double ab = MathEngine.calculateLength(pointA, pointB);
			double ac = MathEngine.calculateLength(pointA, pointC);
			double bc = MathEngine.calculateLength(pointB, pointC);

			double p = (ab + bc + bc) / 2.0;
			double highPrecisionSquare = Math.sqrt(p * (p - ab) * (p - bc) * (p - ac));
			square = (float) (highPrecisionSquare);
		}
	}

	private void calculateNormal() {
		Vector3 sideAB = new Vector3(pointA.x - pointB.x, pointA.y - pointB.y, pointA.z - pointB.z);
		Vector3 sideAC = new Vector3(pointA.x - pointC.x, pointA.y - pointC.y, pointA.z - pointC.z);

		normal = sideAB.crs(sideAC).nor();
	}

	private void calculateBoundingBox() {
		boundingBox = new BoundingBox();
		boundingBox.ext(pointA);
		boundingBox.ext(pointB);
		boundingBox.ext(pointC);
	}

	public BoundingBox getBoundingBox() {
		if (boundingBox == null)
			calculateBoundingBox();

		return boundingBox;
	}

	public Point3D getPointA() {
		return pointA;
	}

	public void setPointA(Point3D pointA) {
		this.pointA = pointA;
	}

	public Point3D getPointB() {
		return pointB;
	}

	public void setPointB(Point3D pointB) {
		this.pointB = pointB;
	}

	public Point3D getPointC() {
		return pointC;
	}

	public void setPointC(Point3D pointC) {
		this.pointC = pointC;
	}

	public Point3D getCenter() {
		return new Point3D((pointA.x + pointB.x + pointC.x) / 3f, (pointA.y + pointB.y + pointC.y) / 3f,
				(pointA.z + pointB.z + pointC.z) / 3f);
	}

	public Vector3 getNormal() {
		if (normal == null)
			calculateNormal();

		return normal;
	}
}
