package ch.bfh.android.zeadl.activity;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

import ch.bfh.android.zeadl.sensor.SensorGroup;

/**
 * Created by adrian on 11.04.15.
 */
public class TableClass {

    private Activity activity;
    private TableLayout table_layout;
    private SensorGroup group;

    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);

    public TableClass(TableLayout table_layout, Activity activity, SensorGroup group){
        this.table_layout = table_layout;
        this.activity = activity;
        this.group = group;

    }

    public void addSegment(SensorGroup.DataSegment segment){
        TableRow row = new TableRow(activity);
        TextView tv;
        Date date;
        SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss.S");

        row.setLayoutParams(params);
        row.setPadding(5,1,5,1);

        tv = new TextView(activity);
        tv.setText("Time   ");
        tv.setBackgroundColor(0x88888888);
        row.addView(tv);

        for(int i=0;i<segment.getChannels().size();i++){
            tv = new TextView(activity);
            tv.setPadding(0,0,10,0);
            tv.setBackgroundColor(0xBBBBBBBB);
            tv.setText(segment.getChannels().get(i).getName());

            row.addView(tv);
        }

        table_layout.addView(row);

        for(int i=0;i<segment.getEntries().size();i++){
            row = new TableRow(activity);

            tv = new TextView(activity);
            tv.setPadding(0,0,5,0);
            //date = segment.getEntries().get(i).getTime();
            //tv.setText(""+date.getTime());

            tv.setText(df.format(segment.getEntries().get(i).getTime()));

            row.addView(tv);

            for (int j=0;j<segment.getChannels().size();j++){
                tv = new TextView(activity);
                tv.setPadding(0,0,5,0);
                tv.setText(""+segment.getEntries().get(i).getChannelData().get(j));

                row.addView(tv);
            }

            table_layout.addView(row);
        }
    }
}
