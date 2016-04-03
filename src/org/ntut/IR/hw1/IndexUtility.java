package org.ntut.IR.hw1;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by VodalokLab on 2016/4/1.
 */

public class IndexUtility {
    private IndexUtility(){

    }

    private static final String LOCK_FILE_NAME = "write.lock";
    private static final int RAM_LIMIT = 250;
    private static final String CONTENT_FIELD_NAME = "content";

    /**
     *  Start indexing with a writer, which is  using default  analyzer and config.
     *  @param  dirPath    A relative or absolute directory path to store the index file.
     *  @param  documents A collection of documents, can be a list, map, or iterable container.
     */
    public static void startWriteIndex(String dirPath, Iterable<? extends Iterable<? extends IndexableField>> documents) throws IOException {
        Directory directory = FSDirectory.open(Paths.get(dirPath));
        FileUtils.cleanDirectory(new File(dirPath));

        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        config.setRAMBufferSizeMB(RAM_LIMIT);
        //config.setCodec(new SimpleTextCodec());

        Logger.LOGGER.info("Now indexing...");
        IndexWriter writer = new IndexWriter(directory,config);
        writer.addDocuments(documents);
        writer.commit();
        deleteLockFile(directory);
        directory.close();
        Logger.LOGGER.info("Indexed.");
    }

    private static void deleteLockFile(Directory directory){
        try{
            directory.deleteFile(LOCK_FILE_NAME);
        }catch (IOException exception){
            if(exception instanceof FileNotFoundException){
                Logger.LOGGER.error("No lock file can be deleted.");
            }
        }
    }

    public static IndexReader getIndexReader(String dirPath) throws IOException{
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(dirPath)));

        return indexReader;
    }
}
