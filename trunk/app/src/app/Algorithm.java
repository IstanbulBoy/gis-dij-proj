package app;

import com.sun.xml.internal.ws.util.StringUtils;
import java.io.PrintStream;
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
//    static Network network;
    static RoutingPath[][] routes;
    static int againCounter = 0;

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

    static public void printResult(Network network, PrintStream... printstream) {
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

    static public void printStatsToFile(Network network, PrintStream... printstream) {
        PrintStream ps = printstream.length == 0 ? System.out : printstream[0];
        ps.println(network.nodeCount() + '\t' + network.linkCount() + '\t' + solvingTime);
    }

    public static void cloneRoutingTable(RoutingPath[][] rFrom, RoutingPath[][] rTo) {
        int i, j;
        for (i = 0; i < rFrom.length; i++) {
            for (j = 0; j < rFrom.length; j++) {
                rTo[i][j] = rFrom[i][j];
            }
        }
    }

    public static void clearRoutingTable(RoutingPath[][] rFrom) {
        int i, j;
        for (i = 0; i < rFrom.length; i++) {
            for (j = 0; j < rFrom.length; j++) {
                rFrom[i][j] = null;
            }
        }
    }

    public static RoutingPath[][] execute(Network net, DemandMatrices demandMatrices, boolean noTime, DemandMatrices dmsWorking, boolean printComments) throws Exception {

        solvingTime = -1l;
        long programStart = 0;
        if (!noTime) {
            programStart = System.currentTimeMillis();
        }
        List<Node> nodes = new ArrayList<Node>();
        Map<String, Double> capacityBackup = new HashMap<String, Double>();
        int matrixCounter = 0;

        for (Node n : net.nodes()) {
            nodes.add(n);
        }

        int nodeCount = net.nodeCount();
        routes = new RoutingPath[nodeCount][nodeCount];
        clearRoutingTable(routes);

        RoutingPath route = null;

        DemandMatrix maxDemMatrix = demandMatrices.getMaxDemandMatrix();

        if (printComments) {
            System.out.println("Szukam sciezki dla maksymalnych zapotrzebowan...");
            maxDemMatrix.print();
        }

        /* sprawdzamy maksymalna macierz zapotrzebowan */
        for (Iterator<Node> iterFirst = nodes.iterator(); iterFirst.hasNext();) {
            Node firstNode = iterFirst.next();
            if (printComments) {
                System.out.println("[" + firstNode.getId() + "]");
            }
            iterFirst.remove();

            if (!nodes.isEmpty()) {
                for (Iterator<Node> iterSec = nodes.iterator(); iterSec.hasNext();) {
                    Node secondNode = iterSec.next();
                    if (firstNode == secondNode) {
                        continue;
                    }
                    if (printComments) {
                        System.out.print("[" + firstNode.getId() + "] -> [" + secondNode.getId() + "] [" + maxDemMatrix.getDemand(firstNode, secondNode) + "] ");
                    }
                    route = Dijkstra.findRoute(firstNode, secondNode, maxDemMatrix.getDemand(firstNode, secondNode), net);

                    if (route == null) {
                        if (printComments) {
                            printGraph(net);
                        }
//                        throw new Exception("Nie znalazlem sciezki");
                        return null;
                    } else {
                        int i = Integer.parseInt(firstNode.getId());
                        int j = Integer.parseInt(secondNode.getId());
                        routes[i][j] = routes[j][i] = route;
                        if (printComments) {
                            printRoute(route);
                            System.out.println();
                        }
                    }
                }
            }
            if (printComments) {
                System.out.println("[/" + firstNode.getId() + "]");
            }
        }

        if (printComments) {
            System.out.println("Szukam sciezki dla reszty zapotrzebowan...");
        }
        RoutingPath routesBackup[][] = new RoutingPath[nodeCount][nodeCount];
        cloneRoutingTable(routes, routesBackup);
        Set<Link> failLinks = new HashSet<Link>();


        /* kopia sieci */
        for (Link link : net.links()) {
            capacityBackup.put(link.getId(), link.getPreCapacity());
        }
        matrixCounter = 0;
        int new_path_counter_local = 0;
        int new_path_counter_global = 0;
        int break_counter = 0;
        boolean break_matrix = false;
        /* sprawdzamy wszystkie macierze zapotrzebowan */

        for (DemandMatrix demandMatrix : demandMatrices.getMatrices()) {
            break_matrix = false;
            matrixCounter++;
            nodes.clear();
            for (Node n : net.nodes()) {
                nodes.add(n);
            }
            if (printComments) {
                demandMatrix.print();
            }
            for (Iterator<Node> iterFirst = nodes.iterator(); iterFirst.hasNext();) {
                Node firstNode = iterFirst.next();
                /* aby nie wyszukac sciezki w druga strone */
                iterFirst.remove();
                if (!nodes.isEmpty()) {
                    for (Iterator<Node> iterSec = nodes.iterator(); iterSec.hasNext();) {
                        Node secondNode = iterSec.next();
                        int i = Integer.parseInt(firstNode.getId());
                        int j = Integer.parseInt(secondNode.getId());

                        if (printComments) {
                            System.out.print("[" + firstNode.getId() + "] -> [" + secondNode.getId() + "] ");
                            printRoute(routes[i][j]);
                            System.out.println(" (" + demandMatrix.getDemand(firstNode, secondNode) + ") ");
                        }

                        double demand = demandMatrix.getDemand(firstNode, secondNode);

                        for (RoutingLink routingLink : routes[i][j].routingLinks()) {
                            Link link = net.getLink(routingLink.getLink().getId());

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
                                net.getLink(link.getId()).setPreCapacity(0);
                            }

                            for (Link link : failLinks) {
                                if (printComments) {
                                    System.out.println("Zeruje sciezke " + link.getId() + "[" + link.getFirstNode().getId() + "] -> [" + link.getSecondNode().getId() + "]");
                                }
                                if (capacityFailBackup.get(link.getId()) == net.getLink(link.getId()).getPreCapacity()) {
//                                    return null;
                                }
                            }

                            if (printComments) {
                                System.out.print("new path for [" + firstNode.getId() + "] -> [" + secondNode.getId() + "] [" + demandMatrix.getDemand(firstNode, secondNode) + "] ");
                                printGraph(net);
                            }

                            /* szukamy nowego polaczenia */
                            //System.out.println("F:"+firstNode+" S:"+secondNode+"\nnet:"+network);
                            route = Dijkstra.findRoute(firstNode, secondNode, demandMatrix.getDemand(firstNode, secondNode), net);
                            if (printComments) {
                                printRoute(route);
                                System.out.println();
                            }

                            //przywracamy stare przepustowosci
                            for (Link link : failLinks) {
                                net.getLink(link.getId()).setPreCapacity(capacityFailBackup.get(link.getId()));
                            }

                            capacityFailBackup.clear();
                            failLinks.clear();

                            if (route == null) {
//                                throw new Exception("Nie znalazlem sciezki");
                                break_matrix = true;
                                failLinks.clear();
                                break_counter++;
                                new_path_counter_local = 0;
                                break;
                            }
                            new_path_counter_local++;
                            routes[i][j] = routes[j][i] = route;
                        }

                        /* wszystkie krawedzie spelniaja zapotrzebowanie, wiec odejmujemy zapotrzebowania od nich */
                        for (RoutingLink routingLink : routes[i][j].routingLinks()) {
                            Link link = net.getLink(routingLink.getLink().getId());

                            /* czy wszystkie krawedzie spelniaja zapotrzebowanie */
                            link.setPreCapacity(link.getPreCapacity() - demand);
                        }
                        if (printComments) {
                            printGraph(net);
                        }
                    }
                }
                if (break_matrix) {
                    break;
                }
            }
            for (Link l : net.links()) {
                l.setPreCapacity(capacityBackup.get(l.getId()));
            }
            if (break_matrix) {
                cloneRoutingTable(routesBackup, routes);
            } else {
                new_path_counter_global += new_path_counter_local;
                cloneRoutingTable(routes, routesBackup);
                if (dmsWorking != null) {
                    dmsWorking.addDemandMatrix(demandMatrix);
                }
            }
            new_path_counter_local = 0;
        }

        for (Link l : net.links()) {
            net.getLink(l.getId()).setPreCapacity(capacityBackup.get(l.getId()));
        }

        long programEnd;
        if (!noTime) {
            programEnd = System.currentTimeMillis();
            solvingTime = programEnd - programStart;
        }
        if (printComments) {
            System.out.println("nowe sciezki: " + new_path_counter_global + "\n przerwalem: " + break_counter + "\n dla ilosci macierzy: " + matrixCounter);
        }

        return routesBackup;
    }

    public static RoutingPath[][] execute(Network net, DemandMatrices demandMatrices, boolean noTime) throws Exception {
        return execute(net, demandMatrices, noTime, null, false);
    }

    public static boolean checkExecute(Network network, DemandMatrices demandMatrices, boolean noTime, DemandMatrices dmsWorking, boolean printComments, RoutingPath[][] routesBackup) throws Exception {

        solvingTime = -1l;
        long programStart = 0;
        if (!noTime) {
            programStart = System.currentTimeMillis();
        }
        List<Node> nodes = new ArrayList<Node>();
        Map<String, Double> capacityBackup = new HashMap<String, Double>();
        int matrixCounter = 0;

        for (Node n : network.nodes()) {
            nodes.add(n);
        }

        int nodeCount = network.nodeCount();

        RoutingPath route = null;

        DemandMatrix maxDemMatrix = demandMatrices.getMaxDemandMatrix();

        if (printComments) {
            System.out.println("Szukam sciezki dla maksymalnych zapotrzebowan...");
            maxDemMatrix.print();
            printGraph(network);
        }

        /* sprawdzamy maksymalna macierz zapotrzebowan */
        for (Iterator<Node> iterFirst = nodes.iterator(); iterFirst.hasNext();) {
            Node firstNode = iterFirst.next();
            if (printComments) {
                System.out.println("[" + firstNode.getId() + "]");
            }
            iterFirst.remove();

            if (!nodes.isEmpty()) {
                for (Iterator<Node> iterSec = nodes.iterator(); iterSec.hasNext();) {
                    Node secondNode = iterSec.next();
                    if (firstNode == secondNode) {
                        continue;
                    }
                    if (printComments) {
                        System.out.print("[" + firstNode.getId() + "] -> [" + secondNode.getId() + "] [" + maxDemMatrix.getDemand(firstNode, secondNode) + "] ");
                    }
                    route = Dijkstra.findRoute(firstNode, secondNode, maxDemMatrix.getDemand(firstNode, secondNode), network);

                    if (route == null) {
                        if (printComments) {
                            printGraph(network);
                        }
                        throw new Exception("Nie znalazlem sciezki");
                    } else {
                        int i = Integer.parseInt(firstNode.getId());
                        int j = Integer.parseInt(secondNode.getId());
                        if (printComments) {
                            printRoute(route);
                            System.out.println();
                        }
                    }
                }
            }
            if (printComments) {
                System.out.println("[/" + firstNode.getId() + "]");
            }
        }

        if (routes == null) {
            return true;
        }

        if (printComments) {
            System.out.println("Szukam sciezki dla reszty zapotrzebowan...");
        }
        RoutingPath routes[][] = new RoutingPath[nodeCount][nodeCount];
        cloneRoutingTable(routesBackup, routes);
        Set<Link> failLinks = new HashSet<Link>();


        /* kopia sieci */
        for (Link link : network.links()) {
            capacityBackup.put(link.getId(), link.getPreCapacity());
        }
        matrixCounter = 0;
        int new_path_counter_local = 0;
        int new_path_counter_global = 0;
        int break_counter = 0;
        boolean break_matrix = false;
        /* sprawdzamy wszystkie macierze zapotrzebowan */

        for (DemandMatrix demandMatrix : demandMatrices.getMatrices()) {
            nodes.clear();
            for (Node n : network.nodes()) {
                nodes.add(n);
            }
            if (printComments) {
                demandMatrix.print();
            }
            for (Iterator<Node> iterFirst = nodes.iterator(); iterFirst.hasNext();) {
                Node firstNode = iterFirst.next();
                /* aby nie wyszukac sciezki w druga strone */
                iterFirst.remove();
                if (!nodes.isEmpty()) {
                    for (Iterator<Node> iterSec = nodes.iterator(); iterSec.hasNext();) {
                        Node secondNode = iterSec.next();
                        int i = Integer.parseInt(firstNode.getId());
                        int j = Integer.parseInt(secondNode.getId());

                        if (printComments) {
                            System.out.print("[" + firstNode.getId() + "] -> [" + secondNode.getId() + "] ");
                            printRoute(routes[i][j]);
                            System.out.println(" (" + demandMatrix.getDemand(firstNode, secondNode) + ") ");
                        }

                        double demand = demandMatrix.getDemand(firstNode, secondNode);

                        for (RoutingLink routingLink : routes[i][j].routingLinks()) {
                            Link link = network.getLink(getLinkName(routingLink.getLink(), network));

                            /* czy wszystkie krawedzie spelniaja zapotrzebowanie */
                            if (network == null || link == null) {
                                printGraph(network);
                            }

                            if (link.getPreCapacity() < demand) {
                                System.out.println("zludny sukces: " + link.getPreCapacity() + " " + network.getLink(link.getId()).getPreCapacity() + " " + link.getId());
//                                throw new Exception("OSZUKUJE!");
                                printGraph(network);

                                for (Link l : network.links()) {
                                    network.getLink(l.getId()).setPreCapacity(capacityBackup.get(l.getId()));
                                }

                                return false;
                            }
                        }
                        /* wszystkie krawedzie spelniaja zapotrzebowanie, wiec odejmujemy zapotrzebowania od nich */
                        for (RoutingLink routingLink : routes[i][j].routingLinks()) {
                            Link link = network.getLink(getLinkName(routingLink.getLink(), network));

                            /* czy wszystkie krawedzie spelniaja zapotrzebowanie */
                            link.setPreCapacity(network.getLink(link.getId()).getPreCapacity() - demand);
                        }
                        if (printComments) {
                            printGraph(network);
                        }
                    }
                }
            }
            for (Link l : network.links()) {
                network.getLink(l.getId()).setPreCapacity(capacityBackup.get(l.getId()));
            }
            new_path_counter_local = 0;
        }

        long programEnd;
        if (!noTime) {
            programEnd = System.currentTimeMillis();
            solvingTime = programEnd - programStart;
        }
        if (printComments) {
            System.out.println("nowe sciezki: " + new_path_counter_global + "\n przerwalem: " + break_counter + "\n dla ilosci macierzy: " + matrixCounter);
        }

        return true;
    }

    public static String getLinkName(Link link, Network net) throws Exception {
        String id = link.getId();
        if (net.getLink(id) != null) {
            return id;
        } else if (net.getLink(new StringBuffer(id).reverse().toString()) != null) {
            System.out.println("ODWRACAM net");
            return new StringBuffer(id).reverse().toString();
        } else {
            System.out.println("trying to get: " + link.getId());
            for (Link l : net.links()) {
                System.out.println("Link: " + l.getId());

            }
            throw new Exception("Niespojnosc danych w net przy getLinkName()!");
        }
    }

    public static String getLinkName(Link link, Map<String, Double> capacities) throws Exception {
        String id = link.getId();

        if (capacities.containsKey(id)) {
            return id;
        } else if (capacities.containsKey(new StringBuffer(id).reverse().toString())) {
            System.out.println("ODWRACAM cap");
            return new StringBuffer(id).reverse().toString();
        } else {
            throw new Exception("Niespojnosc danych w capacities przy getLinkName()!");
        }
    }

    public static Double getCapacityValue(Link link, Map<String, Double> capacities) throws Exception {
        return capacities.get(getLinkName(link, capacities));
    }

    public static void setCapacityValue(Link link, Map<String, Double> capacities, Double value) throws Exception {
        int size = capacities.size();
        capacities.put(getLinkName(link, capacities), value);
        if (size != capacities.size()) {
            throw new Exception("Niespojnosc danych w capacities przy setCapacityValue!");
        }
    }

    public static RoutingPath[][] properExecute(Network net, DemandMatrices demandMatrices, boolean noTime, DemandMatrices dmsWorking, boolean printComments) throws Exception {
        againCounter = 0;
        solvingTime = -1l;
        long programStart = 0;
        if (!noTime) {
            programStart = System.currentTimeMillis();
        }
        int againcounter = 0;
        int test = 0;
        List<Node> nodes = new ArrayList<Node>();
        Map<String, Double> capacityBackup = new HashMap<String, Double>();

        /* kopia sieci */
        for (Link link : net.links()) {
            capacityBackup.put(link.getId(), link.getPreCapacity());
        }

        int matrixCounter = 0;

        for (Node n : net.nodes()) {
            nodes.add(n);
        }
        Node n[] = new Node[net.nodeCount()];
        matrixCounter = 0;
        for (Node node : net.nodes()) {
            n[matrixCounter++] = node;
        }

        int nodeCount = net.nodeCount();
        RoutingPath[][] routes = new RoutingPath[nodeCount][nodeCount];
        clearRoutingTable(routes);

        RoutingPath route = null;

        DemandMatrix maxDemMatrix = demandMatrices.getMaxDemandMatrix();

        if (printComments) {
            System.out.println("Szukam sciezki dla maksymalnych zapotrzebowan...");
            maxDemMatrix.print();
        }

        /* sprawdzamy maksymalna macierz zapotrzebowan */
        for (Iterator<Node> iterFirst = nodes.iterator(); iterFirst.hasNext();) {
            Node firstNode = iterFirst.next();
            if (printComments) {
                System.out.println("[" + firstNode.getId() + "]");
            }
            iterFirst.remove();

            if (!nodes.isEmpty()) {
                for (Iterator<Node> iterSec = nodes.iterator(); iterSec.hasNext();) {
                    Node secondNode = iterSec.next();
                    if (firstNode == secondNode) {
                        continue;
                    }
                    if (printComments) {
                        System.out.print("[" + firstNode.getId() + "] -> [" + secondNode.getId() + "] [" + maxDemMatrix.getDemand(firstNode, secondNode) + "] ");
                    }
                    route = Dijkstra.findRoute(firstNode, secondNode, maxDemMatrix.getDemand(firstNode, secondNode), net);

                    if (route == null) {
                        if (printComments) {
                            printGraph(net);
                        }
                        throw new Exception("Nie znalazlem sciezki");
                    } else {
                        int i = Integer.parseInt(firstNode.getId());
                        int j = Integer.parseInt(secondNode.getId());
                        routes[i][j] = routes[j][i] = route;
                        if (printComments) {
                            printRoute(route);
                            System.out.println();
                        }
                    }
                }
            }
            if (printComments) {
                System.out.println("[/" + firstNode.getId() + "]");
            }
        }

        /* sprawdzamy wszystkie macierze zapotrzebowan */
        if (printComments) {
            System.out.println("Szukam sciezki dla reszty zapotrzebowan...");
        }
        RoutingPath routesBackup[][] = new RoutingPath[nodeCount][nodeCount];
        cloneRoutingTable(routes, routesBackup);
        Set<Link> failLinks = new HashSet<Link>();



        int new_path_counter_local = 0;
        int new_path_counter_global = 0;
        int break_counter = 0;

        DemandMatrix matrices[] = new DemandMatrix[demandMatrices.countMatrices()];
        matrixCounter = 0;
        for (DemandMatrix dm : demandMatrices.getMatrices()) {
            matrices[matrixCounter++] = dm;
        }
        Map<String, Double> capacities[] = new HashMap[demandMatrices.countMatrices()];
        //macierz z kopia aktualnych przepustowosci
        //capacities[index-1] == capacityFailBackup[index]
        Map<String, Double> capacityFailBackup[] = new HashMap[demandMatrices.countMatrices()];
        matrixCounter = 0;

        //do kazdej macierzy zapotrzebowan przypozadkowujemy siec z aktualnie "puszczonym" ruchem zapotrzebowan
        //kolejnej pary nodow
        int ic = 0, jc = 0, zc = 0;
        for (zc = 0; zc < matrices.length; zc++) {
            capacities[zc] = new HashMap<String, Double>();
            capacityFailBackup[zc] = new HashMap<String, Double>();
            for (Link link : net.links()) {
                capacities[zc].put(link.getId(), link.getPreCapacity());
                capacityFailBackup[zc].put(link.getId(), link.getPreCapacity());
            }
        }

        for (ic = 0; ic < n.length; ic++) {
            Node firstNode = n[ic];
            for (jc = ic + 1; jc < n.length; jc++) {
                Node secondNode = n[jc];
                int i = Integer.parseInt(firstNode.getId());
                int j = Integer.parseInt(secondNode.getId());
                boolean spr = false, found = false;

                for (zc = 0; zc < matrices.length; zc++) {
                    int demand = matrices[zc].getDemand(firstNode, secondNode);

                    if (capacities[zc].size() != capacityFailBackup[zc].size()) {
                        for (String key1S : capacities[zc].keySet()) {
                            System.out.println("cap : " + key1S);
                        }
                        for (String key2S : capacityFailBackup[zc].keySet()) {
                            System.out.println("capback : " + key2S);
                        }
                        throw new Exception("niespojnosc danych properExecute capacities[zc].size() != capacityFailBackup[zc].size()");
                    }
                    for (String key : capacities[zc].keySet()) {
                        if (!capacityFailBackup[zc].containsKey(key)) {
                            for (String key1S : capacities[zc].keySet()) {
                                System.out.println("cap : " + key1S);
                            }
                            for (String key2S : capacityFailBackup[zc].keySet()) {
                                System.out.println("capback : " + key2S);
                            }
                            throw new Exception("niespojnosc danych properExecute !capacityFailBackup[zc].containsKey(key)");
                        }
                    }

                    test++;
//                    System.out.println(ic + " " + jc + " " + zc);
//                    printGraph(net);

                    if (printComments) {
                        System.out.print("[" + firstNode.getId() + "] -> [" + secondNode.getId() + "] ");
                        printRoute(routes[i][j]);
                        System.out.println(" (" + demand + ") ");
                    }

                    //uzupelniamy siec o aktualne przepustowosci
                    for (Link link : net.links()) {
                        link.setPreCapacity(getCapacityValue(link, capacities[zc]));
                    }

                    //sprawdzamy czy kazda krawedz spelnia zapotrzebowanie
                    for (RoutingLink routingLink : routes[i][j].routingLinks()) {

                        Link link = net.getLink(getLinkName(routingLink.getLink(), capacities[zc]));

                        if (link == null) {
                            printGraph(net);
                            System.out.println(getLinkName(routingLink.getLink(), capacities[zc]));
                        }
                        if (net.getLink(link.getId()).getPreCapacity() < demand) {
//                            printGraph(net);

//                            System.out.println("pre: " + link.getPreCapacity() + " " + net.getLink(link.getId()).getPreCapacity() + " " + link.getId());
                            if (!failLinks.contains(link)) {
                                failLinks.add(link);
                                found = true;
                            }
                        }
                    }
                    if (!failLinks.isEmpty() && found) {
                        againcounter++;
                        /* usuwamy przepelnione krawedzie */

//                        System.out.println("v v v v v v v v v v v v v v v v v v v v v v v v ");
//                        System.out.println("aktualny graf: ");
//                        printGraph(net);
                        for (Link link : failLinks) {
                            net.getLink(link.getId()).setPreCapacity(0);
                        }

                        for (Link link : failLinks) {
                            if (printComments) {
//                                System.out.println("Zeruje sciezke " + link.getId() + "[" + link.getFirstNode().getId() + "] -> [" + link.getSecondNode().getId() + "]");
                            }
                        }

//                        if (printComments) {
//                        System.out.print("current path [" + firstNode.getId() + "] -> [" + secondNode.getId() + "] ");
//                        printRoute(routes[i][j]);
//                        System.out.println(" demand: (" + demand + ") ");
//
//                        System.out.println("new path for [" + firstNode.getId() + "] -> [" + secondNode.getId() + "] [" + demand + "] demand matrix: " + zc);
//                        printGraph(net);

//                        }

                        /* szukamy nowego polaczenia */
                        //System.out.println("F:"+firstNode+" S:"+secondNode+"\nnet:"+network);
                        route = Dijkstra.findRoute(firstNode, secondNode, demand, net);

                        if (route == null) {
//                            System.out.println("test " + test + " " + ic + " " + jc + " " + zc);

                            for (Link l : net.links()) {
                                net.getLink(l.getId()).setPreCapacity(capacityBackup.get(l.getId()));
                            }
                            againCounter = -againcounter;
//                            throw new Exception("Nie znalazlem sciezki");
                            return null;

                        }
                        routes[i][j] = routes[j][i] = route;

//                        if (printComments) {
//                        System.out.println("nowa: ");
//                        printRoute(route);
//                        System.out.println();
//                        System.out.println("^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ");
//                        }

                        new_path_counter_local++;

                        //przywracamy przepustowosci z przed wykonania macierzy dla i,j
                        for (Link link : net.links()) {
                            for (matrixCounter = 0; matrixCounter <= zc; matrixCounter++) {
                                capacities[matrixCounter].put(getLinkName(link, capacities[matrixCounter]), capacityFailBackup[matrixCounter].get(getLinkName(link, capacities[matrixCounter])));
                            }
                        }

                        found = false;
                        if (zc != 0) {
                            spr = true;
                            break;
                        }
                    }

                    /* wszystkie krawedzie spelniaja zapotrzebowanie, wiec odejmujemy zapotrzebowania od nich */
                    for (RoutingLink routingLink : routes[i][j].routingLinks()) {
                        Link link = net.getLink(getLinkName(routingLink.getLink(), capacities[zc]));

                        /* czy wszystkie krawedzie spelniaja zapotrzebowanie */
                        capacities[zc].put(getLinkName(routingLink.getLink(), capacities[zc]), link.getPreCapacity() - demand);
                    }
                    if (printComments) {
                        printGraph(net);
                    }
                    if (capacities[zc].size() != capacityFailBackup[zc].size()) {
                        for (String key1S : capacities[zc].keySet()) {
                            System.out.println("cap : " + key1S);
                        }
                        for (String key2S : capacityFailBackup[zc].keySet()) {
                            System.out.println("capback : " + key2S);
                        }
                        throw new Exception("niespojnosc danych properExecute capacities[zc].size() != capacityFailBackup[zc].size()");
                    }
                    for (String key : capacities[zc].keySet()) {
                        if (!capacityFailBackup[zc].containsKey(key)) {
                            for (String key1S : capacities[zc].keySet()) {
                                System.out.println("cap : " + key1S);
                            }
                            for (String key2S : capacityFailBackup[zc].keySet()) {
                                System.out.println("capback : " + key2S);
                            }
                            throw new Exception("niespojnosc danych properExecute !capacityFailBackup[zc].containsKey(key)");
                        }
                    }
                }

                if (!spr) {
                    for (matrixCounter = 0; matrixCounter < matrices.length; matrixCounter++) {
                        for (String id : capacities[matrixCounter].keySet()) {
                            capacityFailBackup[matrixCounter].put(id, capacities[matrixCounter].get(id));
                        }
                    }
                    failLinks.clear();
                }
                if (spr) {
//                    System.out.println("test " + test + " " + ic + " " + jc + " " + zc);
                    jc--;
                }
            }
        }

        for (Link l : net.links()) {
            net.getLink(l.getId()).setPreCapacity(capacityBackup.get(l.getId()));
        }

        long programEnd;
        if (!noTime) {
            programEnd = System.currentTimeMillis();
            solvingTime = programEnd - programStart;
        }
        if (printComments) {
            System.out.println("nowe sciezki: " + new_path_counter_global + "\n przerwalem: " + break_counter + "\n dla ilosci macierzy: " + matrixCounter);

        }
        if (capacityBackup.size() != net.linkCount()) {
            throw new Exception("niespojnosc danych: count " + capacityBackup.size() + " " + net.linkCount());
        }
        for (Link link : net.links()) {
            if (capacityBackup.get(link.getId()) != link.getPreCapacity()) {

                throw new Exception("niespojnosc danych: LinkID " + link.getId() + " backup: " + capacityBackup.get(link.getId()) + " orig: " + link.getPreCapacity());
            }
        }

//        System.out.println("znalezione sciezki alternatywne: " + againcounter);
        if (againcounter > 0) {
//            throw new Exception("SUKCES!!");
        }
        againCounter = againcounter;
        return routes;
    }
}
