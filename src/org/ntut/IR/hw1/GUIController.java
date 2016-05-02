package org.ntut.IR.hw1;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.InputEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by vodalok on 2016/5/1.
 */
public class GUIController implements Initializable {
    //Index
    @FXML
    private Button browseWARCButton;
    @FXML
    private TextField warcPathTextField;
    @FXML
    private TextField dictionaryFileNameTextField;
    @FXML
    private TextField postingListFileNameTextField;
    @FXML
    private TextField outputPathTextField;
    @FXML
    private Button browseOutputPathButton;
    @FXML
    private Button startIndexButton;
    //Query
    @FXML
    private TextField indexPathTextField;
    @FXML
    private Button broweIndexPathButton;
    @FXML
    private TextField queryTextField;
    @FXML
    private Button queryButton;
    @FXML
    private TableView queryResultTableView;
    //MenuBar
    @FXML
    private MenuItem exitMenuItem;
    //Stage
    private Stage controlStage;

    //Control Properties
    private boolean isStartIndexButtonDisabled = true;
    private boolean isQueryButtonDisabled = true;
    private String dictionaryFileName = "Dictionary";
    private String postingListFileName = "PostingList";
    private String warcFilePath = "";
    private String outputPath = "";

    public void setInitialDirectory(String initialDirectory) {
        StringBuilder builder = new StringBuilder(initialDirectory);
        //Check is directory or file
        if(!initialDirectory.endsWith("/")){
            int index = initialDirectory.lastIndexOf("/");
            builder.setLength(index+1);
        }
        this.initialDirectory = builder.toString();
    }

    private String initialDirectory = ".";

    public void setStartIndexButtonDisabled(boolean startIndexButtonDisabled) {
        this.isStartIndexButtonDisabled = startIndexButtonDisabled;
        this.startIndexButton.setDisable(isStartIndexButtonDisabled);
        //this.checkStartIndexPrepared();
    }

    public void setQueryButtonDisabled(boolean queryButtonDisabled) {
        isQueryButtonDisabled = queryButtonDisabled;
        this.queryButton.setDisable(isQueryButtonDisabled);
        this.checkStartIndexPrepared();
    }

    public void setDictionaryFileName(String dictionaryFileName) {
        this.dictionaryFileName = dictionaryFileName;
        this.dictionaryFileNameTextField.setText(this.dictionaryFileName);
        this.checkStartIndexPrepared();
    }

    public void setPostingListFileName(String postingListFileName) {
        this.postingListFileName = postingListFileName;
        this.postingListFileNameTextField.setText(this.postingListFileName);
        this.checkStartIndexPrepared();
    }

    public void setWarcFilePath(String warcFilePath){
        this.warcFilePath = warcFilePath;
        this.warcPathTextField.setText(warcFilePath);
        this.checkStartIndexPrepared();
    }

    public void setOutputPath(String outputPath){
        this.outputPath = outputPath;
        this.outputPathTextField.setText(outputPath);
        this.checkStartIndexPrepared();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startIndexButton.setDisable(isStartIndexButtonDisabled);
        queryButton.setDisable(isQueryButtonDisabled);
        dictionaryFileNameTextField.setText(dictionaryFileName);
        postingListFileNameTextField.setText(postingListFileName);
        warcPathTextField.setText(warcFilePath);
        outputPathTextField.setText(this.outputPath);
        dictionaryFileNameTextField.textProperty().addListener(this.dictionaryTextFieldChangedListener);
        postingListFileNameTextField.textProperty().addListener(this.postingListTextFieldChangedListener);
        this.checkStartIndexPrepared();
    }

    public GUIController(){
    }

    public void setStage(Stage stage){
        this.controlStage = stage;
    }

    @FXML
    private void handleBrowseWARCFile(ActionEvent actionEvent){
        final String INITIAL_DIRECTORY = ".";
        final String EXTENSION_DESC = "WARC File";
        final String EXTENSION = "*.warc";
        FileChooser warcFileChooser = new FileChooser();
        warcFileChooser.setInitialDirectory(new File(INITIAL_DIRECTORY));
        warcFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(EXTENSION_DESC, EXTENSION));
        File selectedFile = warcFileChooser.showOpenDialog(controlStage);
        if(checkFile(selectedFile)){
            this.setWarcFilePath(selectedFile.getAbsolutePath());
        }
    }

    private boolean checkFile(File selectedFile) {
        if(selectedFile!=null) {
            try {
                this.setInitialDirectory(selectedFile.getCanonicalPath());
            }catch (IOException exception){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("An Exception Occured!");
                alert.setContentText(exception.getStackTrace().toString());
            }
            return true;
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No file was opened");
            alert.setHeaderText("No file was opened.");
            alert.setContentText("Please choose another file.");
        }
        return false;
    }

    @FXML
    private void handleBrowseOutputPathAction(ActionEvent actionEvent){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File(this.initialDirectory));
        chooser.setTitle("Select Output Path");
        File outputPath = chooser.showDialog(controlStage);
        if(checkFile(outputPath)){
            this.setOutputPath(outputPath.getAbsolutePath());
        }
    }

    @FXML
    private void handleExitMenuItemAction(ActionEvent actionEvent){
        Platform.exit();
    }

    private ChangeListener dictionaryTextFieldChangedListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            setDictionaryFileName(dictionaryFileNameTextField.getText());
        }
    };

    private ChangeListener postingListTextFieldChangedListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            setPostingListFileName(postingListFileNameTextField.getText());
        }
    };

    private void checkStartIndexPrepared(){
         this.setStartIndexButtonDisabled(!(!this.warcFilePath.isEmpty() &
                !this.dictionaryFileName.isEmpty() &
                !this.postingListFileName.isEmpty() &
                !this.outputPath.isEmpty()));
    }
}
