package com.example.concurrency;

import java.util.concurrent.*;

public class Multithreading {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // Use an ExecutorService just as you did before
        ExecutorService executeService = Executors.newFixedThreadPool(2);

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                System.out.println("Exception");
            }
            return "Hello";
        }, executeService);

        // Get the result from the future
        String result = future.get();
        System.out.println("Result: " + result);

        // Shutdown the executor
        executeService.shutdown();

        // Now call our function that prints even/odd alternately
        Multithreading m = new Multithreading();
        m.callfunction();
    }

    private void callfunction() {
        final Object lock = new Object();
        final int limit = 10;       // print numbers from 0 to 9
        final int[] currentNum = {0};
        // If true => it's the even thread's turn; if false => it's the odd thread's turn
        final boolean[] isEvenTurn = {true};

        // Thread that prints even numbers
        Runnable even1 = () -> {
            while (true) {
                synchronized (lock) {
                    // If it's not the even thread's turn, wait
                    while (!isEvenTurn[0]) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    // Check if we've reached the limit
                    if (currentNum[0] >= limit) {
                        lock.notifyAll();
                        break;
                    }
                    // Print the even number
                    System.out.print(currentNum[0] + " ");
                    currentNum[0]++;
                    // Switch turn to odd
                    isEvenTurn[0] = false;
                    lock.notifyAll();
                }
            }
        };

        // Thread that prints odd numbers
        Runnable odd1 = () -> {
            while (true) {
                synchronized (lock) {
                    // If it's not the odd thread's turn, wait
                    while (isEvenTurn[0]) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    // Check if we've reached the limit
                    if (currentNum[0] >= limit) {
                        lock.notifyAll();
                        break;
                    }
                    // Print the odd number
                    System.out.print(currentNum[0] + " ");
                    currentNum[0]++;
                    // Switch turn to even
                    isEvenTurn[0] = true;
                    lock.notifyAll();
                }
            }
        };

        Thread even = new Thread(even1, "EvenThread");
        Thread odd  = new Thread(odd1, "OddThread");

        even.start();
        odd.start();

        // Wait for both threads to finish
        try {
            even.join();
            odd.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nDone printing!");
    }
}
