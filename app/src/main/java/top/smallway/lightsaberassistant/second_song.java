package top.smallway.lightsaberassistant;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;

import top.smallway.lightsaberassistant.FTP.FTPUploader;
import top.smallway.lightsaberassistant.requestsBS.FileDownloader;
import top.smallway.lightsaberassistant.zip.FileUnzipper;

public class second_song extends AppCompatActivity {
    private AlertDialog dialog, complate,zip,ready;
    private ImageView pic;
    private TextView song_name, song_author, BPM;
    private String Song_name, song_speed, author_name, SongPicURL, downloadURL;
    private final String host = MainActivity.getIP(); //拿到IP地址
    private final int port = 8023; //指定端口号
    private final String username = "admin";  //指定用户名
    private final String password = ""; //指定密码


    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_song);
        initview();
        get_intent();
        song_name.setText(Song_name);
        song_author.setText(author_name);
        song_author.setTextColor(Color.parseColor("#4E8C2A"));
        BPM.setText(song_speed);

        Glide.with(this).load(SongPicURL).into(pic);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ready==null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(second_song.this);
                    builder.setMessage("正在开始准备工作");
                    ready = builder.create();
                    ready.setCancelable(false);
                    ready.show();
                }
                String fileName = Song_name + ".zip";
                String saveDir = Environment.getExternalStorageDirectory() + "/Download/Lightsaber_song_zip";
                FileDownloader fileDownloader = new FileDownloader(downloadURL);
                fileDownloader.download(saveDir, fileName, new FileDownloader.DownloadListener() {
                    private long lastTimeMillis = System.currentTimeMillis(); // 上次统计的时间戳
                    private long lastDownloadedBytes = 0; // 上次统计的已下载字节数


                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // 在UI线程更新UI界面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long currentTimeMillis = System.currentTimeMillis();
                                long elapsedTimeMillis = currentTimeMillis - lastTimeMillis;
                                long downloadedBytes = bytesDownloaded - lastDownloadedBytes;

                                double downloadSpeedKB = 0.0; // 下载速度，单位是KB/s
                                if (elapsedTimeMillis > 0) {
                                    double downloadSpeed = ((double) downloadedBytes / elapsedTimeMillis) * 1000; // 下载速度，单位是字节/秒
                                    downloadSpeedKB = downloadSpeed / 1024; // 下载速度，单位是KB/s
                                }

                                String progress = String.valueOf((bytesDownloaded * 100) / totalBytes);
                                String speed = String.format("%.2f", downloadSpeedKB); // 下载速度，保留两位小数，单位是KB/s

                                // 创建或更新AlertDialog
                                ready.dismiss();
                                if (dialog == null) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(second_song.this);
                                    builder.setTitle("正在下载...");
                                    builder.setMessage("下载进度：" + progress + "%" + "\n下载速度：" + speed + " KB/s");
                                    dialog = builder.create();
                                    dialog.setCancelable(false);
                                    dialog.show();
                                } else {
                                    dialog.setMessage("下载进度：" + progress + "%" + "\n下载速度：" + speed + " KB/s");
                                }

                                lastTimeMillis = currentTimeMillis;
                                lastDownloadedBytes = bytesDownloaded;
                            }
                        });
                    }

                    @Override
                    public void onComplete(String filePath) {
                        dialog.dismiss();
                        // 下载完成
                        String zipFilePath = saveDir + "/" + fileName;
                        String destDir = Environment.getExternalStorageDirectory() + "/Download/Lightsaber_song/" + Song_name;
                        FileUnzipper unzipper = new FileUnzipper(zipFilePath, destDir);
                        String localDirectoryPath = destDir;
                        String remoteDirectoryPath = "/Android/data/com.StarRiverVR.LightBand/files/CustomMusic/"+Song_name;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 创建或更新AlertDialog
                                if (complate == null) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(second_song.this);
                                    builder.setTitle("正在解压并导入VR");
                                    builder.setMessage("请稍后......");
                                    complate = builder.create();
                                    complate.setCancelable(false);
                                    complate.show();
                                }
                            }
                        });
                        try {
                            unzipper.unzip();
                            FTPUploader.uploadDirectory(host, port, username, password, localDirectoryPath, remoteDirectoryPath);
                        } catch (IOException e) {
                            Log.d("ZIP", e.getMessage());
                        }
                        complate.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (zip==null){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(second_song.this);
                                    builder.setTitle("导入完成");
                                    builder.setMessage("导入成攻，emmmmm啊对，成攻！");
                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            zip.dismiss();
                                        }
                                    });
                                    zip = builder.create();
                                    zip.setCancelable(false);
                                    zip.show();
                                }
                            }
                        });




                        Log.i("TAG", "Download completed. File path: " + filePath);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // 下载出错
                        dialog.dismiss();
                        Toast.makeText(second_song.this, "下载出错", Toast.LENGTH_SHORT).show();
                        // TODO: 根据需要执行相应操作，例如弹出错误提示
                        Log.i("TAG", "Download error: " + errorMessage);
                    }
                });

            }
        });


    }

    private void get_intent() {
        Intent intent = getIntent();
        Song_name = intent.getStringExtra("song_name");
        song_speed = intent.getStringExtra("song_speed");
        author_name = intent.getStringExtra("author_name");
        SongPicURL = intent.getStringExtra("SongPicURL");
        downloadURL = intent.getStringExtra("downloadURL");
    }

    private void initview() {
        pic = findViewById(R.id.pic);
        song_name = findViewById(R.id.song_name);
        song_author = findViewById(R.id.song_author);
        BPM = findViewById(R.id.BPM);
        button = findViewById(R.id.button);
    }
}