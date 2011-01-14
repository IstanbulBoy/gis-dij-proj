package app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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

    static public void printRoute(RoutingPath route) {
        System.out.print(route.routingLinks().get(0).getSource().getId());
        for (RoutingLink r : route.routingLinks()) {
            System.out.print(":" + r.getTarget().getId());
        }

    }

    public static RoutingPath[][] execute(Network net, DemandMatrices demandMatrices) throws Exception {
        Network network = net;
        List<Node> nodes = new ArrayList<Node>();

        for (Node n : net.nodes()) {
            nodes.add(n);
        }

        int nodeCount = net.nodeCount();
        RoutingPath routes[][] = new RoutingPath[nodeCount][nodeCount];
        RoutingPath route = null;

        System.out.println("Szukam sciezki dla maksymalnych zapotrzebowan...");
        DemandMatrix maxDemMatrix = demandMatrices.getMaxDemandMatrix();

        maxDemMatrix.print();

        /* sprawdzamy maksymalna macierz zapotrzebowan */
        for (Iterator<Node> iterFirst = nodes.iterator(); iterFirst.hasNext();) {
            Node firstNode = iterFirst.next();
            System.out.println("[" + firstNode.getId() + "]");
            iterFirst.remove();

            if (!nodes.isEmpty()) {
                for (Iterator<Node> iterSec = nodes.iterator(); iterSec.hasNext();) {
                    Node secondNode = iterSec.next();
                    if (firstNode == secondNode) {
                        continue;
                    }
                    System.out.print("[" + firstNode.getId() + "] -> [" + secondNode.getId() + "] [" + maxDemMatrix.getDemand(firstNode, secondNode) + "] ");
                    route = Dijkstra.findRoute(firstNode, secondNode, maxDemMatrix.getDemand(firstNode, secondNode), network);

                    if (route == null) {
                        throw new Exception("Nie znalazlem sciezki");
                    } else {
                        int i = Integer.parseInt(firstNode.getId());
                        int j = Integer.parseInt(secondNode.getId());
                        routes[i][j] = routes[j][i] = route;
                        printRoute(route);
                        System.out.println();
                    }
                }
            }
            System.out.println("[/" + firstNode.getId() + "]");
        }

        System.out.println("Szukam sciezki dla reszty zapotrzebowan...");
        RoutingPath routesBackup[][] = routes;
        Set<RoutingLink> failLinks = new HashSet<RoutingLink>();

        for (Node n : net.nodes()) {
            nodes.add(n);
        }
        /* sprawdzamy wszystkie macierze zapotrzebowan */
        for (DemandMatrix demandMatrix : demandMatrices.getMatrices()) {
            demandMatrix.print();
            for (Iterator<Node> iterFirst = nodes.iterator(); iterFirst.hasNext();) {
                Node firstNode = iterFirst.next();
                /* aby nie wyszukac sciezki w druga strone */
                iterFirst.remove();
                if (!nodes.isEmpty()) {
                    for (Iterator<Node> iterSec = nodes.iterator(); iterSec.hasNext();) {
                        Node secondNode = iterSec.next();
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

                            System.out.print("new path for [" + firstNode.getId() + "] -> [" + secondNode.getId() + "] [" + maxDemMatrix.getDemand(firstNode, secondNode) + "] ");

                            /* szukamy nowego polaczenia */
                            routesBackup[i][j] = routes[i][j] = routesBackup[j][i] = routes[j][i] =
                                    Dijkstra.findRoute(firstNode, secondNode, maxDemMatrix.getDemand(firstNode, secondNode), tmpnet);
                            printRoute(routes[i][j]);
                            System.out.println();
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

        return routesBackup;
    }

    public void test() {
        Network network = new Network();


    }
}
