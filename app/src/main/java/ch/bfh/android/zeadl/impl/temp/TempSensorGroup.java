package ch.bfh.android.zeadl.impl.temp;

import ch.bfh.android.zeadl.DisplayName;
import ch.bfh.android.zeadl.SensorGroup;

/**
 * Created by timo on 4/4/15.
 */


@DisplayName("Temperator Sensor")
public class TempSensorGroup  extends SensorGroup {
    TempSensorGroup() {
        addChannel(new I2CTempChannel());
    }

    @Override
    public String getUnit() {
         return "\u2103"; //grad celsisus
    }
}
