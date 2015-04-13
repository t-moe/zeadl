package ch.bfh.android.zeadl.activity;

import org.achartengine.model.TimeSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.List;

import ch.bfh.android.zeadl.sensor.SensorChannel;
import ch.bfh.android.zeadl.sensor.SensorGroup;

/**
 * Created by timo on 4/13/15.
 */
public class SensorChannelSeries extends TimeSeries implements SensorGroup.DataSegment.EntryAddedListener{

    public static final int LIMIT_LABELS_VISIBLE = 50;
    public static final int LIMIT_DATASIZE = 300;


    final int mChInd;
    final XYSeriesRenderer mChannelRenderer;
    public SensorChannelSeries(SensorChannel channel, SensorGroup.DataSegment dataSegment, XYSeriesRenderer renderer) {
        super(channel.getName());
        mChInd = dataSegment.getChannels().indexOf(channel);
        mChannelRenderer = renderer;


        List<SensorGroup.DataSegment.Entry> entries = dataSegment.getEntries();
        if(entries.size()>=SensorChannelSeries.LIMIT_LABELS_VISIBLE) {
            renderer.setDisplayChartValues(false);
        }

        //Add available data
        int startInd = (entries.size()>LIMIT_DATASIZE) ? (entries.size()-LIMIT_DATASIZE): 0;

        for (int i=startInd; i<entries.size(); i++ ) {
            SensorGroup.DataSegment.Entry entry = entries.get(i);
            add(entry.getTime(),entry.getChannelData().get(mChInd));
        }

        dataSegment.addWeakEventListener(this); //to allow garbage collection of this object, if no longer needed

    }

    @Override
    public void onEntryAdded(SensorGroup.DataSegment.EntryAddedEvent event) {
        final SensorGroup.DataSegment.Entry entry =event.getEntry();

        //Check if we have exactly LIMIT_LABELS_VISIBLE visible data points
        if(((SensorGroup.DataSegment)event.getSource()).getEntries().size()== LIMIT_LABELS_VISIBLE) {
            mChannelRenderer.setDisplayChartValues(false);
        }

        while(getItemCount() > LIMIT_DATASIZE) {
            remove(0);
        }

        add(entry.getTime(), entry.getChannelData().get(mChInd));
    }
}
