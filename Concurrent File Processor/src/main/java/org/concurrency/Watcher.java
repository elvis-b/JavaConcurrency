package org.concurrency;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Watcher implements Runnable {

    private static final String INPUT_DIR = "./src/main/resources";
    private static final String OUTPUT_DIR = "./src/main/output";
    private static final long SLEEP_INTERVAL = 60000; // 1 minute

    private final File inputDirectory = new File(INPUT_DIR);
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public Watcher() {
        createDirectory(inputDirectory);
        createDirectory(new File(OUTPUT_DIR));
    }

    @Override
    public void run() {
        while (true) {
            File[] files = inputDirectory.listFiles((dir, name) -> name.endsWith(".txt"));
            if (files != null && files.length != 0) {
                Arrays.stream(files).forEach(
                        file -> {
                            Runnable fileProcessor = new FileProcessor(file);
                            Thread t = new Thread(fileProcessor);
                            t.setUncaughtExceptionHandler(new ExceptionHandler());
                            executorService.submit(fileProcessor);
                        }
                );
            }
            sleep();
        }
    }

    private void createDirectory(File directory) {
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directory created: " + directory.getAbsolutePath());
            } else {
                System.err.println("Failed to create directory: " + directory.getAbsolutePath());
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(SLEEP_INTERVAL);
        } catch (InterruptedException e) {
            System.err.println("Watcher thread interrupted");
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
