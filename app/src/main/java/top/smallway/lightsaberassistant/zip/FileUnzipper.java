package top.smallway.lightsaberassistant.zip;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUnzipper {
    private String zipFilePath;
    private String destDir;

    public FileUnzipper(String zipFilePath, String destDir) {
        this.zipFilePath = zipFilePath;
        this.destDir = destDir;
    }

    public void unzip() throws IOException {
        File dir = new File(destDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        FileInputStream fis = new FileInputStream(zipFilePath);
        ZipInputStream zis = new ZipInputStream(fis);

        ZipEntry entry = zis.getNextEntry();

        while (entry != null) {
            String filePath = destDir + File.separator + entry.getName();

            if (!entry.isDirectory()) {
                // If the entry is a file, extract it
                extractFile(zis, filePath);
            } else {
                // If the entry is a directory, create the directory
                File dirPath = new File(filePath);
                dirPath.mkdirs();
            }

            zis.closeEntry();
            entry = zis.getNextEntry();
        }

        zis.close();
        fis.close();
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = zipIn.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }

        fos.close();
    }
}