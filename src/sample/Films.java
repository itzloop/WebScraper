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
import com.google.gson.Gson;

public class Films extends ArrayList<Film> {

    class checkWorker implements Runnable
    {
        private ArrayList<Future<?>> futures;
        checkWorker(ArrayList<Future<?>> futures)
        {
            this.futures = futures;
        }

        @Override
        public void run() {
            boolean allDone = true;
            while (!allDone)
            {
                for(Future<?> future : futures){
                    allDone &= future.isDone();
                }
                System.out.println(allDone);
            }
            System.out.println(allDone);
        }
    }


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
                    // System.out.println(tempdoc.select(".video-player__playlist-header-index").text().split("of ")[1]);
                      String link ="https://www.imdb.com"+ films.get(count).getDocument().select("#titleVideoStrip .video-modal:nth-of-type(1)").attr("href");
                    if(!link.equals("https://www.imdb.com"))
                    {
                        String s = Jsoup.connect(link).get().select("script").get(6) + "";
                        s=s.split("videoMetadata")[1];
                        s=s.split("</script>")[0];
                        Trailers t = new Trailers(films.get(count).getTitle());
                        t.LoadTrailer(s);
                        t.setName(films.get(count).getTitle());
                        films.get(count).setTrailers(t);
                    }
                    System.out.println(count);



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
            ArrayList<Future<?>> futures = new ArrayList<Future<?>>();
            ExecutorService executor = Executors.newFixedThreadPool(10);
            for (int i = 0; i < this.size(); i++) {

                executor.execute(new Worker(i , this));
                Runnable worker = new Worker(i,this);
                Future<?> f = executor.submit(worker);
                futures.add(f);
            }
            executor.shutdown();
            //new Thread(new checkWorker(futures)).start();
//
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
            File trailersFolder = new File(f.getPath()+"/Trailers");
            f.mkdirs();
            imageFolder.mkdirs();
            trailersFolder.mkdirs();
            File imageFile;
            BufferedImage bImage;
            File result = new File(f.getPath() + "/result.txt");
            FileWriter fr = new FileWriter(result, true);
            BufferedWriter br = new BufferedWriter(fr);
            PrintWriter pr = new PrintWriter(br);


            Gson gson = new Gson();
            int i = 1 , j = 1;
            ArrayList<Trailers> allTrailers = new ArrayList<>();
            for (Film film: this) {
                imageFile = new File(imageFolder.getPath()+ "/" + i++ + ".jpg");
                bImage = SwingFXUtils.fromFXImage(film.getImage() , null);
                ImageIO.write(bImage , "jpg" , imageFile);
                pr.println(film.getTitle() +"$"+ film.getURL() +"$");
               try {

                   if(!film.getTrailers().isEmpty())
                       allTrailers.add(film.getTrailers());

               }catch (Exception e)
               {
                   e.printStackTrace();
                   continue;
               }
            }
            pr.flush();
            pr.close();
            br.close();
            fr.close();
            System.out.println(gson.toJson(allTrailers));
            File trailer = new File(trailersFolder.getPath() + "/" + "trailers.json");
            fr = new FileWriter(trailer, true);
            br = new BufferedWriter(fr);
            pr = new PrintWriter(br);
            pr.print(gson.toJson(allTrailers));
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
