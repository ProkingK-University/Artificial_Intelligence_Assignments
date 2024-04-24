class Instance {
    int numItems;
    int capacity;

    double[] values;
    double[] weights;

    String solution;

    Instance(int numItems, int capacity, double[] values, double[] weights, String solution) {
        this.values = values;
        this.weights = weights;
        this.solution = solution;
        this.numItems = numItems;
        this.capacity = capacity;
    }
}