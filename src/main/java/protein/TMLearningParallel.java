package protein;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class TMLearningParallel {

    public void randomizeTMLearnTrials(int numberSimulations) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        MaxAccuracyTask task = new MaxAccuracyTask(0, numberSimulations);
        float maxAccuracy = forkJoinPool.invoke(task);

        System.out.println("Max Accuracy: " + maxAccuracy);
    }

    private class MaxAccuracyTask extends RecursiveTask<Float> {
        private static final int THRESHOLD = 100; // Adjust this based on your performance needs
        private int start, end;

        public MaxAccuracyTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Float compute() {
            if (end - start <= THRESHOLD) {
                // Perform sequential computation for small range
                float maxAccuracy = 0;
                for (int i = start; i < end; i++) {
                    int clauses = 2000 + (int) (Math.random() * 2000);
                    int nLiterals = 50 + (int) (Math.random() * 300);
                    int threshold = 100 + (int) (Math.random() * 200);
                    float specificity = 10f + (float) (Math.random() * 10);
                    boolean negativeFocused = true;

                    float accuracy = learning(clauses, nLiterals, threshold, specificity, negativeFocused);
                    maxAccuracy = Math.max(maxAccuracy, accuracy);
                }
                return maxAccuracy;
            } else {
                // Split the task into smaller tasks
                int mid = (start + end) / 2;
                MaxAccuracyTask leftTask = new MaxAccuracyTask(start, mid);
                MaxAccuracyTask rightTask = new MaxAccuracyTask(mid, end);

                leftTask.fork(); // Asynchronously execute the left task
                float rightResult = rightTask.compute(); // Compute the right task
                float leftResult = leftTask.join(); // Wait for left task result

                // Return the maximum of the two results
                return Math.max(leftResult, rightResult);
            }
        }
    }

    private float learning(int clauses, int nLiterals, int threshold, float specificity, boolean negativeFocused) {
        // Your learning logic goes here
        // Placeholder for illustration
        return (float) Math.random();
    }

    public static void main(String[] args) {
        TMLearningParallel tmLearningParallel = new TMLearningParallel();
        tmLearningParallel.randomizeTMLearnTrials(1000); // Example usage
    }
}
