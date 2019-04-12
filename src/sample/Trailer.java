package sample;

import java.util.ArrayList;

public class Trailer {


    private String titleName;
    private String videoId;
    private String description;
    private String duration;
    private ArrayList<Encoding> encodings;

    public Trailer(String titleName,String videoId, String description, String duration, ArrayList<Encoding> encodings) {
       this.titleName = titleName;
        this.videoId = videoId;
        this.description = description;
        this.duration = duration;
        this.encodings = encodings;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getDescription() {
        return description;
    }

    public String getDuration() {
        return duration;
    }

    public ArrayList<Encoding> getEncodings() {
        return encodings;
    }
}
