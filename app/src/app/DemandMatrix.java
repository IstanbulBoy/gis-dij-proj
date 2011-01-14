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
    protected List<Node> nodes = new ArrayList<Node>();

    public DemandMatrix() {
        int i=0, j=0;

        for (;i < MAX_NODES; i++) {
            for (; j < MAX_NODES; j++) {
                matrix[i][j] = -1;
            }
        }
    }

    public Double getDemand(Node first, Node last) {
        int i, j;

        i = nodes.indexOf(first);
        j = nodes.indexOf(last);

        return matrix[i][j];
    }

    public void addDemand(double demand, Node first, Node last) throws ArrayIndexOutOfBoundsException {
        int i, j;
        i = addNode(first);
        j = addNode(last);
        matrix[i][j] = matrix[j][i] = demand;
        matrix[i][i] = matrix[j][j] = -1d;
    }

    protected int addNode(Node node) throws ArrayIndexOutOfBoundsException {
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

    public Collection<Node> getNodes() {
        return nodes;
    }
}