package ru.otus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AlternatingThreadNumbersPrinter {
    private static final Object monitor = new Object();
    private static final int numberOfThreads = 2;
    private static volatile int currentTurn = 1;
    private static final AtomicInteger activeThreads = new AtomicInteger(numberOfThreads);
    private static final AtomicInteger cyclesRemaining = new AtomicInteger(2);

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(new NumberPrinter(i + 1));
        }

        executor.shutdown();
    }

    static class NumberPrinter implements Runnable {
        private final int threadId;
        private int current = 1;
        private boolean ascending = true;
        private int cycleCount = 0;

        public NumberPrinter(int threadId) {
            this.threadId = threadId;
        }

        @Override
        public void run() {
            while (cycleCount < cyclesRemaining.get()) {
                synchronized (monitor) {
                    try {
                        while (currentTurn != threadId) {
                            monitor.wait();

                            if (cycleCount >= cyclesRemaining.get()) {
                                return;
                            }
                        }

                        if (ascending) {
                            if (current == 10) {
                                ascending = false;
                            }
                        } else {
                            if (current == 1) {
                                ascending = true;
                                cycleCount++;
                            }
                        }

                        System.out.printf("Поток %d: %d ", threadId, current);

                        if (ascending) {
                            current++;
                        } else {
                            current--;
                        }

                        currentTurn = (currentTurn % numberOfThreads) + 1;
                        monitor.notifyAll();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            synchronized (monitor) {
                activeThreads.decrementAndGet();
                monitor.notifyAll();
            }
        }
    }
}