package top.smallway.lightsaberassistant.FTP;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FTPUploader {

    public static void uploadDirectory(String server, int port, String username, String password, String localDirectoryPath, String remoteDirectoryPath) throws IOException {

        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("UTF-8");

        try {
            // 连接到FTP服务器
            ftp.connect(server, port);
            int reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                throw new IOException("FTP server refused connection.");
            }

            // 登录到FTP服务器
            if (!ftp.login(username, password)) {
                throw new IOException("FTP login failed.");
            }

            // 设置文件类型为二进制，避免文件损坏
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            // 创建远程目录（如果不存在）
            createRemoteDirectory(ftp, remoteDirectoryPath);

            // 上传本地文件夹及内容
            uploadDirectoryRecursive(ftp, localDirectoryPath, remoteDirectoryPath);

            // 登出并断开连接
            ftp.logout();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException e) {
                    // 忽略异常
                }
            }
        }
    }

    private static void uploadDirectoryRecursive(FTPClient ftp, String localPath, String remotePath) throws IOException {
        File localDir = new File(localPath);
        File[] subFiles = localDir.listFiles();

        if (subFiles != null && subFiles.length > 0) {
            for (File file : subFiles) {
                String localFilePath = file.getAbsolutePath();
                String remoteFilePath = remotePath + "/" + file.getName();

                if (file.isDirectory()) {
                    // 递归上传子文件夹
                    uploadDirectoryRecursive(ftp, localFilePath, remoteFilePath);
                } else {
                    // 上传单个文件
                    uploadFile(ftp, localFilePath, remoteFilePath);
                }
            }
        }
    }

    private static void uploadFile(FTPClient ftp, String localFilePath, String remoteFilePath) throws IOException {
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(localFilePath);
            ftp.storeFile(remoteFilePath, inputStream);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // 忽略异常
                }
            }
        }
    }

    private static void createRemoteDirectory(FTPClient ftp, String remoteDirectoryPath) throws IOException {
        String[] directories = remoteDirectoryPath.split("/");

        for (String dir : directories) {
            if (!dir.isEmpty()) {
                if (!ftp.changeWorkingDirectory(dir)) {
                    ftp.makeDirectory(dir);
                    ftp.changeWorkingDirectory(dir);
                }
            }
        }
    }
}