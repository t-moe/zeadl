package ch.bfh.android.zeadl.impl.gravitydummy;

import ch.bfh.android.zeadl.DisplayName;
import ch.bfh.android.zeadl.SensorGroup;
import ch.bfh.android.zeadl.impl.RandomChannel;

/**
 * Created by timo on 4/11/15.
 */

@DisplayName("Gravity Sensor")
public class GravityDummyGroup extends SensorGroup {
    public GravityDummyGroup() {
        setSampleRate(getMaximalSampleRate());
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
        return 314;
    }
}
