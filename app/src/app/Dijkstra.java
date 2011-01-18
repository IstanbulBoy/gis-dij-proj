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
    //LinkedHashMap<Node, Integer> distances = new LinkedHashMap<Node, Integer>();
    static PriorityQueue<ExtendedNode> priorities = new PriorityQueue<ExtendedNode>();
    static HashMap<Node, Link> predecessorLink = new HashMap<Node, Link>();
    static HashMap<Node, Node> predecessorNode = new HashMap<Node, Node>();
    
    /* Zmodyfikowany algorytm Dijkstra*/
    public static RoutingPath findRoute(Node first, Node last, Double maxDemand, Network networkArg) {
    	//System.out.println("--findroute: first="+first.getId()+" last="+last.getId()+" net="+networkArg+"\n");
        priorities.clear();
        predecessorLink.clear();
        predecessorNode.clear();
        
    	net = networkArg;
    	for(Node n: net.nodes()){
    		priorities.add(new ExtendedNode(n, n == first ? 0 : Double.MAX_VALUE));
    	}
    	boolean success = false;
    	
    	/* G³ówna pêtla algorytmu Dijkstry*/
    	AlgDij:
    	while(!priorities.isEmpty()){
    		//System.out.println("priorities:" + priorities);
    		Double currentDist = priorities.peek().getDistance();
    		Node start = priorities.peek().getNode();
    		ExtendedNode exN = priorities.peek();
    		//System.out.println("WYBÓR: node " + start.getId() + "["+currentDist+"]");
    		for(Link l: NetworkUtils.getIncidentLinks(start, net)){
    			if(areBothNodesInQueue(l) && l.getPreCapacity()>=maxDemand){
    				Node secondNode = l.getFirstNode() == start ? l.getSecondNode() : l.getFirstNode();
    				Double secondDist = getDistFromQ(secondNode);
    				if(currentDist + l.getPreCost() < secondDist){
    					setDistInQ(secondNode, currentDist + l.getPreCost());
    					predecessorLink.put(secondNode, l);
    					predecessorNode.put(secondNode, start);
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
    		//ERROR
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
    
    /* Funkcja pomocnicza - sprawdza czy wêz³y po³¹czone krawêdzi¹ s¹ w kolejce*/
    static boolean areBothNodesInQueue(Link l){
    	if(isInPriorityQueue(l.getFirstNode()) && isInPriorityQueue(l.getSecondNode()))
    		return true;
    	else return false;
    }
    /* Funkcja pomocnicza - sprawdza czy wêze³ jest w kolejce*/
    static boolean isInPriorityQueue(Node n){
    	for(ExtendedNode s:priorities)
    		if(s.getNode() == n)
    			return true;
    	return false;
    }
    
    /* Funkcja pomocnicza - pobiera wartoœæ "odleg³oœci" dla danego wêz³a z kolejki*/
    static Double getDistFromQ(Node n){
    	for(ExtendedNode ex:priorities){
    		if(ex.getNode() == n)
    			return ex.getDistance();
    	}
    	return -Double.MAX_VALUE;
    }
    /* Funkcja pomocnicza - usawia wartoœæ "odleg³oœci" dla danego wêz³a w kolejce*/
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
