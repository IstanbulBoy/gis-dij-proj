package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
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

    public static void main(String[] args) {
        RoutingPath routes[][] = null;
        File file = new File("statystyki.txt");
        PrintStream fileStream = new PrintStream(System.out);
        try {
            file.createNewFile();
            fileStream = new PrintStream(file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        DemandMatrices dms = new DemandMatrices();
        DemandMatrices dmsWorking = new DemandMatrices();
        int i = 100;
        try {
//            loadConfig("config.txt", net, dms, true);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            //System.out.println("Szukam dobrych macierzy...");
            int nodes = 5;
            while (nodes < 6) {
                for (int number = 10; number > 0; number--) {
                    dmsWorking.clear();
                    dms.clear();
                    net = GraphGenerator.generate(nodes, 0.3, 20, 30);
                    DemandMatrices randDMs = null;
                    while (dmsWorking.countMatrices() < 300) {
                        dmsWorking.clear();
                        dms.clear();
                        dms = ProblemGenerator.genDemandMatrices(net, 1000, 1.0, 10.0);
                        randDMs = dms.getRandMatrices(300);
                        ProblemGenerator.genNetwork(net, randDMs);
                        //usuwam macierze ktore posluzyly do tworzenia sieci
                        for (DemandMatrix dm : randDMs.getMatrices()) {
                            dms.removeDemandMatrix(dm);
                        }
                        //wyluskuje te ktore sa spelnialne dla sieci
                        Algorithm.execute(net, dms, true, dmsWorking, false);
                    }

                    dmsWorking = dmsWorking.getSubDemandMatrices(300);
                    //System.out.println("vvvvvvvvv");
                    if ((routes = Algorithm.properExecute(net, dmsWorking, false, null, false)) == null) {
                        return;
                    }
                    ProblemGenerator.genNetwork(net, randDMs);
                    if (Algorithm.checkExecute(net, dmsWorking, false, null, false, routes)) {
                        return;
                    }
                    //System.out.println("^^^^^^^^^");
                    Stat.addStatistics();
                }
                nodes++;
                Stat.generateStatistics(fileStream);
            }
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("STAT:\n");
        //Stat.generateStatistics();



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
