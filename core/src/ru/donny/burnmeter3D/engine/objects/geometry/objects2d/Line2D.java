package ru.donny.burnmeter3D.engine.objects.geometry.objects2d;

public class Line2D {

	private float a;
	private float b;
	private float c;

	public Line2D(Point2D point1, Point2D point2) {
		a = point1.getY() - point2.getY();
		b = point2.getX() - point1.getX();
		c = point1.getX() * point2.getY() - point2.getX() * point1.getY();
	}

	public Point2D intersect(Line2D line) throws Exception {
		Matrix2x2 pointMatrix = new Matrix2x2(new float[][] { { a, b }, { line.a, line.b } });
		return pointMatrix.solve(c * -1f, line.c * -1f);
	}

	public int isBelongs(Point2D point) {
		return (int) Math.signum(point.getX() * a + point.getY() * b + c);
	}

	public float getA() {
		return a;
	}

	public void setA(float a) {
		this.a = a;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	public float getC() {
		return c;
	}

	public void setC(float c) {
		this.c = c;
	}
}