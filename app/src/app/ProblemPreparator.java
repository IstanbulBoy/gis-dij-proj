/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.network.Node;
import sndlib.core.problem.RoutingLink;
import sndlib.core.problem.RoutingPath;

/**
 *
 * @author hmsck
 */
public class ProblemPreparator {

    public DemandMatrices genMatrices() {
        DemandMatrices dms = new DemandMatrices();

        return dms;
    }

    public void genNetwork(Network network, DemandMatrices demandMatrices) {
        List<Node> nodes = new ArrayList<Node>();
        RoutingPath routes[][] = new RoutingPath[network.nodeCount()][network.nodeCount()];

        /* ustawiamy przepustowosc poszczegolnych link-ow na maksymalna,
        aby algorytm Dijkstry zawsze znalazl najtansza sciezke */
        for (Link link : network.links()) {
            link.setPreCapacity(Double.MAX_VALUE);
        }

        for (DemandMatrix demandMatrix : demandMatrices.getMatrices()) {
            nodes.clear();
            for (Node n : network.nodes()) {
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

                        double demand = demandMatrix.getDemand(firstNode, secondNode);

                        routes[i][j] = routes[j][i] =
                                Dijkstra.findRoute(firstNode, secondNode, demandMatrix.getDemand(firstNode, secondNode), network);

                        for (RoutingLink routingLink : routes[i][j].routingLinks()) {
                            Link link = routingLink.getLink();
                            link.setPreCapacity(link.getPreCapacity() - demand);
                        }
                    }
                }
            }
        }
        /* ustawiamy prawidlowa przepustowosc na krawedziach */
        for (Link link : network.links()) {
            link.setPreCapacity(Double.MAX_VALUE - link.getPreCapacity());
        }
    }

    public DemandMatrices genDemandMatrices(Network network, int matricesCount, double minValue, double maxValue) {
        DemandMatrices dms = new DemandMatrices();
        double val = maxValue - minValue;


        while (matricesCount-- > 0) {
            DemandMatrix m = new DemandMatrix();
            for (Node n1 : network.nodes()) {
                for (Node n2 : network.nodes()) {
                    m.addDemand((Math.random()*val)+minValue, n1, n2);
                }
            }
            dms.addDemandMatrix(m);
        }

        return dms;
    }
}
