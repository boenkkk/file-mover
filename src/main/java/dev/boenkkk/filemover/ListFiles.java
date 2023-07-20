package dev.boenkkk.filemover;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class ListFiles {

    private static final Logger logger = LoggerFactory.getLogger(ListFiles.class);

    public static void doit(){
        String host = "";
        int port = 22; // default 22
        String username = "";
        String password = "";
        String directory = "";

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            channelSftp.cd(directory);
            Vector<ChannelSftp.LsEntry> files = channelSftp.ls(".");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            for (ChannelSftp.LsEntry file : files) {
                if (!file.getAttrs().isDir()) {
                    long lastModifiedTime = file.getAttrs().getMTime() * 1000L;
                    Date lastModifiedDate = new Date(lastModifiedTime);
                    String formattedDate = dateFormat.format(lastModifiedDate);

                    logger.info(formattedDate + " " +file.getFilename());
                }
            }

            channelSftp.disconnect();
            session.disconnect();
        } catch (JSchException | SftpException e) {
            logger.error(e.getMessage());
        }
    }
}
