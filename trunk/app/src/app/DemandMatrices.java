package app;

import java.util.ArrayList;
import java.util.List;
import sndlib.core.network.Node;

/**
 *
 * @author hmsck
 */
public class DemandMatrices {

    protected List<DemandMatrix> matrices = new ArrayList<DemandMatrix>();

    public DemandMatrices() {
    }

    public void addDemandMatrix(DemandMatrix matrix) {
        matrices.add(matrix);
    }

    public double getMaxDemand(Node first, Node last) {
        double demand = -1d;

        for (DemandMatrix matrix : matrices) {
            if (matrix.getDemand(first, last) > demand) {
                demand = matrix.getDemand(first, last);
            }
        }

        return demand;
    }

    public List<DemandMatrix> getMatrices() {
        return matrices;
    }

    public DemandMatrix getMaxDemandMatrix() {
        DemandMatrix dm = new DemandMatrix();

        if (!matrices.isEmpty()) {
            List<Node> nodes = (List<Node>) matrices.get(0).getNodes();

            for (Node first : nodes) {
                for (Node second : nodes) {
                    dm.addDemand(getMaxDemand(first, second), first, second);
                }
            }
        }
        return dm;
    }
}
