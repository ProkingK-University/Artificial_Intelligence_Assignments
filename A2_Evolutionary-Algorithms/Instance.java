class Instance {
    int numItems;
    int capacity;

    int[] values;
    int[] weights;

    String solution;

    Instance(int numItems, int capacity, int[] values, int[] weights, String solution) {
        this.values = values;
        this.weights = weights;
        this.solution = solution;
        this.numItems = numItems;
        this.capacity = capacity;
    }
}