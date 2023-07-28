package dev.boenkkk.filemover;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileMoverApplication {

	private static final Logger logger = LoggerFactory.getLogger(FileMoverApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(FileMoverApplication.class, args);
		logger.info("File Mover Apps Running!");

		// list files recursively
		// ls -R -l --time-style="+%Y%m%d" /path

		// check size
		// du -h /path

		// count files
		// find /path -type f | wc -l

		// Run
		// ListFiles.doit();
		// MoverFilesRecursive.doit();
		MoverFilesWoSFTP.doit();

		logger.info("File Mover Apps Finished!");
	}
}
/* 
TODO:
-backup/copy files
-count files
-cek size
-run apps
-count files
-cek size
*/