
import org.concurrency.FileProcessor;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileProcessorTest {

    @Test
    public void testFileProcessing() throws IOException {
        String testDir = "./src/test/resources";
        String testFileName = "testfile.txt";
        File testFile = new File(testDir, testFileName);

        // Ensure the test file exists
        Assert.assertTrue(testFile.exists(), "Test file should exist");

        // Process the file
        FileProcessor fileProcessor = new FileProcessor(testFile);
        fileProcessor.run();

        // Verify output file exists
        File outputFile = new File("./src/main/output/" + testFileName);
        Assert.assertTrue(outputFile.exists(), "Output file should exist");

        // Verify backup file exists
        File backupFile = new File("./src/main/backup/" + testFileName);
        Assert.assertTrue(backupFile.exists(), "Backup file should exist");

        // Clean up (optional)
        Files.deleteIfExists(outputFile.toPath());
        Files.deleteIfExists(backupFile.toPath());
    }
}