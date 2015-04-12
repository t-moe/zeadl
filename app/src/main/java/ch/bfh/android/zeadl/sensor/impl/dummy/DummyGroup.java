package ch.bfh.android.zeadl.sensor.impl.dummy;

import ch.bfh.android.zeadl.sensor.DisplayName;
import ch.bfh.android.zeadl.sensor.SensorGroup;

/**
 * Created by kevin on 12.04.15.
 */
@DisplayName("Dummy Sensor")
public class DummyGroup extends SensorGroup {

    public DummyGroup() {
        setSampleRate(getMaximalSampleRate());
        addChannel(new RandomChannel("noise rand",5,100));
        addChannel(new RandomChannel("rand rand",9,150));
    }

    public String getUnit() {
        return "apples";
    }

    public int getMaximalSampleRate() {
        return 3600/5; //1 sample every 5 secs;
    }
}
