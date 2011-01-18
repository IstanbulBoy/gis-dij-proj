//package app;

import sndlib.core.network.*;
import sndlib.core.problem.*;
import sndlib.core.problem.RoutingPath.*;
import sndlib.core.problem.RoutingPath;

public class Klasa {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// net wygl�da tak:
		//	  b
		//	/   \
		// a	 d
		//	\   /
		//	  c
		//
		
		Network net = new Network();
		net.newNode("a");
		net.newNode("b");
		net.newNode("c");
		net.newNode("d");
		//net.newNode("e");
		//net.newNode("f");
		net.newLink("1", net.getNode("a"), net.getNode("b")).setPreCost(1.0);
		net.newLink("2", net.getNode("a"), net.getNode("c")).setPreCost(3.0);
		net.newLink("3", net.getNode("b"), net.getNode("d")).setPreCost(2.0);
		net.newLink("4", net.getNode("c"), net.getNode("d")).setPreCost(4.0);
		
		net.getLink("1").setPreCapacity(10.0);
		net.getLink("2").setPreCapacity(10.0);
		net.getLink("3").setPreCapacity(10.0);
		net.getLink("4").setPreCapacity(10.0);
		
		
		RoutingPath r = Dijkstra.findRoute(net.getNode("a"), net.getNode("c"), 2.0, net);
		System.out.print(r.routingLinks().get(0).getSource().getId());
		for(RoutingLink rout:r.routingLinks())
			System.out.print(":"+rout.getTarget().getId());
		
		/*Network net2 = new Network();
		net2.newNode("a");
		net2.newNode("b");
		net2.newNode("c");
		net2.newNode("d");
		//net.newNode("e");
		//net.newNode("f");
		net2.newLink("1", net.getNode("b"), net.getNode("a")).setPreCost(1.0);
		net2.newLink("2", net.getNode("a"), net.getNode("c")).setPreCost(3.0);
		net2.newLink("3", net.getNode("b"), net.getNode("d")).setPreCost(2.0);
		net2.newLink("4", net.getNode("c"), net.getNode("d")).setPreCost(4.0);
		
		net2.getLink("1").setPreCapacity(10.0);
		net2.getLink("2").setPreCapacity(10.0);
		net2.getLink("3").setPreCapacity(10.0);
		net2.getLink("4").setPreCapacity(10.0);*/
		
		
		
	}

}
