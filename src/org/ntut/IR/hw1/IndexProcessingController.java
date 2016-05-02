package org.ntut.IR.hw1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by vodalok on 2016/5/2.
 */
public class IndexProcessingController{
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label loadingLabel;
    @FXML
    private Label stageLabel;

    private String warcFileName;
    private String dictionaryFileName;
    private String postingFileName;
    private String outputPath;
    private boolean isOutputDictAndPostingList;
    private ProgressHelper helper = null;
    private final String FIELD_NAME = "content";
    private Stage selfStage;
    private final String TXT_SUFFIX = ".txt";
    private int stages = 0;
    private final String STAGE_DESC = "Stage:%d/%d";

    public IndexProcessingController(){
    }

    public void setSelfStage(Stage stage){
        this.selfStage = stage;
    }

    public void initData(String warcFileName, String dictionaryFileName, String postingListFileName, String outputPath, boolean isOutputDictAndPostingList){
        this.warcFileName = warcFileName;
        this.dictionaryFileName = dictionaryFileName+TXT_SUFFIX;
        this.postingFileName = postingListFileName+TXT_SUFFIX;
        this.outputPath = outputPath;
        this.isOutputDictAndPostingList = isOutputDictAndPostingList;
        this.helper = new ProgressHelper(progressBar);
        if(isOutputDictAndPostingList)
            stages = 3;
        else
            stages = 2;
    }

    public void start(){

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stageLabel.setText(String.format(STAGE_DESC, 1, stages));
                loadingLabel.setText("Loading Documents...");
            }
        });
        WARCLoader loader = new WARCLoader(this.warcFileName, helper);
        loader.start();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stageLabel.setText(String.format(STAGE_DESC, 2, stages));
                loadingLabel.setText("Indexing...");
            }
        });
        try {
            IndexUtility.startWriteIndex(this.outputPath, loader.getDocuments());
            loader.getDocuments().clear();
            if(isOutputDictAndPostingList){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        stageLabel.setText(String.format(STAGE_DESC, 3, stages));
                        loadingLabel.setText("Preparing Data...");
                    }
                });
                Outputer outputer = new Outputer(FIELD_NAME, IndexUtility.getIndexReader(this.outputPath), this.outputPath, helper);
                outputer.prepareData();
                outputer.outputPostingListAndDictionary();
            }
        }catch(Exception exception){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An Exception Occurred.");
            alert.setContentText(exception.getStackTrace().toString());
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                selfStage.close();
            }
        });
    }
}
