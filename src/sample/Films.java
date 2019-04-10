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
import java.util.concurrent.*;

public class Films extends ArrayList<Film> {


    class Worker implements Runnable
    {
        private int count;
        private Films films;
        Worker(int count , Films films)
        {
            this.count = count;
            this.films = films;
        }

        @Override
        public void run() {
            while (films.get(count).getDocument() == null){
                try {
                    films.get(count).setDocument(Jsoup.connect(films.get(count).getURL()).get());
                    //System.out.println(count);
                }catch (Exception e)
                {
                    System.out.println(e);
                }
            }
        }
    }


    private String searchWord;
    Films(String searchWord)
    {
        this.searchWord = searchWord;
    }

    public String getSearchWord() {
        return searchWord;
    }
    //TODO Just Load The Data TO Viewa and then we're done
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

            ExecutorService executor = Executors.newFixedThreadPool(10);
            for (int i = 0; i < this.size(); i++) {

                executor.execute(new Worker(i , this));
            }

            Document tempdoc = Jsoup.connect("https://www.imdb.com/title/tt0241527/videoplayer/vi3944653593?ref_=tt_pv_vi_aiv_1").get();
           // System.out.println(tempdoc.select(".video-player__playlist-header-index").text().split("of ")[1]);
            String s =tempdoc.select("script").get(6) + "";

            s=s.split("videoMetadata")[1];
            s=s.split("</script>")[0];
            Trailers t = new Trailers();
            t.LoadTrailer(s);
            this.get(3).setTrailers(t);
            System.out.println(this.get(3).getTrailers().get(0).getEncodings().get(1).getDefinition());

            //            for (int i = 0; i < this.get(0).getTrailers().size(); i++) {
//                System.out.println(this.get(3).getTrailers().get(i).getEncodings().get(0).getVideoUrl());
//            }
            //System.out.println(s);
        //            Trailers t = new Trailers();
//            t.LoadTrailer(s);
//            for (int i = 0; i <this.get(0).getTrailers().size(); i++) {
//
//                System.out.println(this.get(3).getTrailers().get(i).getEncodings().get(0).getVideoUrl());
//            }
            //            String temp;
//
//            temp = s.split("\"encodings\":\\[")[1].split("],")[0];
//            temp = temp.split("},")[0];
//            System.out.println(s);
            executor.shutdown();
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
            System.out.println(e.getStackTrace());
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
