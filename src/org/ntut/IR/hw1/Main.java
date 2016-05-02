package org.ntut.IR.hw1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        GUIController controller = (GUIController)loader.getController();
        controller.setStage(primaryStage);
    }

    public static void main(String args[]) throws Exception{
        final String usage = "Usage:\njava -jar IRHW.jar -warc WARCFileName " +
                "[-idp indexDirectoryPath] " +
                "[-ofp outputFilePath] " +
                "[-odfn outputDictionaryFileName] " +
                "[-oplfn outputPostingListFileName]\n" +
                "The args surrounded with [] sign is optional.\n" +
                "Default index files, which are generate by Lucene, are placed in the \"index\" folder.\n" +
                "Default output path is as same as where jar file located.\n" +
                "Default dictionary file name is \"Dictionary.txt\".\n" +
                "Default posting list file name is \"PostingList.txt\".\n";
        String warcFileName = null;
        String indexDirPath = null;
        String outputFilePath = null;
        String outputDictionaryName = null;
        String outputPostingListName = null;
        launch(args);
        /*
        try {
            for (int i = 0; i < args.length; i++) {
                if ("-idp".equals(args[i])) {
                    indexDirPath = args[++i];
                } else if ("-ofp".equals(args[i])) {
                    outputFilePath = args[++i];
                } else if ("-odfn".equals(args[i])) {
                    outputDictionaryName = args[++i];
                } else if ("-oplfn".equals(args[i])) {
                    outputPostingListName = args[++i];
                } else if ("-warc".equals(args[i])) {
                    warcFileName = args[++i];
                }
            }
        }catch (IndexOutOfBoundsException exception){
            System.err.println(usage);
            System.exit(-1);
        }

        if(warcFileName==null){
            Logger.LOGGER.error("WARCFileName is not valid.");
            System.err.println(usage);
            System.exit(1);
        }

        if(indexDirPath==null)
            indexDirPath = DEFAULT_INDEX_PATH;
        if(outputFilePath==null)
            outputFilePath = DEFAULT_OUTPUT_FILE_PATH;

        WARCLoader loader = new WARCLoader(warcFileName);
        IndexUtility.startWriteIndex(indexDirPath, loader.getDocuments());
        //Free the memory.
        loader.getDocuments().clear();
        IndexReader indexReader = IndexUtility.getIndexReader(indexDirPath);
        Outputer outputer = new Outputer(FIELD_NAME, indexReader, outputFilePath);
        if(outputDictionaryName!=null)
            outputer.setDictionaryName(outputDictionaryName);
        if(outputPostingListName!=null)
            outputer.setPostingListFileName(outputPostingListName);
        outputer.prepareData();
        outputer.outputPostingListAndDictionary();
*/
        /*Gui gui = new Gui();
        gui.run();
        QueryTFIDF qt  = new QueryTFIDF();
        System.out.print("Enter the Query :");
        Scanner scanner = new Scanner(System.in);
        qt.SetInput(scanner.nextLine());
        qt.SearchFiles();
        */
    }
}
