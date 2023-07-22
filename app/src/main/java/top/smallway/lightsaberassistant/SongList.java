package top.smallway.lightsaberassistant;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import top.smallway.lightsaberassistant.requestsBS.Main;
import top.smallway.lightsaberassistant.requestsBS.Song;

public class SongList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText edittext;
    private Button button;
    private List<Song> data = new ArrayList<>();
    private AlertDialog dialog;
    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {

            super.handleMessage(msg);
            if (msg.what == 1) {
                dialog.dismiss();
                SongAdapeter songAdapeter=new SongAdapeter((List<Song>) msg.obj,SongList.this);
                recyclerView.setAdapter(songAdapeter);
            }else if (msg.what==0){
                dialog.dismiss();
                Toast.makeText(SongList.this, "搜索出现未知异常", Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        initview();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songname = edittext.getText().toString();
                if (dialog == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SongList.this);

                    builder.setMessage("正在搜索中......");
                    dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                }else {
                    dialog.show();
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        try {
                            data = Main.search(songname);
                            Log.d("TAG", String.valueOf(data));
                            message.what = 1;
                            message.obj = data;
                        } catch (IOException e) {
                            message.what = 0;
                        }
                        mHandler.sendMessage(message);

                    }
                }).start();
            }
        });
    }

    private void initview() {
        button=findViewById(R.id.search);
        recyclerView = findViewById(R.id.Recycelview);
        edittext=findViewById(R.id.song_name_edit);

    }
}