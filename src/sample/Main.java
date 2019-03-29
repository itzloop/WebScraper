package sample;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static Stage stage;

    @Override
   public void start(Stage primaryStage)throws Exception
   {
       Views v = new Views("IMDB") {
           @Override
           public void start(Stage stage) throws Exception {

           }
       };
       v.manageViews(primaryStage);
       v.handelEvents();
       primaryStage.show();
   }


    public static void main(String[] args) {

       launch(args);
    }
}


