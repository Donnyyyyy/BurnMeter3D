package ru.donny.burnmeter3D.engine.objects.geometry.point;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ru.donny.burnmeter3D.engine.objects.geometry.objects2d.Point2D;

public class Point3D extends Vector3 implements AbstractPoint{

	private static final long serialVersionUID = 6718935979102843587L;

	public static final int COORDINATES_NUMBER = 3;
	
	private int id;
	public static final int X_ID = 0;
	public static final int Y_ID = 1;
	public static final int Z_ID = 2;

	public Point3D() {
	}

	public Point3D(Vector3 vector) {
		super(vector);
	}

	public Point3D(float[] values) {
		super(values);
	}

	public Point3D(Vector2 vector, float z) {
		super(vector, z);
	}

	public Point3D(float x, float y, float z, int id) {
		super(x, y, z);
		this.id = id;
	}
	
	public Point3D(float x, float y, float z) {
		super(x, y, z);
	}

	public Point3D(Point2D point2d) {
		this(point2d, 0);
	}

	public Point3D(Point2D point2d, float z) {
		this(point2d.getX(), point2d.getY(), z);
	}

	/**
	 * @param fieldId
	 *            constant like {@link Point3D}.X_ID.
	 * @return field with the same id and 0 if fieldId is invalid.
	 */
	@Override
	public float get(int fieldId) {
		switch (fieldId) {
		case X_ID:
			return x;
		case Y_ID:
			return y;
		case Z_ID:
			return z;
		default:
			return 0;
		}
	}
	
	public void set(int fieldId, float value){
		switch (fieldId) {
		case X_ID:
			x = value;
			break;
		case Y_ID:
			y = value;
			break;
		case Z_ID:
			z = value;
			break;
		}
		return;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
