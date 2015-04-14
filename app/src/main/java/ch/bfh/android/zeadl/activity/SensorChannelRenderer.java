package ch.bfh.android.zeadl.activity;

import org.achartengine.renderer.XYSeriesRenderer;

import ch.bfh.android.zeadl.sensor.SensorChannel;
import ch.bfh.android.zeadl.sensor.SensorGroup;

/**
 * Created by timo on 4/13/15.
 */
public class SensorChannelRenderer extends XYSeriesRenderer implements SensorChannel.UpdateListener {


    public SensorChannelRenderer(SensorChannel channel) {
        channel.addWeakEventListener(this); //to allow garbage collection of this object, if no longer needed
        setColor(channel.getColor());
    }

    @Override
    public void onColorChanged(SensorChannel.ColorChangedEvent event) {
        setColor(event.getNewColor());
    }
}
