package org.ntut.IR.hw1;

import javafx.scene.control.ProgressBar;

/**
 * Created by VodalokLab on 2016/4/1.
 */
public class ProgressHelper {
    public ProgressHelper(String loadingString,String completeString){
        this.loadingString = loadingString;
        this.completeString = completeString;
    }

    public ProgressHelper(){
        this.loadingString = "Loading...";
        this.completeString = "Load complete.";
    }

    public ProgressHelper(String loadingString, String completeString, ProgressBar progressBar){
        this(loadingString, completeString);
        this.progressBar = progressBar;
    }

    public ProgressHelper(ProgressBar progressBar){
        this();
        this.progressBar = progressBar;
    }

    private final int BAR_WIDTH = 5;//TAB_WIDTH*BAR_WIDTH should not greater than 100, and should be a  divisor of 100
    private final char[] BAR_STYLE = {'[',']'};
    private final int TAB_WIDTH = 4;
    private final int ONE_HUNDRED_PERCENT = 100;
    private final char PERCENTAGE_SYMBOL = '>';
    private final char PERCENT_SYMBOL = '%';
    private String loadingString;
    private String completeString;
    private ProgressBar progressBar = null;

    public void printProgress(long currentLine,long totalLine) {
        float percentage = calculatePercentage(currentLine, totalLine);

        this.printProgressBar(percentage);
    }

    private float calculatePercentage(long currentLine, long totalLine) {
        if(currentLine > totalLine)
            throw new IllegalArgumentException("The currentLine should not greater than totalLine.");
        if(totalLine == 0)
            throw new ArithmeticException();
        return (((float)currentLine / totalLine)*ONE_HUNDRED_PERCENT);
    }

    public void displayOnProgressBar(long currentLine, long totalLine){
        float percentage = this.calculatePercentage(currentLine, totalLine);
        percentage = percentage/ONE_HUNDRED_PERCENT;
        progressBar.setProgress(percentage);
    }

    private void printProgressBar(float percentage){

        //Insert tab
        StringBuilder printOut = new StringBuilder();

        int progressBarCapacity = BAR_WIDTH*TAB_WIDTH;
        int aLatticePercentage = ONE_HUNDRED_PERCENT/progressBarCapacity;
        int showProgressSymbolsCount = ((int)percentage)/aLatticePercentage;
        int tabToBeFillWithSymbol = showProgressSymbolsCount/TAB_WIDTH;
        int remainSymbols = showProgressSymbolsCount%TAB_WIDTH;
        StringBuilder aPackOfSymbol = new StringBuilder();
        for(int i = 0 ; i < TAB_WIDTH ; i++){
            aPackOfSymbol.append(PERCENTAGE_SYMBOL);
        }
        StringBuilder remainSymbolsString = new StringBuilder();
        for(int i = 0 ; i < remainSymbols ; i++){
            remainSymbolsString.append(PERCENTAGE_SYMBOL);
        }
        for(int i = 0 ; i < tabToBeFillWithSymbol ; i++){
            printOut.append(aPackOfSymbol);
        }
        printOut.append(remainSymbolsString);
        for(int i = 0 ; i < BAR_WIDTH-tabToBeFillWithSymbol -1 ; i++)
            printOut.append("    ");
        for(int i = 0 ; i < TAB_WIDTH - remainSymbols && percentage!= 100f; i++){
            printOut.append(' ');
        }
        printOut.insert(0,BAR_STYLE[0]);
        printOut.append(BAR_STYLE[1]);
        //
        printOut.append(percentage);
        printOut.append(PERCENT_SYMBOL);
        printOut.insert(0, "\r"+this.loadingString);

        System.out.print(printOut);
        if(percentage == 100f) {
            System.out.println();
            Logger.LOGGER.info(this.completeString);
        }
    }
}
