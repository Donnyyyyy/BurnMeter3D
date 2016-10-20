package ru.donny.burnmeter3D.data.graphstorage;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import ru.donny.burnmeter3D.data.Parser;
import ru.donny.burnmeter3D.engine.objects.model.graph.Graph;
import ru.donny.burnmeter3D.engine.objects.model.graph.GraphVertex;

public class GraphStorage implements ModelGraphStorage {

	private GraphStorageProvider storageProvider;

	public GraphStorage(GraphStorageProvider storageProvider) {
		this.storageProvider = storageProvider;
	}

	@Override
	public Graph getGraph(String modelName, int graphSize) {
		try {
			return parseGraph(storageProvider.getReadableStorage(modelName), graphSize);
		} catch (FileNotFoundException e) {
			System.out.println("Can't find stored data for " + modelName + " (" + e.getMessage() + ").");
			return null;
		}
	}

	private Graph parseGraph(InputStream storage, int graphSize) {
		Graph modelGraph = createEmptyGraph(graphSize);
		Parser in = new Parser(storage);
		for (GraphVertex i : modelGraph) {
			parseVertexFast(in, modelGraph, i);
		}

		return modelGraph;
	}

	private Graph createEmptyGraph(int size) {
		Graph graph = new Graph();
		for (int i = 0; i < size; i++) {
			graph.add(new GraphVertex(i));
		}
		return graph;
	}

	protected void parseVertex(Parser storageParser, Graph graph, GraphVertex changing) {
		String line = storageParser.getLine();
		Scanner lineParser = new Scanner(line);

		while (lineParser.hasNextInt())
			changing.getAdjacentList().add(graph.get(lineParser.nextInt()));

		lineParser.close();
	}

	protected void parseVertexFast(Parser storageParser, Graph graph, GraphVertex changing) {
		for (Integer i : storageParser.intArrayList()) {
			changing.getAdjacentList().add(graph.get(i));
		}
	}

	@Override
	public void insertGraph(Graph modelGraph, String modelName) {
		try {
			PrintStream out = new PrintStream(storageProvider.getWritableStorage(modelName));

			for (GraphVertex i : modelGraph)
				printVertexInfo(i, out);

		} catch (Exception e) {
			System.out.println("Storage can't be created.");
		}
	}

	private void printVertexInfo(GraphVertex v, PrintStream out) {
		String info = "";

		for (GraphVertex i : v.getAdjacentList())
			info += i.getId() + " ";

		out.println(info);
	}
}
