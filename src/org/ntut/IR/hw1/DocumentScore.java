package org.ntut.IR.hw1;

/**
 * Created by vodalok on 2016/5/2.
 */
public class DocumentScore {
    private Integer docID;
    private Double score;

    public DocumentScore(Integer docID, Double score){
        this.docID = docID;
        this.score = score;
    }

    public Integer getDocID() {
        return docID;
    }

    public void setDocID(Integer docID) {
        this.docID = docID;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
