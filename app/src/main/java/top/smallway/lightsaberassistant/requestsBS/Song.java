package top.smallway.lightsaberassistant.requestsBS;

public class Song {
    private String songAuthorName;
    private String songName;
    private String songPicURL;
    private String downloadURL;
    private String difficulty;

    public Song(String songAuthorName, String songName, String songPicURL, String downloadURL, String difficulty) {
        this.songAuthorName = songAuthorName;
        this.songName = songName;
        this.songPicURL = songPicURL;
        this.downloadURL = downloadURL;
        this.difficulty = difficulty;
    }

    public String getSongAuthorName() {
        return songAuthorName;
    }

    public void setSongAuthorName(String songAuthorName) {
        this.songAuthorName = songAuthorName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongPicURL() {
        return songPicURL;
    }

    public void setSongPicURL(String songPicURL) {
        this.songPicURL = songPicURL;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
