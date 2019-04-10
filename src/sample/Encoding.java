package sample;

public class Encoding {
    private String definition;
    private String videoUrl;

    public String getDefinition() {
        return definition;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public Encoding(String definition, String videoUrl) {
        this.definition = definition;
        this.videoUrl = videoUrl;
    }
}
