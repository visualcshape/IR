package org.ntut.IR.hw1;

import java.io.*;

/**
 * Created by VodalokLab on 2016/4/1.
 */
public class Utility {
    private Utility(){}

    public static long countFileLine(String fileName) throws IOException{
        LineNumberReader reader = null;
        Logger.LOGGER.info("Calculating File Size... Shall take a few seconds...");
        try {
            reader = new LineNumberReader(new FileReader(fileName));
            while ((reader.readLine()) != null);
            return reader.getLineNumber();
        } catch (Exception ex) {
            return -1;
        } finally {
            if(reader != null)
                reader.close();
        }
    }
}
