package org.ntut.IR.hw1;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by VodalokLab on 2016/4/1.
 */
public class IndexUtility {
    private IndexUtility(){

    }

    private static final int RAM_LIMIT = 250;

    /**
     *  Start indexing with a writer, which is  using default  analyzer and config.
     *  @param  dirPath    A relative or absolute directory path to store the index file.
     *  @param  documents A collection of document, can be a list, map, or iterable container.
     */
    public static void startWriteIndex(String dirPath, Iterable<? extends Iterable<? extends IndexableField>> documents) throws IOException {
        Directory directory = FSDirectory.open(Paths.get(dirPath));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        config.setRAMBufferSizeMB(RAM_LIMIT);

        IndexWriter writer = new IndexWriter(directory,config);
        writer.addDocuments(documents);

        writer.close();
    }
}
