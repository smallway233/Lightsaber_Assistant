package top.smallway.lightsaberassistant.requestsBS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FileDownloader {
    private static final String TAG = "FileDownloader";
    private OkHttpClient httpClient;
    private String downloadUrl;

    public FileDownloader(String downloadUrl) {
        this.httpClient = new OkHttpClient();
        this.downloadUrl = downloadUrl;
    }

    public void download(String savePath, String fileName, final DownloadListener listener) {
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    listener.onError("Unexpected code " + response);
                    return;
                }

                File dir = new File(savePath);
                if (!dir.exists()) {
                    dir.mkdir();
                }

                File file = new File(dir, fileName);
                OutputStream os = new FileOutputStream(file);
                InputStream is = response.body().byteStream();

                byte[] buffer = new byte[4096];
                int len;
                long bytesDownloaded = 0;
                long totalBytes = response.body().contentLength();

                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                    bytesDownloaded += len;
                    listener.onProgress(bytesDownloaded, totalBytes);
                }

                os.flush();
                os.close();
                is.close();

                listener.onComplete(file.getAbsolutePath());
            }
        });
    }

    public interface DownloadListener {
        void onProgress(long bytesDownloaded, long totalBytes);
        void onComplete(String filePath);
        void onError(String errorMessage);
    }
}