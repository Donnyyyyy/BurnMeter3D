package ru.donny.burnmeter3D.graphics.renderable;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public abstract class DebugModelBuilder {

	private static final float DEFAULT_SIDE = 0.5f;
	private static final Color DEFAULT_COLOR = Color.RED;

	public static Model build(ArrayList<Vector3> sphereCentres) {
		return build(sphereCentres, DEFAULT_SIDE, DEFAULT_COLOR);
	}

	public static Model build(ArrayList<? extends Vector3> sphereCentres, float boxSide) {
		return build(sphereCentres, boxSide, DEFAULT_COLOR);
	}

	public static Model build(ArrayList<? extends Vector3> sphereCentres, Color color) {
		return build(sphereCentres, DEFAULT_SIDE, color);
	}

	public static Model build(Vector3 boxCenter) {
		ArrayList<Vector3> boxCentres = new ArrayList<Vector3>();
		boxCentres.add(boxCenter);
		return build(boxCentres, DEFAULT_SIDE, DEFAULT_COLOR);
	}

	public static Model build(Vector3 boxCenter, float boxSide, Color color) {
		ArrayList<Vector3> boxCentres = new ArrayList<Vector3>();
		boxCentres.add(boxCenter);
		return build(boxCentres, boxSide, color);
	}

	public static Model build(ArrayList<? extends Vector3> boxCentres, float boxSide, Color color) {
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();

		MeshPartBuilder meshBuilder = modelBuilder.part("main", GL20.GL_TRIANGLES,
				Usage.ColorPacked | Usage.Normal | Usage.Position, new Material());
		meshBuilder.setColor(color);

		int vertexInMeshCount = 0;
		for (Vector3 i : boxCentres) {
			if (vertexInMeshCount >= Short.MAX_VALUE - (6 * 2 * 3)) {
				meshBuilder = modelBuilder.part("addition", GL20.GL_TRIANGLES,
						Usage.ColorPacked | Usage.Normal | Usage.Position, new Material());
				meshBuilder.setColor(color);

				vertexInMeshCount = 0;
			}
			meshBuilder.box(i.x, i.y, i.z, boxSide, boxSide, boxSide);
			vertexInMeshCount += 6 * 2 * 3; // number of the vertices in the box figure
		}

		return modelBuilder.end();
	}
}
