package jpm.travelling.salesman;

import java.util.Arrays;
import java.util.Random;

public class SimulatedAnnealing {

    private MatrixWrapper matrix;
    private Random random;
    private int numCities;
    private int[] optimumTour;
    private static final int INITIAL_TEMP = 5000000; // The starting temperature.
    private static final double FREEZE_POINT = 0.00001; // The freezing point.
    private static final int MAX_ITERATIONS = 10;
    private double THRESHOLD = 500; // The threshold for bad moves.

    public SimulatedAnnealing(MatrixWrapper matrixIn) {
        matrix = matrixIn;
        random = new Random();
        numCities = matrix.getCitiesCount();
    }

    public int[] execute() {
        int lowest = Integer.MAX_VALUE;
        int[] currentTour;
        int newCost;
        // Perform annealing several times
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            currentTour = anneal(MatrixWrapper.getRandomTour(numCities));
            newCost = MatrixWrapper.getCost(currentTour);
            if (newCost < lowest) {
                lowest = newCost;
                optimumTour = currentTour;
            }
        }
        return optimumTour;
    }

    private int[] anneal(int[] currentState) {
        int[] bestState = currentState;
        int[] newState;
        int newCost;
        double temperature = INITIAL_TEMP;
        while (temperature > FREEZE_POINT) {
            //Two random cities are swapped
            newState = swap(currentState);
            //int[] newState = worstSwap(currentState);
            //int[] newState = bestSwap(currentState);
            //newState = mutate(newState);
            newCost = MatrixWrapper.getCost(newState);
            boolean moved = false;
            if (random.nextDouble()
                    < moveProbability(MatrixWrapper.getCost(currentState),
                    newCost, temperature)) {
                currentState = newState;
            }
            //Is the new state better?
            if (newCost < MatrixWrapper.getCost(bestState)) {
                bestState = newState;
                moved = true;
            }
            THRESHOLD = MatrixWrapper.getCost(bestState) / 4;
            //If above threshold then H-C restart
            //Geometric Progression
            if (!moved) {
                temperature *= 0.999999;
            }
        }
        return bestState;
    }

    /**
     * This original schedule did not cool slow enough. However, it was much
     * more efficient.
     */
    @SuppressWarnings("unused")
	private double getTemp(int t) {
        if (t < 1000) {
            double res = 20 * Math.exp((-1) * 0.045 * t);
            return res;
        } else {
            return 0.0;
        }
    }

    private double moveProbability(int curLength, int newLength, double curTemp) {
        //If it is shorter then move and very poor moves are prevented completely.
        if (newLength <= curLength) {
            return 1;
        }
        if (newLength > (curLength + THRESHOLD)) {
            return -1;
        }
        //The change is calculated.
        double deltaE = Math.abs(newLength - curLength);
        double probability = (1 / (Math.pow(Math.E, deltaE * curTemp)));
        //double probability = (1 / (Math.pow(Math.E, deltaE * curTemp)));
        return probability;
    }

    /**
     * Swaps random cities in a tour.
     */
    private int[] swap(int[] in) {
        int a = random.nextInt(in.length);
        int b;
        do {
            b = random.nextInt(in.length);
        } while (b == a);
        int[] out = Arrays.copyOf(in, in.length);
        int temp = out[a];
        out[a] = out[b];
        out[b] = temp;
        return out;
    }
}
