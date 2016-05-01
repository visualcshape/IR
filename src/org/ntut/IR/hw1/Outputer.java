package org.ntut.IR.hw1;

import org.apache.lucene.index.*;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.BytesRef;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by vodalok on 2016/4/3.
 */
public class Outputer {
    private String fieldNameToOutput;
    private IndexReader indexReader;
    private String outputPath = ".";
    private String dictionaryName = "Dictionary.txt";
    private String postingListFileName = "PostingList.txt";
    //ThreeTuple Integer:docId, Integer: termsFreqInDoc, List<Integer>:Positions
    private Map<String, Map<Integer,List<ThreeTuple<Integer, Integer, List<Integer>>>>> termsDocFreqPos = new HashMap<>();
    private boolean isPrepared = false;
    private boolean isShowProgress = true;
    private final long FREE_MEMORY_LOWER_BOUND_MB = 80;
    private boolean isFirstWriteDictionary = true;
    private boolean isFirstWritePostingList = true;

    public Outputer(String fieldToOutput, IndexReader indexReader, String outputPath){
        this.fieldNameToOutput = fieldToOutput;
        this.indexReader = indexReader;
        this.outputPath = outputPath;

    }

    public void setIsShowProgress(boolean show){
        this.isShowProgress = show;
    }

    public void setDictionaryName(String dictionaryName) {
        this.dictionaryName = dictionaryName;
    }

    public void setPostingListFileName(String postingListFileName) {
        this.postingListFileName = postingListFileName;
    }

    public void prepareData() throws IOException, NotPreparedException{
        Fields fields = MultiFields.getFields(this.indexReader);
        Terms contentTerms = fields.terms(this.fieldNameToOutput);
        TermsEnum termsEnum = contentTerms.iterator();
        BytesRef aTerm;
        ProgressHelper helper = new ProgressHelper("Preparing Progress", "Data Preparation Complete");
        long termProgress = 0;
        this.isPrepared = true;

        Logger.LOGGER.info("Preparing Data...");
        while((aTerm = termsEnum.next())!=null){
            boolean isFreeMemoryAvailable = this.checkMemory();

            if(!isFreeMemoryAvailable){
                //flush to file.
                Logger.LOGGER.warn("Memory is about to exceed. Dumping things to file...");
                this.outputPostingListAndDictionary();
                //Clean up.
                termsDocFreqPos.clear();
            }

            String termString = aTerm.utf8ToString();
            PostingsEnum termsPosition = MultiFields.getTermPositionsEnum(this.indexReader, this.fieldNameToOutput, aTerm);

            List<ThreeTuple<Integer, Integer, List<Integer>>> docIdTermFreqPosesList = new ArrayList<>();
            while(termsPosition.nextDoc()!= DocIdSetIterator.NO_MORE_DOCS){
                ThreeTuple<Integer, Integer, List<Integer>> termsAttr = new ThreeTuple<>(0,0,new ArrayList<>());
                List<Integer> docTermPositions = new ArrayList<>();
                int docID = termsPosition.docID();
                int docTermFreq = termsPosition.freq();
                int termPos;
                for(int i = 0;i < termsPosition.freq()&&(termPos = termsPosition.nextPosition())!=-1;i++)
                    docTermPositions.add(termPos);
                termsAttr.add(docID, docTermFreq, docTermPositions);
                docIdTermFreqPosesList.add(termsAttr);
            }
            Map<Integer,List<ThreeTuple<Integer, Integer, List<Integer>>>> freqListMap = new HashMap<>();
            freqListMap.put(termsEnum.docFreq(),docIdTermFreqPosesList);
            this.termsDocFreqPos.put(termString, freqListMap);

            //Show Progress
            termProgress++;
            if(isShowProgress) {
                helper.printProgress(termProgress, contentTerms.size());
            }

        }
        //Logger.LOGGER.info("Data Preparation Completed.");
    }

    public void outputDictionary() throws IOException,NotPreparedException{
        checkPrepared();
        try(FileWriter dictionaryWriter = new FileWriter(new File(outputPath + "/" + this.dictionaryName), !this.isFirstWriteDictionary);
            BufferedWriter bufferedWriter = new BufferedWriter(dictionaryWriter)
        ) {
            this.isFirstWriteDictionary = false;
            Logger.LOGGER.info("Writing Dictionary...");
            for (Map.Entry entry : this.termsDocFreqPos.entrySet()) {
                bufferedWriter.write(entry.getKey().toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            dictionaryWriter.flush();
        }
    }

    public void outputPostingList() throws IOException, NotPreparedException{
        checkPrepared();
        try(FileWriter dictionaryWriter = new FileWriter(new File(outputPath + "/" + this.postingListFileName), !this.isFirstWritePostingList);
            BufferedWriter bufferedWriter = new BufferedWriter(dictionaryWriter)
        ) {
            this.isFirstWritePostingList = false;
            Logger.LOGGER.info("Writing Invert Index File...");
            for (Map.Entry<String,Map<Integer,List<ThreeTuple<Integer, Integer, List<Integer>>>>> entry : this.termsDocFreqPos.entrySet()) {
                StringBuilder aTermInvertIndexOutputStringBuilder = new StringBuilder();
                aTermInvertIndexOutputStringBuilder.append(entry.getKey());
                aTermInvertIndexOutputStringBuilder.append(",");
                Map<Integer,List<ThreeTuple<Integer, Integer, List<Integer>>>> freqMap = entry.getValue();
                for(Map.Entry<Integer,List<ThreeTuple<Integer, Integer, List<Integer>>>> docsEntry:freqMap.entrySet()){
                    boolean isFirstRow = true;
                    aTermInvertIndexOutputStringBuilder.append(docsEntry.getKey());
                    aTermInvertIndexOutputStringBuilder.append(":");
                    aTermInvertIndexOutputStringBuilder.append("\n");
                    aTermInvertIndexOutputStringBuilder.append("\t<");
                    for(ThreeTuple<Integer,Integer, List<Integer>> element:docsEntry.getValue()){
                        if(!isFirstRow) {
                            aTermInvertIndexOutputStringBuilder.append("\t ");
                        }
                        isFirstRow = false;
                        aTermInvertIndexOutputStringBuilder.append(element.getFirst());
                        aTermInvertIndexOutputStringBuilder.append(", ");
                        aTermInvertIndexOutputStringBuilder.append(element.getSecond());
                        aTermInvertIndexOutputStringBuilder.append(": <");
                        for(Integer positions:element.getThird()){
                            aTermInvertIndexOutputStringBuilder.append(positions);
                            aTermInvertIndexOutputStringBuilder.append(", ");
                        }
                        //Backward 2 character(Eliminate ", ")
                        aTermInvertIndexOutputStringBuilder.setLength(aTermInvertIndexOutputStringBuilder.length()-2);
                        aTermInvertIndexOutputStringBuilder.append(">;\n");
                    }
                    //Backward 1 character to eliminate newline.
                    aTermInvertIndexOutputStringBuilder.append(">\n");
                }
                bufferedWriter.write(aTermInvertIndexOutputStringBuilder.toString());
            }
            bufferedWriter.flush();
            dictionaryWriter.flush();
        }
        Logger.LOGGER.info("Invert Index File Write Complete.");
    }

    public void outputPostingListAndDictionary() throws IOException, NotPreparedException{
        outputDictionary();
        outputPostingList();
        Logger.LOGGER.info("Writing File Finished.");
    }

    private boolean checkMemory(){
        final long MB = 1024*1024;
        Runtime runtime = Runtime.getRuntime();
        long freeMemoryMB = runtime.freeMemory()/MB;

        return freeMemoryMB > FREE_MEMORY_LOWER_BOUND_MB;
    }

    private void checkPrepared() throws NotPreparedException{
        if(!this.isPrepared)
            throw new NotPreparedException();
    }
}
