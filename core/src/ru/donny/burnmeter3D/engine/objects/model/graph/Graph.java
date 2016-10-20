package ru.donny.burnmeter3D.engine.objects.model.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Stack;

public class Graph extends ArrayList<GraphVertex> {

	private static final long serialVersionUID = 7128920235678516360L;

	private Stack<Integer> recursionStack = new Stack<Integer>();
	private PriorityQueue<GraphVertex> queue;
	private int maxDistance;
	private int start;
	private HashSet<Integer> activeVertices;

	public Graph(int capacity) {
		super(capacity);
	}

	public Graph() {
		super();
	}

	public void initialize(int size) {
		for (int i = 0; i < size; i++)
			add(new GraphVertex(i));
	}

	private void initQueue(ArrayList<Integer> queueElements) {
		queue = new PriorityQueue<GraphVertex>();

		if (queueElements != null)
			for (Integer i : queueElements)
				queue.add(get(i));
		else
			for (GraphVertex i : this)
				queue.add(i);

		return;
	}

	/*
	 * Sets all vertices as unchecked and distances equal to 0, resets
	 * calculated path sequence.
	 */
	private void resetPath() {
		for (GraphVertex i : this) {
			i.setDistance(maxDistance);
			i.setChecked(false);
			i.setPrevious(null);
		}
	}

	@Deprecated
	public void calculatePathDejkstra(int start, int end) {
		maxDistance = Integer.MAX_VALUE / 2;
		this.start = start;
		resetPath();

		ArrayList<Integer> activeVerticesList;
		if (activeVertices != null)
			activeVerticesList = new ArrayList<Integer>(activeVertices);
		else
			activeVerticesList = null;

		calculateDistances(activeVerticesList, end);
	}

	public void calculatePathBfs(int start, int end) {
		resetPath();
		ArrayList<GraphVertex> queue = new ArrayList<GraphVertex>();
		queue.add(get(start));
		get(start).visit(null, 0);

		GraphVertex endVertex = get(end);
		while (!queue.isEmpty()) {
			for (int k = 0; k < queue.size(); k++) {
				GraphVertex i = queue.remove(k);
				if (i == endVertex)
					return;

				for (GraphVertex j : i.getAdjacentList()) {
					if (j.isChecked())
						continue;

					queue.add(j);
					j.visit(i, 1);
				}
			}
		}
	}

	private void calculateDistances(ArrayList<Integer> pathElements, int end) {
		this.get(start).setDistance(0);

		if (start == end)
			return;

		initQueue(pathElements);

		GraphVertex endVertex = get(end);
		for (GraphVertex i = queue.poll(); !queue.isEmpty(); i = queue.poll()) {
			if (i != null) {
				for (GraphVertex j : i.getAdjacentList())
					if (j.getDistance() > i.getDistance() + 1) {
						j.setDistance(i.getDistance() + 1);
						j.setPrevious(i);
						if (j == endVertex) {
							return;
						}
						queue.remove(j);
						queue.offer(j);
					}
			}
		}
	}

	public ArrayList<Integer> getPath(int start, int end) {
		// calculatePathDejkstra(start, end);
		calculatePathBfs(start, end);

		ArrayList<Integer> path = new ArrayList<Integer>();
		path.add(end);

		GraphVertex startVertex = get(start);
		for (GraphVertex i = get(end); i != startVertex; i = i.getPrevious())
			if (i == null)
				return new ArrayList<Integer>();
			else
				path.add(i.getId());

		return path;
	}

	public ArrayList<HashSet<Integer>> getDetachedAreas(HashSet<Integer> separated) {
		ArrayList<HashSet<Integer>> detachedAreas = new ArrayList<HashSet<Integer>>();

		for (GraphVertex i : this)
			i.setChecked(false);

		for (Integer i : separated)
			get(i).setChecked(true);

		for (Integer i : separated)
			for (GraphVertex j : get(i).getAdjacentList()) {
				HashSet<Integer> detachedArea;
				if (((detachedArea = getDetachedArea(j)).size()) > 0)
					detachedAreas.add(detachedArea);
			}

		return detachedAreas;
	}

	public ArrayList<Integer> getTrianglesInRange(int id, int lightingRange) {
		resetPath();
		ArrayList<GraphVertex> queue = new ArrayList<GraphVertex>();
		queue.add(get(id));
		get(id).visit(null, 0);

		ArrayList<Integer> circle = new ArrayList<Integer>();
		circle.add(id);
		int range = 0;
		while (!queue.isEmpty() && (range < lightingRange)) {
			for (int k = 0; k < queue.size() && (range < lightingRange); k++) {
				GraphVertex i = queue.remove(k);

				for (GraphVertex j : i.getAdjacentList()) {
					if (j.isChecked())
						continue;

					queue.add(j);
					circle.add(j.getId());
					j.visit(i, 1);
				}
				range++;
			}
		}

		return circle;
	}

	public ArrayList<HashSet<Integer>> getDetachedAreas() {
		return getDetachedAreas(new HashSet<Integer>());
	}

	private HashSet<Integer> getDetachedArea(GraphVertex startId) {
		// System.out.println("--------------------------launched--------------------------");
		HashSet<Integer> detachedArea = new HashSet<Integer>();
		recursionStack.clear();
		recursionStack.push(startId.getId());

		while (!recursionStack.isEmpty()) {
			for (GraphVertex i : get(recursionStack.pop()).getAdjacentList())
				dfsVisit(i, detachedArea);
		}

		return detachedArea;
	}

	private void dfsVisit(GraphVertex i, HashSet<Integer> detachedArea) {
		if (!i.isChecked()) {
			detachedArea.add(i.getId());
			i.setChecked(true);
			recursionStack.push(i.getId());
		}
	}

	public void setActiveVertices(HashSet<Integer> active) {
		this.activeVertices = active;
	}
}
