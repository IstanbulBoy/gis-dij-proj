package app;

import sndlib.core.network.*;

public class ExtendedNode implements Comparable<ExtendedNode> {
	Node node;
	Double distance;
	
	ExtendedNode(Node node, Double distanceValue){
		this.node = node;
		this.distance = distanceValue;
	}
	
	public Node getNode(){
		return node;
	}
	
	public Double getDistance(){
		return distance;
	}
	
	public void setDistance(Double distanceValue){
		distance = distanceValue;
	}
	
	public int compareTo(ExtendedNode node2){
		return this.distance.compareTo(node2.getDistance());
			
	}
	
}
