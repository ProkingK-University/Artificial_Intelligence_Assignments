import java.util.Random;

public class Chromosome {
    private double[] genes;
    private double fitness;
    private static final Random rand = new Random();

    public Chromosome(int length) {
        genes = new double[length];

        for (int i = 0; i < length; i++) {
            genes[i] = rand.nextInt(2);
        }

        fitness = 0;
    }

    public double getGene(int index) {
        return genes[index];
    }

    public void setGene(int index, double value) {
        genes[index] = value;
    }

    public int getLength() {
        return genes.length;
    }

    public double getFitness() {
        return fitness;
    }

    public String toString() {
        String output = "";

        for (int i = 0; i < genes.length; i++) {
            if (i != genes.length - 1) {
                output += genes[i] + " ";
            } else {
                output += genes[i];
            }
        }

        return output;
    }

    public void calculateFitness(double[] weights, double[] values, int capacity) {
        int totalWeight = 0;

        fitness = 0;

        for (int i = 0; i < genes.length; i++) {
            if (genes[i] == 1) {
                fitness += values[i];
                totalWeight += weights[i];
            }
        }

        if (totalWeight > capacity) {
            fitness = 0;
        }
    }

    public void mutate() {
        int index = rand.nextInt(genes.length);

        genes[index] = 1 - genes[index];
    }

    public void flipGene(int index) {
        if (genes[index] == 0) {
            genes[index] = 1;
        } else {
            genes[index] = 0;
        }
    }
}
