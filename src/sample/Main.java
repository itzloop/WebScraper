package sample;

import javafx.application.Application;
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
import javafx.scene.control.Label;
import javafx.scene.control.Hyperlink;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Scanner;

public class Main extends Application {
    @Override
   public void start(Stage primaryStage)throws Exception
   {

       Button btnSearch = new Button("Search");
       btnSearch.setDisable(true);
       Button btnSave = new Button("Save");
       Label lblProgress = new Label("Done.");
       TextField txtSearchWord = new TextField() ;
       GridPane gridPane = new GridPane();
       ScrollPane scrollPane = new ScrollPane(gridPane);
       BorderPane root = new BorderPane(scrollPane);
       HBox toolBar = new HBox();
       toolBar.getChildren().addAll(txtSearchWord,btnSearch, btnSave , lblProgress);
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
                gridPane.getChildren().clear();
                String searchWord = txtSearchWord.getText();
                try {
                    Document doc = Jsoup.connect("https://www.imdb.com/find?q=" + searchWord +"&s=tt").get();
                    Elements titles = doc.select(".findList tr");
                    Hyperlink link;
                    ImageView image;
                    int i = 0;
                    lblProgress.setText("Searching...");
                    for ( Element title : titles) {

                        link = new Hyperlink(title.select("td:nth-of-type(2) a").text() + title.text());
                        image = new ImageView(new Image(title.select("td:nth-of-type(1) img").attr("src")));
                        //System.out.println(title.select("td:nth-of-type(1) img").attr("src"));
                        link.setOnAction(actionEvent1 -> getHostServices().showDocument("https://www.imdb.com/"+title.select("td:nth-of-type(2) a").attr("href")));
                        gridPane.addRow(i++,image, link);

                    }

                    lblProgress.setText("Done. Found " + titles.size() + " results");


                }catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

       btnSave.setOnAction(actionEvent -> getHostServices().showDocument("https://www.imdb.com/"));

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
