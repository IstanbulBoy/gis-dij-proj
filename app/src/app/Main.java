package app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.network.Node;
import sndlib.core.problem.RoutingPath;

/**
 *
 * @author hmsck
 */
public class Main {

    static Network net = new Network();

    enum BlockType {

        NONE, GRAPH, MATRIX
    };

    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
//        System.out.println("Main");
//        DemandMatrix dm = null;
//        DemandMatrices dms = new DemandMatrices();
//        Node n1 = net.newNode("0");
//        Node n2 = net.newNode("1");
//        Node n3 = net.newNode("2");
//        Node n4 = net.newNode("3");
//        Node n5 = net.newNode("4");
//        net.newLink("a", n1, n2).setPreCapacity(50d);
//        net.newLink("b", n1, n3).setPreCapacity(50d);
//        net.newLink("c", n4, n2).setPreCapacity(50d);
//        net.newLink("d", n4, n5).setPreCapacity(50d);
//        net.newLink("e", n3, n5).setPreCapacity(50d);
//
//        dm = getdm(1d);
//        for (Link link : net.links()) {
//            link.setPreCost(dm.getDemand(link.getFirstNode(), link.getSecondNode()));
//        }
//
//        dms.addDemandMatrix(getdm(5d));
//        dms.addDemandMatrix(getdm(2d));
//        dms.addDemandMatrix(getdm(3d));
//        dms.addDemandMatrix(getdm(4d));
//        dms.addDemandMatrix(getdm(5d));
//
//        try {
//            RoutingPath[][] routes = Algorithm.execute(net, dms);
//        } catch (Exception ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//            Algorithm.printResult();
//        }
//    }
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

    public static void main(String[] args) {
        DemandMatrices dms = new DemandMatrices();
        
        try {
            loadConfig("config.txt", net, dms, true);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        dms = ProblemGenerator.genDemandMatrices(net, 100, 1.0, 10.0);
        ProblemGenerator.genNetwork(net, dms.getRandMatrices(20));

        try {
            RoutingPath[][] routes = Algorithm.execute(net, dms);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//  public static void main(String[] args) {
//	System.out.println("Graf:\n");
//	Algorithm.printGraph(GraphGenerator.generate(20, 0.1, 20, 30)); //GraphGenerator.generate(10, 0.1);
//}

    static public void loadConfig(String filename, Network network, DemandMatrices matrices, boolean onlyGraph) throws Exception {
        FileReader fr = null;
        try {
            fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            String line;
            BlockType block = BlockType.NONE;
            int tmpc = 0, tmpn = 0;
            boolean blockend = false;
            String lines = "";

            while ((line = br.readLine()) != null) {
                line.trim();
                line.toLowerCase();
                if (line.length() == 0 || line.charAt(0) == '#') {
                    continue;
                } else {
                    if ((line.equals("{") || line.equals("}")) && block == BlockType.NONE) {
                        throw new Exception("Nie rozumiem pliku konfiguracyjnego");
                    } else if (line.equals("}") && block != BlockType.NONE) {
                        if (tmpc != tmpn) {
                            throw new Exception("Nie rozumiem pliku konfiguracyjnego w sekcji " + block);
                        }
                        tmpc = tmpn = 0;
                        blockend = true;
                    } else if (line.equals("{")) {
                        continue;
                    }
                    if (block != BlockType.NONE) {
                        String tmp[];
                        if (!blockend) {
                            tmpc++;
                        }
                        switch (block) {
                            case GRAPH:
                                if (!blockend) {
                                    tmpn = tmpc;
                                    tmp = line.split("\\s+");
                                    if (tmp.length != 5) {
                                        throw new Exception("Nie rozumiem pliku konfiguracyjnego w sekcji GRAPH");
                                    } else {
                                        Node n1, n2;
                                        if ((n1 = network.getNode(tmp[0])) == null) {
                                            n1 = network.newNode(tmp[0]);
                                        }
                                        if ((n2 = network.getNode(tmp[1])) == null) {
                                            n2 = network.newNode(tmp[1]);
                                        }
                                        Link l = network.newLink(tmp[2], n1, n2);

                                        l.setPreCapacity(Double.valueOf(tmp[3]));
                                        l.setPreCost(Double.valueOf(tmp[4]));
                                    }
                                }
                                break;
                            case MATRIX:
                                tmpn = network.nodeCount() + 1;
                                if (onlyGraph) {
                                    break;
                                }
                                
                                if (!blockend) {
                                    lines += line + '\n';
                                } else {
                                    matrices.addDemandMatrix(genDemandMatrix(lines));
                                    lines = "";
                                }
//                                System.out.println(line + block);
                                break;
                            default:
                                break;
                        }
                    } else {
                        if (line.startsWith("graph")) {
                            block = BlockType.GRAPH;
                        } else if (line.startsWith("matrix")) {
                            block = BlockType.MATRIX;
                        } else {
                            throw new Exception("Nie rozumiem pliku konfiguracyjnego");
                        }
                    }
                    if (blockend) {
                        block = BlockType.NONE;
                        blockend = false;
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static DemandMatrix genDemandMatrix(String matrix) {
        DemandMatrix dm = new DemandMatrix();
        String lines[] = matrix.split("\\n");
        String nodes[] = lines[0].trim().split("\\s+");
        int i, n;

        for (String node : nodes) {
            dm.addNode(node);
        }
        for (n = 1; n < nodes.length + 1; n++) {
            String m[] = lines[n].split("\\s+");

            for (i = 1; i < nodes.length; i++) {
                dm.addDemand(Double.valueOf(m[i]), m[0], nodes[i - 1]);
            }
        }

        return dm;
    }
}
