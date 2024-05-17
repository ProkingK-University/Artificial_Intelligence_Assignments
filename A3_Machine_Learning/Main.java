import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<double[]> trainInputData = parseInputData("./mushroom_data/mushroom_train.csv");
        List<double[]> trainOutputData = parseOutputData("./mushroom_data/mushroom_train.csv");

        List<double[]> testInputData = parseInputData("./mushroom_data/mushroom_test.csv");
        List<double[]> testOutputData = parseOutputData("./mushroom_data/mushroom_test.csv");

        int numInputs = 8;
        int numHiddenNodes = 1;
        int numOutputNodes = 1;

        // List<double[]> normalizedTrainInputData = normalizeInputData(trainInputData);
        // List<double[]> normalizedTestInputData = normalizeInputData(testInputData);

        NeuralNetwork ann = new NeuralNetwork(numInputs, numHiddenNodes, numOutputNodes, trainInputData,
                trainOutputData);
        ann.train();

        testNeuralNetwork(ann, testInputData, testOutputData);
    }

    private static List<double[]> parseInputData(String fileName) {
        List<double[]> inputData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                double[] inputs = new double[8];
                for (int i = 0; i < 8; i++) {
                    inputs[i] = Double.parseDouble(values[i]);
                }
                inputData.add(inputs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputData;
    }

    private static List<double[]> parseOutputData(String fileName) {
        List<double[]> outputData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                double[] output = new double[1];
                output[0] = Double.parseDouble(values[values.length - 1]);
                outputData.add(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputData;
    }

    public static List<double[]> normalizeInputData(List<double[]> inputData) {
        int numFeatures = inputData.get(0).length;
        int numSamples = inputData.size();
        double[][] normalizedData = new double[numSamples][numFeatures];

        double[] means = new double[numFeatures];
        double[] stds = new double[numFeatures];

        for (int j = 0; j < numFeatures; j++) {
            double sum = 0.0;
            for (int i = 0; i < numSamples; i++) {
                sum += inputData.get(i)[j];
            }
            means[j] = sum / numSamples;
        }

        for (int j = 0; j < numFeatures; j++) {
            double sum = 0.0;
            for (int i = 0; i < numSamples; i++) {
                sum += Math.pow(inputData.get(i)[j] - means[j], 2);
            }
            stds[j] = Math.sqrt(sum / numSamples);
        }

        for (int i = 0; i < numSamples; i++) {
            for (int j = 0; j < numFeatures; j++) {
                if (stds[j] > 0) {
                    normalizedData[i][j] = (inputData.get(i)[j] - means[j]) / stds[j];
                } else {
                    normalizedData[i][j] = 0.0;
                }
            }
        }

        List<double[]> normalizedInputData = new ArrayList<>();
        for (double[] sample : normalizedData) {
            normalizedInputData.add(sample);
        }

        return normalizedInputData;
    }

    private static void testNeuralNetwork(NeuralNetwork ann, List<double[]> testInputData,
            List<double[]> testOutputData) {
        int correct = 0;
        int total = testInputData.size();

        for (int i = 0; i < total; i++) {
            double[] inputs = testInputData.get(i);
            double[] targets = testOutputData.get(i);
            double[] outputs = ann.computeOutputs(inputs);

            double predictedClass = outputs[0] >= 0.5 ? 1.0 : 0.0;
            double actualClass = targets[0];

            if (predictedClass == actualClass) {
                correct++;
            }
        }

        double accuracy = (double) correct / total * 100;
        double roundedAccuracy = Math.round(accuracy * 100.0) / 100.0;
        System.out.println("\u001B[32mTesting accuracy: " + roundedAccuracy + "%\u001B[0m");
    }
}