package ru.donny.burnmeter3D.engine.objects.geometry;

import com.badlogic.gdx.math.Vector3;

import ru.donny.burnmeter3D.engine.MathEngine;
import ru.donny.burnmeter3D.engine.objects.Matrix;

public class Plane {
	private float a;
	private float b;
	private float c;
	private float d;

	private int id;

	public Plane(float a, float b, float c, float d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	public Plane(float a, float b, float c, float d, int id) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.id = id;
	}

	public Plane(Vector3 point1, Vector3 point2, Vector3 point3) {
		this(point1, point2, point3, 0);
	}

	public Plane(Vector3 point1, Vector3 point2, Vector3 point3, int id) {
		this.id = id;
		float[][] matrixAarr = { //
				{ 1, point1.y, point1.z }, // 1 y1 z1
				{ 1, point2.y, point2.z }, // 1 y2 z2
				{ 1, point3.y, point3.z } }; // 1 y3 z3
		Matrix matrixA = new Matrix(matrixAarr);
		setA(matrixA.countDeterminant());

		float[][] matrixBarr = { //
				{ point1.x, 1, point1.z }, // x1 1 z1
				{ point2.x, 1, point2.z }, // x2 1 z2
				{ point3.x, 1, point3.z } }; // x3 1 z3
		Matrix matrixB = new Matrix(matrixBarr);
		setB(matrixB.countDeterminant());

		float[][] matrixCarr = { //
				{ point1.x, point1.y, 1 }, // x1 y1 1
				{ point2.x, point2.y, 1 }, // x2 y2 1
				{ point3.x, point3.y, 1 } }; // x3 y3 1
		Matrix matrixC = new Matrix(matrixCarr);
		setC(matrixC.countDeterminant());

		float[][] matrixDarr = { { point1.x, point1.y, point1.z }, { point2.x, point2.y, point2.z },
				{ point3.x, point3.y, point3.z } };
		Matrix matrixD = new Matrix(matrixDarr);
		setD(matrixD.countDeterminant() * -1.0f);
	}

	public boolean isPartOf(Vector3 p) {
		float equation = p.x * getA() + p.y * getB() + p.z * getC() + getD();
		return equation == 0;
	}

	@Deprecated
	public float[] toArray() {
		float[] arr = { a, b, c, d };
		return arr;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass().equals(getClass())) {
			Plane verifiable = (Plane) obj;
			MathEngine comparator = new MathEngine();
			comparator.setAccuracy(MathEngine.ACCURACY_HIGH);
			
			return (Math.abs(comparator.compare(a, verifiable.getA()))
					+ Math.abs(comparator.compare(b, verifiable.getB()))
					+ Math.abs(comparator.compare(c, verifiable.getC()))
					+ Math.abs(comparator.compare(d, verifiable.getD()))) == MathEngine.COMPARE_EQUAL;
		} else
			return false;
	}

	public float getD() {
		return d;
	}

	public void setD(float d) {
		this.d = d;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
