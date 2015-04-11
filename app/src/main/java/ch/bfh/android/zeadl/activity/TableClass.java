package ch.bfh.android.zeadl.activity;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.widget.TableLayout;
import android.widget.TableRow;

/**
 * Created by adrian on 11.04.15.
 */
public class TableClass {

    private Activity activity;
    private TableLayout table_layout;
    public TableClass(TableLayout table_layout){
        this.table_layout = table_layout;


    }

    public void addRow(TableRow row){
        table_layout.addView(row);
    }

    public TableRow generateRow(int time, int data1, int data2){
        TableRow row = new TableRow(activity);



        return row;
    }


}
