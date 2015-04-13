package ch.bfh.android.zeadl.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.ScatterChart;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.List;

import ch.bfh.android.zeadl.R;
import ch.bfh.android.zeadl.sensor.SensorChannel;
import ch.bfh.android.zeadl.sensor.SensorGroup;
import ch.bfh.android.zeadl.sensor.SensorGroupController;

/**
 * Created by timo on 4/11/15.
 */
public class MainListViewAdapter extends ArrayAdapter<SensorGroup>
        implements SensorGroupController.UpdateListener, SensorGroup.UpdateListener {
    private Activity activity;
    private LayoutInflater inflater=null;



    public MainListViewAdapter(Activity a) {
        super(a, R.layout.main_list_row, SensorGroupController.getActiveGroups());
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SensorGroupController.addEventListener(this); //TODO: remove listener at some point?
    }

    public static int dpToPx(float dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.main_list_row, null);

        TextView title = (TextView)vi.findViewById(R.id.txtTitle);
        TextView info = (TextView)vi.findViewById(R.id.txtInfo);

        if(getCount()==0) {
            Log.w("LazyMainListViewAdapter","Unexpected empty list");
            return null;
        }
        SensorGroup sensorGroup = getItem(position);

        title.setText(sensorGroup.getName());
        int sampleRate= sensorGroup.getSampleRate();
        String sampleRateText;
        if(sampleRate>=3600) {
            sampleRateText = sampleRate/3600.0 + " Samples/sec";
        } else if( sampleRate>=60) {
            sampleRateText = sampleRate/60.0 + " Samples/min";
        } else {
            sampleRateText = sampleRate + " Samples/hour";
        }

        info.setText(sampleRateText+ " in " +sensorGroup.getUnit());

        reCreateGraph(vi,sensorGroup);

        return vi;
    }


    private void reCreateGraph(View vi, SensorGroup sensorGroup) {

        final LinearLayout layout = (LinearLayout) vi.findViewById(R.id.graphview);
        layout.removeAllViews();

        // create dataset and renderer
        final XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
        final XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

        //Colors
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.argb(0,1,1,1));
        mRenderer.setMarginsColor(Color.argb(0,1,1,1)); //transparent
        mRenderer.setXLabelsColor(Color.DKGRAY);
        mRenderer.setYLabelsColor(0,Color.DKGRAY);
        mRenderer.setLabelsColor(Color.BLACK);
        mRenderer.setGridColor(Color.GRAY);

        //TextSizes
        mRenderer.setLabelsTextSize(dpToPx(8));
        mRenderer.setLegendTextSize(dpToPx(8));

        //Spacings
        float density = Resources.getSystem().getDisplayMetrics().density;

        int marginBottom = dpToPx(8);
        if(density<=1.5) { //beagle bone
            mRenderer.setLegendHeight(dpToPx(27));
            marginBottom= dpToPx(-3);
        }
        mRenderer.setMargins(new int[]{dpToPx(12), dpToPx(30), marginBottom, dpToPx(6)});
        mRenderer.setYLabelsAlign(Paint.Align.RIGHT, 0);
        mRenderer.setYLabelsPadding(dpToPx(5));

        mRenderer.setPointSize(dpToPx(3));
        mRenderer.setClickEnabled(false);
        mRenderer.setPanEnabled(false,false);
        mRenderer.setZoomEnabled(false, false);
        mRenderer.setShowGridY(true);



        final SensorGroup.DataSegment dataSegment = sensorGroup.getLastDataSegment();
        for(SensorChannel channel : dataSegment.getChannels()) {

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

            TimeSeries channelSeries = new SensorChannelSeries(channel,dataSegment,chRenderer);
            mDataset.addSeries(channelSeries);
        }

        final GraphicalView mChartView =  new SensorTimeChart(activity, mDataset, mRenderer,dataSegment);
        layout.addView(mChartView);
    }

    @Override
    public void onActiveGroupsChanged(SensorGroupController.ActiveGroupsChangedEvent event) {
        if(event.getType()== SensorGroupController.ActiveGroupsChangedEvent.Type.GROUP_ACTIVATED) {
            event.getGroup().addEventListener(this);
        } else {
            event.getGroup().removeEventListener(this);
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onDataSegmentAdded(SensorGroup.DataSegmentAddedEvent event) {
        //SensorGroup sensorGroup = (SensorGroup)event.getSource();
        //TODO: Change this to only update the changed group
        //See also: http://stackoverflow.com/questions/3724874/how-can-i-update-a-single-row-in-a-listview
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onActiveChannelsChanged(SensorGroup.ActiveChannelsChangedEvent event) {
        //Not needed
    }

    @Override
    public void onSampleRateChanged(SensorGroup.SampleRateChangedEvent event) {
        //Not needed
    }
}
