# Knapsack Problem Solver

This project uses Genetic Algorithm and GA + Local Search to solve the Knapsack Problem.

## Problem Description

The Knapsack problem is a classic optimization problem in the field of computer science and operations research. It involves filling a knapsack with a collection of items, each with a weight and value, while keeping the total weight of the items in the knapsack within a given limit and maximizing the total value of the selected items.

## Algorithms Used

1. **Genetic Algorithm (GA)**: This is a search heuristic that is inspired by Charles Darwin’s theory of natural evolution. This algorithm reflects the process of natural selection where the fittest individuals are selected for reproduction in order to produce the offspring of the next generation.

2. **GA + Local Search**: This is a hybrid algorithm that combines the global search capabilities of Genetic Algorithms with the fine-tuning capabilities of Local Search.

## Running the Code

Ensure you have Java installed on your machine. You can compile and run the program using the following commands:

```bash
javac Main.java
java Main <true/false>
```

You can also run the program using the jar file:

```bash
java -jar GA.jar <true/false>
```

The <true/false> argument is used to run the GA with (true) or without (false) Local Search.