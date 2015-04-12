package ch.bfh.android.zeadl.sensor.impl.noise;

import ch.bfh.android.zeadl.sensor.DisplayName;
import ch.bfh.android.zeadl.sensor.SensorGroup;
import ch.bfh.android.zeadl.sensor.impl.RandomChannel;

/**
 * Created by kevin on 12.04.15.
 */
@DisplayName("Noise Sensor")
public class NoiseSensorGroup extends SensorGroup {

    public NoiseSensorGroup() {
        setSampleRate(getMaximalSampleRate());
        addChannel(new RandomChannel("noise rand",5,100));
    }

    public String getUnit() {
        return "dB";
    }

    public int getMaximalSampleRate() {
        return 3600/5; //1 sample every 5 secs;
    }
}
