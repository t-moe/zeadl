package ch.bfh.android.zeadl;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Created by adrian on 09.04.15.
 */
public class DataClass {

    private String title;
    private int max;
    private int min;

    int[] DataArray;


    public DataClass(String title, int min, int max, int[] DataArray){
        this.title = title;
        this.min = min;
        this.max = max;
        this.DataArray = DataArray;

    }

    public int getMax(){
        return max;
    }

    public int getMin(){
        return min;
    }

    public String getTitle(){
        return title;
    }

    public int[] getDataArray(){
        return DataArray;
    }
}
