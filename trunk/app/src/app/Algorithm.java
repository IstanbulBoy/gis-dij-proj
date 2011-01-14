package app;

import java.util.HashSet;
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

    public static Set<RoutingPath> execute(Network net, DemandMatrices demandMatrices) throws Exception {
        Network network = net;
        List<Node> nodes = (List) network.nodes();
        int nodeCount = nodes.size();
        RoutingPath routes[][] = new RoutingPath[nodeCount][nodeCount];
        RoutingPath route = null;

        DemandMatrix maxDemMatrix = demandMatrices.getMaxDemandMatrix();
        /* sprawdzamy maksymalna macierz zapotrzebowan */
        for (Node firstNode : nodes) {
            nodes.remove(firstNode);
            if (!nodes.isEmpty()) {
                for (Node secondNode : nodes) {
                    route = Dijkstra.findRoute(firstNode, secondNode, maxDemMatrix.getDemand(firstNode, secondNode), network);
                    if (route == null) {
                        throw new Exception("Nie znalazlem sciezki");
                    } else {
                        routes[Integer.parseInt(firstNode.getId())][Integer.parseInt(secondNode.getId())] = route;
                    }
                }
            }
        }

        RoutingPath routesBackup[][] = routes;
        Set<RoutingLink> failLinks = new HashSet<RoutingLink>();

        /* sprawdzamy wszystkie macierze zapotrzebowan */
        for (DemandMatrix demandMatrix : demandMatrices.getMatrices()) {
            for (Node firstNode : nodes) {
                /* aby nie wyszukac sciezki w druga strone */
                nodes.remove(firstNode);
                if (!nodes.isEmpty()) {
                    for (Node secondNode : nodes) {
                        int i = Integer.parseInt(firstNode.getId());
                        int j = Integer.parseInt(secondNode.getId());
                        double demand = demandMatrix.getDemand(firstNode, secondNode);

                        for (RoutingLink routingLink : routes[i][j].routingLinks()) {
                            Link link = routingLink.getLink();

                            /* czy wszystkie krawedzie spelniaja zapotrzebowanie */
                            if (link.getPreCapacity() < demand) {
                                failLinks.add(routingLink);
                            }
                        }
                        if (!failLinks.isEmpty()) {
                            /* sa przepelnione krawedzie.. kopiujemy siec i usuwamy te krawedzie*/
                            Network tmpnet = network;

                            /* usuwamy przepelnione krawedzie */
                            for (RoutingLink rLink : failLinks) {
                                tmpnet.removeLink(rLink.getLink());
                            }
                            failLinks.clear();

                            /* szukamy nowego polaczenia */
                            routesBackup[i][j] = routes[i][j] =
                                    Dijkstra.findRoute(firstNode, secondNode, maxDemMatrix.getDemand(firstNode, secondNode), network);
                            if (routes[i][j] == null) {
                                throw new Exception("Nie znalazlem sciezki");
                            }

                        }
                        /* wszystkie krawedzie spelniaja zapotrzebowanie, wiec odejmujemy zapotrzebowania od nich */
                        for (RoutingLink routingLink : routes[i][j].routingLinks()) {
                            Link link = routingLink.getLink();

                            /* czy wszystkie krawedzie spelniaja zapotrzebowanie */
                            link.setPreCapacity(link.getPreCapacity() - demand);
                        }
                    }
                }
            }
            routes = routesBackup;
        }
        return null;
    }

    public void test() {
        Network network = new Network();


    }
}
