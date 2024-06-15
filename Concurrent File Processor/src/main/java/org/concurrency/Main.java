package org.concurrency;

public class Main {
    public static void main(String[] args) {
        new Thread(new Watcher()).start();
    }
}

