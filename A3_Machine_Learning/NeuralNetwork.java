import java.util.*;

public class NeuralNetwork {
    private static final int MAX_EPOCHS = 5000;
    private static final double THRESHOLD = 0.001;
    private static final double LEARNING_RATE = 0.001;

    private int numInputs;
    private int numHiddenNodes;
    private int numOutputNodes;

    private double[][] inputWeights;
    private double[][] hiddenWeights;

    private double[] hiddenBiases;
    private double[] outputBiases;

    private List<double[]> inputData;
    private List<double[]> outputData;

    public NeuralNetwork(int numInputs, int numHiddenNodes, int numOutputNodes, List<double[]> inputData, List<double[]> outputData) {
        this.numInputs = numInputs;
        this.numHiddenNodes = numHiddenNodes;
        this.numOutputNodes = numOutputNodes;
        this.inputData = inputData;
        this.outputData = outputData;

        initializeWeightsAndBiases();
    }

    private void initializeWeightsAndBiases() {
        Random random = new Random();

        inputWeights = new double[numInputs][numHiddenNodes];
        hiddenWeights = new double[numHiddenNodes][numOutputNodes];

        hiddenBiases = new double[numHiddenNodes];
        outputBiases = new double[numOutputNodes];

        for (int i = 0; i < numInputs; i++) {
            for (int j = 0; j < numHiddenNodes; j++) {
                inputWeights[i][j] = random.nextDouble() - 0.5;
            }
        }

        for (int i = 0; i < numHiddenNodes; i++) {
            hiddenBiases[i] = random.nextDouble() - 0.5;

            for (int j = 0; j < numOutputNodes; j++) {
                hiddenWeights[i][j] = random.nextDouble() - 0.5;
            }
        }

        for (int i = 0; i < numOutputNodes; i++) {
            outputBiases[i] = random.nextDouble() - 0.5;
        }
    }

    public void train() {
        int epoch = 0;
        double error = 0.0;

        do {
            error = 0.0;

            for (int i = 0; i < inputData.size(); i++) {
                double[] inputs = inputData.get(i);
                double[] targets = outputData.get(i);

                error += backpropagation(inputs, targets);
            }

            error /= inputData.size();
            System.out.println("Epoch " + epoch + ": Error = " + error);

            epoch++;
        } while (error > THRESHOLD && epoch < MAX_EPOCHS);
    }

    private double backpropagation(double[] inputs, double[] targets) {
        double[] finalOutputs = new double[numOutputNodes];
        double[] hiddenOutputs = new double[numHiddenNodes];

        // Forward propagation
        for (int j = 0; j < numHiddenNodes; j++) {
            double sum = 0.0;

            for (int i = 0; i < numInputs; i++) {
                sum += inputs[i] * inputWeights[i][j];
            }

            hiddenOutputs[j] = relu(sum + hiddenBiases[j]);
        }

        for (int k = 0; k < numOutputNodes; k++) {
            double sum = 0.0;

            for (int j = 0; j < numHiddenNodes; j++) {
                sum += hiddenOutputs[j] * hiddenWeights[j][k];
            }

            finalOutputs[k] = relu(sum + outputBiases[k]);
        }

        // Backpropagation
        double error = 0.0;
        double[] outputDeltas = new double[numOutputNodes];

        for (int k = 0; k < numOutputNodes; k++) {
            double delta = finalOutputs[k] * (1 - finalOutputs[k]) * (targets[k] - finalOutputs[k]);

            outputDeltas[k] = delta;
            error += Math.pow(targets[k] - finalOutputs[k], 2);
        }

        double[] hiddenDeltas = new double[numHiddenNodes];

        for (int j = 0; j < numHiddenNodes; j++) {
            double sum = 0.0;

            for (int k = 0; k < numOutputNodes; k++) {
                sum += outputDeltas[k] * hiddenWeights[j][k];
            }

            hiddenDeltas[j] = hiddenOutputs[j] * (1 - hiddenOutputs[j]) * sum;
        }

        // Update weights and biases
        for (int i = 0; i < numInputs; i++) {
            for (int j = 0; j < numHiddenNodes; j++) {
                inputWeights[i][j] += LEARNING_RATE * hiddenDeltas[j] * inputs[i];
            }
        }

        for (int j = 0; j < numHiddenNodes; j++) {
            hiddenBiases[j] += LEARNING_RATE * hiddenDeltas[j];

            for (int k = 0; k < numOutputNodes; k++) {
                hiddenWeights[j][k] += LEARNING_RATE * outputDeltas[k] * hiddenOutputs[j];
            }
        }

        for (int k = 0; k < numOutputNodes; k++) {
            outputBiases[k] += LEARNING_RATE * outputDeltas[k];
        }

        return error / numOutputNodes;
    }

    private double relu(double x) {
        return Math.max(0.0, x);
    }

    public double[] computeOutputs(double[] inputs) {
        double[] hiddenOutputs = new double[numHiddenNodes];
        double[] finalOutputs = new double[numOutputNodes];

        // Forward propagation
        for (int j = 0; j < numHiddenNodes; j++) {
            double sum = 0.0;

            for (int i = 0; i < numInputs; i++) {
                sum += inputs[i] * inputWeights[i][j];
            }

            hiddenOutputs[j] = relu(sum + hiddenBiases[j]);
        }

        for (int k = 0; k < numOutputNodes; k++) {
            double sum = 0.0;

            for (int j = 0; j < numHiddenNodes; j++) {
                sum += hiddenOutputs[j] * hiddenWeights[j][k];
            }
            
            finalOutputs[k] = relu(sum + outputBiases[k]);
        }

        return finalOutputs;
    }
}