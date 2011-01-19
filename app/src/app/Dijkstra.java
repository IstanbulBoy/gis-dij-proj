package app;

import java.util.*;
import sndlib.core.model.DemandModel;
import sndlib.core.model.LinkModel;
import sndlib.core.network.Network;
import sndlib.core.network.Link;
import sndlib.core.network.Node;
import sndlib.core.problem.RoutingPath;
import sndlib.core.problem.RoutingPath.Builder;
import sndlib.core.util.NetworkUtils;

public class Dijkstra {
    static Network net;
    
    /*Kolejka priorytetowa odleglosci od wezla startowego*/
    static PriorityQueue<ExtendedNode> priorities = new PriorityQueue<ExtendedNode>();
    static HashMap<Node, Link> predecessorLink = new HashMap<Node, Link>();
    static HashMap<Node, Node> predecessorNode = new HashMap<Node, Node>();
    
    /* Zmodyfikowany algorytm Dijkstra*/
    public static RoutingPath findRoute(Node first, Node last, Integer maxDemand, Network networkArg) {
    	//System.out.println("--findroute: first="+first.getId()+" last="+last.getId()+" net="+networkArg+"\n");
        priorities.clear();
        predecessorLink.clear();
        predecessorNode.clear();
        
    	net = networkArg;
    	for(Node n: net.nodes()){
    		priorities.add(new ExtendedNode(n, n == first ? 0 : Double.MAX_VALUE));
    	}
    	boolean success = false;
    	
    	/* Glowna petla algorytmu Dijkstry*/
    	AlgDij:
    	while(!priorities.isEmpty()){
    		Double currentDist = priorities.peek().getDistance();
    		Node start = priorities.peek().getNode();
    		ExtendedNode exN = priorities.peek();
    		/*Petla wykonywana dla wszystkich wezlow polaczonych z aktualnym*/
    		for(Link l: NetworkUtils.getIncidentLinks(start, net)){
    			/*rozpatrywane sa tylko te wezly, ktore sa w kolejce*/
    			if(areBothNodesInQueue(l) && l.getPreCapacity()>=maxDemand){
    				Node secondNode = l.getFirstNode() == start ? l.getSecondNode() : l.getFirstNode();
    				Double secondDist = getDistFromQ(secondNode);
    				/*sprawdzenie warunku poprawy aktualnej odleglosci wezla*/
    				if(currentDist + l.getPreCost() < secondDist){
    					setDistInQ(secondNode, currentDist + l.getPreCost());
    					predecessorLink.put(secondNode, l);
    					predecessorNode.put(secondNode, start);
    					/*jesli to wezel koncowy, to znaleziono najkrosza sciezke*/
    					if(secondNode == last){
    						success = true;
    						break AlgDij;
    					}
    						
    				}
    			}
    		}
    		priorities.remove(exN);
    	}
    	if (!success){
    		/*Blad - nie znalazlo polaczenia*/
    		return null;
    	}
    	
    	Builder builderFor = RoutingPath.getBuilderFor(LinkModel.UNDIRECTED, DemandModel.UNDIRECTED);
    	LinkedList<Link> l = new LinkedList<Link>();
    	Node pathPartialEnd = last;
    	while(predecessorNode.containsKey(pathPartialEnd)){
    		l.offerFirst(predecessorLink.get(pathPartialEnd));
    		pathPartialEnd = predecessorNode.get(pathPartialEnd);
    	}
        
    	RoutingPath path = builderFor.newPath(l, first, last);
        
        return path;
    }
    
    /* Funkcja pomocnicza - sprawdza czy wezly polaczone krawedzia sa w kolejce*/
    static boolean areBothNodesInQueue(Link l){
    	if(isInPriorityQueue(l.getFirstNode()) && isInPriorityQueue(l.getSecondNode()))
    		return true;
    	else return false;
    }
    /* Funkcja pomocnicza - sprawdza czy wezel jest w kolejce*/
    static boolean isInPriorityQueue(Node n){
    	for(ExtendedNode s:priorities)
    		if(s.getNode() == n)
    			return true;
    	return false;
    }
    
    /* Funkcja pomocnicza - pobiera wartosc odleglosci dla danego wezlaa z kolejki*/
    static Double getDistFromQ(Node n){
    	for(ExtendedNode ex:priorities){
    		if(ex.getNode() == n)
    			return ex.getDistance();
    	}
    	return -Double.MAX_VALUE;
    }
    /* Funkcja pomocnicza - ustawia wartosc odleglosci dla danego wezlaa w kolejce*/
    static void setDistInQ(Node n, Double distanceValue){
    	for(ExtendedNode ex:priorities){
    		if(ex.getNode() == n){
    			priorities.remove(ex);
    			ex.setDistance(distanceValue);
    			priorities.add(ex);
    			return;
    		}
    	}
    }
}
