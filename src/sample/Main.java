package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class Main extends Application {
    private static String searchWord = "";
    private static Integer imageCount = 0;
    ArrayList<String> links;
    private static GridPane foundGridPane;
    private static ScrollPane foundScrollPane ;
    private static VBox savedResults ;
    private static ScrollPane savedScrolledPane ;
    private static ProgressBar pb;
    private static Button btnSearch;
    private static Button btnSave ;
    private static TextField txtSearchWord ;

    @Override
   public void start(Stage primaryStage)throws Exception
   {
       //setting up the layouts

       //left side of the page
       foundGridPane = new GridPane();
       foundScrollPane = new ScrollPane(foundGridPane);
       foundScrollPane.setPrefWidth(1300);

       //right side of the page
       savedResults = new VBox();
       savedScrolledPane =  new ScrollPane(savedResults);
       savedScrolledPane.setPrefWidth(300);
       //main Layout
       HBox mainBox =new HBox(foundScrollPane , savedScrolledPane);
       BorderPane root = new BorderPane(mainBox);

       //setting alerts
//       Alert alert = new Alert(Alert.AlertType.WARNING);
//       alert.setTitle("Warning");
//       alert.setHeaderText(null);
       //setting up the controls.
       pb = new ProgressBar();
       pb.setPrefSize(500 , 30);
       btnSearch = new Button("Search");
       btnSave = new Button("Save");
       txtSearchWord = new TextField() ;
       btnSearch.setDisable(true);
       btnSave.setDisable(true);
       //adding the toolbar.
       HBox toolBar = new HBox();
       toolBar.getChildren().addAll(txtSearchWord,btnSearch, btnSave , pb);
       root.setLayoutX(0);
       root.setLayoutY(0);
       root.setBottom(toolBar);

       //Load the current searched words
       loadSavedResults();

       //setting up events
       txtSearchWord.setOnKeyTyped(new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent keyEvent) {
               if(txtSearchWord.getText().trim().isEmpty())
                   btnSearch.setDisable(true);
               else
                   btnSearch.setDisable(false);

           }
       });


       btnSearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(searchWord.trim().equals(txtSearchWord.getText().trim().toLowerCase()))
                {
                    alert(Alert.AlertType.WARNING , "You haven't changed your search word" , "Warning");
                    return;
                }
                searchWord = txtSearchWord.getText().toLowerCase();
                if(exists()){
                    alert(Alert.AlertType.WARNING , "Search result exists" , "Warning");
                    return;
                }
                controlsDisabled(true);
                foundGridPane.getChildren().clear();
                imageCount = 0;
                try {
                    Document doc = Jsoup.connect("https://www.imdb.com/find?q=" + searchWord +"&s=tt").get();
                    Elements titles = doc.select(".findList tr");
                    getDataFromIMDB(titles);


                }catch (Exception e) {
                    System.out.println(e);
                }
            }
        });


       btnSave.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent actionEvent) {

               if(!exists())
               {
                   if(save(foundGridPane.getChildren())){
                       loadSavedResults();
                       btnSave.setDisable(true);
                       alert(Alert.AlertType.INFORMATION , "Saved Successfully." , "Information");
                   }
                   else
                       alert(Alert.AlertType.WARNING , "Didn't Saved it." , "Warning");


               }
               else
                   alert(Alert.AlertType.WARNING , "Search result exists." , "Warning");

           }
       });

       Scene scene = new Scene(root, 1600 , 900);
       primaryStage.setScene(scene);
       primaryStage.setTitle("IMDB");
       primaryStage.show();
   }


   void getDataFromIMDB(Elements titles)
   {
       savedResults.getChildren().clear();
       Hyperlink link;
       ImageView imageView;
       links = new ArrayList<>();
       int i = 0;
       for ( Element title : titles) {
           link = new Hyperlink(title.select("td:nth-of-type(2) a").text() +" " +title.text());
           final Image image = new Image(title.select("td:nth-of-type(1) img").attr("src") , true);
           image.progressProperty().addListener(new ChangeListener<Number>() {
               @Override
               public void changed(ObservableValue<? extends Number> observableValue, Number number, Number progress) {
                   if ((Double) progress == 1.0 && ! image.isError()) {
                       imageCount++;
                       pb.setProgress(imageCount/200.d);
                       if(imageCount == titles.size())
                       {
                           controlsDisabled(false);
                           loadSavedResults();
                       }
                   }
               }
           });
           imageView = new ImageView(image);
           links.add("https://www.imdb.com/"+ title.select("td:nth-of-type(2) a").attr("href"));
           link.setOnAction(actionEvent1 -> getHostServices().showDocument("https://www.imdb.com/"+ title.select("td:nth-of-type(2) a").attr("href")));
           foundGridPane.addRow(i++,imageView, link);

       }

   }



   private void controlsDisabled(boolean isDisabled)
   {
       txtSearchWord.setDisable(isDisabled);
       btnSave.setDisable(isDisabled);
       btnSearch.setDisable(isDisabled);
   }


    private void alert(Alert.AlertType type , String contentText , String title)
    {
       Alert alert = new Alert(type);
       alert.setTitle(title);
       alert.setHeaderText(null);
       alert.setContentText(contentText);
       alert.showAndWait();
    }


    private void loadSavedResults()
    {
        savedResults.getChildren().clear();
        for(String str : load())
        {
            Hyperlink h = new Hyperlink(str);
            h.setOnAction(actionEvent -> loadData(str));
            savedResults.getChildren().add(h);
        }
    }



    private boolean save(ObservableList<Node> nodes )
    {


        try{
            File f = new File("Resources/"+searchWord);
            File imageFolder= new File(f.getPath() + "/Images");
            f.mkdirs();
            imageFolder.mkdirs();
            File imageFile;
            BufferedImage bImage;
            File result = new File(f.getPath() + "/result.txt");
            FileWriter fr = new FileWriter(result, true);
            BufferedWriter br = new BufferedWriter(fr);
            PrintWriter pr = new PrintWriter(br);
            int i = 1 , j = 0;
            for (Node node : nodes) {
                if(node instanceof ImageView)
                {
                    imageFile = new File(imageFolder.getPath()+ "/" + i++ + ".jpg");
                    bImage = SwingFXUtils.fromFXImage(((ImageView) node).getImage() , null);
                    ImageIO.write(bImage , "jpg" , imageFile);
                }else if (node instanceof Hyperlink){
                    pr.println(((Hyperlink)node).getText() +"$"+ links.get(j++) +"$");
                }

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



    private boolean exists() {
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

    private String[] load()
    {
        File f = new File("Resources/");
        return f.list();
    }

    private void loadData(String searchKey)
    {

        foundGridPane.getChildren().clear();
        try {
            String[] eachLink = new String[2];
            ImageView imageView;
            Hyperlink link ;
            eachLink[0] = "";
            eachLink[1] = "";
            File f = new File("Resources/"+searchKey+"/result.txt");
            FileReader fr = new FileReader(f);
            BufferedReader fbr = new BufferedReader(fr);
            int i , j = 0 , counter = 0 , imageCounter = 1;
            while(( i = fbr.read()) != -1)
            {

                if(j > 1)
                {
                    final Image image = new Image("file:Resources/"+ searchKey+"/Images/" + imageCounter+".jpg"   );
                    imageCounter++;
                    imageView = new ImageView(image);
                    final String URL= eachLink[1];
                    link = new Hyperlink(eachLink[0]);
                    link.setOnAction(actionEvent -> getHostServices().showDocument(URL));
                    foundGridPane.addRow((counter)++ ,imageView, link);
                    eachLink = new String[2];
                    eachLink[0] = "";
                    eachLink[1] = "";
                    j = 0;
                }
                if((char)i == '$')
                    j++;
                else
                    eachLink[j] += (char)i;

            }

            fbr.close();
            fr.close();


        }catch (Exception e) {
            System.out.println(e);
        }

    }





    public static void main(String[] args) {

        launch(args);



    }
}


