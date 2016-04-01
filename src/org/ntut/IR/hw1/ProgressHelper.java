package org.ntut.IR.hw1;

/**
 * Created by VodalokLab on 2016/4/1.
 */
public class ProgressHelper {
    public ProgressHelper(){
    }

    private final int BAR_WIDTH = 5;
    private final String BAR_STYLE = "[]";
    private final int TAB_WIDTH = 4;
    private final int ONE_HUNDRED_PERCENT = 100;
    private final String PERCENTAGE_SYMBOL = "=";

    public void printProgress(long currentLine,long totalLine) {
        if(totalLine == 0)
            throw new ArithmeticException();
        float percentage = (((float)currentLine / totalLine)*100);

        this.printProgressBar(percentage);
    }

    private void printProgressBar(float percentage){
        //Insert tab
        StringBuilder printOut = new StringBuilder();
        StringBuilder tabToInsert = new StringBuilder();
        for(int i = 0 ; i < BAR_WIDTH-1 ; i++)
            tabToInsert.append("\t");

        int progressBarCapacity = BAR_WIDTH*4;
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
        for(int i = 0 ; i < TAB_WIDTH - remainSymbols; i++){
            printOut.append(' ');
        }
        printOut.insert(0,BAR_STYLE.charAt(0));
        printOut.append(BAR_STYLE.charAt(1));
        //
        printOut.append(percentage+"%");
        printOut.insert(0, "\rLoading...");

        System.out.print(printOut);
    }
}
