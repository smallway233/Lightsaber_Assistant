package top.smallway.lightsaberassistant.requestsBS;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import top.smallway.lightsaberassistant.Okhttp.OkHttpUtils;

public class Main {
    private static String url = "https://beatsaver.com/api/search/text/0?sortOrder=Relevance&q=";

    public static List<Song> search(String songName) throws IOException {
        String requestURL = url + songName;
        String response;

        response = OkHttpUtils.sendGetRequest(requestURL);

        return analysis(response);
    }

    private static List<Song> analysis(String response) {
        List<Song> songList = new ArrayList<>();
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONArray docsArray = jsonObject.getJSONArray("docs");
        for (int i = 0; i < docsArray.size(); i++) {
            JSONObject docObject = docsArray.getJSONObject(i);
            String songAuthorName = docObject.getJSONObject("metadata").getString("songAuthorName");
            String songName = docObject.getString("name");
            String songPicURL = docObject.getJSONObject("uploader").getString("avatar");
            String downloadURL = docObject.getJSONArray("versions").getJSONObject(0).getString("downloadURL");
            String difficulty = String.valueOf(docObject.getJSONArray("versions").getJSONObject(0).getJSONArray("diffs").getJSONObject(0).getInteger("nps"));

            Song song = new Song(songAuthorName, songName, songPicURL, downloadURL, difficulty);
            songList.add(song);
        }
        return songList;
    }


}