package ru.donny.burnmeter3D.engine.objects.geometry.point;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;

public class ModelIntersection extends Point3D {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Triangle triangle;

    public ModelIntersection() {
        super();
    }

    public ModelIntersection(Point3D parent) {
        super(parent);
    }

    public ModelIntersection(Vector3 vector, Triangle triangle) {
        super(vector);
        this.triangle = triangle;
    }

    public ModelIntersection(Vector2 vector, float z, Triangle triangle) {
        super(vector, z);
        this.triangle = triangle;
    }

    public ModelIntersection(float x, float y, float z, Triangle triangle) {
        super(x, y, z);
        this.triangle = triangle;
    }

    public Triangle getTriangle() {
        return triangle;
    }

    public void setTriangle(Triangle triangle) {
        this.triangle = triangle;
    }

}
