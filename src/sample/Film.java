package sample;

import javafx.scene.image.Image;

public class Film {
    private String title;
    private final String URL;
    private final Image image;



    Film(String title , String URL , Image image)
    {
        this.title = title;
        this.URL = URL;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getURL() {
        return URL;
    }

    public Image getImage() {
        return image;
    }


}
