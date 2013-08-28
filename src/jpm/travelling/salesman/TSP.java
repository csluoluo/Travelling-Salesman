package jpm.travelling.salesman;

import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TSP {

	private static String fileName;
	private static String filePath;
	private static int[][] distances;

	public static void main(String[] args) {
		String[] names = { "012", "017", "021", "026", "042", "048", "058",
				"175", "180", "535" };
		for (int i = 0; i < names.length; i++) {
			filePath = "cityfiles/SAfile" + names[i] + ".txt";
			run();
		}
	}

	private static void run() {
		try {
			File file = new File(filePath);
			readText(file);
			MatrixWrapper wrapper = new MatrixWrapper(distances);
			// SimulatedAnnealing sa = new SimulatedAnnealing(wrapper);
			// Genetic gen = new Genetic(wrapper);
			GreedyBestFirst gen = new GreedyBestFirst(wrapper);
			int[] tour = gen.execute();
			int currentBest = getBest(new File(getOutputPath()));
			int tourCost = MatrixWrapper.getCost(tour);
			System.out.println("Returned: " + tourCost);
			if (tourCost < currentBest) {
				System.out.println("New best tour: " + tourCost);
				writeTextFile(tour.length, tourCost,
						MatrixWrapper.toString(tour));
			}
		} catch (Exception ex) {
			Logger.getLogger(TSP.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private static void readText(File file) throws Exception {
		Scanner scanner;
		StringBuilder input = new StringBuilder();
		scanner = new Scanner(file);
		fileName = file.getName();
		while (scanner.hasNextLine()) {
			input.append(scanner.nextLine()).append("\n");
		}
		scanner.close();
		processText(input.toString());
	}

	private static void processText(String input) throws Exception {
		String[] values = input.split("\\r?\\n");
		fileName = values[0].substring(7).replace(",", "");
		int size = Integer.parseInt(values[1].substring(7).replace(",", ""));
		distances = new int[size][size];
		for (int i = 2; i < values.length; i++) {
			String dist[] = values[i].split(",");
			for (int j = 0; j < dist.length; j++) {
				distances[i - 2][j + 1 + (i - 2)] = Integer.parseInt(dist[j]);
			}
		}
	}

	private static void writeTextFile(int num, int cost, String pathString)
			throws IOException {
		try {
			String tourFileName = getOutputPath();
			String output = "NAME = " + fileName + ",\n";
			output += "TOURSIZE = " + num + ",\n";
			output += "LENGTH = " + cost + ",\n";
			output += pathString;
			System.out.println("Writing results to " + tourFileName);
			BufferedWriter out = new BufferedWriter(
					new FileWriter(tourFileName));
			out.write(output);
			out.close();
		} catch (IOException e) {
			Logger.getLogger(TSP.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	private static String getOutputPath() {
		String output = filePath.replace(fileName, "tour" + fileName);
		return output;
	}

	private static int getBest(File file) {
		try {
			Scanner scanner;
			scanner = new Scanner(file);
			scanner.nextLine();
			scanner.nextLine();
			String input = scanner.nextLine();
			input = input.substring(9, input.length() - 1);
			scanner.close();
			return Integer.parseInt(input);
		} catch (FileNotFoundException ex) {}
		// If the file could not be found start anew.
		return Integer.MAX_VALUE;
	}
}
