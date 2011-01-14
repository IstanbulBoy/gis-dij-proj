package app;

import java.util.*;
import sndlib.core.model.DemandModel;
import sndlib.core.model.LinkModel;
import sndlib.core.network.Network;
import sndlib.core.network.Link;
import sndlib.core.network.Node;
import sndlib.core.problem.RoutingLink;
import sndlib.core.problem.RoutingPath;
import sndlib.core.problem.RoutingPath.Builder;
import sndlib.core.util.NetworkUtils;

/**
 *
 * @author hmsck
 */
public class Dijkstra {
    static Network net;
    //LinkedHashMap<Node, Integer> distances = new LinkedHashMap<Node, Integer>();
    static PriorityQueue<ExtendedNode> priorities = new PriorityQueue<ExtendedNode>();
    static HashMap<Node, Link> predecessorLink = new HashMap<Node, Link>();
    static HashMap<Node, Node> predecessorNode = new HashMap<Node, Node>();
    

    public static RoutingPath findRoute(Node first, Node last, Double maxDemand, Network networkArg) {
    	net = networkArg;
    	for(Node n: net.nodes()){
    		priorities.add(new ExtendedNode(n, n == first ? 0 : Double.MAX_VALUE));
    	}
    	boolean success = false;
    	
    	AlgDij:
    	while(!priorities.isEmpty()){
    		Double currentDist = priorities.peek().getDistance();
    		Node start = priorities.peek().getNode();
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
    		priorities.remove();
    	}
    	if (!success){
    		//ERROR
    		System.out.println("ERROR\n");
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
    
    static boolean areBothNodesInQueue(Link l){
    	if(isInPriorityQueue(l.getFirstNode()) && isInPriorityQueue(l.getSecondNode()))
    		return true;
    	else return false;
    }
    
    static boolean isInPriorityQueue(Node n){
    	for(ExtendedNode s:priorities)
    		if(s.getNode() == n)
    			return true;
    	return false;
    }
    static Double getDistFromQ(Node n){
    	for(ExtendedNode ex:priorities){
    		if(ex.getNode() == n)
    			return ex.getDistance();
    	}
    	return -Double.MAX_VALUE;
    }
    
    static void setDistInQ(Node n, Double distanceValue){
    	for(ExtendedNode ex:priorities){
    		if(ex.getNode() == n){
    			ex.setDistance(distanceValue);
    			return;
    		}
    	}
    }
}
