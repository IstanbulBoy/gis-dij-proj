/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package app;

import sndlib.core.network.Network;
import sndlib.core.network.Node;
import sndlib.core.problem.RoutingPath;

/**
 *
 * @author hmsck
 */
public class Dijkstra {
    Network net;
    
    Dijkstra(Network net) {
        this.net = net;
    }

    public RoutingPath findRoute(Node first, Node last, Double maxDemand) {
        return null;
    }
}
