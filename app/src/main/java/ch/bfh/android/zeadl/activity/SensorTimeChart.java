package ch.bfh.android.zeadl.activity;

import android.content.Context;

import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.ScatterChart;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.lang.reflect.Field;
import java.util.Timer;

import ch.bfh.android.zeadl.sensor.SensorChannel;
import ch.bfh.android.zeadl.sensor.SensorGroup;

/**
 * Created by timo on 4/13/15.
 */
public class SensorTimeChart extends GraphicalView implements SensorGroup.DataSegment.EntryAddedListener {

    private static final int UPDATE_TIME = 500;

    private final TimeChart mTimeChart;
    private long mLastTime =0;

    public SensorTimeChart(Context context,XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer, SensorGroup.DataSegment dataSegment) {
              super(context, new TimeChart(dataset, renderer));
        mTimeChart = (TimeChart) getChart();
        mTimeChart.setDateFormat("H:mm:ss");
        dataSegment.addWeakEventListener(this);

        if(dataSegment.getEntries().size()>= SensorChannelSeries.LIMIT_LABELS_VISIBLE) {
            setPointSize(0);
        }
    }

    private void setPointSize(int size) {
        //Sadly we can not set the point size here without creating a new Graph.
        //This is because the TimeChart uses a ScatterChart which has a private size member which is initialized in the ctor.
        //So we use reflection to hack our way into the class
        try {
            Field f = ScatterChart.class.getDeclaredField("size");
            f.setAccessible(true);
            f.setInt(mTimeChart.getPointsChart(),size);
        } catch (Exception e){
            //no access :(
        }
    }

    @Override
    public void onEntryAdded(SensorGroup.DataSegment.EntryAddedEvent event) {

        //Check if we have exactly LIMIT_LABELS_VISIBLE visible data points
        if(((SensorGroup.DataSegment)event.getSource()).getEntries().size()==SensorChannelSeries.LIMIT_LABELS_VISIBLE) {
            setPointSize(0);
        }

        long curTime = System.currentTimeMillis();
        if((curTime-mLastTime) >= UPDATE_TIME) {
            mLastTime = curTime;
            repaint();
        }
    }
}
