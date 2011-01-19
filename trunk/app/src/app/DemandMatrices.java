package app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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

    public int getMaxDemand(String first, String last) {
        int demand = -1;

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
            List<String> nodes = (List<String>) matrices.get(0).getNodes();

            for (String first : nodes) {
                for (String second : nodes) {
                    dm.addDemand(getMaxDemand(first, second), first, second);
                }
            }
        }
        return dm;
    }

    public DemandMatrices getRandMatrices(int count) {
        DemandMatrices dms = new DemandMatrices();
        Random gen = new Random();
        Set<Integer> nums = new HashSet<Integer>();
        Integer num;

        while (count-- > 0) {
            num = gen.nextInt(matrices.size());
            while (nums.contains(num)) {
                num = gen.nextInt(matrices.size());
            }
            nums.add(num);

            dms.addDemandMatrix(matrices.get(num.intValue()));
        }

        return dms;
    }

    public int countMatrices() {
        return matrices.size();
    }

    public void removeDemandMatrix(int i) {
        matrices.remove(i);
    }

    public void removeDemandMatrix(DemandMatrix dm) {
        matrices.remove(dm);
    }

    public void clear() {
        matrices.clear();
    }

    public DemandMatrix get(int i) {
        return matrices.get(i);
    }

    public DemandMatrices getSubDemandMatrices(int count) {
        DemandMatrices dms = new DemandMatrices();
        int i = 0;

        while (i < count) {
            dms.addDemandMatrix(matrices.get(i));
            i++;
        }

        return dms;
    }
}
