import java.util.Random;

public class GeneticAlgorithm {
    final int TOURNAMENT_SIZE = 15;
    final int POPULATION_SIZE = 1000;
    final double MUTATION_RATE = 0.01;
    final int NUMBER_OF_GENERATIONS = 1000;

    boolean localSearch = false;

    long seed = "COS314".hashCode();
    final Random rand = new Random(seed);

    public Chromosome solve(Instance instance, boolean localSearch) {
        this.localSearch = localSearch;
        Chromosome[] population = generateInitialPopulation(instance.weights.length);
        calculateFitness(population, instance.weights, instance.values, instance.capacity);

        for (int i = 0; i < NUMBER_OF_GENERATIONS; i++) {
            population = breedOffspring(population, instance);

            calculateFitness(population, instance.weights, instance.values, instance.capacity);
        }

        return findBestChromosome(population);
    }

    Chromosome[] generateInitialPopulation(int numberOfGenes) {
        Chromosome[] population = new Chromosome[POPULATION_SIZE];

        for (int i = 0; i < POPULATION_SIZE; i++) {
            population[i] = new Chromosome(numberOfGenes);
        }

        return population;
    }

    void calculateFitness(Chromosome[] population, double[] weights, double[] values, int capacity) {
        for (Chromosome chromosome : population) {
            chromosome.calculateFitness(weights, values, capacity);
        }
    }

    Chromosome[] breedOffspring(Chromosome[] population, Instance instance) {
        Chromosome[] offspring = new Chromosome[POPULATION_SIZE];

        for (int i = 0; i < POPULATION_SIZE; i++) {
            Chromosome parent1 = tournamentSelection(population);
            Chromosome parent2 = tournamentSelection(population);

            offspring[i] = crossover(parent1, parent2);

            if (rand.nextDouble() < MUTATION_RATE) {
                offspring[i].mutate();
            }

            if (localSearch == true) {
                localSearch(offspring[i], instance.weights, instance.values, instance.capacity);
            }
        }

        return offspring;
    }

    Chromosome tournamentSelection(Chromosome[] population) {
        Chromosome best = null;

        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            Chromosome individual = population[rand.nextInt(POPULATION_SIZE)];

            if (best == null || individual.getFitness() > best.getFitness()) {
                best = individual;
            }
        }

        return best;
    }

    Chromosome crossover(Chromosome parent1, Chromosome parent2) {
        Chromosome offspring = new Chromosome(parent1.getLength());

        for (int i = 0; i < parent1.getLength(); i++) {
            offspring.setGene(i, rand.nextBoolean() ? parent1.getGene(i) : parent2.getGene(i));
        }

        return offspring;
    }

    void localSearch(Chromosome chromosome, double[] weights, double[] values, int capacity) {
        for (int i = 0; i < chromosome.getLength(); i++) {
            double oldFitness = chromosome.getFitness();

            chromosome.flipGene(i);
            chromosome.calculateFitness(weights, values, capacity);

            if (chromosome.getFitness() < oldFitness) {
                chromosome.flipGene(i);
                chromosome.calculateFitness(weights, values, capacity);
            }
        }
    }

    Chromosome findBestChromosome(Chromosome[] population) {
        Chromosome best = population[0];

        for (Chromosome chromosome : population) {
            if (chromosome.getFitness() > best.getFitness()) {
                best = chromosome;
            }
        }

        return best;
    }
}