package ch.bfh.android.zeadl.activity;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.List;

import ch.bfh.android.zeadl.R;
import ch.bfh.android.zeadl.sensor.SensorChannel;
import ch.bfh.android.zeadl.sensor.SensorGroup;
import ch.bfh.android.zeadl.sensor.SensorGroupController;


public class DetailActivity extends ActionBarActivity {

    private TableLayout table_layout;

    /*
            int[] data = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        final DataClass DatenKlasse = new DataClass("TestDaten",0,10,data);

        Button Buttentest = (Button) findViewById(R.id.mainbutten);

        Buttentest.setText(DatenKlasse.getTitle());

  //*
        Buttentest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DetailActivity.class));
            }
        });
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int SensorGroupId = extras.getInt("sensorGroupId");
            SensorGroup group = SensorGroupController.getActiveGroups().get(SensorGroupId);

            //TODO Adrian: Use information from this class to setup ui

            //General
            String name = group.getName();
            String unit = group.getUnit();
            int samplerate = group.getSampleRate();

            //Static info about channels
            //List<SensorChannel> activeChannels = group.getActiveChannels();

            //SensorChannel ch1 = activeChannels.get(0);
            //ch1.getColor();
            //ch1.getName();

            //Getting Data
            //SensorGroup.DataSegment segment = group.getLastDataSegment();
            //segment.getChannels();
            //segment.getEntries();


        }



        // Add the Tabs
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabspec = tabHost.newTabSpec("Graph");
        tabspec.setContent(R.id.tabGraph);
        tabspec.setIndicator("Graph");
        tabHost.addTab(tabspec);

        tabspec = tabHost.newTabSpec("Table");
        tabspec.setContent(R.id.tabTable);
        tabspec.setIndicator("Table");
        tabHost.addTab(tabspec);

        tabspec = tabHost.newTabSpec("Settings");
        tabspec.setContent(R.id.tabSettings);
        tabspec.setIndicator("Settings");
        tabHost.addTab(tabspec);

        // Add Table

        TableRow row = new TableRow(this);
        TableRow anotherRow = new TableRow(this);

        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));
        row.setPadding(0,1,0,1);
        anotherRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));
        anotherRow.setPadding(0,1,0,1);

        TextView tv = new TextView(this);
        TextView tv2 = new TextView(this);
        tv.setText("Column 1  ");
        tv2.setText("Column 2  ");



        row.addView(tv);
        row.addView(tv2);
        //row.addView();


        table_layout = (TableLayout) findViewById(R.id.TableData);
        table_layout.addView(row);






    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
