import sndlib.*;
import sndlib.core.model.*;
import sndlib.core.network.*;
import sndlib.core.problem.*;
import sndlib.core.problem.RoutingPath.*;
import sndlib.core.problem.RoutingPath;
import java.io.*;

public class Klasa {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// net wygl¹da tak:
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
		
		Dijkstra d = new Dijkstra(net);
		RoutingPath r = d.findRoute(net.getNode("a"), net.getNode("d"), 2.0);
		System.out.print(r.routingLinks().get(0).getSource().getId());
		for(RoutingLink rout:r.routingLinks())
			System.out.print(":"+rout.getTarget().getId());
	}

}
