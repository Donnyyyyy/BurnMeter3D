package ru.donny.burnmeter3D.engine.objects.geometry.objects2d;

public class Matrix2x2 {

	public static final int WIDTH = 2;
	public static final int HEIGTH = 2;
	private Float determinant;

	private float[][] values;

	public Matrix2x2(float[][] values) throws Exception {
		if ((values.length == HEIGTH) && (values[0].length == WIDTH))
			this.values = values;
		else
			throw new Exception("The values array of matrix must have size 2x2");

		determinant = null;
	}

	public Point2D solve(float result1, float result2) throws Exception {
		calculateDeterminant();

		Matrix2x2 xMatrix = new Matrix2x2(new float[][] { { result1, values[0][1] }, { result2, values[1][1] } });
		float x = xMatrix.getDeterminant() / determinant;

		Matrix2x2 yMatrix = new Matrix2x2(new float[][] { { values[0][0], result1 }, { values[1][0], result2 } });
		float y = yMatrix.getDeterminant() / determinant;

		return new Point2D(x, y);
	}

	private void calculateDeterminant() {
		determinant = values[0][0] * values[1][1] - values[1][0] * values[0][1];
	}

	public float getDeterminant() {
		if (determinant == null)
			calculateDeterminant();

		return determinant;
	}
}