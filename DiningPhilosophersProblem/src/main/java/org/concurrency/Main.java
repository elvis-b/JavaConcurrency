package org.concurrency;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private static List<Lock> forks = new ArrayList<>();

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            forks.add(new ReentrantLock());
        }

        Semaphore semaphore = new Semaphore(4);

        for (int i = 0; i < 5; i++) {
            new Thread(new Philosopher(i, semaphore)).start();
        }
    }

    static class Philosopher implements Runnable {

        private final int id;
        private final Semaphore semaphore;

        public Philosopher(int id, Semaphore semaphore) {
            this.id = id;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            while (true) {
                think();
                try {
                    pick_forks();
                    eat();
                } finally {
                    put_forks();
                }
            }
        }

        private void put_forks() {
            forks.get(id).unlock();
            forks.get((id + 1) % 5).unlock();
            semaphore.release();
            System.out.println("Philosopher " + id + " put down forks");
        }

        private void eat() {
            System.out.println("Philosopher " + id + " eats");
            try {
                Thread.sleep((long) (Math.random() * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void pick_forks() {
            try {
                semaphore.acquire();
                System.out.println("Philosopher " + id + " is trying to pick up forks");
                while (true) {
                    boolean pickedUpRightFork = forks.get(id).tryLock();
                    boolean pickedUpLeftFork = forks.get((id + 1) % 5).tryLock();

                    if (pickedUpRightFork && pickedUpLeftFork) {
                        System.out.println("Philosopher " + id + " picked up both forks");
                        break;
                    }

                    if (pickedUpRightFork) {
                        forks.get(id).unlock();
                    }

                    if (pickedUpLeftFork) {
                        forks.get((id + 1) % 5).unlock();
                    }

                    Thread.sleep((long) (Math.random() * 100));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void think() {
            System.out.println("Philosopher " + id + " thinks");
            try {
                Thread.sleep((long) (Math.random() * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}