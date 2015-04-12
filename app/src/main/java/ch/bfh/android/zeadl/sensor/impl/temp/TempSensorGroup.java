package ch.bfh.android.zeadl.sensor.impl.temp;

import android.os.Build;

import ch.bfh.android.zeadl.sensor.DisplayName;
import ch.bfh.android.zeadl.sensor.SensorGroup;
import ch.bfh.android.zeadl.sensor.impl.RandomChannel;

/**
 * Created by timo on 4/4/15.
 */


@DisplayName("Temperature Sensor")
public class TempSensorGroup  extends SensorGroup {
    public TempSensorGroup() {
        setSampleRate(getMaximalSampleRate());

        if(!Build.FINGERPRINT.startsWith("generic")) { //not in emulator
           // addChannel(new I2CTempChannel());
        }
        //addChannel(new RandomChannel("Ambient Temp (Rand)",10,30));
        //addChannel(new RandomChannel("Inner Temp (Rand)",20,25));
        addChannel(new I2CTempChannel());

    }

    @Override
    public String getUnit() {
         return "\u2103"; //grad celsisus
    }

    @Override
    public int getMaximalSampleRate() {
        return 3600*3; //3 samples per sec
    }
}
