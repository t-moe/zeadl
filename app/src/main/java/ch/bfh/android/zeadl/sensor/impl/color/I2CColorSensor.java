package ch.bfh.android.zeadl.sensor.impl.color;

import java.nio.ByteBuffer;

import ch.bfh.android.zeadl.I2C;
import ch.bfh.android.zeadl.sensor.SensorChannel;

/**
 * Created by kevin on 12.04.15.
 */
public class I2CColorSensor {

    //
    private static final String TCS3414_FILE_NAME = "/dev/i2c-3";
    private static final byte TCS3414_I2C_ADDR = 0x39;

    // Bit mask for command transmissions
    private static final byte TCS3414_CMD_BIT = (byte)(0x01 << 7);
    // Bit masks for the CTRL Register
    private static final byte TCS3414_ADCEN_BIT = (0x01 << 1);
    private static final byte TCS3414_POWER_BIT = (0x01 << 0);

    // TCS3414 internal Register
    private static final byte TCS3414_CTRL_REG  =       0x00;
    private static final byte TCS3414_ID_REG    = (byte)0x04;

    private boolean ready = false;
    private I2C i2c_tcs;
    private byte[] i2cCommBuffer_temp = new byte[16];

    public void start() {
        if (ready) {
            return;
        }
        i2c_tcs  = new I2C(TCS3414_FILE_NAME,TCS3414_I2C_ADDR);
        if(!i2c_tcs.open()) {
            return; //TODO handle error
        }

        i2cCommBuffer_temp[0] = TCS3414_CMD_BIT | TCS3414_CTRL_REG;     // write to Control Register
        i2cCommBuffer_temp[1] = TCS3414_ADCEN_BIT | TCS3414_POWER_BIT;  // enable ADC and Power
        i2c_tcs.write(i2cCommBuffer_temp,2);
        ready= true;
    }

    public void stop() {
        i2c_tcs.close();
        ready=false;
    }

    public int read(byte[] buffer,int length) {
        return i2c_tcs.read(buffer,length);
    }

    public int write(byte[] buffer,int length) {
        return i2c_tcs.write(buffer,length);
    }
}
