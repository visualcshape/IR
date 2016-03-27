package org.ntut.IR.hw1;

/**
 * Created by Vodalok on 2016/3/25.
 */

public class HW1{
    public static void main(String args[]) throws Exception{
        WARCLoader loader = new WARCLoader("09.warc");
        System.out.println("Document Collection size : "+loader.getDocuments().size());
    }
}
