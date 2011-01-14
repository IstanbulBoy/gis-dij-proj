package app;

import java.util.List;
import java.util.Set;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.network.Node;
import sndlib.core.problem.RoutingLink;
import sndlib.core.problem.RoutingPath;

/**
 *
 * @author hmsck
 */
public class Algorithm {

    public Algorithm() {
    }

    protected void setupCapacities(Network net, DemandMatrix capacityMatrix) {
        for (Link link : net.links()) {
            link.setPreCapacity(capacityMatrix.getDemand(link.getFirstNode(), link.getSecondNode()));
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
        RoutingPath route = null;

        DemandMatrix maxDemMatrix = demandMatrices.getMaxDemandMatrix();
        for (Node firstNode : nodes) {
            nodes.remove(firstNode);
            if (!nodes.isEmpty()) {
                for (Node secondNode : nodes) {
                    routes[Integer.parseInt(firstNode.getId())][Integer.parseInt(secondNode.getId())] =
                            dijkstra.findRoute(firstNode, secondNode, maxDemMatrix.getDemand(firstNode, secondNode));
                }
            }
        }

        for (DemandMatrix demandMatrix : demandMatrices.getMatrices()) {
            for (Node firstNode : nodes) {
                nodes.remove(firstNode);
                if (!nodes.isEmpty()) {
                    for (Node secondNode : nodes) {
                        double demand = demandMatrix.getDemand(firstNode, secondNode);
                        route = dijkstra.findRoute(firstNode, secondNode, demand);
                        for (RoutingLink routingLink : route.routingLinks()) {
                            Link link = routingLink.getLink();

                            if (link.getPreCapacity() < demand) {
                                /* fail */
                            }
                        }

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
