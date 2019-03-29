<------------ Description ------------>
This app gets all the titles from IMDB's search page.
There are 3 classes beside Main Class called Views, Film and Films.
Views is everything related to layouts and controls such as buttons and text fields.
Film is each item in IMBD's search page which has a title a link to the page and an image.
Films is an ArrayList of Film which contains all the items in IMBD's Page and has the 
functionality to load and save the data user has searched for.

<------------ IDE ------------>
IntelliJ IDEA 2018.3.5 (Ultimate Edition)
Build #IU-183.5912.21, built on February 26, 2019
Licensed to Sina Shabani
Subscription is active until March 12, 2020
For educational use only.
JRE: 1.8.0_152-release-1343-b28 amd64
JVM: OpenJDK 64-Bit Server VM by JetBrains s.r.o
Linux 4.18.0-16-generic

<------------ Libaries ------------>
jsoup-1.11.3
javafx-sdk-11.0.2

<------------ Command ------------>
*For the following command to work provide the path inside {} 
java --add-modules javafx.base,javafx.graphics --add-reads javafx.base=ALL-UNNAMED --add-reads javafx.graphics=ALL-UNNAMED -javaagent:{YOUR_idea-IU_DIR}/idea-IU-183.5912.21/lib/idea_rt.jar=38269:{YOUR_DIR}/idea-IU-183.5912.21/bin -Dfile.encoding=UTF-8 -p javafx-sdk-11.0.2/lib/javafx.base.jar:{YOUR_JAVAFX_DIR}/javafx-sdk-11.0.2/lib/javafx.graphics.jar:IDEA_PROJECTS_DIR/IdeaProjects/WebScraper/out/production/WebScraper:{YOUR_JAVAFX_DIR}/javafx-sdk-11.0.2/lib/javafx.controls.jar:{YOUR_JAVAFX_DIR}/javafx-sdk-11.0.2/lib/javafx.fxml.jar:{YOUR_JAVAFX_DIR}/javafx-sdk-11.0.2/lib/javafx.media.jar:{YOUR_JAVAFX_DIR}/javafx-sdk-11.0.2/lib/javafx.swing.jar:{YOUR_JAVAFX_DIR}/javafx-sdk-11.0.2/lib/javafx.web.jar:{YOUR_JAVAFX_DIR}/javafx-sdk-11.0.2/lib/javafx-swt.jar:{YOUR_JSoup_DIR}/jsoup-1.11.3.jar -m WebScraper/sample.Main
