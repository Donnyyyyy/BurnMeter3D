package ru.donny.burnmeter3D.engine.objects.geometry.objects2d;

public class Point2D {

	public static final int COORDINATES_NUMBER = 2;
	public static final int X = 0;
	public static final int Y = 1;
	
	private float x;
	private float y;

	public Point2D(float x, float y) {
		super();
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public float get(int valueId){
		switch (valueId) {
		case 0:
			return getX();
		case 1:
			return getY();
		default:
			return 0f;
		}
	}
	
	public void set(int valueId, float value){
		switch (valueId) {
		case 0:
			setX(value);
			break;
		case 1:
			setY(value);
			break;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if(obj.getClass() != this.getClass()) return false;
		
		return ((Float.floatToIntBits(((Point2D)obj).getX()) == Float.floatToIntBits(x))
				&& (Float.floatToIntBits(((Point2D)obj).getY()) == Float.floatToIntBits(y)));
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
