package app;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

    static Long solvingTime = -1l;
    static Network network;
    static RoutingPath[][] routes;

    public Algorithm() {
    }

    static public void printRoute(RoutingPath route, PrintStream... printstream) {
        PrintStream ps = printstream.length == 0 ? System.out : printstream[0];

        if (route != null) {
            ps.print(route.routingLinks().get(0).getSource().getId());
            for (RoutingLink r : route.routingLinks()) {
                ps.print(":" + r.getTarget().getId());

            }
        }
    }

    static public void printGraph(Network network, PrintStream... printstream) {
        PrintStream ps = printstream.length == 0 ? System.out : printstream[0];

        ps.println();
        ps.println("[ ====== GRAPH ====== ]");
        for (Link link : network.links()) {
            ps.println("[" + link.getId() + "]: [" + link.getFirstNode().getId() + "] -> [" + link.getSecondNode().getId() + "] (" + link.getPreCapacity() + ")");
        }
        ps.println("[ =================== ]");
        ps.println();
    }

    static public void printResult(PrintStream... printstream) {
        PrintStream ps = printstream.length == 0 ? System.out : printstream[0];
        ps.print("OK: " + "(E,V)=(" + network.nodeCount() + "," + network.linkCount() + ") , " + solvingTime + "ms ");
        int routings = 0;
        for (int i = 0; i < routes.length; i++) {
            for (int j = 0; j < routes.length; j++) {
                if (!(i == j || routes[i][j] == null || i > j)) {
                    routings++;
                }
            }
        }

        ps.println(", " + routings + " routings:");
        for (int i = 0; i < routes.length; i++) {
            for (int j = 0; j < routes.length; j++) {
                RoutingPath r = routes[i][j];
                if (i == j || r == null || i > j) {
                    continue;
                }
                ps.print(r.getFirst().getSource().getId() + "->" + r.getLast().getTarget().getId());
                ps.print("[");
                printRoute(r, ps);
                ps.print("], ");
                ps.print("");
            }
        }
        ps.println();
    }
    
    static public void printStatsToFile(PrintStream... printstream) {
    	PrintStream ps = printstream.length == 0 ? System.out : printstream[0];
    	ps.println(network.nodeCount() + '\t' + network.linkCount() + '\t' + solvingTime);
    }

    public static RoutingPath[][] execute(Network net, DemandMatrices demandMatrices) throws Exception {
        solvingTime = -1l;
        long programStart = System.currentTimeMillis();
        network = net;
        List<Node> nodes = new ArrayList<Node>();
        List<Node> nodesBackup = new ArrayList<Node>();
        Map<String, Double> capacityBackup = new HashMap<String, Double>();
        int matrixCounter = 0;

        for (Node n : net.nodes()) {
            nodes.add(n);
        }

        int nodeCount = net.nodeCount();
        routes = new RoutingPath[nodeCount][nodeCount];
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
                        printGraph(net);
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
        Set<Link> failLinks = new HashSet<Link>();

        /* kopia sieci */
        for (Link link : net.links()) {
            capacityBackup.put(link.getId(), link.getPreCapacity());
        }
        matrixCounter = 0;
        /* sprawdzamy wszystkie macierze zapotrzebowan */
        for (DemandMatrix demandMatrix : demandMatrices.getMatrices()) {
            matrixCounter++;
            nodes.clear();
            for (Node n : net.nodes()) {
                nodes.add(n);
            }
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

                        System.out.print("[" + firstNode.getId() + "] -> [" + secondNode.getId() + "] ");
                        printRoute(routes[i][j]);
                        System.out.println(" (" + demandMatrix.getDemand(firstNode, secondNode) + ") ");

                        double demand = demandMatrix.getDemand(firstNode, secondNode);

                        for (RoutingLink routingLink : routes[i][j].routingLinks()) {
                            Link link = routingLink.getLink();

                            /* czy wszystkie krawedzie spelniaja zapotrzebowanie */
                            if (link.getPreCapacity() < demand) {
                                failLinks.add(routingLink.getLink());
                            }
                        }
                        if (!failLinks.isEmpty()) {
                            /* sa przepelnione krawedzie*/

                            Map<String, Double> capacityFailBackup = new HashMap<String, Double>();

                            /* usuwamy przepelnione krawedzie */
                            for (Link link : failLinks) {
                                capacityFailBackup.put(link.getId(), link.getPreCapacity());
                                network.getLink(link.getId()).setPreCapacity(0);
                            }
                            for (Link link : failLinks) {
                                System.out.println("Zeruje sciezke " + link.getId() + "[" + link.getFirstNode().getId() + "] -> [" + link.getSecondNode().getId() + "]");
                                if (capacityFailBackup.get(link.getId()) == network.getLink(link.getId()).getPreCapacity()) {
//                                    return null;
                                }
                            }

                            System.out.print("new path for [" + firstNode.getId() + "] -> [" + secondNode.getId() + "] [" + demandMatrix.getDemand(firstNode, secondNode) + "] ");

                            /* szukamy nowego polaczenia */
                            printGraph(network);
                            //System.out.println("F:"+firstNode+" S:"+secondNode+"\nnet:"+network);
                            routesBackup[i][j] = routes[i][j] = routesBackup[j][i] = routes[j][i] =
                                    Dijkstra.findRoute(firstNode, secondNode, demandMatrix.getDemand(firstNode, secondNode), network);
                            printRoute(routes[i][j]);
                            System.out.println();
                            if (routes[i][j] == null) {
                                throw new Exception("Nie znalazlem sciezki");
                            }

                            for (Link link : failLinks) {
                                network.getLink(link.getId()).setPreCapacity(capacityFailBackup.get(link.getId()));
                            }
                            failLinks.clear();
                            capacityFailBackup.clear();
                        }
                        /* wszystkie krawedzie spelniaja zapotrzebowanie, wiec odejmujemy zapotrzebowania od nich */
                        for (RoutingLink routingLink : routes[i][j].routingLinks()) {
                            Link link = routingLink.getLink();

                            /* czy wszystkie krawedzie spelniaja zapotrzebowanie */
                            BigDecimal bd = new BigDecimal(link.getPreCapacity() - demand);
                            bd = bd.setScale(2, BigDecimal.ROUND_UP);
                            link.setPreCapacity(bd.doubleValue());
                        }
                        printGraph(network);
                    }
                }
            }
            for (Link l : net.links()) {
                l.setPreCapacity(capacityBackup.get(l.getId()));
            }
            routes = routesBackup;
        }
        long programEnd = System.currentTimeMillis();
        solvingTime = programEnd - programStart;
        return routesBackup;
    }
}
