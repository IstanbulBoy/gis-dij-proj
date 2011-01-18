package app;

import sndlib.core.network.*;

/* Klasa pomocnicza - rozszerzony w�ze� - struktura zaieraj�ca w�ze� sieci wraz z warto�ci� "odleg�o�ci"
 * oraz funkcjami pomocniczymi (m.in. odpowiednie wypisywanie w�z��w sieci)*/
public class ExtendedNode implements Comparable<ExtendedNode> {
	Node node;
	Double distance;
	
	ExtendedNode(Node node, Double distanceValue){
		this.node = node;
		this.distance = distanceValue;
	}
	/* Funkcja pomocnicza - pobieranie w�z�a w�a�ciwego*/
	public Node getNode(){
		return node;
	}
	/* Funkcja pomocnicza - pobieranie warto�ci parametru "odleg�o��"*/
	public Double getDistance(){
		return distance;
	}
	/* Funkcja pomocnicza - ustawianie warto�ci parametru "odleg�o��"*/
	public void setDistance(Double distanceValue){
		distance = distanceValue;
	}
	/* Funkcja pomocnicza - por�wnywalno�� w�z��w, aby mog�y trafi� do kolejki priorytetowej*/
	public int compareTo(ExtendedNode node2){
		return this.distance.compareTo(node2.getDistance());
			
	}
	/* Funkcja pomocnicza - wypisywanie nazwy w�z�a wraz z warto�ci� "odleg�o�ci"*/
	public String toString(){
		return node.getId()+"["+distance+"]";
	}
	
}
