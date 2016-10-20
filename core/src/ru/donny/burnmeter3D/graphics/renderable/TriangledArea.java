package ru.donny.burnmeter3D.graphics.renderable;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;

import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;

public class TriangledArea extends Renderable {

	private ArrayList<Triangle> renderedTriangles;
	private Color color;

	public TriangledArea() {
		renderedTriangles = new ArrayList<Triangle>();
		color = new Color();
	}

	public TriangledArea(ArrayList<Triangle> triangles) {
		this(triangles, Color.BLACK);
	}

	public TriangledArea(ArrayList<Triangle> triangles, Color color) {
		super();
		renderedTriangles = triangles;
		this.color = color;
		createMesh();
	}

	private void createMesh() {
		MeshBuilder meshBuilder = new MeshBuilder();
		material = new Material(ColorAttribute.createDiffuse(color));

		meshBuilder.setColor(color);
		meshBuilder.begin(Usage.Position, GL20.GL_TRIANGLES);
		int verticesCount = 0;
		int id = 0;
		System.out.println(renderedTriangles.size() + " - size of rendTrngls");
		for (Triangle i : renderedTriangles) {
			if (verticesCount >= Short.MAX_VALUE - 100) {
				meshBuilder.part(id + "", GL20.GL_TRIANGLES);
				id++;
				verticesCount = 0;
				System.out.println("Created part");
			}

			meshBuilder.triangle(i.getPointA(), i.getPointB(), i.getPointC());
			verticesCount += 3;

		}
		meshPart.primitiveType = GL20.GL_TRIANGLES;
		meshPart.mesh = meshBuilder.end();
		meshPart.size = meshPart.mesh.getNumIndices();
		meshPart.offset = 0;
	}

	public void build() {
		if ((renderedTriangles == null) || (renderedTriangles.size() == 0))
			return;

		createMesh();
	}

	public Renderable getTrianglesBorders(Color color) {
		MeshBuilder meshBuilder = new MeshBuilder();
		meshBuilder.begin(Usage.Position, GL20.GL_LINES);

		for (Triangle i : renderedTriangles) {
			meshBuilder.line(i.getPointA(), i.getPointB());
			meshBuilder.line(i.getPointA(), i.getPointC());
			meshBuilder.line(i.getPointB(), i.getPointC());
		}

		Renderable trianglesBorders = new Renderable();
		trianglesBorders.meshPart.mesh = meshBuilder.end();
		trianglesBorders.meshPart.primitiveType = GL20.GL_LINES;
		trianglesBorders.material = new Material(ColorAttribute.createDiffuse(color));
		trianglesBorders.meshPart.size = trianglesBorders.meshPart.mesh.getNumIndices();
		trianglesBorders.meshPart.offset = 0;

		return trianglesBorders;
	}

	public ArrayList<Triangle> getRenderedTriangles() {
		return renderedTriangles;
	}

	public void setRenderedTriangles(ArrayList<Triangle> renderedTriangles) {
		this.renderedTriangles = renderedTriangles;
		createMesh();
	}

	public Color getColor() {
		return color;
	}

	/**
	 * Must be called before setRenderedTriangles(ArrayList
	 * <Triangle> renderedTriangles)
	 * 
	 * @param color
	 *            color to set
	 */
	public void setColor(Color color) {
		this.color = color;
		this.color.a = 0.5f;
	}
}
