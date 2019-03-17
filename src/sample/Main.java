package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Scanner;

public class Main extends Application {
    @Override
   public void start(Stage primaryStage)throws Exception
   {

       Button btnSearch = new Button("Search");
       btnSearch.setDisable(true);
       Button btnSave = new Button("Save");
       TextField txtSearchWord = new TextField() ;
       //b1.setTranslateX(1530);
//       b1.setTranslateY(350);
       VBox texts = new VBox();
       ScrollPane scrollPane = new ScrollPane(texts);
       BorderPane root = new BorderPane(scrollPane);
       HBox buttons = new HBox();
       buttons.getChildren().addAll(txtSearchWord,btnSearch, btnSave);
       root.setLayoutX(0);
       root.setLayoutY(0);
       root.setBottom(buttons);
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
                texts.getChildren().clear();
                String searchWord = txtSearchWord.getText();
                int x = -500 , y = -500;
                try {
                    Document doc = Jsoup.connect("https://www.imdb.com/find?q=" + searchWord +"&s=tt").get();
                    //System.out.println(doc.title());
                    Elements titles = doc.select(".findList tr td:nth-of-type(2) a");
                    Text t;
                    for ( Element title : titles) {
                        t = new Text(title.text());
                        texts.getChildren().addAll(t);
                    }

                }catch (Exception e) {
                    System.out.println(e.getMessage());
                }
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
