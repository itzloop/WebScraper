package sample;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;
import javafx.scene.control.Hyperlink;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Main extends Application {
    public static String searchWord;
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

       //setting up the controls.
       Button btnSearch = new Button("Search");
       Button btnSave = new Button("Save");
       TextField txtSearchWord = new TextField() ;
       btnSearch.setDisable(true);
       btnSave.setDisable(true);
       //adding the toolbar.
       HBox toolBar = new HBox();
       toolBar.getChildren().addAll(txtSearchWord,btnSearch, btnSave);
       root.setLayoutX(0);
       root.setLayoutY(0);
       root.setBottom(toolBar);



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
                btnSave.setDisable(true);
                foundGridPane.getChildren().clear();
                searchWord = txtSearchWord.getText();
                try {
                    Document doc = Jsoup.connect("https://www.imdb.com/find?q=" + searchWord +"&s=tt").get();
                    Elements titles = doc.select(".findList tr");
                    Hyperlink link;
                    ImageView image;
                    int i = 0;
                    for ( Element title : titles) {

                        link = new Hyperlink(title.select("td:nth-of-type(2) a").text() + title.text());
                        image = new ImageView(new Image(title.select("td:nth-of-type(1) img").attr("src") , true));
                        link.setOnAction(actionEvent1 -> getHostServices().showDocument("https://www.imdb.com/"+ title.select("td:nth-of-type(2) a").attr("href")));
                        foundGridPane.addRow(i++,image, link);

                    }
                    btnSave.setDisable(false);


                }catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });


        //TODO Fix This Shit Tomorrow :)
       btnSave.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent actionEvent) {

               for (Node node : savedResults.getChildren() ) {
                   if(node instanceof Hyperlink && ((Hyperlink) node).getText() == searchWord);
                        break;
               }
               try {
                   FileWriter myWriter= new FileWriter("output.txt");
                   myWriter.write(searchWord);
                   myWriter.close();
               }catch (Exception e)
               {
                   System.out.println("Sth Hpnd");
               }
               Hyperlink saveLink = new Hyperlink(searchWord);
               saveLink.setOnAction(actionEvent1 -> System.out.println("Hello"));
               savedResults.getChildren().add(saveLink);
               btnSave.setDisable(true);

           }
       });

       Scene scene = new Scene(root, 1600 , 900);
       primaryStage.setScene(scene);
       primaryStage.setTitle("First javaFX Application");
       primaryStage.show();
   }

    private static Scanner scanner;

    public static void main(String[] args) {
        launch(args);
        scanner = new Scanner(System.in);

    }
}
