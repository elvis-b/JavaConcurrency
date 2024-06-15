package org.concurrency;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

public class FileProcessor implements Runnable {

    private final File file;
    private static final String OUTPUT_PATH = "./src/main/output/";
    private static final String BACKUP_PATH = "./src/main/backup/";

    public FileProcessor(File file) {
        this.file = file;
        createDirectory(new File(BACKUP_PATH));
    }

    @Override
    public void run() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_PATH + file.getName()));
             Stream<String> lines = Files.lines(Path.of(file.getCanonicalPath()))) {

            lines.map(this::hash)
                    .map(line -> line + "\n")
                    .forEach(el -> {
                        try {
                            writer.write(el);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });

            Files.move(file.toPath(), Path.of(BACKUP_PATH, file.getName()), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Processed and moved file: " + file.getName());

        } catch (IOException | UncheckedIOException e) {
            System.err.println("Error processing file: " + file.getName());
            e.printStackTrace();
        }
    }

    private String hash(String input) {
        if (input.isEmpty()) {
            throw new RuntimeException("The line is empty, hashing cannot be done");
        }

        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
            final byte[] hashbytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashbytes);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Hashing algorithm not found");
            e.printStackTrace();
            return "";
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
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
}
