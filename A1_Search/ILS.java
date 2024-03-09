import java.util.*;

public class ILS {
    int numCampuses = 5;

    int[][] costMatrix = {
            { 0, 15, 20, 22, 30 },
            { 15, 0, 10, 12, 25 },
            { 20, 10, 0, 8, 22 },
            { 22, 12, 8, 0, 18 },
            { 30, 25, 22, 18, 0 }
    };

    public void run() {
        List<Integer> route = initialRoute();

        for (int i = 0; i < 1000; i++) {
            List<Integer> newRoute = perturb(route);
            route = localSearch(route);

            if (calculateDistance(newRoute) < calculateDistance(route)) {
                route = newRoute;
            }
        }

        System.out.println("Shortest possible route: " + route);
        System.out.println("Distance: " + calculateDistance(route));
    }

    private List<Integer> initialRoute() {
        List<Integer> route = new ArrayList<>();

        for (int i = 0; i < numCampuses; i++) {
            route.add(i);
        }

        return route;
    }

    private List<Integer> perturb(List<Integer> route) {
        Random rand = new Random();
        List<Integer> newRoute = new ArrayList<>(route);

        int i = rand.nextInt(numCampuses);
        int j = rand.nextInt(numCampuses);

        Collections.swap(newRoute, i, j);

        return newRoute;
    }

    private List<Integer> localSearch(List<Integer> route) {
        boolean improved = true;
        List<Integer> newRoute = new ArrayList<>(route);

        while (improved) {
            improved = false;

            for (int i = 0; i < numCampuses; i++) {
                for (int j = i + 1; j < numCampuses; j++) {
                    List<Integer> swapped = swap(newRoute, i, j);

                    if (calculateDistance(swapped) < calculateDistance(newRoute)) {
                        newRoute = swapped;
                        improved = true;
                    }
                }
            }
        }

        return newRoute;
    }

    private List<Integer> swap(List<Integer> tour, int i, int j) {
        List<Integer> newTour = new ArrayList<>(tour);

        while (i < j) {
            Collections.swap(newTour, i++, j--);
        }

        return newTour;
    }

    private int calculateDistance(List<Integer> route) {
        int distance = 0;

        for (int i = 0; i < route.size(); i++) {
            int campus1 = route.get(i);
            int campus2 = route.get((i + 1) % route.size());

            distance += costMatrix[campus1][campus2];
        }

        return distance;
    }
}