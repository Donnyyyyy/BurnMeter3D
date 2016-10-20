package ru.donny.burnmeter3D.engine.objects.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Json;

import ru.donny.burnmeter3D.data.ModelSeparationStorage;
import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;

public class ModelWrapper {

    private static final String GLOBAL_BALANCE_TABLE_PATH = "res/modelsData/balanceTables/humanProportions.json";

    public static final String CUBE_MODEL_NAME = "Cube";
    public static final String PLANE_MODEL_NAME = "Plane";
    public static final String HUMAN_MODEL_NAME = "FinalBaseMesh";
    public static final String FEMALE_MODEL_NAME = "Female";
    public static final String DROW_MODEL_NAME = "drow_body";
    public static final String SCULPT_MODEL_NAME = "ManSculpt";
    public static final String CHILD_MODEL_NAME = "Child";
    public static final String PILOT_MODEL_NAME = "Pilot";
    public static final String PRETTY_MODEL_NAME = "Pretty";

    // TODO auto loading model data, creating graph/balance table providers.

    private ModelAbstraction modelAbstraction;
    private Model model;
    private String name;
    private BalanceTable balanceTable;
    private Integer vertexSize;
    private Map<BodyPart, BodyPartList> modelSeparation;

    public ModelWrapper(String name) {
        model = null;
        this.name = name;
        setVertexSize(vertexSize);
        loadBalanceTable();

    }

    public ModelWrapper(String name, Model model) {
        this(name);
        setModel(model);
    }

    public static String getModelFilePath(String modelName) {
        if (modelName.equals(CUBE_MODEL_NAME))
            return "data/cube/Cube.g3db";
        else if (modelName.equals(HUMAN_MODEL_NAME))
            return "data/man/FinalBaseMesh.g3db";
        else if (modelName.equals(FEMALE_MODEL_NAME))
            return "data/man/Female.g3db";
        else if (modelName.equals(PLANE_MODEL_NAME))
            return "data/plane/Plane.g3db";
        else if (modelName.equals(SCULPT_MODEL_NAME))
            return "data/man/ManSculpt.g3db";
        else if (modelName.equals(DROW_MODEL_NAME))
            return "data/drow/drow_body.g3db";
        else if (modelName.equals(CHILD_MODEL_NAME))
            return "data/man/Kind.g3db";
        else if (modelName.equals(PILOT_MODEL_NAME))
            return "data/pilot/ArmyPilot.g3db";
        else if (modelName.equals(PRETTY_MODEL_NAME))
            return "data/pretty/human_12k.g3db";
        else
            return null;
    }

    public String getModelFilePath() {
        return ModelWrapper.getModelFilePath(name);
    }

    private void loadBalanceTable() {
        Json instance = new Json();

        try {
            balanceTable = instance.fromJson(BalanceTable.class, Gdx.files.local(GLOBAL_BALANCE_TABLE_PATH));
        } catch (Exception e) {
            System.out.println("Something wend wrong with the loading of the BalanceTable");
        }
        return;
    }

    private void loadModelSeparation() {
        HashMap<BodyPart, ArrayList<Triangle>> modelSeparationData = ModelSeparationStorage.load(modelAbstraction);

        if (modelSeparationData != null)
            convertModelSeparation(modelSeparationData);
        else
            System.out.println("There is no stored model separation for " + name);
    }

    private void convertModelSeparation(Map<BodyPart, ArrayList<Triangle>> modelSeparationData) {
        modelSeparation = new HashMap<BodyPart, BodyPartList>();

        for (BodyPart i : modelSeparationData.keySet())
            modelSeparation.put(i, new BodyPartList(modelSeparationData.get(i)));
    }

    /**
     * @return number of slots occupied by a single vertex.
     */
    public int getVertexSize() {
        if (vertexSize != null)
            return vertexSize;
        else if (name.equals(CUBE_MODEL_NAME))
            return vertexSize = 3;
        else if (name.equals(HUMAN_MODEL_NAME))
            return vertexSize = 6;
        else if (name.equals(FEMALE_MODEL_NAME))
            return vertexSize = 8;
        else if (name.equals(PLANE_MODEL_NAME))
            return vertexSize = 5;
        else if (name.equals(SCULPT_MODEL_NAME))
            return vertexSize = 5;
        else if (name.equals(CHILD_MODEL_NAME))
            return vertexSize = 5;
        else if (name.equals(PILOT_MODEL_NAME))
            return vertexSize = 13;
        else if (name.equals(PRETTY_MODEL_NAME))
            return vertexSize = 23;
        else
            return vertexSize = 3;
    }

    public BodyPart classify(String meshId) {
        return BodyPart.getPartByName(meshId);
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BalanceTable getTable() {
        return balanceTable;
    }

    public void setTable(BalanceTable table) {
        this.balanceTable = table;
    }

    public void setVertexSize(Integer vertexSize) {
        this.vertexSize = vertexSize;
    }

    public Map<BodyPart, BodyPartList> getModelSeparation() {
        return modelSeparation;
    }

    public void setModelSeparation(Map<BodyPart, ArrayList<Triangle>> modelSeparation) {
        convertModelSeparation(modelSeparation);
        ModelSeparationStorage.store(modelSeparation, name);
    }

    public ModelAbstraction getModelAbstraction() {
        return modelAbstraction;
    }

    public void setModelAbstraction(ModelAbstraction modelAbstraction) {
        this.modelAbstraction = modelAbstraction;
        loadModelSeparation();
    }
}
