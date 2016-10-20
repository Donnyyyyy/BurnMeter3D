package ru.donny.burnmeter3D.engine.objects.model.graph;

import java.util.ArrayList;

public class GraphVertex implements Comparable<GraphVertex> {

	public static int NO_PREVIOUS = -1;

	private int id;
	private GraphVertex previous;
	private boolean isChecked;
	private int distance;
	private ArrayList<GraphVertex> adjacentList;

	@Override
	public String toString() {
		return "GraphVertex [id=" + id + ", isChecked=" + isChecked + ", adjacentList=" + getAdjacentListString() + "]";
	}

	private String getAdjacentListString() {
		if (adjacentList.size() == 0)
			return "empty";

		String adjacentListString = "";
		for (GraphVertex i : adjacentList) {
			adjacentListString += i.getId() + " ";
		}
		return adjacentListString;
	}

	public GraphVertex(int id) {
		this(id, new ArrayList<GraphVertex>());
	}

	public GraphVertex(int id, ArrayList<GraphVertex> adjacentList) {
		this.id = id;
		this.adjacentList = adjacentList;
		previous = null;
		isChecked = false;
	}

	public void visit(GraphVertex previous, int distanceFromPrevious) {
		setChecked(true);
		if (previous == null) {
			setDistance(distanceFromPrevious);
			setPrevious(previous);
		} else {
			setDistance(distanceFromPrevious + previous.distance);
			setPrevious(previous);
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public ArrayList<GraphVertex> getAdjacentList() {
		return adjacentList;
	}

	public void setAdjacentList(ArrayList<GraphVertex> adjacentList) {
		this.adjacentList = adjacentList;
	}

	public GraphVertex getPrevious() {
		return previous;
	}

	public void setPrevious(GraphVertex previousId) {
		this.previous = previousId;
	}

	@Override
	public int compareTo(GraphVertex b) {
		if (b.getClass() != GraphVertex.class)
			return 0;

		return (int) Math.signum(getDistance() - b.getDistance());
	}
}
