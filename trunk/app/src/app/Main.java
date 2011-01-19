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
    static int MATRICES_COUNT = 300;

    enum BlockType {

        NONE, GRAPH, MATRIX
    };

    public static void main(String[] args) {
        RoutingPath routing[][] = null;
        File file = new File("statystyki.txt");
        PrintStream fileStream = new PrintStream(System.out);
        try {
            file.createNewFile();
            fileStream = new PrintStream(file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//
//        try {
//            loadConfig("config.txt", net, dms, true);
//        } catch (Exception ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }

        int ic, jc;
        try {
            int nodes = 5;
            while (nodes < 21) {
                DemandMatrices dms = new DemandMatrices();
                DemandMatrices dmsWorking = new DemandMatrices();
                DemandMatrices randDms = null;

                if (routing != null) {
                    for (ic = 0; ic < routing.length; ic++) {
                        for (jc = 0; jc < routing.length; jc++) {
                            routing[ic][jc] = null;
                        }
                    }
                }

                net = GraphGenerator.generate(nodes, 0.3, 20, 30);

                while (dmsWorking.countMatrices() < MATRICES_COUNT) {
                    dmsWorking.clear();
                    dms.clear();
                    dms = ProblemGenerator.genDemandMatrices(net, 1000, 10, 100);
                    randDms = dms.getRandMatrices(500);
                    ProblemGenerator.genNetwork(net, randDms);
                    //usuwa macierze ktore posluzyly do tworzenia sieci
                    for (DemandMatrix dm : randDms.getMatrices()) {
                        dms.removeDemandMatrix(dm);
                    }
                    //wyluskuje te ktore sa spelnialne dla sieci
                    if (Algorithm.extractWorkingMatrices(net, dms, dmsWorking, true, false) == null) {
                        continue;
                    }
                }
                dmsWorking = dmsWorking.getSubDemandMatrices(MATRICES_COUNT);

                ProblemGenerator.genNetwork(net, randDms);
                routing = Algorithm.findRouting(net, dmsWorking.getRandMatrices(MATRICES_COUNT), false, null, false);
                if (routing == null) {
                    //nie znaleziono routingu dla tych macierzy
                    continue;
                }

                ProblemGenerator.genNetwork(net, randDms);
                if (Algorithm.checkRouting(net, dmsWorking, false, null, false, routing) == false) {
                    ProblemGenerator.genNetwork(net, randDms);
                    Algorithm.checkRouting(net, dmsWorking, false, null, true, routing);
                    throw new Exception("Algorytm okreslil nieprawidlowy routing!");
                }

                System.out.println("wezlow: " + nodes);
                System.out.println("z nowymi sciezkami: " + Algorithm.againCounter);

                nodes++;
                Stat.addStatistics();
//                Stat.generateStatistics(fileStream);
            }
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("STAT:\n");
        Stat.generateStatistics();
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
                                } //                                System.out.println(line + block);
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
        for (n = 1; n
                < nodes.length + 1; n++) {
            String m[] = lines[n].split("\\s+");
            for (i = 1; i
                    < nodes.length; i++) {
                dm.addDemand(Integer.valueOf(m[i]), m[0], nodes[i - 1]);
            }
        }

        return dm;

    }
}
