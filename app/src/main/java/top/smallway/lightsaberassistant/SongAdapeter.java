package top.smallway.lightsaberassistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import top.smallway.lightsaberassistant.requestsBS.Song;

public class SongAdapeter extends RecyclerView.Adapter<SongAdapeter.MyViewHolder> {
    public SongAdapeter(List<Song> data, Context context) {
        this.data = data;
        this.context = context;
    }

    private List<Song> data;
    private Context context;


    @NonNull
    @Override
    public SongAdapeter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapeter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Song song = data.get(position);
        holder.song_name.setText(song.getSongName());
        holder.song_speed.setText("速度："+song.getDifficulty());
        holder.author_name.setText(song.getSongAuthorName());
        Glide.with(holder.itemView.getContext()).load(song.getSongPicURL())
                .placeholder(R.drawable.moxi)
                .error(R.drawable.moxi)
                .into(holder.pic);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), second_song.class);
                intent.putExtra("song_name",data.get(position).getSongName());
                intent.putExtra("song_speed",data.get(position).getDifficulty());
                intent.putExtra("author_name",data.get(position).getSongAuthorName());
                intent.putExtra("SongPicURL",data.get(position).getSongPicURL());
                intent.putExtra("downloadURL",data.get(position).getDownloadURL());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView pic;
        private TextView song_name, author_name, song_speed;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.pic);
            song_name = itemView.findViewById(R.id.song_name);
            author_name = itemView.findViewById(R.id.author_name);
            song_speed = itemView.findViewById(R.id.song_speed);
        }
    }
}
