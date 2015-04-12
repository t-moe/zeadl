package ch.bfh.android.zeadl.sensor.impl.color;

import ch.bfh.android.zeadl.sensor.DisplayName;
import ch.bfh.android.zeadl.sensor.SensorGroup;


/**
 * Created by kevin on 11.04.15.
 */
@DisplayName("Color Sensor")
public class ColorSensorGroup extends SensorGroup {

    public ColorSensorGroup() {
        setSampleRate(getMaximalSampleRate());

        // Create a new Sensor instance
        I2CColorSensor Sensor = new I2CColorSensor();

        addChannel(new I2CColorChannel(Sensor, I2CColorChannel.ChannelColor.BLUE));
        addChannel(new I2CColorChannel(Sensor, I2CColorChannel.ChannelColor.RED));
        addChannel(new I2CColorChannel(Sensor, I2CColorChannel.ChannelColor.GREEN));
        addChannel(new I2CColorChannel(Sensor, I2CColorChannel.ChannelColor.CLEAR));
    }

    public String getUnit() {
        return "Candela??";
    }

    public int getMaximalSampleRate() {
        return 5;
    }

}
