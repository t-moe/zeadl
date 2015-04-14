package ch.bfh.android.zeadl.sensor.impl.gravity;

import ch.bfh.android.zeadl.sensor.DisplayName;
import ch.bfh.android.zeadl.sensor.SensorGroup;

/**
 * Created by timo on 4/11/15.
 */

@DisplayName("Gravity Sensor")
public class GravitySensorGroup extends SensorGroup {
    public GravitySensorGroup() {
        setSampleRate(3600);
        addChannel(new I2CGSensChannelX());
        addChannel(new I2CGSensChannelY());
        addChannel(new I2CGSensChannelZ());
    }

    @Override
    public String getUnit() {
        return "G";
        //return "m/s²";
    }

    @Override
    public int getMaximalSampleRate() {
        return 3600*10; //10 sample every secs;
    }
}
