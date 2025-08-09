import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

//ArchedNosi: There are likely ways to optimize this to be faster that I am unaware of, if any advancements can be made, feel free to let me know.

public class C4 {
    public static int counterTotalHits = 0;
    public static int counterTotalElectroApp = 0;
    public static int minApp = Integer.MAX_VALUE;
    public static int maxApp = Integer.MIN_VALUE;
    public static final List<Integer> apps = new ArrayList<>();
    public static float avgApp = 0;

    static final int CYCLE_LENGTH = 29;

    public static void main(String[] args) {

        try (Scanner reader = new Scanner(System.in)) {
            System.out.println("Is C4 active? (True/False)");
            boolean checkC4 = reader.nextBoolean();
            System.out.println("Enter the desired amount of entities (1, 2, or 3+).");
            int entityCount = Math.max(reader.nextInt(), 1);
            System.out.println("Enter the total number of iterations.");
            int trialCount = Math.max(reader.nextInt(), 1);
            System.out.println("-----");

            if (entityCount == 1) {
                runSingleEntity(trialCount);
            } else {
                C4entity[] entities = new C4entity[entityCount];
                for (int i = 0; i < entityCount; i++) {
                    entities[i] = new C4entity(i + 1);
                }
                if (entityCount == 2) {
                    runTwoEntities(trialCount, checkC4, entities);
                } else {
                    runMultipleEntities(trialCount, checkC4, entities);
                }
            }
        }
    }

    private static void runSingleEntity(int trialCount) {
        for (int i = 1; i <= trialCount; i++) {
            int counterICD = 0;
            double timerICD = 0.0;
            int counterElectroApp = 0;

            System.out.println("Trial " + i + ".\nEntity #1 | ");
            for (int k = 1; k <= CYCLE_LENGTH; k++) {
                if (counterICD == 0) {
                    System.out.print("X");
                    counterICD++;
                    counterElectroApp++;
                } else if (timerICD >= 2.5) {
                    System.out.print("X");
                    timerICD = 0;
                    counterICD = 1;
                    counterElectroApp++;
                } else if (counterICD == 3) {
                    System.out.print("X");
                    timerICD += 0.5;
                    counterICD = 1;
                    counterElectroApp++;
                } else {
                    System.out.print("O");
                    timerICD += 0.5;
                    counterICD++;
                }
                counterTotalHits++;
            }
            System.out.println(" | Applied Electro " + counterElectroApp + " times");
            counterTotalElectroApp += counterElectroApp;
        }
        System.out.println("-----\nApplied Electro " + counterTotalElectroApp + " times total out of " + counterTotalHits);
    }

    private static void runTwoEntities(int trialCount, boolean checkC4, C4entity[] entities) {
        for (int i = 1; i <= trialCount; i++) {
            System.out.print("Trial " + i + ".\n");
            for (int k = 1; k <= CYCLE_LENGTH; k++) {
                int countTarget = 1;

                // target count selector
                if (checkC4) {
                    double rand = Math.random();
                    if (k == 1) {
                        countTarget = (rand >= 0.6) ? 2 : 1;
                    } else {
                        countTarget = (rand >= 0.15) ? 2 : 1;
                    }
                }

                List<Integer> entityList = new ArrayList<>(entities.length);
                for (int a = 1; a <= entities.length; a++) entityList.add(a);
                shuffle(entityList);

                List<Integer> entityHitList = entityList.subList(0, countTarget);
                List<Integer> entityMissList = entityList.subList(countTarget, entityList.size());

                for (int b : entityHitList) entities[b - 1].hitCheck();
                for (int b : entityMissList) entities[b - 1].missCheck();
            }
            int concurrentElectroApp = 0;
            for (C4entity entity : entities) {
                concurrentElectroApp += entity.getCounterElectroApp();
                entity.finalPass();
            }
            System.out.println("Electro was individually applied " + concurrentElectroApp + " times");
            if (minApp > concurrentElectroApp) minApp = concurrentElectroApp;
            if (maxApp < concurrentElectroApp) maxApp = concurrentElectroApp;
            apps.add(concurrentElectroApp);
            avgApp = (float) apps.stream().mapToInt(Integer::intValue).average().orElse(0);
        }
        printSummary(trialCount, entities);
    }

    private static void runMultipleEntities(int trialCount, boolean checkC4, C4entity[] entities) {
        for (int i = 1; i <= trialCount; i++) {
            System.out.print("Trial " + i + ".\n");
            int countTargetPrev = 0;
            for (int k = 1; k <= CYCLE_LENGTH; k++) {
                int countTarget = 1;

                if (!checkC4) {
                    countTarget = 1;
                } else {
                    double rand = Math.random();
                    if (k == 1) {
                        countTarget = (rand >= 0.55) ? 2 : 1;
                    } else if (countTargetPrev == 3) {
                        countTarget = (rand >= 0.5) ? 2 : 1;
                    } else {
                        if (rand <= 0.25) countTarget = 1;
                        else if (rand <= 0.5) countTarget = 3;
                        else countTarget = 2;
                    }
                }
                countTargetPrev = countTarget;

                List<Integer> entityList = new ArrayList<>(entities.length);
                for (int a = 1; a <= entities.length; a++) entityList.add(a);
                shuffle(entityList);

                List<Integer> entityHitList = entityList.subList(0, countTarget);
                List<Integer> entityMissList = entityList.subList(countTarget, entityList.size());

                for (int b : entityHitList) entities[b - 1].hitCheck();
                for (int b : entityMissList) entities[b - 1].missCheck();
            }
            int concurrentElectroApp = 0;
            for (C4entity entity : entities) {
                concurrentElectroApp += entity.getCounterElectroApp();
                entity.finalPass();
            }
            System.out.println("Electro was individually applied " + concurrentElectroApp + " times");
            if (minApp > concurrentElectroApp) minApp = concurrentElectroApp;
            if (maxApp < concurrentElectroApp) maxApp = concurrentElectroApp;
            apps.add(concurrentElectroApp);
            avgApp = (float) apps.stream().mapToInt(Integer::intValue).average().orElse(0);
        }
        printSummary(trialCount, entities);
    }

    private static void printSummary(int trialCount, C4entity[] entities) {
        System.out.println("-----\nApplied Electro " + counterTotalElectroApp + " times total out of " + counterTotalHits
                + " | (" + round(100.0 * counterTotalElectroApp / counterTotalHits) + "%)");
        System.out.println("Min: " + minApp + " | Avg: " + round(avgApp) + " | Max: " + maxApp);
        for (C4entity entity : entities) {
            System.out.println(entity.arcLog
                    + "\n  Hit " + entity.totalHitsSum + " times total out of " + entity.totalAttempts + " ("
                    + round(100.0 * entity.totalHitsSum / entity.totalAttempts) + "%)"
                    + "\n  Was hit on average of " + round((double) entity.totalHitsSum / trialCount) + " times."
                    + "\n  Was applied Electro on average of " + round(entity.electroAppsAvg) + " times.");
        }
    }

    public static double round(double x) {
        x *= 100;
        return (x > 0) ? ((int) (x + 0.5) / 100.0) : ((int) (x - 0.5) / 100.0);
    }

    public static <T> void shuffle(List<T> list) {
        Random random = new Random();
        for (int i = list.size() - 1; i >= 1; i--) {
            int j = random.nextInt(i + 1);
            T obj = list.get(i);
            list.set(i, list.get(j));
            list.set(j, obj);
        }
    }
}

//
class C4entity {
    public int counterICD;
    public double timerICD;
    public int counterElectroApp;
    public int totalHits;
    public int totalHitsSum;
    public int totalAttempts;
    public final List<Integer> electroApps = new ArrayList<>();
    public double electroAppsAvg;

    String arcLog;

    public C4entity(int i) {
        this.counterICD = 0;
        this.timerICD = 0.0;
        this.counterElectroApp = 0;
        this.totalHits = 0;
        this.totalAttempts = 0;
        this.arcLog = "Entity #" + i + " | ";
    }

    @Override
    public String toString() {
        return "\ncounterICD: " + counterICD
                + "\ntimerICD: " + timerICD
                + "\ncounterElectroApp: " + counterElectroApp
                + "\narcLog: " + arcLog + "\n";
    }

    public int getCounterElectroApp() {
        return counterElectroApp;
    }

    public void hitCheck() {
        if (counterICD == 0) {
            arcLog += "X";
            counterICD++;
            counterElectroApp++;
            totalHits++;
            totalAttempts++;
        } else if (timerICD > 2.5 || counterICD == 3) {
            arcLog += "X";
            counterICD = 1;
            counterElectroApp++;
            totalHits++;
            totalAttempts++;
        } else {
            arcLog += "O";
            counterICD++;
            totalHits++;
            totalAttempts++;
        }
        C4.counterTotalHits++;

        // timer reset
        if (timerICD >= 2.5)
            timerICD = 0;
        else
            timerICD += 0.5;
    }

    public void missCheck() {
        if (timerICD != 0)
            timerICD += 0.5;
        arcLog += "-";
        totalAttempts++;
    }

    public void finalPass() {
        System.out.println(arcLog + " | Hit " + totalHits + " times and Applied Electro " + counterElectroApp + " times");
        C4.counterTotalElectroApp += counterElectroApp;
        counterICD = 0;
        timerICD = 0.0;
        totalHitsSum += totalHits;
        totalHits = 0;
        // Remove last cycle logs (29 chars)
        arcLog = arcLog.substring(0, arcLog.length() - C4.CYCLE_LENGTH);
        electroApps.add(counterElectroApp);
        electroAppsAvg = electroApps.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        counterElectroApp = 0;
    }
}
