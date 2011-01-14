package app;

import java.util.List;
import java.util.Set;
import sndlib.core.network.Network;
import sndlib.core.network.Node;
import sndlib.core.problem.RoutingPath;

/**
 *
 * @author hmsck
 */
public class Algorithm {

    public Algorithm() {
    }

    public Set<RoutingPath> execute() {
        DemandMatrix dm = new DemandMatrix();
        DemandMatrices demandMatrices = new DemandMatrices();
        Network network = new Network();
        List<Node> nodes = (List) network.nodes();
        Dijkstra dijkstra = new Dijkstra(network);

        int i, j, nodeCount;

        nodeCount = 5;
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
