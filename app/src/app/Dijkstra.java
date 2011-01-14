/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package app;

import java.util.PriorityQueue;
import sndlib.core.model.DemandModel;
import sndlib.core.model.LinkModel;
import sndlib.core.network.Network;
import sndlib.core.network.Node;
import sndlib.core.problem.RoutingLink;
import sndlib.core.problem.RoutingPath;
import sndlib.core.problem.RoutingPath.Builder;

/**
 *
 * @author hmsck
 */
public class Dijkstra {
    Network net;
    PriorityQueue<Node> priorities = new PriorityQueue<Node>();
    
    Dijkstra(Network net) {
        this.net = net;
    }

    public RoutingPath findRoute(Node first, Node last, Double maxDemand) {
        Builder builderFor = RoutingPath.getBuilderFor(LinkModel.DIRECTED, DemandModel.UNDIRECTED);

        RoutingPath path = builderFor.newPath(null, first, last);
       
        RoutingLink link;
        
        return null;
    }
}
