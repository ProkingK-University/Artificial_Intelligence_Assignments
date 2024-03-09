import java.util.*;

public class SA {
    private double t = 0.95;
    private double a = 0.01;

    private int numCampuses = 5;
    private int[][] costMatrix = {
            { 0, 15, 20, 22, 30 },
            { 15, 0, 10, 12, 25 },
            { 20, 10, 0, 8, 22 },
            { 22, 12, 8, 0, 18 },
            { 30, 25, 22, 18, 0 }
    };

    public void solve() {

        ArrayList<Integer> bestRoute = new ArrayList<>(initialRoute());
        ArrayList<Integer> currentRoute = new ArrayList<>(initialRoute());

        while (t > 0) {
            ArrayList<Integer> newRoute = new ArrayList<>(currentRoute);

            int tourPos1 = (int) (newRoute.size() * Math.random());
            int tourPos2 = (int) (newRoute.size() * Math.random());
            Collections.swap(newRoute, tourPos1, tourPos2);

            int currentEnergy = calculateDistance(currentRoute);
            int neighbourEnergy = calculateDistance(newRoute);

            if (acceptanceProbability(currentEnergy, neighbourEnergy) > Math.random()) {
                currentRoute = new ArrayList<>(newRoute);
            }

            if (calculateDistance(currentRoute) < calculateDistance(bestRoute)) {
                bestRoute = new ArrayList<>(currentRoute);
            }

            t *= a;
        }

        System.out.println("Shortest possible route: " + bestRoute);
        System.out.println("Distance: " + calculateDistance(bestRoute));
    }

    private List<Integer> initialRoute() {
        List<Integer> route = new ArrayList<>();

        for (int i = 0; i < numCampuses; i++) {
            route.add(i);
        }

        return route;
    }

    private double acceptanceProbability(int energy, int newEnergy) {
        if (newEnergy < energy) {
            return 1.0;
        }
        return Math.exp((energy - newEnergy) / t);
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