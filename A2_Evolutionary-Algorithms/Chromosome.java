import java.util.Random;

public class Chromosome {
    private int[] genes;
    private int fitness;
    private static final Random rand = new Random();

    public Chromosome(int length) {
        genes = new int[length];

        for (int i = 0; i < length; i++) {
            genes[i] = rand.nextInt(2);
        }

        fitness = 0;
    }

    public int getGene(int index) {
        return genes[index];
    }

    public void setGene(int index, int value) {
        genes[index] = value;
    }

    public int getLength() {
        return genes.length;
    }

    public int getFitness() {
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

    public void calculateFitness(int[] weights, int[] values, int capacity) {
        int totalWeight = 0;

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
