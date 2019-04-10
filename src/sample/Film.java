package sample;

import javafx.scene.image.Image;
import org.jsoup.nodes.Document;

public class Film {
    private String title;
    private final String URL;
    private final Image image;
    private Document document;


    Film(String title , String URL , Image image)
    {
        this.title = title;
        this.URL = URL;
        this.image = image;
        this.document = null;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
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
