package com.example.concurrency;

public class FizzbuzzConcurrency {

    public void concurrency() {
        final Object lock = new Object();
        final int limit = 30;
        final int[] currentNum = {1};

        // Runnable for "Fizz Buzz" (divisible by 15)
        Runnable fizzBuzz = () -> {
            while (true) {
                synchronized (lock) {
                    if (currentNum[0] <= limit && currentNum[0] % 15 != 0) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }

                    if (currentNum[0] > limit) {
                        lock.notifyAll();
                        break;
                    }

                    System.out.println("Fizz Buzz");
                    currentNum[0]++;
                    lock.notifyAll();
                }
            }
        };

        // Runnable for "Fizz" (divisible by 3 but not by 15)
        Runnable fizz = () -> {
            while (true) {
                synchronized (lock) {
                    if (currentNum[0] <= limit &&
                            (currentNum[0] % 3 != 0 || currentNum[0] % 15 == 0)) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    if (currentNum[0] > limit) {
                        lock.notifyAll();
                        break;
                    }

                    System.out.println("Fizz");
                    currentNum[0]++;
                    lock.notifyAll();
                }
            }
        };

        // Runnable for "Buzz" (divisible by 5 but not by 15)
        Runnable buzz = () -> {
            while (true) {
                synchronized (lock) {
                    if (currentNum[0] <= limit &&
                            (currentNum[0] % 5 != 0 || currentNum[0] % 15 == 0)) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }

                    if (currentNum[0] > limit) {
                        lock.notifyAll();
                        break;
                    }

                    System.out.println("Buzz");
                    currentNum[0]++;
                    lock.notifyAll();
                }
            }
        };

        // Runnable for numbers (not divisible by 3 or 5)
        Runnable number = () -> {
            while (true) {
                synchronized (lock) {
                    if (currentNum[0] <= limit &&
                            (currentNum[0] % 3 == 0 || currentNum[0] % 5 == 0)) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }

                    if (currentNum[0] > limit) {
                        lock.notifyAll();
                        break;
                    }

                    System.out.println(currentNum[0]);
                    currentNum[0]++;
                    lock.notifyAll();
                }
            }
        };

        // Creating threads
        Thread threadFizzBuzz = new Thread(fizzBuzz, "FizzBuzzThread");
        Thread threadFizz = new Thread(fizz, "FizzThread");
        Thread threadBuzz = new Thread(buzz, "BuzzThread");
        Thread threadNumber = new Thread(number, "NumberThread");

        // Starting threads
        threadFizzBuzz.start();
        threadFizz.start();
        threadBuzz.start();
        threadNumber.start();

        // Waiting for threads to finish
        try {
            threadFizzBuzz.join();
            threadFizz.join();
            threadBuzz.join();
            threadNumber.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Done printing!");
    }

    public static void main(String[] args) {
        FizzbuzzConcurrency fz = new FizzbuzzConcurrency();
        fz.concurrency();
    }
}
