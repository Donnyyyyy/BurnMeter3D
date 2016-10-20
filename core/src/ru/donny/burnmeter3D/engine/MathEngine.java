package ru.donny.burnmeter3D.engine;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

import ru.donny.burnmeter3D.engine.objects.Matrix;
import ru.donny.burnmeter3D.engine.objects.geometry.Line;
import ru.donny.burnmeter3D.engine.objects.geometry.Plane;
import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;
import ru.donny.burnmeter3D.engine.objects.geometry.objects2d.Point2D;
import ru.donny.burnmeter3D.engine.objects.geometry.point.ModelIntersection;
import ru.donny.burnmeter3D.engine.objects.geometry.point.Point3D;
import ru.donny.burnmeter3D.engine.objects.model.ModelAbstraction;
import ru.donny.burnmeter3D.objects.Pair;
import ru.donny.burnmeter3D.objects.Touching;

public class MathEngine {

    /**
     * Z = 0 on the screen and nearest z in the world.
     */
    public static final int Z_MODE_NEAR = 0;

    /**
     * Z > 0 on the screen and far from camera in the world.
     */
    public static final int Z_MODE_FAR = 1;
    /**
     * accuracy = 3
     */
    public static final int ACCURACY_LOW = 3;
    /**
     * accuracy = 6
     */
    public static final int ACCURACY_MEDIUM = 4;
    /**
     * accuracy = 9
     */
    public static final int ACCURACY_HIGH = 5;
    public static final int COMPARE_EQUAL = 0;
    public static final int COMPARE_GREATER = 1;
    public static final int COMPARE_LESS = -1;
    /**
     * number of characters after '.' that will be counted.
     */
    private int accuracy;
    private HashSet<Triangle> extremeTriangles = new HashSet<Triangle>();

    public MathEngine() {
    }

    public static ArrayList<Pair<Integer, Float>> computeRanges(Point2D relatedPoint, ArrayList<Triangle> trianglesList,
                                                                Camera projector) {
        ArrayList<Point2D> projectedPointList = new ArrayList<Point2D>();

        for (Triangle i : trianglesList) {
            Vector3 projected = projector.project(i.getCenter());
            projectedPointList.add(new Point2D(projected.x, projected.y));
        }

        return MathEngine.computeRanges(relatedPoint, projectedPointList);
    }

    /**
     * @param relatedPoint {@link Point2D} relatively to it will be computed ranges.
     * @param pointList    list to compute ranges for.
     * @return square ranges between each {@link Point2D} in list and
     * relatedPoint. Returned list is ascending sorted. Each member of
     * list has square range and index of point in the pointList array.
     */
    public static ArrayList<Pair<Integer, Float>> computeRanges(Point2D relatedPoint, ArrayList<Point2D> pointList) {
        ArrayList<Pair<Integer, Float>> computions = new ArrayList<Pair<Integer, Float>>();

        int index = 0;
        for (Point2D i : pointList) {
            computions.add(new Pair<Integer, Float>(index,
                    Math.abs(relatedPoint.getX() - i.getX()) + Math.abs(relatedPoint.getY() - i.getY())));
            index++;
        }

        quickSort(computions, new Comparator<Pair<Integer, Float>>() {
            @Override
            public int compare(Pair<Integer, Float> arg0, Pair<Integer, Float> arg1) {
                return (int) Math.signum(arg0.getB() - arg1.getB());
            }
        });

        return computions;
    }

    public static void quickSort(ArrayList<Pair<Integer, Float>> array, Comparator<Pair<Integer, Float>> comparator) {
        int startIndex = 0;
        int endIndex = array.size() - 1;
        doSort(array, comparator, startIndex, endIndex);
    }

    private static void doSort(ArrayList<Pair<Integer, Float>> array, Comparator<Pair<Integer, Float>> comparator,
                               int start, int end) {
        if (start >= end)
            return;
        int i = start, j = end;
        int cur = i - (i - j) / 2;
        while (i < j) {
            while (i < cur && (comparator.compare(array.get(i), array.get(cur)) <= 0)) {
                i++;
            }
            while (j > cur && (comparator.compare(array.get(cur), array.get(j)) <= 0)) {
                j--;
            }
            if (i < j) {
                Pair<Integer, Float> temp = array.get(i);
                array.set(i, array.get(j));
                array.set(j, temp);
                if (i == cur)
                    cur = j;
                else if (j == cur)
                    cur = i;
            }
        }
        doSort(array, comparator, start, cur);
        doSort(array, comparator, cur + 1, end);
    }

    /**
     * Calculates intersections between 3D model and {@link Touching} at the
     * screen.
     *
     * @param model    {@link ArrayList<Triangle>} to intersect with.
     * @param touching {@link Touching} to intersect with.
     * @param camera   {@link Camera} to unproject screen coordinates.
     * @return {@link ArrayList} that contains intersection points.
     */
    public static ArrayList<ModelIntersection> getIntersection(ArrayList<Triangle> model, Touching touching,
                                                               Camera camera, HashSet<Integer> innerTrianglesId) {
        ArrayList<ModelIntersection> modelItersections = new ArrayList<ModelIntersection>();

        ModelIntersection intersection;
        ArrayList<ModelIntersection> lineIntersections;

        for (Line j : touching.getWorldPickRays()) {
            lineIntersections = new ArrayList<ModelIntersection>();
            for (Triangle i : model)
                if ((intersection = getIntersectionWithTriangle(i, j)) != null) {
                    lineIntersections.add(intersection);
                }
            if ((intersection = getClosest(lineIntersections, camera.position)) != null)
                modelItersections.add(intersection);
        }

        return modelItersections;
    }

//    @Deprecated
//    public static ModelIntersection getIntersection(ArrayList<Triangle> model, Point2D screenPoint, Camera camera) {
//        ModelIntersection intersection = null;
//        ArrayList<ModelIntersection> lineIntersections;
//
//        Line pickRay = createPickRay(screenPoint, camera);
//        lineIntersections = new ArrayList<ModelIntersection>();
//        for (Triangle i : model)
//            if ((intersection = getIntersectionWithTriangle(i, pickRay)) != null) {
//                lineIntersections.add(intersection);
//            }
//        if ((intersection = getClosest(lineIntersections, camera.position)) != null)
//            return intersection;
//        else
//            return null;
//    }

    public static ModelIntersection getIntersection(ModelAbstraction modelAbstraction, Point2D screenPoint, Camera camera) {
        Ray screenPointRay = camera.getPickRay(screenPoint.getX(), screenPoint.getY());

        ModelIntersection modelIntersection = new ModelIntersection();
        float minDist2 = Float.MAX_VALUE;
        boolean hasHit = false;

        Vector3 intersection = new Vector3();
        for (Triangle modelTriangle : modelAbstraction) {

            if (Intersector.intersectRayBoundsFast(screenPointRay, modelTriangle.getBoundingBox())) {
                boolean hasIntersection = Intersector.intersectRayTriangle(screenPointRay, modelTriangle.getPointA(), modelTriangle.getPointB(),
                        modelTriangle.getPointC(), intersection);

                if (hasIntersection) {
                    float dist2 = screenPointRay.origin.dst2(intersection);

                    if(dist2 < minDist2){
                        minDist2 = dist2;
                        modelIntersection.set(intersection);
                        modelIntersection.setTriangle(modelTriangle);
                        hasHit = true;
                    }
                }

            }
        }
        return (hasHit) ? modelIntersection : null;
    }

    public static Line createPickRay(Point2D screenPoint, Camera camera) {
        Point3D linePoin2 = new Point3D(screenPoint.getX(), screenPoint.getY(), Z_MODE_FAR);
        Point3D linePoin1 = new Point3D(screenPoint.getX(), screenPoint.getY(), Z_MODE_NEAR);

        return new Line(camera.unproject(linePoin1), camera.unproject(linePoin2));
    }

    public static HashSet<Integer> getInnerTrianglesId(ArrayList<Triangle> abstraction, BoundingBox boundingBox) {
        HashSet<Integer> innerIds = new HashSet<Integer>();
        for (Triangle i : abstraction)
            if (boundingBox.contains(i.getBoundingBox()))
                innerIds.add(i.getId());

        return innerIds;
    }

    private static ModelIntersection getClosest(ArrayList<ModelIntersection> lineIntersections, Vector3 cameraPos) {
        if (lineIntersections.size() < 1)
            return null;

        float minLen = calculateLength(lineIntersections.get(0), cameraPos);
        int minLenInd = 0;

        float len;
        for (int i = 1; i < lineIntersections.size(); i++)
            if ((len = calculateLength(lineIntersections.get(i), cameraPos)) <= minLen) {
                minLenInd = i;
                minLen = len;
            }

        return lineIntersections.get(minLenInd);
    }

    /**
     * @param triangle triangle to intersect with
     * @return {@link Vector3} of the intersection. <code>null</code> if there
     * is no intersections or if intersection is outside of
     * {@link Triangle}'s bounds.
     */
    @Deprecated
    public static ModelIntersection getIntersectionWithTriangle(Triangle triangle, Line line) {
        Vector3 intersection = getIntersectionWithPlane(triangle, line);
        if (intersection == null)
            return null;

        if (triangle.isBelong(intersection))
            return new ModelIntersection(intersection, triangle);
        else
            return null;
    }

    /**
     * @param plane Plane to intersect with
     * @return {@link Vector3} of the intersection. <code>null</code> if there
     * is no intersections.
     */
    @SuppressWarnings("deprecation")
    public static Vector3 getIntersectionWithPlane(Plane plane, Line line) {
        float[][] planeSystemArr = {line.getPlane1().toArray(), line.getPlane2().toArray(), plane.toArray()};
        Matrix planeSystem = new Matrix(planeSystemArr);
        Vector3 intersection = planeSystem.solvePlaneSystem();
        if (intersection == null) {
            return null;
        }

        return intersection;
    }

    public static ArrayList<ModelIntersection> getIntersectedPlanesPoints(ArrayList<Triangle> model, Line line) {
        ArrayList<ModelIntersection> intersections = new ArrayList<ModelIntersection>();

        Matrix planeSystem = new Matrix(3, 4);
        for (Triangle i : model) {
            planeSystem.fillSystem(line, i);
            Vector3 intersection = planeSystem.solvePlaneSystem();

            if (intersection == null) {
                continue;
            }
            if (i.isBelong(intersection)) {
                intersections.add(new ModelIntersection(intersection, i));
            }
        }

        return intersections;
    }

    public static float calculatePolygonSquare(ArrayList<Vector3> vertices) throws LowAccuracyException {
        if (vertices.size() < 3)
            return 0.0f;

        float square = 0;

        Vector3 basePoint = vertices.get(0);
        for (int i = 1; i < vertices.size() - 1; i++) {
            float a = calculateLength(basePoint, vertices.get(i));
            float b = calculateLength(basePoint, vertices.get(i + 1));
            float c = calculateLength(vertices.get(i + 1), vertices.get(i));
            square += calculateTriangleSquare(a, b, c);
        }
        return square;
    }

    public static float calculateTriangleSquare(Triangle triangle) throws LowAccuracyException {
        return calculateTriangleSquare(calculateLength(triangle.getPointA(), triangle.getPointB()),
                calculateLength(triangle.getPointB(), triangle.getPointC()),
                calculateLength(triangle.getPointA(), triangle.getPointC()));
    }

    public static float calculateTriangleSquare(Vector3 a, Vector3 b, Vector3 c) throws LowAccuracyException {
        float abLength = calculateLength(a, b);
        float bcLength = calculateLength(b, c);
        float acLength = calculateLength(a, c);

        return calculateTriangleSquare(abLength, bcLength, acLength);
    }

    public static float calculateTriangleSquare(float a, float b, float c) throws LowAccuracyException {
        float p = (a + b + c) / 2.0f;
        float square = (float) Math.sqrt(p * (p - a) * (p - b) * (p - c));

        if (a != 0f && b != 0 && c != 0 && square == 0)
            throw new LowAccuracyException("Large difference bewteen numbers, can't calculate correct result");
        return square;
    }

    /**
     * @return range between two points or -1 if one (or both) point is
     * <code>null</code>.
     */
    public static float calculateLength(Vector3 point1, Vector3 point2) {
        if ((point1 == null) || (point2 == null))
            return -1;
        else {
            float length = (float) Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2)
                    + Math.pow(point1.z - point2.z, 2));
            return length;
        }
    }

    /**
     * Compares a and b with an accuracy.
     *
     * @return constants: {@link MathEngine}.COMPARE_GREATER in case when a > b
     * {@link MathEngine}.COMPARE_LESS in case when a < b
     * {@link MathEngine}.COMPARE_EQUAL in case when a = b.
     */
    public static int compare(float a, float b, int accuracy) {
        if (a == b)
            return COMPARE_EQUAL;
        else {
            int maxOffset = (int) Math.pow(10, accuracy);
            if (Math.abs(a - b) * maxOffset <= 1)
                return COMPARE_EQUAL;
            else {
                if (a < b)
                    return COMPARE_LESS;
                else
                    return COMPARE_GREATER;
            }
        }
    }

    /**
     * @param a        see compare(float a, float b, float accuracy)
     * @param b        see compare(float a, float b, float accuracy)
     * @param accuracy see compare(float a, float b, float accuracy)
     * @return COMPARE_EQUAL if a = b and another value in another case.
     */
    public static int compare(Vector3 a, Vector3 b, int accuracy) {
        return Math.abs(compare(a.x, b.x, accuracy)) + Math.abs(compare(a.y, b.y, accuracy))
                + Math.abs(compare(a.z, b.z, accuracy));
    }

    public static BoundingBox constructBoundingBox(ArrayList<Triangle> triangles) {
        BoundingBox boundingBox = new BoundingBox();

        for (Triangle i : triangles)
            boundingBox.ext(i.getBoundingBox());

        return boundingBox;
    }

    // TODO add extreme triangles algorithm
    public ArrayList<Triangle> getIntersectedTriangles(ArrayList<Triangle> model, Touching touching, Camera camera) {
        ArrayList<Triangle> triangles = new ArrayList<Triangle>();

        ModelIntersection intersection;
        ArrayList<ModelIntersection> lineIntersections;

        ArrayList<Line> touchingLines = touching.getWorldPickRays();
        for (Line i : touchingLines) {
            lineIntersections = getIntersectedPlanesPoints(model, i);

            if ((intersection = getClosest(lineIntersections, camera.position)) != null) {
                triangles.add(intersection.getTriangle());
            }
        }

        return triangles;
    }

    public ArrayList<Triangle> getIntersectedTriangles(ModelAbstraction model, ArrayList<Point2D> screenPoints, Camera camera) {
        ArrayList<Triangle> triangles = new ArrayList<Triangle>();

        int index = 0;
        for(Point2D screenPoint : screenPoints){
            ModelIntersection intersection = getIntersection(model, screenPoint, camera);

            if(intersection != null)
                triangles.add(intersection.getTriangle());

            index++;
        }

        return triangles;
    }

    public int compare(float a, float b) {
        return compare(a, b, accuracy);
    }

    public int compare(Vector3 a, Vector3 b) {
        return compare(a, b, accuracy);
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public HashSet<Triangle> getExtremeTriangles() {
        return extremeTriangles;
    }

    public void setExtremeTriangles(HashSet<Triangle> extremeTriangles) {
        this.extremeTriangles = extremeTriangles;
    }

    public static class LowAccuracyException extends Exception {

        private static final long serialVersionUID = -8351450635850885416L;

        public LowAccuracyException() {
            super();
        }

        public LowAccuracyException(String msg) {
            super(msg);
        }
    }

    public class ExtremeTriangles extends Renderable {
        private Color color;

        public ExtremeTriangles(Color color) {
            this.color = color;
            createMesh();
        }

        private void createMesh() {
            MeshBuilder meshBuilder = new MeshBuilder();
            material = new Material(ColorAttribute.createDiffuse(color));

            meshBuilder.setColor(color);
            meshBuilder.begin(Usage.Position, GL20.GL_TRIANGLES);
            for (Triangle i : MathEngine.this.extremeTriangles) {
                meshBuilder.triangle(i.getPointA(), i.getPointB(), i.getPointC());
            }

            meshPart.primitiveType = GL20.GL_TRIANGLES;
            meshPart.mesh = meshBuilder.end();
            meshPart.size = meshPart.mesh.getNumIndices();
            meshPart.offset = 0;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }
    }

}
