package dev.boenkkk.filemover;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class MoverFilesRecursive {

    private static final Logger logger = LoggerFactory.getLogger(MoverFilesRecursive.class);
    
    public static void doit() {

        // credential
        String host = "";
        int port = 22; // default 22
        String username = "";
        String password = "";

        // parameter
        String sourceDirectory = "";
        String destinationDirectory = ""; // must create destination parent directory
        int inputDate = 20230301; // yyyyMMdd

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            moveFiles(channelSftp, sourceDirectory, destinationDirectory, inputDate);

            channelSftp.disconnect();
            session.disconnect();
        } catch (JSchException | SftpException e) {
            logger.info(e.getMessage());
        }
    }

    private static void moveFiles(ChannelSftp channelSftp, String sourceDirectory, String destinationDirectory, int inputDate) throws SftpException {
        Vector<ChannelSftp.LsEntry> files = channelSftp.ls(sourceDirectory);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        for (ChannelSftp.LsEntry file : files) {
            String filename = file.getFilename();
            if (!filename.equals(".") && !filename.equals("..")) {
                String filePath = sourceDirectory + "/" + filename;

                if (file.getAttrs().isDir()) {
                    String newDestinationDirectory = destinationDirectory + "/" + filename;
                    
                    // channelSftp.mkdir(newDestinationDirectory);
                    // Check if the directory exists before attempting to create it
                    try {
                        channelSftp.ls(newDestinationDirectory);
                    } catch (SftpException ex) {
                        // Directory does not exist, so create it
                        channelSftp.mkdir(newDestinationDirectory);
                    }

                    moveFiles(channelSftp, filePath, newDestinationDirectory, inputDate);
                } else {
                    long lastModifiedTime = file.getAttrs().getMTime() * 1000L;
                    Date lastModifiedDate = new Date(lastModifiedTime);
                    String formattedDate = dateFormat.format(lastModifiedDate);

                    // Extract the date part
                    String fileDate = formattedDate.substring(0, 8);
                    int fileDateInt = Integer.parseInt(fileDate);

                    // move file condition
                    if (fileDateInt <= inputDate) {
                        String newFilePath = destinationDirectory + "/" + filename;
                        // moving files
                        channelSftp.rename(filePath, newFilePath);

                        logger.info("Moved date|old|new: "+fileDate+"|"+filePath+"|"+newFilePath);
                    }
                }
            }
        }
    }
}
