package examples;

import io.github.abdol_ahmed.btp.BoundedThreadPool;
import io.github.abdol_ahmed.btp.RejectionPolicy;

/**
 * Example demonstrating basic usage of BoundedThreadPool.
 * This is not part of the API - just a demonstration.
 */
public class BasicUsageExample {
    public static void main(String[] args) {
        // Create a thread pool using factory method
        BoundedThreadPool threadPool = BoundedThreadPool.createFixed(4);
        
        // Submit tasks
        for (int i = 0; i < 100; i++) {
            final int taskId = i;
            
            try {
                threadPool.submit(() -> {
                    System.out.println("Task " + taskId + " running in " + 
                        Thread.currentThread().getName());
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Task submission interrupted");
                break;
            }
        }
        
        // Shutdown the pool
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
