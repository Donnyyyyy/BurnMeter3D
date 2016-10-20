package ru.donny.burnmeter3D.graphics.renderable;

import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import ru.donny.burnmeter3D.data.Burn.BurnType;
import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;

public abstract class BurnModelBuilder {

	public static BurnInstance build(Collection<Triangle> burnTriangles, Color color) {
		return build(burnTriangles, BurnType.createFromColor(color));
	}
	
	public static BurnInstance build(Collection<Triangle> burnTriangles, BurnType burnType) {
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();

		Color burnColor = burnType.getColor();
		MeshPartBuilder meshBuilder = modelBuilder.part("burn", GL20.GL_TRIANGLES,
				Usage.Normal | Usage.Position | Usage.ColorUnpacked, createMatrial(burnColor));

		VertexInfo p1 = new VertexInfo(), p2 = new VertexInfo(), p3 = new VertexInfo();

		int vertices = 0;
		for (Triangle i : burnTriangles) {
			if (vertices >= Short.MAX_VALUE - 3) {
				meshBuilder = modelBuilder.part("extraPart", GL20.GL_TRIANGLES,
						Usage.Normal | Usage.Position | Usage.ColorUnpacked, createMatrial(burnColor));
				vertices = 0;
			}

			Vector3 normal = i.getNormal();
			meshBuilder.triangle(p1.set(i.getPointA(), normal, null, null), p3.set(i.getPointB(), normal, null, null),
					p2.set(i.getPointC(), normal, null, null));
			vertices += 3;
		}

		return new BurnInstance(modelBuilder.end(), new ArrayList<Triangle>(burnTriangles),
				burnType);
	}

	private static Material createMatrial(Color color) {
		Material material = new Material();
		material.set(new ColorAttribute(ColorAttribute.Diffuse, color));
		// material.set(new ColorAttribute(ColorAttribute.Specular, new
		// Color(1f, 1f, 1f, 0f)));
		material.set(new ColorAttribute(ColorAttribute.Ambient, new Color(0.1f, 0.1f, 0.1f, 1f)));
		material.set(new ColorAttribute(ColorAttribute.Emissive, color));
		material.set(new ColorAttribute(ColorAttribute.Reflection, new Color(0.3f, 0.3f, 0.3f, 1f)));
		material.set(new FloatAttribute(FloatAttribute.Shininess));

		return material;
	}
}
