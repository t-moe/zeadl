package ch.bfh.android.zeadl.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ch.bfh.android.zeadl.R;
import ch.bfh.android.zeadl.sensor.SensorChannel;
import ch.bfh.android.zeadl.sensor.SensorGroup;
import ch.bfh.android.zeadl.sensor.SensorGroupController;


public class DetailActivity extends ActionBarActivity {

    private TableLayout table_layout;
    private List<SensorChannel> activeChannels;
    private String[] ChannelNames;
    private SensorGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int SensorGroupId = extras.getInt("sensorGroupId");
            group = SensorGroupController.getActiveGroups().get(SensorGroupId);

            final ListView listView= (ListView) findViewById(R.id.channelList);
            final DetailChannelListViewAdapter adapter = new DetailChannelListViewAdapter(this,group);
            listView.setAdapter(adapter);

            activeChannels = group.getActiveChannels();

            int chanNum = activeChannels.size();
            ChannelNames = new String[chanNum];

            for(int i=0;i<chanNum;i++) {
                ChannelNames[i] = activeChannels.get(i).getName();
            }

            reCreateGraph((LinearLayout) findViewById(R.id.layoutChart),group);

            final SeekBar barSamplerate = (SeekBar) findViewById(R.id.barSamplerate);
            barSamplerate.setMax(group.getMaximalSampleRate());
            barSamplerate.setProgress(group.getSampleRate());

            final TextView textViewSamplerate = (TextView)findViewById(R.id.textSamplerate);
            textViewSamplerate.setText(formatSampleRate(group.getSampleRate()));

            barSamplerate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(progress==0){
                        progress = 1;
                        barSamplerate.setProgress(1);
                    }
                    int samplerate = progress;

                    group.setSampleRate(samplerate);
                    textViewSamplerate.setText(formatSampleRate(samplerate));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
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

        table_layout = (TableLayout) findViewById(R.id.TableData);
        TableClass tc = new TableClass(table_layout,this,group);

        tc.addSegment(group.getLastDataSegment());
    }


    public static int dpToPx(float dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void reCreateGraph(LinearLayout layout, SensorGroup sensorGroup) {
        layout.removeAllViews();

        // create dataset and renderer
        final XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
        final XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

        //Colors
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.argb(0, 1, 1, 1));
        mRenderer.setMarginsColor(Color.argb(0, 1, 1, 1)); //transparent
        mRenderer.setXLabelsColor(Color.DKGRAY);
        mRenderer.setYLabelsColor(0, Color.DKGRAY);
        mRenderer.setLabelsColor(Color.BLACK);
        mRenderer.setGridColor(Color.GRAY);

        //TextSizes
        mRenderer.setLabelsTextSize(dpToPx(8));
        mRenderer.setLegendTextSize(dpToPx(8));

        //Spacings
        float density = Resources.getSystem().getDisplayMetrics().density;

        int marginBottom = dpToPx(8);
        if (density <= 1.5) { //beagle bone
            mRenderer.setLegendHeight(dpToPx(27));
            marginBottom = dpToPx(-3);
        }
        mRenderer.setMargins(new int[]{dpToPx(12), dpToPx(30), marginBottom, dpToPx(6)});
        mRenderer.setYLabelsAlign(Paint.Align.RIGHT, 0);
        mRenderer.setYLabelsPadding(dpToPx(5));

        mRenderer.setPointSize(dpToPx(3));
        mRenderer.setClickEnabled(false);
        mRenderer.setPanEnabled(true, true);
        mRenderer.setZoomEnabled(true, true);
        mRenderer.setShowGridY(true);

        //mRenderer.setPanLimits(new double[]{startTime.getTime(), d.getTime() + 1000, 0, 60});
        //mRenderer.setZoomLimits(new double[]{startTime.getTime(),d.getTime(),0,0});


        final SensorGroup.DataSegment dataSegment = sensorGroup.getLastDataSegment();
        for (SensorChannel channel : dataSegment.getChannels()) {

            final XYSeriesRenderer chRenderer = new SensorChannelRenderer(channel);
            chRenderer.setPointStyle(PointStyle.CIRCLE);
            chRenderer.setFillPoints(true);
            chRenderer.setLineWidth(dpToPx(2));
            chRenderer.setDisplayChartValues(true);
            chRenderer.setDisplayChartValuesDistance(dpToPx(30));
            chRenderer.setChartValuesTextSize(dpToPx(8));
            chRenderer.setChartValuesFormat(NumberFormat.getInstance());
            chRenderer.setChartValuesSpacing(dpToPx(5));
            mRenderer.addSeriesRenderer(chRenderer);

            TimeSeries channelSeries = new SensorChannelSeries(channel, dataSegment, chRenderer);
            mDataset.addSeries(channelSeries);
        }

        final GraphicalView mChartView = new SensorTimeChart(this, mDataset, mRenderer, dataSegment);
        layout.addView(mChartView);
    }

    private String formatSampleRate(int samplerate){
        String val, temp;
        if(samplerate>=3600){
            temp = String.format("%1.2f",samplerate/3600.0);
            val = temp + "/sec";
        }
        else if(samplerate>=60){
            temp = String.format("%2.1f",samplerate/60.0);
            val = temp + "/min";
        }
        else {
            val = samplerate + "/h";
        }

        return val;
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
        if (id == R.id.action_savegraph) {
            Log.d("DetailActivity", "Action Save Graph");
            Toast.makeText(getApplicationContext(),"not implemented yet",Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_savetable) {
            FileClass fc = new FileClass();
            try {
                fc.saveSegment(group.getLastDataSegment());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("DetailActivity", "Action Save Table");
            Toast.makeText(getApplicationContext(),"Table saved",Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_cleargraph) {
            Log.d("DetailActivity", "Action Clear Graph");
            Toast.makeText(getApplicationContext(),"not implemented yet",Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_back) {
            Intent i = new Intent(DetailActivity.this,MainActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
