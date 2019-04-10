package sample;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
   public void start(Stage primaryStage)throws Exception
   {
       Views v = new Views("IMDB");
       v.manageViews(primaryStage);
       v.handelEvents();

   }

    public static void main(String[] args) {
       launch(args);
    }
}


