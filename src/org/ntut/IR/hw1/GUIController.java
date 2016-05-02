package org.ntut.IR.hw1;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.*;
import org.apache.commons.io.FilenameUtils;

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
    @FXML
    private CheckBox outputDictionaryAndPostingListCheckBox;
    //Query
    @FXML
    private TextField indexPathTextField;
    @FXML
    private Button browseIndexPathButton;
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
    private boolean isOutputDictionaryAndPostingList = true;
    private boolean isDictionaryTextFieldDisabled = false;
    private boolean isPostingListTextFieldDisabled = false;
    private final String INDEX_PROCESS_GUI_FXML_NAME = "IndexProcessing.fxml";
    private String queryIndexPath = "";
    private String queryString = "";

    public void setQueryIndexPath(String queryIndexPath) {
        this.queryIndexPath = queryIndexPath;
        this.indexPathTextField.setText(this.queryIndexPath);
        checkQueryPrepared();
    }

    public String getDictionaryFileName() {
        return dictionaryFileName;
    }

    public String getPostingListFileName() {
        return postingListFileName;
    }

    public String getWarcFilePath() {
        return warcFilePath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public boolean isOutputDictionaryAndPostingList() {
        return isOutputDictionaryAndPostingList;
    }

    public void setPostingListTextFieldDisabled(boolean postingListTextFieldDisabled) {
        isPostingListTextFieldDisabled = postingListTextFieldDisabled;
        this.postingListFileNameTextField.setDisable(this.isPostingListTextFieldDisabled);
        this.checkStartIndexPrepared();
    }

    public void setDictionaryTextFieldDisabled(boolean dictionaryTextFieldDisabled) {
        isDictionaryTextFieldDisabled = dictionaryTextFieldDisabled;
        this.dictionaryFileNameTextField.setDisable(this.isDictionaryTextFieldDisabled);
        this.checkStartIndexPrepared();
    }

    public void setInitialDirectory(String initialDirectory) {
        //Check is directory or file
        this.initialDirectory = FilenameUtils.getFullPath(initialDirectory);
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

    public void setOutputDictionaryAndPostingListCheckBoxEnabled(boolean checkBoxEnabled){
        this.isOutputDictionaryAndPostingList = checkBoxEnabled;
        this.outputDictionaryAndPostingListCheckBox.setSelected(this.isOutputDictionaryAndPostingList);
        this.setPostingListTextFieldDisabled(!this.isOutputDictionaryAndPostingList);
        this.setDictionaryTextFieldDisabled(!this.isOutputDictionaryAndPostingList);
        this.checkStartIndexPrepared();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startIndexButton.setDisable(isStartIndexButtonDisabled);
        queryButton.setDisable(isQueryButtonDisabled);
        dictionaryFileNameTextField.setText(dictionaryFileName);
        dictionaryFileNameTextField.setDisable(isDictionaryTextFieldDisabled);
        postingListFileNameTextField.setText(postingListFileName);
        postingListFileNameTextField.setDisable(isPostingListTextFieldDisabled);
        warcPathTextField.setText(warcFilePath);
        outputPathTextField.setText(this.outputPath);
        outputDictionaryAndPostingListCheckBox.setSelected(this.isOutputDictionaryAndPostingList);
        dictionaryFileNameTextField.textProperty().addListener(this.dictionaryTextFieldChangedListener);
        postingListFileNameTextField.textProperty().addListener(this.postingListTextFieldChangedListener);
        queryTextField.textProperty().addListener(this.queryTextChangedListener);
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
        if(selectedFile != null) {
            try {
                this.setInitialDirectory(selectedFile.getCanonicalPath());
            }catch (IOException exception){
                showErrorAlert("Error", "An Exception Occured!", exception.getStackTrace().toString());
            }
            return true;
        }else{
            showErrorAlert("Error", "No file was opened.", "Please choose another file.");
        }
        return false;
    }

    private void showErrorAlert(String error2, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(error2);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
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

    @FXML
    private void handleOutputPostingAndDictionaryCheckboxAction(ActionEvent event){
        this.setOutputDictionaryAndPostingListCheckBoxEnabled(this.outputDictionaryAndPostingListCheckBox.isSelected());
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
        if(this.isOutputDictionaryAndPostingList)
            this.setStartIndexButtonDisabled(!(!this.warcFilePath.isEmpty() &
                    !this.dictionaryFileName.isEmpty() &
                    !this.postingListFileName.isEmpty() &
                    !this.outputPath.isEmpty()));
        else
            this.setStartIndexButtonDisabled(!(!this.warcFilePath.isEmpty()&
            !this.outputPath.isEmpty()));
    }

    @FXML
    private void handleStartIndexButtonAction(ActionEvent event){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(INDEX_PROCESS_GUI_FXML_NAME));
        try {
            Parent processingRoot = loader.load();
            Stage processingStage = new Stage();
            processingStage.initModality(Modality.APPLICATION_MODAL);
            processingStage.setAlwaysOnTop(true);
            processingStage.setTitle("Processing");
            processingStage.setScene(new Scene(processingRoot));
            processingStage.setResizable(false);
            processingStage.initStyle(StageStyle.UNDECORATED);

            processingStage.show();
            IndexProcessingController controller = (IndexProcessingController) loader.getController();
            controller.setSelfStage(processingStage);
            controller.initData(this.getWarcFilePath(), this.getDictionaryFileName(), this.getPostingListFileName(), this.getOutputPath(), this.isOutputDictionaryAndPostingList());
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    controller.start();
                    return null;
                }
            };
            new Thread(task).start();
        }catch (IOException exception){
            showErrorAlert("Error", "An Exception Occured!", exception.getStackTrace().toString());
        }
    }

    private void checkQueryPrepared(){
        this.setQueryButtonDisabled(!(!this.queryIndexPath.isEmpty()));
    }

    private ChangeListener queryTextChangedListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            queryString = queryTextField.getText();
        }
    };

    @FXML
    private void handleBrowseIndexPathAction(ActionEvent event){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File(this.initialDirectory));
        chooser.setTitle("Select Output Path");
        File outputPath = chooser.showDialog(controlStage);
        if(checkFile(outputPath))
            this.setQueryIndexPath(outputPath.getAbsolutePath());
    }

    @FXML
    private void handleQueryButtonAction(ActionEvent event){
        QueryTFIDF queryTFIDF = new QueryTFIDF(this.queryIndexPath);
        queryTFIDF.SetInput(this.queryString);
        try {
            queryResultTableView.getColumns().clear();
            ObservableList list = queryTFIDF.SearchFiles();
            TableColumn docIDCol = new TableColumn("Doc #");
            docIDCol.setMinWidth(100);
            docIDCol.setCellValueFactory(new PropertyValueFactory<DocumentScore, Integer>("docID"));
            TableColumn docScoreCol = new TableColumn("Score");
            docScoreCol.setMinWidth(100);
            docScoreCol.setCellValueFactory(new PropertyValueFactory<DocumentScore, Double>("score"));
            queryResultTableView.setItems(list);
            queryResultTableView.getColumns().addAll(docIDCol, docScoreCol);
        }catch (Exception exception){
            showErrorAlert("Error", "An Exception Occured!", exception.getStackTrace().toString());
        }
    }
}
