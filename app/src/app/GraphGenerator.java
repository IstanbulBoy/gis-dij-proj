package app;

import sndlib.core.network.*;
import sndlib.core.problem.RoutingPath.*;

public class GraphGenerator {

    static public Network generate(int nodes, double percent, double costMin, double costMax) {
        Network net = new Network();
        Integer numberOfNodes = nodes;
        for (Integer j = 0; j < numberOfNodes; j++) {
            net.newNode(j.toString());
        }
        for (int i = 1; i < numberOfNodes; i++) {
            Integer connectTo = (int) Math.round(Math.random() * (i - 1));
            net.newLink(i + "a" + connectTo, net.getNode(String.valueOf(i)), net.getNode(connectTo.toString())).setPreCost(Math.random() * (costMax - costMin) + costMin);
        }

        int numberOfCons = (int) Math.round(percent * nodes) - 1;
        if (numberOfCons > 0) {
            for (int j = numberOfNodes - 1; j > -1; j--) {
                for (int i = numberOfCons; i > 0; i--) {
                    Integer connectTo = (int) Math.round(Math.random() * (nodes - 1));
                    if (!(net.hasLink(j + "a" + connectTo) || net.hasLink(connectTo + "a" + j) || j == connectTo)) {
                        net.newLink(j + "a" + connectTo, net.getNode(String.valueOf(j)), net.getNode(connectTo.toString())).setPreCost(Math.random() * (costMax - costMin) + costMin);
                    }
                }
            }
        }

        return net;
    }
}
