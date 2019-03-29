package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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

    public void loadDataFromIMDB()
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
    }


    public boolean save()
    {
        try{
            //creating the Folder structure for the current searchWord that we want to save
            File f = new File("Resources/"+this.getSearchWord());
            File imageFolder= new File(f.getPath() + "/Images");
            f.mkdirs();
            imageFolder.mkdirs();
            File imageFile;
            BufferedImage bImage;
            File result = new File(f.getPath() + "/result.txt");
            FileWriter fr = new FileWriter(result, true);
            BufferedWriter br = new BufferedWriter(fr);
            PrintWriter pr = new PrintWriter(br);

            int i = 1;
            for (Film film: this) {
                imageFile = new File(imageFolder.getPath()+ "/" + i++ + ".jpg");
                bImage = SwingFXUtils.fromFXImage(film.getImage() , null);
                ImageIO.write(bImage , "jpg" , imageFile);
                pr.println(film.getTitle() +"$"+ film.getURL() +"$");
            }

            pr.flush();
            pr.close();
            br.close();
            fr.close();
            return true;


        }catch (Exception e ){
            System.out.println(e);
        }
        return false;
    }



    public void loadLocalData() {
        try {

            String[] eachLink = new String[2];
            eachLink[0] = "";
            eachLink[1] = "";
            File f = new File("Resources/" + searchWord + "/result.txt");
            FileReader fr = new FileReader(f);
            BufferedReader fbr = new BufferedReader(fr);
            int i, j = 0, imageCounter = 1;
            while ((i = fbr.read()) != -1) {

                if (j > 1) {
                    this.add(new Film(eachLink[0] , eachLink[1] , new Image("file:Resources/" + searchWord + "/Images/" + imageCounter + ".jpg")));
                    eachLink = new String[2];
                    eachLink[0] = "";
                    eachLink[1] = "";
                    j = 0;
                    imageCounter++;
                }
                if ((char) i == '$')
                    j++;
                else
                    eachLink[j] += (char) i;
            }
            fbr.close();
            fr.close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }


    /**
     * this will return all the words the user searched for
     *
     * */


    public String[] history()
    {
        File f = new File("Resources/");
        return f.list();
    }


    /**
     * this method will check if we have searched for a word before or not
     *
     * */


    public boolean exists(String searchWord) {

        try{
            File f = new File("Resources");
            String[] dirs = f.list();
            for (String dir: dirs) {
                if(dir.equals(searchWord))
                    return true;
            }
        }catch (Exception e ){
            System.out.println(e);
        }
        return false;

    }

}
