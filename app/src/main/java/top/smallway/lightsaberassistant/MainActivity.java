package top.smallway.lightsaberassistant;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import top.smallway.lightsaberassistant.FTP.FTPServerConnectionTester;

public class MainActivity extends AppCompatActivity {
    private Button buttonSubmit;
    private EditText editText;
    private TextView question;

    public void setIP(String IP) {
        this.IP = IP;
    }

    private static String IP;

    public static String getIP() {
        return IP;
    }

    private Boolean Boolean;



    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private boolean havePermission = false;



    
    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Toast.makeText(MainActivity.this, "连接失败，请检查VR端是否开启服务器！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                Toast.makeText(MainActivity.this, "连接成攻，我绝对不是故意打错的QAQ！", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initview();
        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpUriToBrowser(MainActivity.this,"https://www.bilibili.com/video/BV1KX4y1Y7cc");
            }
        });
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIP(editText.getText().toString());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Boolean = FTPServerConnectionTester.isFTPServerReachable(getIP());
                        Message message = new Message();
                        if (Boolean) {
                            message.what=1;
                            Intent intent = new Intent(MainActivity.this, SongList.class);
                            mHandler.sendMessage(message);
                            startActivity(intent);
                        } else {

                            message.what=0;
                            mHandler.sendMessage(message);
                        }
                    }
                }).start();
//                Intent intent = new Intent(MainActivity.this, SongList.class);
//                startActivity(intent);
            }
        });

    }



    private void initview() {
        buttonSubmit = findViewById(R.id.buttonSubmit);
        editText = findViewById(R.id.editTextIP);
        question=findViewById(R.id.question);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    private AlertDialog dialog;

    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                dialog = new AlertDialog.Builder(this)
                        .setTitle("提示")//设置标题
                        .setMessage("请开启文件访问权限，用于下载曲包至本地。否则无法正常使用本应用！")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "授权“光团助手”管理所有文件的权限", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                startActivity(intent);
                            }
                        }).create();
                dialog.show();
            } else {
                havePermission = true;
                Log.i("swyLog", "Android 11以上，当前已有权限");
            }
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    dialog = new AlertDialog.Builder(this)
                            .setTitle("提示")//设置标题
                            .setMessage("请开启文件访问权限，用于下载曲包至本地。否则无法正常使用本应用！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                                }
                            }).create();
                    dialog.show();
                } else {
                    havePermission = true;
                    Log.i("swyLog", "Android 6.0以上，11以下，当前已有权限");
                }
            } else {
                havePermission = true;
                Log.i("swyLog", "Android 6.0以下，已获取权限");
            }
        }
    }
    public static void jumpUriToBrowser(Context context, String url) {
        if (url.startsWith("www."))
            url = "http://" + url;
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            Toast.makeText(context, "网址错误", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        // 设置意图动作为打开浏览器
        intent.setAction(Intent.ACTION_VIEW);
        // 声明一个Uri
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        context.startActivity(intent);
    }


}
