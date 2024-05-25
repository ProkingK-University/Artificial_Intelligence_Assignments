import java.io.BufferedReader;

import java.io.FileReader;

import java.io.IOException;

import java.util.ArrayList;

import java.util.Arrays;

import java.util.List;

import java.util.Random;

public class MushroomClassification {

    public static void main(String[] args) {

        List<MushroomData> trainingData = loadMushroomData("./mushroom_data/mushroom_train.csv");

        List<MushroomData> testData = loadMushroomData("./mushroom_data/mushroom_test.csv");

        Network network = new Network(8, 1, 0.001);

        network.train(trainingData);

        int correctCount = 0;

        for (MushroomData testInstance : testData) {

            int predicted = network.predict(testInstance.getFeatures());

            int actual = testInstance.getLabel();

            if (predicted == actual) {

                correctCount += 1;

            }

        }

        double accuracy = (double) correctCount / testData.size();

        System.out.println("Accuracy: " + accuracy);

    }

    public static List<MushroomData> loadMushroomData(String filename) {

        List<MushroomData> data = new ArrayList<>();

        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            br.readLine();

            while ((line = br.readLine()) != null) {

                String[] tokens = line.split(",");

                double[] features = new double[8];

                for (int i = 0; i < 8; i++) {

                    features[i] = Double.parseDouble(tokens[i]);

                }

                int label = Integer.parseInt(tokens[8]);

                data.add(new MushroomData(features, label));

            }

        } catch (IOException e) {

            e.printStackTrace();

        }

        return data;

    }

}

class MushroomData {

    private final double[] features;

    private final int label;

    public MushroomData(double[] features, int label) {

        this.features = features;

        this.label = label;

    }

    public double[] getFeatures() {

        return features;

    }

    public int getLabel() {

        return label;

    }

}

class Neuron {

    private final Random random = new Random(123);

    private double[] weights;

    private double bias;

    private double output;

    private double delta;

    public Neuron(int numInputs) {

        weights = new double[numInputs];

        for (int i = 0; i < numInputs; i++) {

            weights[i] = random.nextDouble() * 2 - 1;

        }

        bias = random.nextDouble() * 2 - 1;

    }

    public double compute(double[] inputs) {

        double sum = 0;

        for (int i = 0; i < inputs.length; i++) {

            sum += weights[i] * inputs[i];

        }

        sum += bias;

        output = sigmoid(sum);

        return output;

    }

    public static double sigmoid(double x) {

        return 1 / (1 + Math.exp(-x));

    }

    public static double sigmoidDerivative(double x) {

        return x * (1 - x);

    }

    public double[] getWeights() {

        return weights;

    }

    public double getBias() {

        return bias;

    }

    public double getOutput() {

        return output;

    }

    public void setWeights(double[] weights) {

        this.weights = weights;

    }

    public void setBias(double bias) {

        this.bias = bias;

    }

    public void setOutput(double output) {

        this.output = output;

    }

    public void setDelta(double delta) {

        this.delta = delta;

    }

    public double getDelta() {

        return delta;

    }

}

class Network {

    private final List<Neuron> inputLayer;

    private final List<Neuron> hiddenLayer;

    private final Neuron outputLayer;

    private final double learningRate;

    public Network(int numInputs, int numHiddenNeurons, double learningRate) {

        this.learningRate = learningRate;

        inputLayer = Arrays.asList(new Neuron(numInputs), new Neuron(numInputs));

        hiddenLayer = Arrays.asList(new Neuron(2), new Neuron(2));

        outputLayer = new Neuron(2);

    }

    public int predict(double[] inputs) {

        for (Neuron neuron : inputLayer) {

            neuron.compute(inputs);

        }

        double[] hiddenOutputs = inputLayer.stream().mapToDouble(Neuron::getOutput).toArray();

        for (Neuron neuron : hiddenLayer) {

            neuron.compute(hiddenOutputs);

        }

        double[] finalOutputs = hiddenLayer.stream().mapToDouble(Neuron::getOutput).toArray();

        double result = outputLayer.compute(finalOutputs);

        return result >= 0.5 ? 1 : 0;

    }

    public void train(List<MushroomData> data) {

        for (int epoch = 0; epoch < 1000; epoch++) {

            double totalError = 0;

            for (MushroomData mushroom : data) {

                double[] inputs = mushroom.getFeatures();

                int expectedOutput = mushroom.getLabel();

                // Forward Pass

                for (Neuron neuron : inputLayer) {

                    neuron.compute(inputs);

                }

                double[] hiddenOutputs = inputLayer.stream().mapToDouble(Neuron::getOutput).toArray();

                for (Neuron neuron : hiddenLayer) {

                    neuron.compute(hiddenOutputs);

                }

                double[] finalOutputs = hiddenLayer.stream().mapToDouble(Neuron::getOutput).toArray();

                double result = outputLayer.compute(finalOutputs);

                // Compute Error

                double error = expectedOutput - result;

                totalError += error * error;

                // Backpropagate the error

                outputLayer.setDelta(error * Neuron.sigmoidDerivative(result));

                double[] outputLayerWeights = outputLayer.getWeights();

                for (int i = 0; i < hiddenLayer.size(); i++) {

                    Neuron hiddenNeuron = hiddenLayer.get(i);

                    hiddenNeuron.setDelta(hiddenNeuron.getOutput() * Neuron.sigmoidDerivative(hiddenNeuron.getOutput())
                            * outputLayerWeights[i] * outputLayer.getDelta());

                }

                for (int i = 0; i < inputLayer.size(); i++) {

                    Neuron inputNeuron = inputLayer.get(i);

                    double deltaSum = 0;

                    for (Neuron hiddenNeuron : hiddenLayer) {

                        deltaSum += hiddenNeuron.getDelta() * hiddenNeuron.getWeights()[i];

                    }

                    inputNeuron.setDelta(
                            inputNeuron.getOutput() * Neuron.sigmoidDerivative(inputNeuron.getOutput()) * deltaSum);

                }

                // Update the weights and biases

                for (Neuron hiddenNeuron : hiddenLayer) {

                    double[] weights = hiddenNeuron.getWeights();

                    for (int j = 0; j < weights.length; j++) {

                        weights[j] += learningRate * hiddenNeuron.getDelta() * inputLayer.get(j).getOutput();

                    }

                    hiddenNeuron.setBias(hiddenNeuron.getBias() + learningRate * hiddenNeuron.getDelta());

                }

                double[] weights = outputLayer.getWeights();

                for (int j = 0; j < weights.length; j++) {

                    weights[j] += learningRate * outputLayer.getDelta() * hiddenLayer.get(j).getOutput();

                }

                outputLayer.setBias(outputLayer.getBias() + learningRate * outputLayer.getDelta());

            }

            System.out.println("Epoch " + epoch + " - Error: " + totalError);

        }

    }

}