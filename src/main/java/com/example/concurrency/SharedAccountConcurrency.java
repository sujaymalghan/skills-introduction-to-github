package com.example.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class SharedAccount {
    private int balance = 100; // Initial balance

    // Thread-safe deposit method
    synchronized void deposit(int amount) {
        balance += amount;
        System.out.println(Thread.currentThread().getName() + " deposited " + amount + ". Current balance: " + balance);
    }

    // Thread-safe withdraw method
    synchronized void withdraw(int amount) {
        if (balance >= amount) {
            balance -= amount;
            System.out.println(Thread.currentThread().getName() + " withdrew " + amount + ". Current balance: " + balance);
        } else {
            System.out.println(Thread.currentThread().getName() + " insufficient balance to withdraw " + amount + ". Current balance: " + balance);
        }
    }

    public int getBalance() {
        return balance;
    }
}

public class SharedAccountConcurrency {
    public static void main(String[] args) {
        SharedAccount account = new SharedAccount();

        // Create a thread pool with 5 threads
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        // Deposit task
        Runnable depositTask = () -> {
            for (int i = 0; i < 10; i++) {
                account.deposit(10);
                try {
                    Thread.sleep(50); // Simulate transaction delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // Withdraw task
        Runnable withdrawTask = () -> {
            for (int i = 0; i < 10; i++) {
                account.withdraw(10);
                try {
                    Thread.sleep(50); // Simulate transaction delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // Submit multiple deposit and withdraw tasks
        for (int i = 0; i < 3; i++) {
            executorService.submit(depositTask); // Simulate 3 depositors
            executorService.submit(withdrawTask); // Simulate 3 withdrawers
        }

        // Shutdown the executor
        executorService.shutdown();
    }
}
