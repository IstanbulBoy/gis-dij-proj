package app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import sndlib.core.network.Node;

/**
 *
 * @author hmsck
 */
public class DemandMatrix {

    protected static int MAX_NODES = 100;
    protected double[][] matrix = new double[MAX_NODES][MAX_NODES];
    protected List<String> nodes = new ArrayList<String>();

    public DemandMatrix() {
        int i=0, j=0;

        for (;i < MAX_NODES; i++) {
            for (; j < MAX_NODES; j++) {
                matrix[i][j] = -1;
            }
        }
    }

    public Double getDemand(Node first, Node second) {
       return getDemand(first.getId(), second.getId());
    }

    public Double getDemand(String first, String second) {
        int i, j;

        i = nodes.indexOf(first);
        j = nodes.indexOf(second);
//
//        System.out.println(i);
//        System.out.println(j);
//        System.out.println(first);
//        System.out.println(second);

        return matrix[i][j];
    }

    public void addDemand(double demand, Node first, Node second) throws ArrayIndexOutOfBoundsException {
        int i, j;
        i = addNode(first);
        j = addNode(second);
        matrix[i][j] = matrix[j][i] = demand;
        matrix[i][i] = matrix[j][j] = -1d;
    }

    public void addDemand(double demand, String first, String second) throws ArrayIndexOutOfBoundsException {
        int i, j;
        i = addNode(first);
        j = addNode(second);
        matrix[i][j] = matrix[j][i] = demand;
        matrix[i][i] = matrix[j][j] = -1d;
    }
    
    protected int addNode(Node node) throws ArrayIndexOutOfBoundsException {
        return addNode(node.getId());
    }

    protected int addNode(String node) throws ArrayIndexOutOfBoundsException {
        if (!nodes.contains(node)) {
            if (nodes.size() == MAX_NODES) {
                throw new ArrayIndexOutOfBoundsException("Osiagnieto limit ilosci wezlow (" + MAX_NODES + ")");
            }
            nodes.add(node);
            return nodes.size() - 1;
        } else {
            return nodes.indexOf(node);
        }
    }

    public Collection<String> getNodes() {
        return nodes;
    }

    public void print() {
        int i,j;
        for (i=0; i < 5; i++) {
            for (j=0; j < 5; j++) {
                System.out.print(matrix[i][j]);
                System.out.print("    ");
            }
            System.out.println();
        }
    }
}
