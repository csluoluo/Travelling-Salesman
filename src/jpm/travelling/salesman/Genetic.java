package jpm.travelling.salesman;

import java.util.*;

public class Genetic {

    private MatrixWrapper matrix;
    private Random random;
    private int numCities;
    private double totalFitness;
    private List<int[]> population; //Holds the current population
    private TourComparator comparator;
    private int[] optimumTour;
    private int lowestCost; //Holds the cost of the optimum tour found
    private final static int POP_SIZE = 600;  // Population size
    private final static int ELITISM_K = POP_SIZE/10; // The number of elites
    private final static int GENERATIONS = 800;             // Max number of iterations
    private final static double MUTATION_RATE = 0.03;     // Probability of mutation
    private final static double CROSSOVER_RATE = 0.65;  // Probability of mutation

    public Genetic(MatrixWrapper matrixIn) {
        matrix = matrixIn;
        random = new Random();
        numCities = matrix.getCitiesCount();
        comparator = new TourComparator();
        generatePopulation();
        evaluate();
        lowestCost = Integer.MAX_VALUE;
        optimumTour = new int[numCities];
    }

    /**
     *  Gets the total fitness of the current population.
     * @return 
     */
    private void evaluate() {
        totalFitness = 0.0;
        for (int i = 0; i < population.size(); i++) {
            //As the TSP is a minimization problem the fitness is the inverse cost.
            totalFitness += (double) 1/(MatrixWrapper.getCost(population.get(i)));
        }
    }

    /**
     * Fitness-proportionate selection.
     */
    private int[] rouletteWheelSelection() {
        evaluate();
        double randNum = random.nextDouble() * (totalFitness);
        int index;
        for (index = 0; index < population.size() && randNum > 0; index++) {
            randNum -= (double)1/MatrixWrapper.getCost(population.get(index));
        }
        return population.remove(population.size()- index);
    }
    
    public int[] execute() {
        List<int[]> newPopulation;
        int slice = GENERATIONS/10;
        setOptimum();
        for (int iter = 0; iter < GENERATIONS; iter++) {
            //The user is updated on progress.
            if(iter%slice == 0)
            {
                System.out.println((iter/slice) * 10 + "%");
            }
            Collections.sort(population, comparator);
            // Shallow copy of the elite tours.
            newPopulation = new ArrayList<int[]>(population.subList(0, ELITISM_K));
            // The new population is built.
            int[] tourA;
            int[] tourB;
            while (newPopulation.size() < POP_SIZE) {
                // Selection
                tourA = rouletteWheelSelection();
                tourB = rouletteWheelSelection();
                // Crossover
                if (random.nextDouble() < CROSSOVER_RATE) {
                    tourA = greedyCrossOver(tourA, tourB);
                    tourB = greedyCrossOver(tourB, tourA);
                }
                // Add children to new population
                newPopulation.add(tourA);
                newPopulation.add(tourB);
            }
            // Reset the generation.
            population.clear();
            population = new ArrayList<int[]>(newPopulation);
            setOptimum();
        }
        return optimumTour;
    }

    /**
     * Sets and tracks the optimum values.
     */
    private void setOptimum() {
        int[] newTour = Collections.min(population, comparator);
        int newCost = MatrixWrapper.getCost(newTour);
        if (newCost < lowestCost) {
            System.arraycopy(newTour, 0, optimumTour, 0, numCities);
            lowestCost = newCost;
            System.out.println("New lowest: " + lowestCost);
        }
    }

    // Generates a random initial population.
    private void generatePopulation() {
        population = new ArrayList<int[]>();
        for (int i = 0; i < POP_SIZE; i++) {
            population.add(MatrixWrapper.getRandomTour(numCities));
        }
    }

    // Random one-city mutation.
    private int[] mutate(int[] in) {
        int a = random.nextInt(in.length);
        int b;
        do {
            b = random.nextInt(in.length);
        } while (b == a);
        int temp = in[a];
        in[a] = in[b];
        in[b] = temp;
        return in;
    }
    
    // My personal greedy crossover.
    private int[] greedyCrossOver(int[] tourA, int[] tourB) {
        ArrayList<Integer> childList = new ArrayList<Integer>();
        ArrayList<Integer> unused = new ArrayList<Integer>();
        // Add the first city of A.
        childList.add(tourA[0]);
        for (int j = 1; j < numCities; j++) {
            unused.add(tourA[j]);
        }
        while (unused.size() > 1) {
            int last = childList.get(childList.size() - 1);
            int next1 = getNext(tourA, last);
            int next2 = getNext(tourB, last);
            int cost1 = MatrixWrapper.getCost(last, next1);
            int cost2 = MatrixWrapper.getCost(last, next2);
            int picked = next2;
            int other = next1;
            if (cost1 < cost2) {
                picked = next1;
                other = next2;
            }
            if (childList.contains(picked)) {
                picked = other;
            }
            if (childList.contains(picked)) {
                picked = unused.get(0);
            }
            childList.add(picked);
            // picked must be cast to an Integer since
            // an int would remove an index, not the object.
            unused.remove((Integer) picked);
        }
        childList.add(unused.get((unused.size() - 1)));
        int[] child = MatrixWrapper.toIntArray(childList);
        if (random.nextDouble() < MUTATION_RATE) {
            return mutate(child);
        }
        return child;
    }

    /**
     * Returns the next city in given a tour given a city index.
     **/
    private int getNext(int[] cities, int x) {
        for (int i = 0; i < cities.length - 1; i++) {
            if (cities[i] == (x)) {
                return cities[i + 1];
            }
        }
        // From the last city we go to the first one
        return cities[0];
    }
}
