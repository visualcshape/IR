package org.ntut.IR.hw1;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by leo880410 on 2016/4/19.
 */
public class QueryTFIDF {
    private String[] input= null;
    private String dirPath = "./index";
    private int documentAmount=0;
    private ArrayList<String> queryInput = new ArrayList<String>();     //輸入的字串 (不重複)
    private ArrayList<Integer> queryCount= new ArrayList<Integer>();     //輸入字串計算次數
    private ArrayList<Integer> queryDocument = new ArrayList<Integer>();    //每個query出現過的document
    private ArrayList<Integer> queryDocumentFrequency = new ArrayList<Integer>();   //每個query總共出現在幾個document
    private ArrayList<Double> documentWeight = new ArrayList<Double>();   //query在document中的權重
    private ArrayList<Double> queryWeight = new ArrayList<Double>();      //query在query中的權重
    private String OutputString="";
    private String pathSim = "querySim.txt";
    public void QueryTFIDF(){

    }
    public void SetInput(String input){
        this.input=input.split(" ");
    }
    public void SearchFiles()throws IOException {
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(dirPath)));
        IndexSearcher searcher = new IndexSearcher(indexReader);
        Analyzer analyzer = new StandardAnalyzer();
        Fields fields = MultiFields.getFields(indexReader);
        Terms terms = fields.terms("content");
        TermsEnum termsEnum = terms.iterator();
        documentAmount = terms.getDocCount();
        //System.out.println("count = " + documentAmount);

        //將輸入字串存成不重複(queryInput) 且計算出現次數(queryCount)
        for(int i = 0 ; i < input.length;i++){
            if(!queryInput.contains(input[i])) {
                queryInput.add(input[i]);
            }
            int count=0;
            for (int j = i; j <input.length; j++) {
                if (input[i].equals(input[j]))count++;
            }
            queryCount.add(count);
        }
        //取得query出現在幾個document數量(queryDocumentFrequency)  出現的documentID(queryDocument)
        boolean found = false;
        for(int i =0;i<queryInput.size();i++) {
            found = termsEnum.seekExact(new BytesRef(queryInput.get(i)));
            if (found) {
                queryDocumentFrequency.add(termsEnum.docFreq());
                PostingsEnum postings = termsEnum.postings(null, PostingsEnum.POSITIONS);
                int docid;
                while ((docid = postings.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
                    if(!queryDocument.contains(docid)){
                        queryDocument.add(docid);
                    }
                }
            }else{
                queryDocumentFrequency.add(0);
            }
        }
        //計算query權重
        for(int i = 0 ; i < queryInput.size();i++){
            double value = 0;
            if(queryDocumentFrequency.get(i)!=0)
                value = (1+Math.log(queryCount.get(i))) * Math.log(documentAmount/queryDocumentFrequency.get(i));
            queryWeight.add(value);
        }


        for(int i = 0;i<queryDocument.size();i++) {
            Terms getDocumentTerms = indexReader.getTermVector(queryDocument.get(i),"content");
            TermsEnum termsEnumDocument;
            if(getDocumentTerms!=null){
                termsEnumDocument = getDocumentTerms.iterator();

                for(int j = 0 ; j < queryInput.size() ; j++) {
                    found = termsEnumDocument.seekExact(new BytesRef(queryInput.get(j)));
                    PostingsEnum postings = termsEnumDocument.postings(null, PostingsEnum.POSITIONS);
                    //找到document 計算query在document中的權重
                    double value = 0;
                    if (found) {
                        int termFreq = 1;
                        if(postings.nextDoc() != DocIdSetIterator.NO_MORE_DOCS)
                            termFreq = postings.freq();
                        if(queryDocumentFrequency.get(j)!=0)
                        value = (1+Math.log(termFreq)) * Math.log(documentAmount/queryDocumentFrequency.get(j));
                    }else {
                        if(queryDocumentFrequency.get(j)!=0)
                        value = Math.log(documentAmount/queryDocumentFrequency.get(j));
                    }
                    documentWeight.add(value);
                }
            }

        }

        String writeSim="";
        System.out.print("Query :" + "\"");
        writeSim +="Query :" + "\"";
        for(int i= 0 ; i < input.length ; i++) {
            System.out.print(input[i]);
            writeSim+=input[i];
            if(i<input.length-1) {
                System.out.print(" ");
                writeSim+=" ";
            }
        }
        System.out.println("\"");
        System.out.println("<doc#> <similarity score>");
        writeSim+="\""+"\r\n" + "<doc#> <similarity score>" + "\r\n";

        double sim=0;
        double weightD=0;
        double weightQ=0;
        for(int i = 0 ; i < queryDocument.size();i+=queryInput.size()) {
            sim = 0;
            weightD = 0;
            weightQ = 0;
            for (int j = 0; j < queryInput.size(); j++) {
                sim += documentWeight.get(i+j)*queryWeight.get(j);
                weightD += Math.pow(documentWeight.get(i+j),2);
                weightQ += Math.pow(queryWeight.get(j),2);
            }
            weightD = Math.sqrt(weightD);
            weightQ = Math.sqrt(weightQ);
            sim = sim/(weightD*weightQ);
            System.out.println(queryDocument.get(i) +" "+ sim);
            sim = (int)(sim*100)/100.0;
            writeSim+=queryDocument.get(i) +" "+ sim+"\r\n";
        }
        try
        {
            File fileSim = new File(pathSim);// 建立檔案，準備寫檔
            BufferedWriter writerSim = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileSim, false), "utf8"));
            writerSim.write(writeSim); // 寫入該字串
            writerSim.close();

        } catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("寫檔錯誤!!");
        }
    }
}
