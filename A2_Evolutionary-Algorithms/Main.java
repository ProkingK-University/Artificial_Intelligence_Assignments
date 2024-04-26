import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        File folder = new File("./Dataset");
        File[] listOfFiles = folder.listFiles();
        Boolean localSearch = args[0].equals("true") ? true : false;

        for (File file : listOfFiles) {
            if (file.isFile()) {
                Instance instance = readFile("Dataset/" + file.getName());

                GeneticAlgorithm ga = new GeneticAlgorithm();
                Chromosome solution = ga.solve(instance, localSearch);
                System.out.println(file.getName() + ": " + solution.getFitness());
            }
        }
    }

    public static Instance readFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        String line;
        String solution = "";

        int numItems = 0;
        int capacity = 0;
        int lineNumber = 0;

        double[] values = null;
        double[] weights = null;

        while ((line = reader.readLine()) != null) {
            String[] numbers = line.split(" ");

            if (lineNumber == 0) {
                numItems = Integer.parseInt(numbers[0]);
                capacity = Integer.parseInt(numbers[1]);

                values = new double[numItems];
                weights = new double[numItems];
            } else if (numbers.length == 2) {
                values[lineNumber - 1] = Double.parseDouble(numbers[0]);
                weights[lineNumber - 1] = Double.parseDouble(numbers[1]);
            } else {
                solution = line;
            }

            lineNumber++;
        }

        reader.close();

        return new Instance(numItems, capacity, values, weights, solution);
    }
}