package ru.donny.burnmeter3D.data.graphstorage;

import ru.donny.burnmeter3D.engine.objects.model.graph.Graph;

public interface ModelGraphStorage {
	
	public Graph getGraph(String modelName, int graphSize);
	
	public void insertGraph(Graph modelGraph, String modelName);

}
