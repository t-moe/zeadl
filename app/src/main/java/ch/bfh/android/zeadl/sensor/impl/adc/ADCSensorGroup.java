package ch.bfh.android.zeadl.sensor.impl.adc;

import ch.bfh.android.zeadl.sensor.DisplayName;
import ch.bfh.android.zeadl.sensor.SensorGroup;
import ch.bfh.android.zeadl.sensor.impl.RandomChannel;

/**
 * Created by kevin on 12.04.15.
 */
@DisplayName("ADC Sensor")
public class ADCSensorGroup extends SensorGroup {

    public ADCSensorGroup() {
        setSampleRate(getMaximalSampleRate());
        addChannel(new RandomChannel("ADC rand",0,5));
    }

    public String getUnit() {
        return "V";
    }

    public int getMaximalSampleRate() {
        return 12;
    }
}