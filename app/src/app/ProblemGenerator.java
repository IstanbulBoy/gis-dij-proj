/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.network.Node;
import sndlib.core.problem.RoutingLink;
import sndlib.core.problem.RoutingPath;

/**
 *
 * @author hmsck
 */
public class ProblemGenerator {

    public static void genNetwork(Network network, DemandMatrices demandMatrices) {
        List<Node> nodes = new ArrayList<Node>();
        RoutingPath routes[][] = new RoutingPath[network.nodeCount()][network.nodeCount()];
        DemandMatrix dmtmp = demandMatrices.getMatrices().get(0);
        Map<String, Integer> capacityBackup[] = new HashMap[demandMatrices.countMatrices()];
        int matrixCounter = 0;

        /* ustawiamy przepustowosc poszczegolnych link-ow na maksymalna,
        aby algorytm Dijkstry zawsze znalazl najtansza sciezke */
        for (Link link : network.links()) {
            link.setPreCapacity(Double.MAX_VALUE);
        }
        for (matrixCounter = 0; matrixCounter < demandMatrices.countMatrices(); matrixCounter++) {
            capacityBackup[matrixCounter] = new HashMap<String, Integer>();
            for (Link link : network.links()) {
                capacityBackup[matrixCounter].put(link.getId(), 0);
            }
        }

        nodes.clear();
        for (Node n : network.nodes()) {
            nodes.add(n);
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

                    int demand = dmtmp.getDemand(firstNode, secondNode);

                    //najlepszy routing - bierzemy tylko pod uwage koszty
                    routes[i][j] = routes[j][i] =
                            Dijkstra.findRoute(firstNode, secondNode, demand, network);
                }
            }
        }

        matrixCounter = 0;
        for (DemandMatrix demandMatrix : demandMatrices.getMatrices()) {
            nodes.clear();
            for (Node n : network.nodes()) {
                nodes.add(n);
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

                        int demand = demandMatrix.getDemand(firstNode, secondNode);

                        for (RoutingLink routingLink : routes[i][j].routingLinks()) {
                            Link link = routingLink.getLink();
                            capacityBackup[matrixCounter].put(link.getId(), capacityBackup[matrixCounter].get(link.getId()) + demand);
                        }
                    }
                }
            }
            matrixCounter++;
        }
        /* ustawiamy prawidlowa przepustowosc na krawedziach */
        for (Link link : network.links()) {
            for (matrixCounter = 0; matrixCounter < demandMatrices.countMatrices(); matrixCounter++) {
                if (matrixCounter == 0) {
                    link.setPreCapacity(capacityBackup[matrixCounter].get(link.getId()));
                } else {
                    if (link.getPreCapacity() < capacityBackup[matrixCounter].get(link.getId())) {
                        link.setPreCapacity(capacityBackup[matrixCounter].get(link.getId()));
                    }
                }
            }
        }
//        for (Link link : network.links()) {
//            for (matrixCounter = 0; matrixCounter < demandMatrices.countMatrices(); matrixCounter++) {
//                if (matrixCounter == 0) {
//                    link.setPreCapacity(capacityBackup[matrixCounter].get(link.getId()));
//                } else {
//                    if (link.getPreCapacity() < capacityBackup[matrixCounter].get(link.getId())) {
//                        link.setPreCapacity(capacityBackup[matrixCounter].get(link.getId()));
//                    }
//                }
//            }
//        }
//        for (Link link : network.links()) {
//            for (matrixCounter = 0; matrixCounter < demandMatrices.countMatrices(); matrixCounter++) {
//                if (matrixCounter == 0) {
//                    link.setPreCapacity(capacityBackup[matrixCounter].get(link.getId()));
//                } else {
//                    if (link.getPreCapacity() < capacityBackup[matrixCounter].get(link.getId())) {
//                        link.setPreCapacity(capacityBackup[matrixCounter].get(link.getId()));
//                    }
//                }
//            }
//        }
    }

    public static DemandMatrices genDemandMatrices(Network network, int matricesCount, int minValue, int maxValue) {
        DemandMatrices dms = new DemandMatrices();
        int val = maxValue - minValue;
        Random gen = new Random();

        while (matricesCount-- > 0) {
            DemandMatrix m = new DemandMatrix();
            for (Node n1 : network.nodes()) {
                for (Node n2 : network.nodes()) {
                    m.addDemand(gen.nextInt(val) + minValue, n1, n2);
                }
            }
            dms.addDemandMatrix(m);
        }

        return dms;
    }
}