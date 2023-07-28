package dev.boenkkk.filemover;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoverFilesWoSFTP {

    private static final Logger logger = LoggerFactory.getLogger(MoverFilesWoSFTP.class);
    
    public static void doit() {
        String sourceDirectory = "";
        String destinationDirectory = "";
        int inputDate = 20230101; // yyyyMMdd

        try {
            Files.walkFileTree(Paths.get(sourceDirectory), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    File sourceFile = file.toFile();
                    long lastModifiedTime = sourceFile.lastModified();
                    Date lastModifiedDate = new Date(lastModifiedTime);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                    String formattedDate = dateFormat.format(lastModifiedDate);

                    String fileDate = formattedDate.substring(0, 8);
                    int fileDateInt = Integer.parseInt(fileDate);

                    if (fileDateInt <= inputDate) {
                        Path relativePath = Paths.get(sourceDirectory).relativize(file);
                        Path destinationPath = Paths.get(destinationDirectory, relativePath.toString());

                        File destinationFile = destinationPath.toFile();
                        File destinationDir = destinationFile.getParentFile();
                        
                        // Create the directory if it doesn't exist
                        createDirectories(destinationDir);

                        // move files
                        sourceFile.renameTo(destinationFile);

                        // System.out.println("Moved file: " + sourceFile.getAbsolutePath() + " to: " + destinationFile.getAbsolutePath());
                        logger.info("Moved date|old|new: " + fileDate + "|" + sourceFile.getAbsolutePath() + "|" + destinationFile.getAbsolutePath());
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    // Handle file visit failure if necessary
                    return FileVisitResult.CONTINUE;
                }

                private void createDirectories(File dir) throws IOException {
                    if (!dir.exists()) {
                        Files.createDirectories(dir.toPath());
                    }
                }
            });
        } catch (IOException e) {
            // e.printStackTrace();
            logger.error(e.getMessage());
        }
    }
}
