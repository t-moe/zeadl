package ch.bfh.android.zeadl.impl.temp;

import ch.bfh.android.zeadl.DisplayName;
import ch.bfh.android.zeadl.SensorGroup;
import ch.bfh.android.zeadl.impl.RandomChannel;

/**
 * Created by timo on 4/4/15.
 */


@DisplayName("Temperatur Sensor")
public class TempSensorGroup  extends SensorGroup {
    public TempSensorGroup() {
        //addChannel(new I2CTempChannel());
        addChannel(new RandomChannel("Ambient Temp (Rand)",10,30));
        addChannel(new RandomChannel("Inner Temp (Rand)",20,25));
    }

    @Override
    public String getUnit() {
         return "\u2103"; //grad celsisus
    }
}
