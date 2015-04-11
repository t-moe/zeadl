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
public class MainListViewAdapter extends ArrayAdapter<SensorGroup> implements SensorGroupController.UpdateListener {
    private Activity activity;
    private LayoutInflater inflater=null;

    private final int limit_labels_visible = 50;
    private final int limit_datasize = 300;

    public MainListViewAdapter(Activity a) {
        super(a, R.layout.main_list_row, SensorGroupController.getActiveGroups());
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SensorGroupController.addEventListener(this);
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
        info.setText(sensorGroup.getSampleRate()+" Samples/sec in " +sensorGroup.getUnit());


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
        mRenderer.setMargins(new int[]{dpToPx(12), dpToPx(18), marginBottom, dpToPx(6)});
        mRenderer.setYLabelsAlign(Paint.Align.RIGHT, 0);
        mRenderer.setYLabelsPadding(dpToPx(5));

        mRenderer.setPointSize(dpToPx(3));
        mRenderer.setClickEnabled(false);
        mRenderer.setPanEnabled(false,false);
        mRenderer.setZoomEnabled(false, false);
        mRenderer.setShowGridY(true);



        final SensorGroup.DataSegment dataSegment = sensorGroup.getLastDataSegment();
        int chInd=0;
        for(SensorChannel channel : dataSegment.getChannels()) {

            XYSeriesRenderer chRenderer = new XYSeriesRenderer();
            chRenderer.setColor(channel.getColor());
            chRenderer.setPointStyle(PointStyle.CIRCLE);
            chRenderer.setFillPoints(true);
            chRenderer.setLineWidth(dpToPx(2));
            chRenderer.setDisplayChartValues(true);
            chRenderer.setDisplayChartValuesDistance(dpToPx(30));
            chRenderer.setChartValuesTextSize(dpToPx(8));
            chRenderer.setChartValuesFormat(NumberFormat.getInstance());
            chRenderer.setChartValuesSpacing(dpToPx(5));
            mRenderer.addSeriesRenderer(chRenderer);

            TimeSeries channelSeries = new TimeSeries(channel.getName());
            List<SensorGroup.DataSegment.Entry> entries = dataSegment.getEntries();

            for (SensorGroup.DataSegment.Entry entry:entries ) {
                channelSeries.add(entry.getTime(),entry.getChannelData().get(chInd));
            }

            if(entries.size()>=limit_labels_visible) {
                mRenderer.setPointSize(0);
                chRenderer.setDisplayChartValues(false);
            }

            mDataset.addSeries(channelSeries);
            chInd++;
        }


        final GraphicalView mChartView = ChartFactory.getTimeChartView(activity, mDataset, mRenderer,
                "H:mm:ss");

        dataSegment.addEventListener(new SensorGroup.DataSegment.EntryAddedListener() {
            @Override
            public void onEntryAdded(SensorGroup.DataSegment.EntryAddedEvent event) {
                SensorGroup.DataSegment.Entry entry = event.getEntry();

                //Check if we have exactly limit_labels_visible visible data points
                if(((SensorGroup.DataSegment)event.getSource()).getEntries().size()==limit_labels_visible) {
                    //Sadly we can not set the point size to 0 here without creating a new Graph.
                    //This is because the TimeChart uses a ScatterChart which has a private size member which is initialized in the ctor.
                    //So we use reflection to hack our way into the class
                    try {
                        Field f = ScatterChart.class.getDeclaredField("size");
                        f.setAccessible(true);
                        f.setInt(((TimeChart) mChartView.getChart()).getPointsChart(),0);
                    } catch (Exception e){
                        //no access :(
                    }

                    for(int chInd=0; chInd<dataSegment.getChannels().size();chInd++) {
                        ((XYSeriesRenderer) mRenderer.getSeriesRendererAt(chInd)).setDisplayChartValues(false);
                    }
                }

                for(int chInd=0; chInd<dataSegment.getChannels().size();chInd++) {
                    TimeSeries channelSeries = (TimeSeries)mDataset.getSeries()[chInd];

                    while(channelSeries.getItemCount() > limit_datasize) {
                        channelSeries.remove(0);
                    }

                    channelSeries.add(entry.getTime(),entry.getChannelData().get(chInd));
                }

                //mRenderer.setPanLimits(new double[]{startTime.getTime(), d.getTime() + 1000, 0, 60});
                //mRenderer.setZoomLimits(new double[]{startTime.getTime(),d.getTime(),0,0});

                mChartView.repaint();
            }
        });

        layout.addView(mChartView);

        return vi;
    }

    @Override
    public void onActiveGroupsChanged(SensorGroupController.ActiveGroupsChangedEvent event) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });

    }
}
