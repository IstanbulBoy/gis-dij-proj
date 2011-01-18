package app;

import sndlib.core.network.*;

/* Klasa pomocnicza - rozszerzony wêze³ - struktura zaieraj¹ca wêze³ sieci wraz z wartoœci¹ "odleg³oœci"
 * oraz funkcjami pomocniczymi (m.in. odpowiednie wypisywanie wêz³ów sieci)*/
public class ExtendedNode implements Comparable<ExtendedNode> {
	Node node;
	Double distance;
	
	ExtendedNode(Node node, Double distanceValue){
		this.node = node;
		this.distance = distanceValue;
	}
	/* Funkcja pomocnicza - pobieranie wêz³a w³aœciwego*/
	public Node getNode(){
		return node;
	}
	/* Funkcja pomocnicza - pobieranie wartoœci parametru "odleg³oœæ"*/
	public Double getDistance(){
		return distance;
	}
	/* Funkcja pomocnicza - ustawianie wartoœci parametru "odleg³oœæ"*/
	public void setDistance(Double distanceValue){
		distance = distanceValue;
	}
	/* Funkcja pomocnicza - porównywalnoœæ wêz³ów, aby mog³y trafiæ do kolejki priorytetowej*/
	public int compareTo(ExtendedNode node2){
		return this.distance.compareTo(node2.getDistance());
			
	}
	/* Funkcja pomocnicza - wypisywanie nazwy wêz³a wraz z wartoœci¹ "odleg³oœci"*/
	public String toString(){
		return node.getId()+"["+distance+"]";
	}
	
}
