package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.io.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Main extends Application {
    public static String searchWord = "";
    public static Integer imageCount = 0;
    ArrayList<String[]> links;
    static ArrayList<Image> images;
    @Override
   public void start(Stage primaryStage)throws Exception
   {
       //setting up the layouts

       //left side of the page
       GridPane foundGridPane = new GridPane();
       ScrollPane foundScrollPane = new ScrollPane(foundGridPane);
       foundScrollPane.setPrefWidth(1300);

       //right side of the page
       VBox savedResults = new VBox();
       ScrollPane savedScrolledPane =  new ScrollPane(savedResults);
       savedScrolledPane.setPrefWidth(300);
       //main Layout
       HBox mainBox =new HBox(foundScrollPane , savedScrolledPane);
       BorderPane root = new BorderPane(mainBox);

       //setting alerts
       Alert alert = new Alert(Alert.AlertType.WARNING);
       alert.setTitle("Warning");
       alert.setHeaderText(null);
       //setting up the controls.
       ProgressBar pb = new ProgressBar();
       pb.setPrefSize(500 , 30);
       Button btnSearch = new Button("Search");
       Button btnSave = new Button("Save");
       TextField txtSearchWord = new TextField() ;
       btnSearch.setDisable(true);
       btnSave.setDisable(true);
       //adding the toolbar.
       HBox toolBar = new HBox();
       toolBar.getChildren().addAll(txtSearchWord,btnSearch, btnSave , pb);
       root.setLayoutX(0);
       root.setLayoutY(0);
       root.setBottom(toolBar);


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
                if(searchWord.equals(txtSearchWord.getText().trim()))
                {
                    alert.setAlertType(Alert.AlertType.WARNING);
                    alert.setContentText("You haven't changed your search word");
                    alert.showAndWait();
                    return;
                }
                searchWord = txtSearchWord.getText();
                if(exists()){
                    alert.setAlertType(Alert.AlertType.WARNING);
                    alert.setContentText("Search result exists");
                    alert.showAndWait();
                    System.out.println("Hello");
                    return;
                }
                txtSearchWord.setDisable(true);
                btnSave.setDisable(true);
                btnSearch.setDisable(true);
                foundGridPane.getChildren().clear();
                imageCount = 0;
                try {
                    Document doc = Jsoup.connect("https://www.imdb.com/find?q=" + searchWord +"&s=tt").get();
                    Elements titles = doc.select(".findList tr");
                    Hyperlink link;
                    ImageView imageView;
                    String[] eachLink;
                    links = new ArrayList<>();
                    int i = 0;
                    for ( Element title : titles) {
                        eachLink = new String[2];
                        link = new Hyperlink(title.select("td:nth-of-type(2) a").text() + title.text());
                        eachLink[0] = link.getText();
                        final Image image = new Image(title.select("td:nth-of-type(1) img").attr("src") , true);

                        image.progressProperty().addListener(new ChangeListener<Number>() {
                            @Override
                            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number progress) {
                                if ((Double) progress == 1.0 && ! image.isError()) {
                                    imageCount++;
                                    pb.setProgress(imageCount/200.d);
                                    //System.out.println(imageCount);
                                    if(imageCount == titles.size())
                                    {
                                        btnSave.setDisable(false);
                                        btnSearch.setDisable(false);
                                        txtSearchWord.setDisable(false);
                                    }
                                }
                            }
                        });
                        imageView = new ImageView(image);
                        eachLink[1] = "https://www.imdb.com/"+ title.select("td:nth-of-type(2) a").attr("href");
                        links.add(eachLink);
                        link.setOnAction(actionEvent1 -> getHostServices().showDocument("https://www.imdb.com/"+ title.select("td:nth-of-type(2) a").attr("href")));
                        foundGridPane.addRow(i++,imageView, link);

                    }


                }catch (Exception e) {
                    System.out.println(e);
                }
            }
        });

       
        //TODO Fix This Shit Tomorrow :)
       btnSave.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent actionEvent) {

               if(!exists())
               {
                   if(save()){
                       Hyperlink saveLink = new Hyperlink(searchWord);
                       saveLink.setOnAction(actionEvent1 -> System.out.println("Hello"));
                       savedResults.getChildren().add(saveLink);
                       btnSave.setDisable(true);
                       alert.setAlertType(Alert.AlertType.INFORMATION);
                       alert.setContentText("Saved Successfully.");
                       alert.showAndWait();
                   }
                   else
                   {
                       alert.setAlertType(Alert.AlertType.WARNING);
                       alert.setContentText("Didn't Saved it.");
                       alert.showAndWait();
                   }


               }
               else{
                   alert.setAlertType(Alert.AlertType.WARNING);
                   alert.setContentText("Search result exists.");
                   alert.showAndWait();
               }

           }
       });

       Scene scene = new Scene(root, 1600 , 900);
       primaryStage.setScene(scene);
       primaryStage.setTitle("IMDB");
       primaryStage.show();
   }

     boolean save()
    {
//        try {
//                FileWriter fr = new FileWriter("log.txt", true);
//                BufferedWriter br = new BufferedWriter(fr);
//                PrintWriter pr = new PrintWriter(br);
//                pr.println(searchWord);
//                pr.close();
//                br.close();
//                fr.close();
//                return true;
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
        try{
            File f = new File("Resources/"+searchWord);
            f.mkdirs();
            File result = new File(f.getPath() + "/result.txt");
            FileWriter fr = new FileWriter(result, true);
            BufferedWriter br = new BufferedWriter(fr);
            PrintWriter pr = new PrintWriter(br);
            for (String[] link: links) {
                pr.println(link[0]+"|"+link[1]);
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
    boolean exists(){
        try {
            FileReader f = new FileReader("log.txt");
            int i;
            ArrayList<String> strings = new ArrayList<>();
            String s = "";
            while((i = f.read()) != -1)
            {
                if((char)i == '\n'){
                    strings.add(s);
                    s = "";
                }
                s+= (char)i;


            }
            for (String str : strings) {
                System.out.println(str.length() + " | " + searchWord.length());
                if(str.trim().equals(searchWord))
                {
                    return true;
                }


            }

        }catch (Exception e){
            System.out.println(e);
            return false;
        }
        return false;
    }

    public static void main(String[] args) {

        launch(args);
//        try{
//            File f = new File("Resources/newFOlder/result.txt");
//            System.out.println(f.getPath());
//            if(f.mkdirs())
//                System.out.println("done");
//            else
//                System.out.println("failed");
//
//        }catch (Exception e ){
//            System.out.println(e);
//        }
    }
}


