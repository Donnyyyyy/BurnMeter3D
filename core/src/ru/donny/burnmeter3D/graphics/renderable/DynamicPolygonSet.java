package ru.donny.burnmeter3D.graphics.renderable;

import java.util.Collection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Vector3;

import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;

public class DynamicPolygonSet extends Renderable {

	private Color color;
	private Collection<Triangle> renderingPolygons;

	public DynamicPolygonSet(Color color, Collection<Triangle> triangles) {
		this.setColor(color);
		this.renderingPolygons = triangles;

		if (triangles != null)
			build();
	}

	private void build() {
		MeshBuilder meshBuilder = new MeshBuilder();
		material = new Material(ColorAttribute.createDiffuse(getColor()));

		meshBuilder.setColor(getColor());
		meshBuilder.begin(Usage.Normal | Usage.Position | Usage.ColorUnpacked, GL20.GL_TRIANGLES);

		VertexInfo v1 = new VertexInfo(), v2 = new VertexInfo(), v3 = new VertexInfo();
		for (Triangle i : renderingPolygons) {
			Vector3 normal = i.getNormal();
			meshBuilder.triangle(v1.set(i.getPointA(), normal, null, null), v2.set(i.getPointB(), normal, null, null),
					v3.set(i.getPointC(), normal, null, null));
		}

		meshPart.mesh = meshBuilder.end();

		meshPart.primitiveType = GL20.GL_TRIANGLES;
		meshPart.size = meshPart.mesh.getNumIndices();
		meshPart.offset = 0;
	}

	public void update(Collection<Triangle> triangles) {
		renderingPolygons = triangles;
		build();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
