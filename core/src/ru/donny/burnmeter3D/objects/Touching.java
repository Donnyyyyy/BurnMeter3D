package ru.donny.burnmeter3D.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.collision.BoundingBox;

import java.util.ArrayList;

import ru.donny.burnmeter3D.engine.MathEngine;
import ru.donny.burnmeter3D.engine.objects.geometry.Line;
import ru.donny.burnmeter3D.engine.objects.geometry.objects2d.Point2D;
import ru.donny.burnmeter3D.engine.objects.geometry.point.Point3D;

public class Touching {

    /**
     * Z = 0 on the screen and nearest z in the world.
     */
    public static final int Z_MODE_NEAR = 0;
    /**
     * Z > 0 on the screen and far from camera in the world.
     */
    public static final int Z_MODE_FAR = 1;
    public static final int STATE_NOT_STARTED = 0;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_FINISHED = 2;
    private Color color;
    /**
     * Id of this touching.
     */
    private int pointer;
    /**
     * Start of {@link Touching}
     */
    private Point2D start;
    /**
     * Point of the movement of the {@link Touching}
     */
    private ArrayList<Point2D> dragPoints;
    /**
     * End of the {@link Touching}
     */
    private Point2D end;
    private Camera camera;
    /**
     * Current state of {@link Touching}, use constants of this class to access
     * it.
     */
    private int state;

    /**
     * Button of touching. Use {@link Buttons} to access.
     */
    private int button;


    public Touching(Point2D start, int pointer, int button, Camera camera) {
        super();
        state = STATE_DRAGGING;
        this.start = start;
        this.pointer = pointer;
        this.button = button;
        dragPoints = new ArrayList<Point2D>();
        this.camera = camera;
    }

    public void dragInto(Point2D into, int pointer) {
        if (this.pointer != pointer)
            return;

        dragPoints.add(into);
    }

    public void touchUp(Point2D end) {
        this.end = end;
        state = STATE_FINISHED;
    }

    public ArrayList<Line> getWorldPickRays() {
        ArrayList<Line> lines = new ArrayList<Line>();

        for (Point2D i : getTouchPoints()) {
            lines.add(MathEngine.createPickRay(i, camera));
        }

        return lines;
    }

    /**
     * returns all points of {@link Touching} including start and end.
     */
    private ArrayList<Point2D> getTouchPoints() {
        ArrayList<Point2D> touchPoints = new ArrayList<Point2D>();

        touchPoints.add(start);

        for (Point2D i : dragPoints)
            touchPoints.add(i);

        touchPoints.add(end);

        return touchPoints;
    }

    /**
     * @return whether the point belongs to the {@link Touching}
     */
    public boolean isInside(Point2D verifiable, float minChance) {
        return true;
    }

    public BoundingBox getBoundingBox() {
        ArrayList<Point2D> screenPoints = new ArrayList<Point2D>();

        if (start != null)
            screenPoints.add(start);
        if (end != null)
            screenPoints.add(end);

        if (screenPoints.size() > 0) {
            screenPoints.addAll(dragPoints);
            Point3D min = new Point3D(screenPoints.get(0).getX(), screenPoints.get(0).getY(), Z_MODE_NEAR);
            Point3D max = new Point3D(screenPoints.get(0).getX(), screenPoints.get(0).getY(), Z_MODE_FAR);

            for (Point2D i : screenPoints) {
                // Compare coordinates except z
                for (int j = 0; j < Point2D.COORDINATES_NUMBER; j++) {
                    if (min.get(j) > i.get(j))
                        min.set(j, i.get(j));

                    if (max.get(j) < i.get(j))
                        max.set(j, i.get(j));
                }

            }
            BoundingBox boundingBox = new BoundingBox(camera.unproject(new Point3D(min)),
                    camera.unproject(new Point3D(max)));
            return boundingBox;
        } else
            return new BoundingBox();
    }

    public ArrayList<Point2D> getAllPoints() {
        ArrayList<Point2D> screenPoints = new ArrayList<Point2D>();

        screenPoints.addAll(dragPoints);
        screenPoints.add(start);
        screenPoints.add(end);

        return screenPoints;
    }

    public ArrayList<Point3D> getWorldPoints() {
        ArrayList<Point3D> worldPoints = new ArrayList<Point3D>();

        if (start != null) {
            worldPoints.add(new Point3D(camera.unproject(new Point3D(start.getX(), start.getY(), Z_MODE_NEAR))));
            worldPoints.add(new Point3D(camera.unproject(new Point3D(start.getX(), start.getY(), Z_MODE_FAR))));
        }

        for (Point2D i : dragPoints) {
            worldPoints.add(new Point3D(camera.unproject(new Point3D(i.getX(), i.getY(), Z_MODE_NEAR))));
            worldPoints.add(new Point3D(camera.unproject(new Point3D(i.getX(), i.getY(), Z_MODE_FAR))));
        }

        if (end != null) {
            worldPoints.add(new Point3D(camera.unproject(new Point3D(end.getX(), end.getY(), Z_MODE_NEAR))));
            worldPoints.add(new Point3D(camera.unproject(new Point3D(end.getX(), end.getY(), Z_MODE_FAR))));
        }

        return worldPoints;
    }

    public String toString() {
        return "Touching [Pointer =" + pointer + ", Start =" + start + ",  Drag Points Size =" + dragPoints.size()
                + ", End =" + end + "]";
    }

    public Point2D getStart() {
        return start;
    }

    public void setStart(Point2D start) {
        this.start = start;
    }

    public ArrayList<Point2D> getDragPoint() {
        return dragPoints;
    }

    public void setDragPoint(ArrayList<Point2D> dragPoint) {
        this.dragPoints = dragPoint;
    }

    public Point2D getEnd() {
        return end;
    }

    public void setEnd(Point2D end) {
        this.end = end;
    }

    public int getPointer() {
        return pointer;
    }

    public void setPointer(int pointer) {
        this.pointer = pointer;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getButton() {
        return button;
    }

    public void setButton(int button) {
        this.button = button;
    }

    public void render(ShapeRenderer renderer) {
        if (dragPoints.size() == 0)
            return;

        renderer.setColor(color);

        Point2D start, end;
        for (int i = -1; i < dragPoints.size(); i++) {
            if (i == -1) {
                start = this.start;
                end = dragPoints.get(0);
            } else if (i == dragPoints.size() - 1) {
                start = dragPoints.get(i);
                end = this.start;
            } else {
                start = dragPoints.get(i);
                end = dragPoints.get(i + 1);
            }

            renderer.line(start.getX(), Gdx.graphics.getHeight() - start.getY(), end.getX(),
                    Gdx.graphics.getHeight() - end.getY());
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
