import java.io.*;
import java.util.*;

class GPNode {
    char op;
    double val;
    GPNode left, right;

    public GPNode(char op, double val) {
        this.op = op;
        this.val = val;
        this.left = this.right = null;
    }

    public GPNode(char op, GPNode left, GPNode right) {
        this.op = op;
        this.val = 0;
        this.left = left;
        this.right = right;
    }

    public double evaluate(double[] input) {
        if (left == null && right == null) {
            if (op == 'v') {
                return input[(int) val];
            } else {
                return val;
            }
        } else {
            double leftVal = left != null ? left.evaluate(input) : 0;
            double rightVal = right != null ? right.evaluate(input) : 0;

            switch (op) {
                case '+':
                    return leftVal + rightVal;
                case '-':
                    return leftVal - rightVal;
                case '*':
                    return leftVal * rightVal;
                case '/':
                    return rightVal != 0 ? leftVal / rightVal : 0;
                default:
                    throw new UnsupportedOperationException("Unknown operator: " + op);
            }
        }
    }
}

class GPClassifier {
    static final int POPULATION_SIZE = 100;
    static final int MAX_GENERATIONS = 50;
    static final int MAX_DEPTH = 4;
    static final char[] OPERATORS = { '+', '-', '*', '/' };
    static final Random rand = new Random();
    static double[][] trainData;
    static double[] trainLabels;
    static double[][] testData;
    static double[] testLabels;

    public static void main(String[] args) throws IOException {
        readTrainData();
        readTestData();
        GPNode bestIndividual = null;
        GPNode[] population = initPopulation();

        for (int gen = 0; gen < MAX_GENERATIONS; gen++) {
            double bestFitness = Double.NEGATIVE_INFINITY;
            for (GPNode individual : population) {
                double fitness = computeFitness(individual);
                if (fitness > bestFitness) {
                    bestFitness = fitness;
                    bestIndividual = individual;
                }
            }
            System.out.println("Generation " + gen + ": Best fitness = " + bestFitness);
            population = nextGeneration(population, bestIndividual);
        }

        double accuracy = (double) computeAccuracy(bestIndividual, testData, testLabels) * 100;
        double roundedAccuracy = Math.round(accuracy * 100.0) / 100.0;
        System.out.println("\u001B[32mTesting accuracy: " + roundedAccuracy + "%\u001B[0m");
    }

    static void readTrainData() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("./mushroom_data/mushroom_train.csv"));
        List<double[]> trainDataList = new ArrayList<>();
        List<Double> trainLabelsList = new ArrayList<>();
        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] vals = line.split(",");
            double[] features = new double[vals.length - 1];
            for (int i = 0; i < features.length; i++) {
                features[i] = Double.parseDouble(vals[i]);
            }
            double label = Double.parseDouble(vals[vals.length - 1]);
            trainDataList.add(features);
            trainLabelsList.add(label);
        }
        reader.close();
        trainData = trainDataList.toArray(new double[trainDataList.size()][]);
        trainLabels = trainLabelsList.stream().mapToDouble(Double::doubleValue).toArray();
    }

    static void readTestData() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("./mushroom_data/mushroom_test.csv"));
        List<double[]> testDataList = new ArrayList<>();
        List<Double> testLabelsList = new ArrayList<>();
        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] vals = line.split(",");
            double[] features = new double[vals.length - 1];
            for (int i = 0; i < features.length; i++) {
                features[i] = Double.parseDouble(vals[i]);
            }
            double label = Double.parseDouble(vals[vals.length - 1]);
            testDataList.add(features);
            testLabelsList.add(label);
        }
        reader.close();
        testData = testDataList.toArray(new double[testDataList.size()][]);
        testLabels = testLabelsList.stream().mapToDouble(Double::doubleValue).toArray();
    }

    static GPNode[] initPopulation() {
        GPNode[] population = new GPNode[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population[i] = growTree(MAX_DEPTH);
        }
        return population;
    }

    static GPNode growTree(int depth) {
        if (depth == 0 || rand.nextDouble() < 0.1) {
            if (rand.nextDouble() < 0.5) {
                return new GPNode('v', rand.nextInt(trainData[0].length));
            } else {
                return new GPNode('c', rand.nextDouble() * 2 - 1);
            }
        } else {
            char op = OPERATORS[rand.nextInt(OPERATORS.length)];
            return new GPNode(op, growTree(depth - 1), growTree(depth - 1));
        }
    }

    static double computeFitness(GPNode individual) {
        double correct = 0;
        for (int i = 0; i < trainData.length; i++) {
            double prediction = individual.evaluate(trainData[i]);
            if ((prediction > 0 && trainLabels[i] == 1) || (prediction <= 0 && trainLabels[i] == 0)) {
                correct++;
            }
        }
        return correct / trainData.length;
    }

    static GPNode[] nextGeneration(GPNode[] population, GPNode bestIndividual) {
        GPNode[] newPopulation = new GPNode[POPULATION_SIZE];
        newPopulation[0] = bestIndividual;

        for (int i = 1; i < POPULATION_SIZE; i++) {
            GPNode parent1 = tournament(population);
            GPNode parent2 = tournament(population);
            GPNode child = crossover(parent1, parent2);
            child = mutate(child);
            newPopulation[i] = child;
        }

        return newPopulation;
    }

    static GPNode tournament(GPNode[] population) {
        int idx1 = rand.nextInt(POPULATION_SIZE);
        int idx2 = rand.nextInt(POPULATION_SIZE);
        double fitness1 = computeFitness(population[idx1]);
        double fitness2 = computeFitness(population[idx2]);
        return (fitness1 > fitness2) ? population[idx1] : population[idx2];
    }

    static GPNode crossover(GPNode parent1, GPNode parent2) {
        if (rand.nextDouble() < 0.9) {
            return subtreeCrossover(parent1, parent2);
        } else {
            return subtreeSwap(parent1, parent2);
        }
    }

    static GPNode subtreeCrossover(GPNode parent1, GPNode parent2) {
        if (parent1 == null) {
            return (parent2 == null) ? null : new GPNode(parent2.op, parent2.val);
        }
        if (parent2 == null) {
            return new GPNode(parent1.op, parent1.val);
        }
        if (parent1.left == null && parent1.right == null) {
            return new GPNode(parent1.op, parent1.val);
        }
        if (parent2.left == null && parent2.right == null) {
            return new GPNode(parent2.op, parent2.val);
        }
        if (rand.nextDouble() < 0.5) {
            GPNode newLeft = (parent1.left != null && parent2.left != null)
                    ? subtreeCrossover(parent1.left, parent2.left)
                    : (parent1.left != null) ? subtreeCrossover(parent1.left, parent2)
                            : subtreeCrossover(parent1, parent2.left);
            GPNode newRight = (parent1.right != null && parent2.right != null)
                    ? subtreeCrossover(parent1.right, parent2.right)
                    : (parent1.right != null) ? subtreeCrossover(parent1.right, parent2)
                            : subtreeCrossover(parent1, parent2.right);
            return new GPNode(parent1.op, newLeft, newRight);
        } else {
            GPNode newLeft = (parent1.left != null && parent2.right != null)
                    ? subtreeCrossover(parent1.left, parent2.right)
                    : (parent1.left != null) ? subtreeCrossover(parent1.left, parent2)
                            : subtreeCrossover(parent1, parent2.right);
            GPNode newRight = (parent1.right != null && parent2.left != null)
                    ? subtreeCrossover(parent1.right, parent2.left)
                    : (parent1.right != null) ? subtreeCrossover(parent1.right, parent2)
                            : subtreeCrossover(parent1, parent2.left);
            return new GPNode(parent1.op, newLeft, newRight);
        }
    }

    static GPNode subtreeSwap(GPNode parent1, GPNode parent2) {
        if (parent1.left == null && parent1.right == null) {
            return new GPNode(parent1.op, parent1.val);
        } else if (rand.nextDouble() < 0.5) {
            GPNode newLeft = (rand.nextDouble() < 0.5) ? parent1.left : parent2.left;
            GPNode newRight = (rand.nextDouble() < 0.5) ? parent1.right : parent2.right;
            return new GPNode(parent1.op, newLeft, newRight);
        } else {
            GPNode newLeft = (rand.nextDouble() < 0.5) ? parent1.left : parent2.right;
            GPNode newRight = (rand.nextDouble() < 0.5) ? parent1.right : parent2.left;
            return new GPNode(parent1.op, newLeft, newRight);
        }
    }

    static GPNode mutate(GPNode individual) {
        if (individual.left == null && individual.right == null) {
            if (rand.nextDouble() < 0.1) {
                individual.val = rand.nextDouble() * 2 - 1;
            }
            return individual;
        } else {
            if (rand.nextDouble() < 0.1) {
                individual.op = OPERATORS[rand.nextInt(OPERATORS.length)];
            }
            if (rand.nextDouble() < 0.2) {
                individual.left = (individual.left != null) ? growTree(MAX_DEPTH) : null;
            } else if (individual.left != null) {
                individual.left = mutate(individual.left);
            }
            if (rand.nextDouble() < 0.2) {
                individual.right = (individual.right != null) ? growTree(MAX_DEPTH) : null;
            } else if (individual.right != null) {
                individual.right = mutate(individual.right);
            }
            return individual;
        }
    }

    static double computeAccuracy(GPNode individual, double[][] data, double[] labels) {
        int correct = 0;
        for (int i = 0; i < data.length; i++) {
            double prediction = individual.evaluate(data[i]);
            double label = labels[i];
            if ((prediction > 0 && label == 1) || (prediction <= 0 && label == 0)) {
                correct++;
            }
        }
        return (double) correct / data.length;
    }
}