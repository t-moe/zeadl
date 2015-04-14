package ch.bfh.android.zeadl.sensor.impl.color;

import android.graphics.Color;

import java.nio.ByteBuffer;

import ch.bfh.android.zeadl.sensor.SensorChannel;

/**
 * Created by kevin on 12.04.15.
 */
public class I2CColorChannel extends SensorChannel {

    public static enum ChannelColor {GREEN,BLUE,RED,CLEAR};

    private I2CColorSensor Sensor;
    private ChannelColor Channel;

    private byte[] i2cCommBuffer_temp = new byte[16];

    // Bit mask for command transmissions
    private static final byte TCS3414_CMD_BIT = (byte)(0x01 << 7);
    // TCS3414 internal Register
    private static final byte TCS3414_GL_REG    =       0x10;
    private static final byte TCS3414_GH_REG    =       0x11;
    private static final byte TCS3414_RL_REG    =       0x12;
    private static final byte TCS3414_RH_REG    =       0x13;
    private static final byte TCS3414_BL_REG    =       0x14;
    private static final byte TCS3414_BH_REG    =       0x15;
    private static final byte TCS3414_CL_REG    =       0x16;
    private static final byte TCS3414_CH_REG    =       0x17;

    // register address for the chosen color
    private byte COL_REG_L;
    private byte COL_REG_H;

    public I2CColorChannel(I2CColorSensor Sensor, ChannelColor Channel) {

        this.Sensor = Sensor;
        this.Channel = Channel;

        switch (Channel) {
            case GREEN:
                COL_REG_L = TCS3414_GL_REG;
                COL_REG_H = TCS3414_GH_REG;
                setColor(Color.GREEN);
                break;
            case BLUE:
                COL_REG_L = TCS3414_BL_REG;
                COL_REG_H = TCS3414_BH_REG;
                setColor(Color.BLUE);
                break;
            case RED:
                COL_REG_L = TCS3414_RL_REG;
                COL_REG_H = TCS3414_RH_REG;
                setColor(Color.RED);
                break;
            case CLEAR:
                COL_REG_L = TCS3414_CL_REG;
                COL_REG_H = TCS3414_CH_REG;
                setColor(Color.BLACK);
                break;
            default:
                COL_REG_L = TCS3414_CL_REG;
                COL_REG_H = TCS3414_CH_REG;
                setColor(Color.BLACK);
                break;
        }
    }

    public float getSample() {
        byte colbuff[] = new byte[2];
        i2cCommBuffer_temp[0] = (byte)(COL_REG_L | TCS3414_CMD_BIT);
        Sensor.write(i2cCommBuffer_temp,1);
        Sensor.read(i2cCommBuffer_temp,1);
        colbuff[0] = i2cCommBuffer_temp[0];

        i2cCommBuffer_temp[0] = (byte)(COL_REG_H | TCS3414_CMD_BIT);
        Sensor.write(i2cCommBuffer_temp,1);
        Sensor.read(i2cCommBuffer_temp,1);
        colbuff[1] = i2cCommBuffer_temp[0];

        // TODO avoid negative values
        short color = ByteBuffer.wrap(colbuff, 0, 2).getShort();

        return color;
    }

    public String getName() {
        String Name;
        switch (Channel) {
            case GREEN:
                Name = "Green";
                break;
            case BLUE:
                Name = "Blue";
                break;
            case RED:
                Name = "Red";
                break;
            case CLEAR:
                Name = "Clear";
                break;
            default:
                Name = "undef";
                break;
        }
        return Name;
    }

    public void start() {
        Sensor.start();
    }

    public void stop() {
        Sensor.stop();
    }
}
