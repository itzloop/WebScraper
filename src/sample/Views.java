package sample;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;

public class Views extends Application{

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
        films = new Films("");
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

    @Override
    public void start(Stage stage) throws Exception {

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
                if(films.getSearchWord().trim().equals(txtSearchWord.getText().trim().toLowerCase()))
                {
                    alert(Alert.AlertType.WARNING , "You haven't changed your search word" , "Warning");
                    return;
                }

                films = new Films(txtSearchWord.getText().trim().toLowerCase());
                if(films.exists(txtSearchWord.getText().trim().toLowerCase())){
                    alert(Alert.AlertType.WARNING , "Search result exists" , "Warning");
                    return;
                }
                rightPanel.getChildren().clear();
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
                                    alert(Alert.AlertType.INFORMATION , "Found " + films.size() + " results","Information");
                                    searchProgressBar.setProgress(-1.d);
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

                if(!films.exists(txtSearchWord.getText().trim()))
                {
                    if(films.save()){
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

    /**
     * show to the searched word to the right side panel
     * */
    private void loadSavedResults()
    {
        rightPanel.getChildren().clear();
        for(String str : films.history())
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

        films = new Films(searchKey);
        leftPanel.getChildren().clear();
        try {
            ImageView imageView;
            Hyperlink title ;
            int row = 0;
            films.loadLocalData();
            for (Film film :films) {
                title = new Hyperlink(film.getTitle());
                title.setOnAction(actionEvent -> getHostServices().showDocument(film.getURL()));
                imageView = new ImageView(film.getImage());
                leftPanel.addRow(row++ , imageView , title);
            }
            txtSearchWord.setText(searchKey);
        }catch (Exception e) {
            System.out.println(e);
        }
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

    /**
     * Show Message Dialogs*/
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
        primaryStage.show();
    }

}
