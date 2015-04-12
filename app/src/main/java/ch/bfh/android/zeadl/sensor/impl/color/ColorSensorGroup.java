package ch.bfh.android.zeadl.sensor.impl.color;

import ch.bfh.android.zeadl.sensor.DisplayName;
import ch.bfh.android.zeadl.sensor.SensorGroup;
import ch.bfh.android.zeadl.sensor.impl.RandomChannel;


/**
 * Created by kevin on 11.04.15.
 */
@DisplayName("Color Sensor")
public class ColorSensorGroup extends SensorGroup {

    public ColorSensorGroup() {
        setSampleRate(getMaximalSampleRate());
        //addChannel(new RandomChannel("Ambient Temp (Rand)",10,30));
        addChannel(new RandomChannel("Color (Rand)",20,25));


    }

    public String getUnit() {
        return "Candela??";
    }

    public int getMaximalSampleRate() {
        return 5;
    }

}
