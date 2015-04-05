package ch.bfh.android.zeadl;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


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


        List<SensorGroupFactory.SensorGroupInfo> strs = SensorGroupFactory.getAvailableGroups();
        SensorGroup a = SensorGroupFactory.activate(strs.get(0));
        SensorGroupFactory.getActiveGroups();

        final SensorChannel b = a.activate(a.getAvailableChannels().get(0));



        final GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.setTitle(a.getName());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(new Date().getTime());
        graph.getViewport().setMaxX(new Date().getTime() + (1000 * 60));

        /*graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(30);*/

        graph.getViewport().setScalable(true);
        //graph.getViewport().setScrollable(true);

        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graph.getGridLabelRenderer().setVerticalAxisTitle(a.getUnit());
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this,DateFormat.getTimeInstance()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);

        final LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        series.setTitle(b.getName());
        series.setDrawDataPoints(true);

        graph.addSeries(series);

        graph.getLegendRenderer().setVisible(true);
        //graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);


        new Thread(new Runnable() {
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(1000, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    final DataPoint d = new DataPoint(new Date(), b.getSample());

                    graph.post(new Runnable() {
                        @Override
                        public void run() {
                            series.appendData(d, false, 1000);
                            //graph.getViewport().setMaxX(d.getX());
                        }
                    });
                }


            }
        }).start();

        series.appendData(new DataPoint(new Date(),b.getSample()),false,1000);

       // for(int i=0; i<29; i++) {
        //    series.appendData(new DataPoint(i,b.getSample()),false,1000);
       // }

       // a.getActiveChannels();
        //a.deactivate(b);

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
