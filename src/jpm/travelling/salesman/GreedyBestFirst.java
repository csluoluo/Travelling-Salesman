package jpm.travelling.salesman;

import java.util.*;

public class GreedyBestFirst {

    private MatrixWrapper matrix;
    private int numCities;
    private int[] optimumTour;
    private ArrayList<Integer> visited; // Holds the currently constructed tour.
    private HashSet<Integer> unVisited; // A set of unvisited cities.
    private CurrentComparator greedyComparison;

    public GreedyBestFirst(MatrixWrapper matrixIn) {
        matrix = matrixIn;
        numCities = matrix.getCitiesCount();
        visited = new ArrayList<Integer>(numCities);
        unVisited = new HashSet<Integer>();
        greedyComparison = new CurrentComparator();
    }

    /**
     * Resets variables for another attempt.
     */
    private void reset() {
        unVisited.clear();
        visited.clear();
        for (int i = 0; i < numCities; i++) {
            unVisited.add(i);
        }
    }

    public int[] execute() {
        int lowest = Integer.MAX_VALUE;
        // Search starting from each city.
        for (int i = 0; i < numCities; i++) {
            int[] newTour = search(i);
            int newCost = MatrixWrapper.getCost(newTour);
            if (newCost < lowest) {
                lowest = newCost;
                optimumTour = newTour;
            }
        }
        return optimumTour;
    }

    /**
     * The main search function.
     */
    private int[] search(int start) {
        reset(); // Reset for another pass.
        add(start); // Add the first city.
        for (int i = 1; i < numCities; i++) {
            add(Collections.min(unVisited, greedyComparison));
        }
        return MatrixWrapper.toIntArray(visited);
    }

    private void add(int value) {
        visited.add(value);
        unVisited.remove(value);
    }

    private class CurrentComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer a, Integer b) {
            int current = visited.get(visited.size() - 1);
            return MatrixWrapper.getCost(a, current) - MatrixWrapper.getCost(b, current);
        }
    }
}
