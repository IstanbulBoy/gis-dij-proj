package app;

import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.network.Node;

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
        net.newNode("1");
        net.newNode("2");
        net.newNode("3");
        net.newNode("4");
        net.newNode("5");

        dm = getdm(55d);
        for (Link link : net.links()) {
            link.setPreCapacity(dm.getDemand(link.getFirstNode(), link.getSecondNode()));
        }

        dms.addDemandMatrix(getdm(1d));
        dms.addDemandMatrix(getdm(2d));
        dms.addDemandMatrix(getdm(3d));
        dms.addDemandMatrix(getdm(4d));
        dms.addDemandMatrix(getdm(5d));

        dm = dms.getMaxDemandMatrix();

        int i,j;
        for (i=1; i < 6; i++) {
            for (j=1; j < 6; j++) {
                System.out.print(dm.getDemand(net.getNode(i+""), net.getNode(j+"")));
                System.out.print("    ");
            }
            System.out.println();
        }
    }

    public static DemandMatrix getdm(double demand) {
        DemandMatrix dm = new DemandMatrix();

        dm.addDemand(demand++, net.getNode("1"), net.getNode("2"));
        dm.addDemand(demand++, net.getNode("1"), net.getNode("3"));
        dm.addDemand(demand++, net.getNode("1"), net.getNode("4"));
        dm.addDemand(demand++, net.getNode("1"), net.getNode("5"));
        dm.addDemand(demand++, net.getNode("2"), net.getNode("3"));
        dm.addDemand(demand++, net.getNode("2"), net.getNode("4"));
        dm.addDemand(demand++, net.getNode("2"), net.getNode("5"));
        dm.addDemand(demand++, net.getNode("3"), net.getNode("4"));
        dm.addDemand(demand++, net.getNode("3"), net.getNode("5"));
        dm.addDemand(demand++, net.getNode("4"), net.getNode("5"));

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
