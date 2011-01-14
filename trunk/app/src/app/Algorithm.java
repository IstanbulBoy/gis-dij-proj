package app;

import java.util.List;
import java.util.Set;
import sndlib.core.network.Demand;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.network.Node;
import sndlib.core.problem.DemandFlow;
import sndlib.core.problem.RoutingPath;

/**
 *
 * @author hmsck
 */
public class Algorithm {

    public Algorithm() {
    }

    protected void setupDemands(Network net, DemandMatrix demandMatrix) {
        Demand demand = null;
        
        for (Link link : net.links()) {
            demand = net.newDemand(null, link.getFirstNode(), link.getSecondNode());

            demand.setDemandValue(demandMatrix.getDemand(link.getFirstNode(), link.getSecondNode()));
        }
    }

    public Set<RoutingPath> execute() {
        DemandMatrix dm = new DemandMatrix();
        DemandMatrices demandMatrices = new DemandMatrices();
        Network network = new Network();
        List<Node> nodes = (List) network.nodes();
        int nodeCount = nodes.size();
        Dijkstra dijkstra = new Dijkstra(network);
        RoutingPath routes[][] = new RoutingPath[nodeCount][nodeCount];

        for (DemandMatrix demandMatrix : demandMatrices.getMatrices()) {
            for (Node firstNode : nodes) {
                for (Node lastNode : nodes) {
                    if (firstNode != lastNode) {
                        RoutingPath path = dijkstra.findRoute(firstNode, lastNode, demandMatrix.getDemand(firstNode, lastNode));
                    }
                }
            }
        }
        return null;
    }

    public void test() {
        Network network = new Network();


    }
}
