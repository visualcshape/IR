package org.ntut.IR.hw1;

/**
 * Created by vodalok on 2016/4/3.
 */
public class ThreeTuple<X,Y,Z> {
    private X x;
    private Y y;
    private Z z;

    public ThreeTuple(X x,Y y,Z z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void add(X first, Y second, Z third){
        this.setFirst(first);
        this.setSecond(second);
        this.setThird(third);
    }

    public void setFirst(X first){
        this.x = first;
    }

    public void setSecond(Y second){
        this.y = second;
    }

    public void setThird(Z third){
        this.z = third;
    }

    public X getFirst(){
        return x;
    }

    public Y getSecond(){
        return y;
    }

    public Z getThird(){
        return z;
    }
}
