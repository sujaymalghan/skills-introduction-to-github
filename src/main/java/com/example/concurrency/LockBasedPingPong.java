package com.example.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockBasedPingPong {

    private static final Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();
    private static final int MAX_PRINT = 10;

    /**
     * True if it's Ping's turn, false if it's Pong's turn.
     */
    private static boolean isPingTurn = true;

    public static void main(String[] args) {

        // Thread for printing "Ping"
        Thread pingThread = new Thread(() -> {
            for (int i = 0; i < MAX_PRINT; i++) {
                lock.lock();
                try {
                    // While it's not Ping's turn, await
                    while (!isPingTurn) {
                        condition.await();
                    }
                    System.out.println("Ping");
                    // Flip turn to Pong
                    isPingTurn = false;
                    // Signal the other thread to proceed
                    condition.signalAll();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupt status
                } finally {
                    lock.unlock();
                }
            }
        });

        // Thread for printing "Pong"
        Thread pongThread = new Thread(() -> {
            for (int i = 0; i < MAX_PRINT; i++) {
                lock.lock();
                try {
                    // While it's still Ping's turn, await
                    while (isPingTurn) {
                        condition.await();
                    }
                    System.out.println("Pong");
                    // Flip turn to Ping
                    isPingTurn = true;
                    // Signal the other thread to proceed
                    condition.signalAll();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        });

        pingThread.start();
        pongThread.start();

        try {
            pingThread.join();
            pongThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Done!");
    }
}
