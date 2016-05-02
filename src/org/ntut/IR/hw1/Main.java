package org.ntut.IR.hw1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.lucene.index.IndexReader;

import java.util.Scanner;

/**
 * Created by Vodalok on 2016/3/25.
 */

public class Main extends Application{
    private static final String DEFAULT_INDEX_PATH = "./index";
    private static final String DEFAULT_OUTPUT_FILE_PATH = ".";
    private static final String FIELD_NAME = "content";
    private final String FXML_NAME = "GUI.fxml";
    private final String TITLE = "IR";

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_NAME));
        Parent root = loader.load();
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
        GUIController controller = (GUIController)loader.getController();
        controller.setStage(primaryStage);
    }

    public static void main(String args[]) throws Exception{
        launch(args);

    }
}
