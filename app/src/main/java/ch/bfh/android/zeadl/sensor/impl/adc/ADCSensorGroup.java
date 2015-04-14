package ch.bfh.android.zeadl.sensor.impl.adc;

import ch.bfh.android.zeadl.sensor.DisplayName;
import ch.bfh.android.zeadl.sensor.SensorGroup;

/**
 * Created by kevin on 12.04.15.
 */
@DisplayName("ADC Sensor")
public class ADCSensorGroup extends SensorGroup {

    public ADCSensorGroup() {
        setSampleRate(getMaximalSampleRate());
        addChannel(new FsAdcChannel(FsAdcChannel.ADCChannelSrc.ADC0));
        addChannel(new FsAdcChannel(FsAdcChannel.ADCChannelSrc.ADC1));
        addChannel(new FsAdcChannel(FsAdcChannel.ADCChannelSrc.ADC2));
        addChannel(new FsAdcChannel(FsAdcChannel.ADCChannelSrc.ADC3));
        addChannel(new FsAdcChannel(FsAdcChannel.ADCChannelSrc.POTI));
    }

    public String getUnit() {
        return "%";
    }

    public int getMaximalSampleRate() {
        return 3600; //1 sample per sec
    }
}
