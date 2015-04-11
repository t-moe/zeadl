package ch.bfh.android.zeadl;

import android.app.Activity;
import android.content.Context;
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
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;

/**
 * Created by timo on 4/11/15.
 */
public class LazyMainListViewAdapter extends ArrayAdapter<SensorGroup> implements SensorGroupController.UpdateListener {
    private Activity activity;
    private LayoutInflater inflater=null;
    private List lis;

    public LazyMainListViewAdapter(Activity a) {
        super(a,R.layout.list_row, SensorGroupController.getActiveGroups());
        lis= SensorGroupController.getActiveGroups();
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SensorGroupController.addEventListener(this);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView title = (TextView)vi.findViewById(R.id.title);
        TextView channels = (TextView)vi.findViewById(R.id.txtChannels);
        TextView samplerate = (TextView)vi.findViewById(R.id.txtSampleRate);

        if(getCount()==0) {
            Log.w("LazyMainListViewAdapter","Unexpected empty list");
            return null;
        }
        SensorGroup sensorGroup = getItem(position);

        title.setText(sensorGroup.getName());
        channels.setText(sensorGroup.getActiveChannels().size()+" Channels");
        samplerate.setText(sensorGroup.getSampleRate()+ " Samples/sec");


        final XYMultipleSeriesDataset mDataset;
        XYMultipleSeriesRenderer mRenderer;
        LinearLayout layout = (LinearLayout) vi.findViewById(R.id.graphview);

        // create dataset and renderer
        mDataset = new XYMultipleSeriesDataset();
        mRenderer = new XYMultipleSeriesRenderer();

        //Colors
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.LTGRAY);
        mRenderer.setMarginsColor(Color.parseColor("#fff3f3f3")); //background_holo_light
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


        //mRenderer.setChartTitle(tempGroup.getName());

        final SensorGroup.DataSegment dataSegment = sensorGroup.getLastDataSegment();
        int chInd=0;
        for(SensorChannel channel : dataSegment.getChannels()) {
            TimeSeries channelSeries = new TimeSeries(channel.getName());

            for (SensorGroup.DataSegment.Entry entry:dataSegment.getEntries() ) {
                channelSeries.add(entry.getTime(),entry.getChannelData().get(chInd));
            }

            mDataset.addSeries(channelSeries);
            chInd++;
        }


        mRenderer.setXTitle("time");
        mRenderer.setYTitle(sensorGroup.getUnit());


        final GraphicalView mChartView = ChartFactory.getTimeChartView(activity, mDataset, mRenderer,
                "H:mm:ss");


        dataSegment.addEventListener(new SensorGroup.DataSegment.EntryAddedListener() {
            @Override
            public void onEntryAdded(SensorGroup.DataSegment.EntryAddedEvent event) {
                SensorGroup.DataSegment.Entry entry = event.getEntry();

                for(int chInd=0; chInd<dataSegment.getChannels().size();chInd++) {
                    TimeSeries channelSeries = (TimeSeries)mDataset.getSeries()[chInd];
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
