package sample;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public abstract class Views extends Application{

    private Films films;
    private String title;
    private double WINDOW_WIDTH;
    private double WINDOW_HEIGHT;
    private static int imageCount;
    private ProgressBar searchProgressBar;
    private Button btnSearch;
    private Button btnSave;
    private TextField txtSearchWord ;
    private GridPane leftPanel; //this is for showing the data related to the search word.
    private ScrollPane leftPanelScrollPane;
    private VBox rightPanel; //this is for showing the search history.
    private ScrollPane rightPanelScrollPane;
    private HBox toolBar;
    private HBox dataPanel;
    private BorderPane root;
    private Stage primaryStage;

    Views(String title)
    {
        this(1600 , 900 , title);
    }

    Views(double WINDOW_WIDTH, double WINDOW_HEIGHT , String title){
        searchProgressBar = new ProgressBar();
        btnSearch = new Button("Search");
        btnSave = new Button("Save");
        txtSearchWord = new TextField();
        leftPanel = new GridPane(); //this is for showing the data related to the search word.
        leftPanelScrollPane = new ScrollPane(leftPanel);
        rightPanel = new VBox(); //this is for showing the search history.
        rightPanelScrollPane = new ScrollPane(rightPanel);
        toolBar = new HBox(txtSearchWord , btnSearch , btnSave , searchProgressBar);
        dataPanel = new HBox(leftPanelScrollPane , rightPanelScrollPane);
        root = new BorderPane(dataPanel);
        root.setLayoutX(0);
        root.setLayoutY(0);
        root.setTop(toolBar);
        this.title = title;
        this.WINDOW_WIDTH = WINDOW_WIDTH;
        this.WINDOW_HEIGHT = WINDOW_HEIGHT;
    }

    void handelEvents()
    {
        loadSavedResults();
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

//
//                if(searchWord.trim().equals(txtSearchWord.getText().trim().toLowerCase()))
//                {
//                    alert(Alert.AlertType.WARNING , "You haven't changed your search word" , "Warning");
//                    return;
//                }

                films = new Films(txtSearchWord.getText());
                if(exists(films.getSearchWord())){
                    alert(Alert.AlertType.WARNING , "Search result exists" , "Warning");
                    return;
                }
                controlsDisabled(true);
                leftPanel.getChildren().clear();
                films.loadDataFromIMDB();
                Hyperlink title;
                ImageView imageView;
                int row = 0 ;
                imageCount = 0;
                for (Film film : films) {
                    film.getImage().progressProperty().addListener(new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number progress) {
                            if ((Double) progress == 1.0 && ! film.getImage().isError()) {
                                imageCount++;
                                searchProgressBar.setProgress(imageCount/(double)films.size());
                                if(imageCount == films.size())
                                {
                                    controlsDisabled(false);
                                    loadSavedResults();
                                }
                            }
                        }
                    });
                    title = new Hyperlink(film.getTitle());
                    title.setOnAction(actionEvent1 -> getHostServices().showDocument(film.getURL()));
                    imageView = new ImageView(film.getImage());
                    leftPanel.addRow(row++ , imageView , title);
                }
            }
        });




        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                if(!exists(films.getSearchWord()))
                {
                    if(save(films)){
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



        primaryStage.widthProperty().addListener( (observableValue, number, t1) -> {
            WINDOW_WIDTH = (double)t1;
            txtSearchWord.setPrefSize(WINDOW_WIDTH/4 , toolBar.getHeight());
            btnSearch.setPrefSize(WINDOW_WIDTH/12 , toolBar.getHeight());
            btnSave.setPrefSize(WINDOW_WIDTH/12 , toolBar.getHeight());
            searchProgressBar.setPrefSize(WINDOW_WIDTH - (WINDOW_WIDTH/6 + WINDOW_WIDTH/4),toolBar.getHeight());
            leftPanelScrollPane.setPrefWidth((3*WINDOW_WIDTH)/4);
            rightPanelScrollPane.setPrefWidth(WINDOW_WIDTH/4);

        });


    }



    private boolean save(Films films )
    {
        try{
            //creating the Folder structure for the current searchWord that we want to save
            File f = new File("Resources/"+films.getSearchWord());
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
            for (Film film: films) {
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




    /**
     * show to the searched word to the right side panel
     * */
    private void loadSavedResults()
    {
        rightPanel.getChildren().clear();
        for(String str : getWords())
        {
            Hyperlink h = new Hyperlink(str);
            h.setOnAction(actionEvent -> loadData(str));
            rightPanel.getChildren().add(h);
        }
    }


    /**
     * load the actual data from Resources folder*/
    private void loadData(String searchKey)
    {

        leftPanel.getChildren().clear();
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
                    leftPanel.addRow((counter)++ ,imageView, link);
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
            txtSearchWord.setText(searchKey);


        }catch (Exception e) {
            System.out.println(e);
        }

    }



    /**
     * this will return all the words the user searched for
     *
     * */
    private String[] getWords()
    {
        File f = new File("Resources/");
        return f.list();
    }



    /**
     * this method will check if we have searched for a word before or not
     *
     * */


    private boolean exists(String searchWord) {

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

    /**
     *disables or enables the controls
     */

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


    void manageViews(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        leftPanelScrollPane.setPrefWidth((3*WINDOW_WIDTH)/4);
        rightPanelScrollPane.setPrefWidth(WINDOW_WIDTH/4);
        btnSearch.setDisable(true);
        btnSave.setDisable(true);
        Scene scene = new Scene(root , WINDOW_WIDTH , WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
    }

}
