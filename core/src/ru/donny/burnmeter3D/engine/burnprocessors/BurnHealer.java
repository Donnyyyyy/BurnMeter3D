package ru.donny.burnmeter3D.engine.burnprocessors;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ru.donny.burnmeter3D.data.Burn;
import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;
import ru.donny.burnmeter3D.engine.objects.model.graph.Graph;
import ru.donny.burnmeter3D.graphics.renderable.BurnInstance;
import ru.donny.burnmeter3D.graphics.renderable.BurnModelBuilder;

public class BurnHealer {

    private ArrayList<BurnInstance> burns;
    protected BurnInstance burn;
    private Graph modelGraph;

    private Map<Integer, Array<Triangle>> healingSteps;
    private int maxRange = -1;

    public BurnHealer(BurnInstance burn, Graph modelGraph, ArrayList<BurnInstance> burns) {
        this.burn = burn;
        this.burns = burns;
        this.modelGraph = modelGraph;

        predictHealing();
    }

    private void predictHealing() {
        healingSteps = new HashMap<Integer, Array<Triangle>>();

        modelGraph.calculatePathDejkstra(getCenter().getId(), -1);

        for (Triangle burnSegment : burn.getBurnTriangles()) {
            int order = modelGraph.get(burnSegment.getId()).getDistance();

            if (healingSteps.containsKey(order)) {
                healingSteps.get(order).add(burnSegment);
            } else {
                Array<Triangle> triangles = new Array<Triangle>();
                triangles.add(burnSegment);

                healingSteps.put(order, triangles);
            }
        }
    }

    public Triangle getCenter() {
        BoundingBox bounds = new BoundingBox();
        burn.calculateBoundingBox(bounds);

        Vector3 geometricCenter = new Vector3();
        bounds.getCenter(geometricCenter);

        Map<Triangle, Float> ranges = new HashMap<Triangle, Float>();

        Map.Entry<Triangle, Float> closest = null;
        for (Triangle burnSegment : burn.getBurnTriangles()) {
            float range = burnSegment.getCenter().dst(geometricCenter);
            ranges.put(burnSegment, range);

            if (closest == null || closest.getValue() > range) {
                closest = new AbstractMap.SimpleEntry<Triangle, Float>(burnSegment, range);
            }
        }

        return closest.getKey();
    }

    public void makeStep() {
        int maxRange = getMaxRange();

        if (maxRange <= 1)
            return;

        for (Triangle healed : healingSteps.get(maxRange)) {
            burn.getBurnTriangles().remove(healed);
        }

        replaceBurnInstance();
    }

    private void replaceBurnInstance() {
        int index = burns.indexOf(burn);
        burns.remove(index);
        burns.add(index, BurnModelBuilder.build(burn.getBurnTriangles(), burn.getType()));
    }

    private int getMaxRange() {
        for (Integer range : healingSteps.keySet()) {
            maxRange = Math.max(maxRange, range);
        }

        return maxRange;
    }

}
