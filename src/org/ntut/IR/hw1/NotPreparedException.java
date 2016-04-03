package org.ntut.IR.hw1;

/**
 * Created by vodalok on 2016/4/3.
 */
public class NotPreparedException extends Exception {
    private final String MESSAGE = "The output is not prepared. Please call prepareData() first to prepare.";

    public NotPreparedException(){

    }

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
