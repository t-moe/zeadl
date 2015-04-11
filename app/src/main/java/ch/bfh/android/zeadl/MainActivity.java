package ch.bfh.android.zeadl;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private TextView tempText;

    private XYMultipleSeriesDataset mDataset;
    private XYMultipleSeriesRenderer mRenderer;
    private GraphicalView mChartView;
    private TimeSeries ch1Series;
    private TimeSeries ch2Series;
    private LinearLayout layout;
    private Date startTime;




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


        SensorGroup tempGroup;
        List<SensorGroup> activeGroups = SensorGroupFactory.getActiveGroups();
        if(activeGroups.size()==0) {
            List<SensorGroupFactory.SensorGroupInfo> strs = SensorGroupFactory.getAvailableGroups();
            tempGroup= SensorGroupFactory.activate(strs.get(0));
        } else
        {
            tempGroup = activeGroups.get(0);
        }
        SensorGroupFactory.getActiveGroups();

        SensorChannel ch1tmp;
        SensorChannel ch2tmp;
        List<SensorChannel> tempChannels = tempGroup.getActiveChannels();
        if(tempChannels.size()==0) {
            List<SensorGroup.SensorChannelInfo> avails = tempGroup.getAvailableChannels();
            ch1tmp = tempGroup.activate(avails.get(0));
            ch2tmp = tempGroup.activate(avails.get(1));
        } else {
            ch1tmp = tempChannels.get(0);
            ch2tmp = tempChannels.get(1);
        }
        final SensorChannel ch1 = ch1tmp;
        final SensorChannel ch2 = ch2tmp;


        layout = (LinearLayout) findViewById(R.id.chart);
/*
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DetailActivity.class));
            }
        });
//*/
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
  //*/

        // create dataset and renderer
        mDataset = new XYMultipleSeriesDataset();
        mRenderer = new XYMultipleSeriesRenderer();

        //Colors
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.LTGRAY);
        mRenderer.setMarginsColor(Color.WHITE);
        mRenderer.setXLabelsColor(Color.DKGRAY);
        mRenderer.setYLabelsColor(0,Color.DKGRAY);
        mRenderer.setLabelsColor(Color.BLACK);
        mRenderer.setGridColor(Color.GRAY);

        //TextSizes
        mRenderer.setAxisTitleTextSize(21);
        mRenderer.setChartTitleTextSize(30);
        mRenderer.setLabelsTextSize(25);
        mRenderer.setLegendTextSize(25);

        //Spacings
        mRenderer.setMargins(new int[]{50, 80, 50, 50});
        mRenderer.setYLabelsAlign(Paint.Align.RIGHT, 0);
        mRenderer.setYLabelsPadding(10);

        mRenderer.setPointSize(10f);
        mRenderer.setClickEnabled(false);
        //mRenderer.setSelectableBuffer(30);
        mRenderer.setPanEnabled(true,true);
        mRenderer.setZoomEnabled(true, true);
        mRenderer.setShowGridY(true);


        XYSeriesRenderer ch1Renderer = new XYSeriesRenderer();
        ch1Renderer.setColor(Color.BLUE);
        ch1Renderer.setPointStyle(PointStyle.CIRCLE);
        ch1Renderer.setFillPoints(true);
        ch1Renderer.setLineWidth(3f);
        ch1Renderer.setDisplayChartValues(true);
        ch1Renderer.setDisplayChartValuesDistance(100);
        ch1Renderer.setChartValuesTextSize(20);
        ch1Renderer.setChartValuesFormat(NumberFormat.getInstance());
        ch1Renderer.setChartValuesSpacing(20);
        mRenderer.addSeriesRenderer(ch1Renderer);

        XYSeriesRenderer ch2Renderer = new XYSeriesRenderer();
        ch2Renderer.setColor(Color.RED);
        ch2Renderer.setPointStyle(PointStyle.CIRCLE);
        ch2Renderer.setFillPoints(true);
        ch2Renderer.setLineWidth(3f);
        mRenderer.addSeriesRenderer(ch2Renderer);


        mRenderer.setChartTitle(tempGroup.getName());
        ch1Series = new TimeSeries(ch1.getName());
        ch2Series = new TimeSeries(ch2.getName());

        mRenderer.setXTitle("time");
        mRenderer.setYTitle(tempGroup.getUnit());

        mDataset.addSeries(ch1Series);
        mDataset.addSeries(ch2Series);

        mChartView = ChartFactory.getTimeChartView(this, mDataset, mRenderer,
                "H:mm:ss");


        layout.addView(mChartView);

        startTime = new Date();




        new Thread(new Runnable() {
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(1000, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mChartView.post(new Runnable() {
                        @Override
                        public void run() {
                            Date d = new Date();

                            ch1Series.add(d, ch1.getSample());
                            ch2Series.add(d, ch2.getSample());

                            //mRenderer.setPanLimits(new double[]{startTime.getTime(), d.getTime() + 1000, 0, 60});
                            //mRenderer.setZoomLimits(new double[]{startTime.getTime(),d.getTime(),0,0});

                            mChartView.repaint();
                        }

                    });
                }


            }
        }).start();



       // a.getActiveChannels();
        //a.deactivate(ch1);
        //a.deactivate(ch2);

        //SensorGroupFactory.deactivate(strs.get(0));

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
