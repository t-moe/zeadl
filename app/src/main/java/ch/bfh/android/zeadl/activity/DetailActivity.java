package ch.bfh.android.zeadl.activity;

import android.content.ContentValues;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

import ch.bfh.android.zeadl.R;
import ch.bfh.android.zeadl.sensor.SensorChannel;
import ch.bfh.android.zeadl.sensor.SensorGroup;
import ch.bfh.android.zeadl.sensor.SensorGroupController;


public class DetailActivity extends ActionBarActivity {

    private List<SensorChannel> activeChannels;
    private String[] ChannelNames;
    private SensorGroup group;
    private SensorGroup.UpdateListener mUpdateListener = null;
    final private int barMaxValue = 1000;
    private int samplerate;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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

            reCreateGraph();

            mUpdateListener = new SensorGroup.UpdateListener() {
                @Override
                public void onActiveChannelsChanged(SensorGroup.ActiveChannelsChangedEvent event) {
                    //not needed
                }

                @Override
                public void onDataSegmentAdded(SensorGroup.DataSegmentAddedEvent event) {
                    reCreateGraph();
                }

                @Override
                public void onSampleRateChanged(SensorGroup.SampleRateChangedEvent event) {
                    //not needed
                }
            };
            group.addEventListener(mUpdateListener);

            final SeekBar barSamplerate = (SeekBar) findViewById(R.id.barSamplerate);
    //        barSamplerate.setMax(group.getMaximalSampleRate());
            barSamplerate.setMax(barMaxValue);
            barSamplerate.setProgress((int)(barMaxValue * Math.log(group.getSampleRate())
                                        / Math.log(group.getMaximalSampleRate())));

            final TextView textViewSamplerate = (TextView)findViewById(R.id.textSamplerate);
            textViewSamplerate.setText(formatSampleRate(group.getSampleRate()));


            barSamplerate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(progress==0){
                        progress = 1;
                        barSamplerate.setProgress(1);
                    }

                    double val;

                    val = Math.pow((double)group.getMaximalSampleRate(),((((double)progress)+1)/ (barMaxValue)));

                    int samplerate_t = (int)(val);

                    if(samplerate_t>group.getMaximalSampleRate()){
                        samplerate_t=group.getMaximalSampleRate();
                    }
    //              int samplerate = progress;

                    samplerate = samplerate_t;
                    textViewSamplerate.setText(formatSampleRate(samplerate_t));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {group.setSampleRate(samplerate);}
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

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equals("Table")) {
                    TableLayout table_layout = (TableLayout) findViewById(R.id.TableData);
                    table_layout.removeAllViews();
                    TableClass tc = new TableClass(table_layout,DetailActivity.this,group);
                    tc.addSegment(group.getLastDataSegment());
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mUpdateListener!=null) {
            group.removeEventListener(mUpdateListener);
            mUpdateListener=null;
        }
        final LinearLayout layout = (LinearLayout) findViewById(R.id.layoutChart);
        if(layout!=null) {
            layout.removeAllViews();
        }
    }

    public static int dpToPx(float dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void reCreateGraph() {
        final LinearLayout layout = (LinearLayout) findViewById(R.id.layoutChart);
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


        final SensorGroup.DataSegment dataSegment = group.getLastDataSegment();
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

    private void saveGraph() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.layoutChart);
        View v = layout.getChildAt(0);

        Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(),
                v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.drawColor(Color.LTGRAY);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.draw(c);

        try {
            String filename= "/sdcard/DCIM/ZeadlGraph"+System.currentTimeMillis()+".jpg";
            FileOutputStream output = new FileOutputStream(filename);

            // Compress into png format image from 0% - 100%
            b.compress(Bitmap.CompressFormat.JPEG, 100, output);
            output.flush();
            output.close();
            ContentValues values = new ContentValues();

            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA, filename);

            getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        }

        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(),"Graph saved to gallery",Toast.LENGTH_LONG).show();
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
            saveGraph();
            return true;
        }

        if (id == R.id.action_savetable) {
            FileClass fc = new FileClass();
            try {
                String filename = fc.saveSegment(group.getLastDataSegment());
                Toast.makeText(getApplicationContext(),"Table saved to "+filename,Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("DetailActivity", "Action Save Table");
            return true;
        }

        if (id == R.id.action_cleargraph) {
            Log.d("DetailActivity", "Action Clear Graph");
            group.clearAllDataSegments();
            ((TableLayout)findViewById(R.id.TableData)).removeAllViews();
            Toast.makeText(getApplicationContext(),"All Data removed",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
