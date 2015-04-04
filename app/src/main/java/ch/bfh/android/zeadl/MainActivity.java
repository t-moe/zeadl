package ch.bfh.android.zeadl;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private TextView tempText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tempText = (TextView)findViewById(R.id.tempText);

        tempText.setVisibility(View.INVISIBLE);
        //InputChannel temp = new TempChannel();
        //temp.start();
        //tempText.setText( String.format("%3.2f%s", temp.getSample(),temp.getUnit()));

        //temp.stop();


        LineChart chart = (LineChart) findViewById(R.id.chart);

        List<SensorGroupFactory.SensorGroupInfo> strs = SensorGroupFactory.getAvailableGroups();
        SensorGroup a = SensorGroupFactory.activate(strs.get(0));
        chart.setDescription("");
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.setHighlightEnabled(true);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        chart.setPinchZoom(true);



        SensorGroupFactory.getActiveGroups();

        SensorChannel b = a.activate(a.getAvailableChannels().get(0));

        ArrayList<Entry> dat = new ArrayList<Entry>();
        for(int i=0; i<10; i++) {
            dat.add(new Entry(b.getSample(),i));
        }


        LineDataSet channel1Set = new LineDataSet(dat,b.getName());

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(channel1Set);

        ArrayList<String> xVals = new ArrayList<String>();
        for(int i=0; i<10; i++) {
            xVals.add(i+"");
        }


        LineData data = new LineData(xVals, dataSets);
        chart.setData(data);
        chart.invalidate(); // refresh

        for(int i=0; i<10; i++) {
            channel1Set.addEntry(new Entry(b.getSample(),i+10));
        }
        for(int i=0; i<10; i++) {
            xVals.add((10+i)+"");
        }
        chart.invalidate(); // refresh

        a.getActiveChannels();
        a.deactivate(b);

        SensorGroupFactory.deactivate(strs.get(0));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
