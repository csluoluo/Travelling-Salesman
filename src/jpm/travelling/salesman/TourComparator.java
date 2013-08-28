package jpm.travelling.salesman;

import java.util.Comparator;

public class TourComparator implements Comparator<int[]> {
        @Override
        public int compare(int[] a, int[] b) {
            return MatrixWrapper.getCost(a) - MatrixWrapper.getCost(b);
        }
    }