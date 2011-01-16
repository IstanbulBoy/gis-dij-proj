package app;

import java.util.logging.Level;
import java.util.logging.Logger;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.network.Node;
import sndlib.core.problem.RoutingLink;
import sndlib.core.problem.RoutingPath;

/**
 *
 * @author hmsck
 */
public class Main {

    static Network net = new Network();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Main");
        DemandMatrix dm = null;
        DemandMatrices dms = new DemandMatrices();
        Node n1 = net.newNode("0");
        Node n2 = net.newNode("1");
        Node n3 = net.newNode("2");
        Node n4 = net.newNode("3");
        Node n5 = net.newNode("4");
        net.newLink("a", n1, n2).setPreCapacity(50d);
        net.newLink("b", n1, n3).setPreCapacity(50d);
        net.newLink("c", n4, n2).setPreCapacity(50d);
        net.newLink("d", n4, n5).setPreCapacity(50d);
        net.newLink("e", n3, n5).setPreCapacity(50d);

        dm = getdm(1d);
        for (Link link : net.links()) {
            link.setPreCost(dm.getDemand(link.getFirstNode(), link.getSecondNode()));
        }

        dms.addDemandMatrix(getdm(5d));
        dms.addDemandMatrix(getdm(2d));
        dms.addDemandMatrix(getdm(3d));
        dms.addDemandMatrix(getdm(4d));
        dms.addDemandMatrix(getdm(5d));

        try {
           RoutingPath[][] routes = Algorithm.execute(net, dms);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        Algorithm.printResult();
    }

    public static DemandMatrix getdm(double demand) {
        DemandMatrix dm = new DemandMatrix();

        dm.addDemand(demand++, net.getNode("0"), net.getNode("1"));
        dm.addDemand(demand++, net.getNode("0"), net.getNode("2"));
        dm.addDemand(demand++, net.getNode("0"), net.getNode("3"));
        dm.addDemand(demand++, net.getNode("0"), net.getNode("4"));
        dm.addDemand(demand++, net.getNode("1"), net.getNode("2"));
        dm.addDemand(demand++, net.getNode("1"), net.getNode("3"));
        dm.addDemand(demand++, net.getNode("1"), net.getNode("4"));
        dm.addDemand(demand++, net.getNode("2"), net.getNode("3"));
        dm.addDemand(demand++, net.getNode("2"), net.getNode("4"));
        dm.addDemand(demand++, net.getNode("3"), net.getNode("4"));

//        Node n1 = net.newNode("1");
//        Node n2 = net.newNode("2");
//        Node n3 = net.newNode("3");
//        Node n4 = net.newNode("4");
//        Node n5 = net.newNode("5");
//        dm.addDemand(55d, n1, n2);
//        dm.addDemand(55d, n1, n3);
//        dm.addDemand(55d, n1, n4);
//        dm.addDemand(55d, n1, n5);
//        dm.addDemand(55d, n2, n3);
//        dm.addDemand(55d, n2, n4);
//        dm.addDemand(55d, n2, n5);
//        dm.addDemand(55d, n3, n4);
//        dm.addDemand(55d, n3, n5);
//        dm.addDemand(55d, n4, n5);
        return dm;
    }
}
