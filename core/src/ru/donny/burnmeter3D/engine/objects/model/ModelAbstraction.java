package ru.donny.burnmeter3D.engine.objects.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import ru.donny.burnmeter3D.controllers.Logger;
import ru.donny.burnmeter3D.data.graphstorage.ModelGraphStorage;
import ru.donny.burnmeter3D.engine.MathEngine;
import ru.donny.burnmeter3D.engine.objects.burns.BurnByContourBuilder;
import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;
import ru.donny.burnmeter3D.engine.objects.geometry.objects2d.Point2D;
import ru.donny.burnmeter3D.engine.objects.geometry.point.Point3D;
import ru.donny.burnmeter3D.engine.objects.model.graph.Graph;
import ru.donny.burnmeter3D.graphics.renderable.BurnInstance;
import ru.donny.burnmeter3D.graphics.renderable.DebugModelBuilder;
import ru.donny.burnmeter3D.renderers.Renderer;

public class ModelAbstraction extends ArrayList<Triangle> implements Renderer {

    public static final int TRIANGLE_NEIGHBOURS = 3;
    private static final long serialVersionUID = 1L;
    public final int VERTEX_SIZE;
    private final float square;
    private final String modelName;

    private ArrayList<Point3D> vertices;
    private ModelInstance verticesModel;
    private Graph graph;
    private AssetManager assetManager;
    private BoundingBox bounds;
    private ModelWrapper modelWrapper;
    private ArrayList<Point2D> screenProjections = new ArrayList<Point2D>();

    private ModelInstance keyPointsRendeerable;

    private boolean renderVertices = false;
    private boolean renderDetachedAreas = false;
    private ArrayList<BurnInstance> detachedAreas;

    public ModelAbstraction(ModelWrapper modelWrapper, AssetManager assetManager, ModelGraphStorage resource,
                            String modelName) {
        this.assetManager = assetManager;
        this.modelName = modelName;
        this.modelWrapper = modelWrapper;

        VERTEX_SIZE = modelWrapper.getVertexSize();
        Logger.printTimeMessage("init vertices list");
        initVerticesList(modelWrapper.getModel());
        Logger.printTimeMessage("calculating triangles");
        initTrianglesList(modelWrapper.getModel());
        Logger.printTimeMessage("calculating graph");
        initGraph(resource);
        Logger.printTimeMessage("creating bounding box");
        setBounds(MathEngine.constructBoundingBox(this));
        square = calculateModelSquare();
        Logger.printTimeMessage("initializing ended");

        initKeyPointsRenderable();
    }

    public void balanceSquare(BalanceTable table) {
        Map<BodyPart, BodyPartList> modelSeparation = modelWrapper.getModelSeparation();

        if (modelSeparation == null) {
            System.out.println("There is no separation for model " + modelName);
            return;
        }

        for (BodyPart i : modelSeparation.keySet()) {
            Float partCoefficient = table.getSquareCoefficient(i);
            if (partCoefficient == null)
                continue;

            Float test_square = new Float(0f);
            for (Triangle j : modelSeparation.get(i)) {
                j.setSquare((j.getSquare() / modelSeparation.get(i).getTotalSquare()) * (partCoefficient * square));
                test_square += j.getSquare();
            }
            System.out
                    .println("total square of " + i + " is " + test_square / square + "%, coeff = " + partCoefficient);
        }
    }

    private float calculateModelSquare() {
        float square = 0;

        for (Triangle i : this)
            square += i.getSquare();

        return square;
    }

    // TODO delete it. It is test method
    private void initKeyPointsRenderable() {
        ArrayList<Point3D> keyPoints = new ArrayList<Point3D>();
        for (int i = 0; i < modelWrapper.getModel().nodes.first().getChildCount(); i++) {
            BoundingBox bounds = new BoundingBox();
            modelWrapper.getModel().nodes.first().getChild(i).calculateBoundingBox(bounds);

            Point3D center = new Point3D();
            bounds.getCenter(center);
            keyPoints.add(center);
        }

        keyPointsRendeerable = new ModelInstance(DebugModelBuilder.build(keyPoints, 1f));
    }

    private void initTrianglesList(Model model) {
        int id = 0;

        int indicesOffset = 0;
        for (int k = 0; k < model.meshes.size; k++) {
            Mesh j = model.meshes.get(k);

            short[] indices = new short[j.getNumIndices()];
            j.getIndices(indices);

            for (int i = 0; i <= indices.length - Triangle.TRIANGLE_NUM_VERTICES; i += Triangle.TRIANGLE_NUM_VERTICES) {
                add(getTriangleFromArray(indices, i, id, indicesOffset));

                Triangle added = get(size() - 1);
                added.precalculateAll();
                id++;
            }

            indicesOffset += j.getNumVertices();
        }
    }

    private Triangle getTriangleFromArray(short[] indices, int startIndex, int id, int offset) {
        Point3D a = getVertices().get(indices[startIndex] + offset);
        Point3D b = getVertices().get(indices[startIndex + 1] + offset);
        Point3D c = getVertices().get(indices[startIndex + 2] + offset);

        return new Triangle(a, b, c, id);
    }

    private void initVerticesList(Model model) {
        setVertices(new ArrayList<Point3D>());

        for (int k = 0; k < model.meshes.size; k++) {
            Mesh j = model.meshes.get(k);
            float[] vertices = new float[j.getNumVertices() * VERTEX_SIZE];
            j.getVertices(vertices);

            for (int i = 0; i < vertices.length - VERTEX_SIZE + 1; i += VERTEX_SIZE)
                this.getVertices().add(getPointFromArray(vertices, i));
        }
    }

    private Point3D getPointFromArray(float[] vertices, int startIndex) {
        Point3D point = new Point3D(vertices[startIndex], vertices[startIndex + 1], vertices[startIndex + 2],
                startIndex / VERTEX_SIZE);
        return point;
    }

    private void initVerticesRendering(AssetManager assetManager) {
        verticesModel = new ModelInstance(DebugModelBuilder.build(getVertices(), Color.GREEN));
    }

    private void initGraph(ModelGraphStorage graphStorage) {
        graph = graphStorage.getGraph(getModelName(), size());

        if (getGraph() == null) {
            computeGraph();
            graphStorage.insertGraph(graph, modelName);
        }
    }

    private void computeGraph() {
        graph = new Graph();
        graph.initialize(size());

        for (int l = 0; l < size(); l++) {
            Logger.printTimeMessage("Computing vertex " + l + " / " + size());
            Triangle i = get(l);
            if (graph.get(i.getId()).getAdjacentList().size() >= TRIANGLE_NEIGHBOURS)
                continue;

            for (int k = l + 1; k < size(); k++) {
                Triangle j = get(k);
                if ((i != j) && (graph.get(j.getId()).getAdjacentList().size() < 3) && (i.hasAdjacentTwoPoints(j))) {
                    graph.get(i.getId()).getAdjacentList().add(graph.get(j.getId()));
                    graph.get(j.getId()).getAdjacentList().add(graph.get(i.getId()));

                    if (graph.get(i.getId()).getAdjacentList().size() >= 3)
                        break;
                }
            }
        }
    }

    @Override
    public void render(ModelBatch modelBatch, Environment environment) {
        if (renderVertices)
            renderVertices(modelBatch, environment);

        if (renderDetachedAreas)
            renderDetachedAreas(modelBatch, environment);

//        modelBatch.render(keyPointsRendeerable);
    }

    private void renderVertices(ModelBatch modelBatch, Environment environment) {
        if (verticesModel == null) {
            initVerticesRendering(assetManager);
            // System.out.println(VERTEX_SIZE);
        }
        modelBatch.render(verticesModel, environment);
        ;
    }

    private void renderDetachedAreas(ModelBatch modelBatch, Environment environment) {
        if (detachedAreas != null)
            modelBatch.render(detachedAreas, environment);
    }

    public void updateProjections(Camera camera) {
        if (camera == null)
            return;

        Logger.printTimeMessage("Start projections updating");
        getScreenProjections().clear();

        for (Triangle i : ModelAbstraction.this) {
            Vector3 projected = camera.project(i.getCenter());
            getScreenProjections().add(new Point2D(projected.x, projected.y));
        }
        Logger.printTimeMessage("Finish projections updating");
    }

    public Triangle getTriangle(int id) {
        return get(id);
    }

    public ArrayList<Triangle> getTriangles(ArrayList<Integer> trianglesID) {
        ArrayList<Triangle> triangles = new ArrayList<Triangle>();
        for (Integer i : trianglesID)
            triangles.add(getTriangle(i));

        return triangles;
    }

    public HashSet<Integer> getTrianglesIds() {
        HashSet<Integer> ids = new HashSet<Integer>();

        for (Triangle i : this)
            ids.add(i.getId());

        return ids;
    }

    public float getSquare() {
        return square;
    }

    public String getModelName() {
        return modelName;
    }

    public Graph getGraph() {
        return graph;
    }

    public ArrayList<Point3D> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<Point3D> vertices) {
        this.vertices = vertices;
    }

    public boolean isRenderVertices() {
        return renderVertices;
    }

    public void setRenderVertices(boolean renderVertices) {
        this.renderVertices = renderVertices;
    }

    public boolean isRenderDetachedAreas() {
        return renderDetachedAreas;
    }

    public void setRenderDetachedAreas(boolean renderDetachedAreas, BurnByContourBuilder separator) {
        this.renderDetachedAreas = renderDetachedAreas;

        if ((separator != null) && renderDetachedAreas) {
            detachedAreas = new ArrayList<BurnInstance>();
            separator.setRememberDetachedAreas(renderDetachedAreas, detachedAreas);
        }
    }

    public BoundingBox getBounds() {
        return bounds;
    }

    public void setBounds(BoundingBox bounds) {
        this.bounds = bounds;
    }

    public ArrayList<Point2D> getScreenProjections() {
        return screenProjections;
    }

    public void setScreenProjections(ArrayList<Point2D> screenProjections) {
        this.screenProjections = screenProjections;
    }

    public class FeetBoundingBox extends Renderable {

        private MeshBuilder meshBuilder;
        private Color color;
        private BoundingBox boundingBox;

        public FeetBoundingBox(Color color) {
            boundingBox = ModelAbstraction.this.getBounds();
            init();
        }

        private void init() {
            meshBuilder = new MeshBuilder();
            meshBuilder.begin(Usage.Position, GL20.GL_TRIANGLES);
            meshBuilder.setColor(color);

            float width = boundingBox.max.x - boundingBox.min.x;
            float height = boundingBox.max.y - boundingBox.min.y;
            float depth = boundingBox.max.z - boundingBox.min.z;
            meshBuilder.box(boundingBox.min.x, boundingBox.min.y, boundingBox.min.z, width, height, depth);

            material = new Material(ColorAttribute.createDiffuse(color));
            meshPart.mesh = meshBuilder.end();
            meshPart.primitiveType = GL20.GL_TRIANGLES;
            meshPart.size = meshPart.mesh.getNumIndices();
            meshPart.offset = 0;
        }
    }
}
