package sample;

import javafx.scene.image.Image;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Films extends ArrayList<Film> {


    private String searchWord;
    Films(String searchWord)
    {
        this.searchWord = searchWord;
    }

    public String getSearchWord() {
        return searchWord;
    }

    public Films loadDataFromIMDB()
    {
        try {
            Document doc = Jsoup.connect("https://www.imdb.com/find?q=" + searchWord +"&s=tt").get();
            Elements elements= doc.select(".findList tr");
            for (Element e : elements) {
                this.add(new Film(e.select("td:nth-of-type(2) a").text() +" " +e.text(),
                                "https://www.imdb.com/"+ e.select("td:nth-of-type(2) a").attr("href"),
                                    new Image(e.select("td:nth-of-type(1) img").attr("src") , true)));
            }
        }catch (Exception e) {
            System.out.println(e);
        }
        return this;
    }
    public Films loadLocalData()
    {
        return this;
    }

}
