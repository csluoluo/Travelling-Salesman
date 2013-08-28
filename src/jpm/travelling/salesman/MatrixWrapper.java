package jpm.travelling.salesman;

import java.util.ArrayList;
import java.util.Collections;

public class MatrixWrapper {

    private static int[][] theMatrix;
    private int cities;

    public MatrixWrapper(int[][] matrixIn) {
        theMatrix = matrixIn;
        cities = theMatrix.length;
        System.out.println(cities);
    }

    public int getCitiesCount() {

        return cities;
    }

    public static int getCost(int a, int b) {
        if(a > b)
        {
            return theMatrix[b][a];
        }
        return theMatrix[a][b];
    }

    public static String toString(int[] tour) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < tour.length - 1; i++) {
            str.append(tour[i] + 1).append(", ");
        }
        str.append(tour[tour.length - 1] + 1);
        return str.toString();
    }
    
    public static int getCost(int[] input)
    {
        int total = 0;
        for (int i = 0; i < input.length; i++) {
            if (i == input.length - 1) {
                total += getCost(input[i],input[0]);
            } else {
                total += getCost(input[i],input[i + 1]);
            }
        }
        return total;
    }
    
    public static int[] toIntArray(ArrayList<Integer> input) {
        int[] output = new int[input.size()];
        for (int i = 0; i < output.length; i++) {
            output[i] = input.get(i);
        }
        return output;
    }
    
    public static int[] getRandomTour(int numCities) {
        ArrayList<Integer> cities = new ArrayList<Integer>();
        for (int i = 0; i < numCities; i++) {
            cities.add(i);
        }
        Collections.shuffle(cities);
        return MatrixWrapper.toIntArray(cities);
    }
}