package ch.bfh.android.zeadl.impl.gravitydummy;

import ch.bfh.android.zeadl.I2C;
import ch.bfh.android.zeadl.SensorChannel;

/**
 * Created by kevin on 11.04.15.
 */
public abstract class I2CGSensChannel extends SensorChannel {

    private static final char LIS302DL_I2C_ADDR = 0x1C;

    /* i2c device file name */
    private static final String LIS302DL_FILE_NAME = "/dev/i2c-3";

    private static final char LIS302DL_I2C_DUMMY_REG = 0x0F;

    // LIS302DL Control Register
    private static final char LIS302DL_I2C_CTRL_REG1 = 0x20;
    private static final char LIS302DL_I2C_CTRL_REG2 = 0x21;
    private static final char LIS302DL_I2C_CTRL_REG3 = 0x22;

    // CTRL_REG1 Bits
    private static final char LIS302DL_CTRL_REG1_PD = 0x40;     // Power Down Bit
    private static final char LIS302DL_CTRL_REG1_XEN = 0x01;    // Enable X axis Bit
    private static final char LIS302DL_CTRL_REG1_YEN = 0x02;    // Enable Y axis Bit
    private static final char LIS302DL_CTRL_REG1_ZEN = 0x04;    // Enable Z axis Bit

    // Calibration values in G
    protected static final float LIS302DL_CAL_NFS = 18/1000f;      // non full scale mode
    protected static final float LIS302DL_CAL_FS = 72/1000f;       // full scale mode

    protected static I2C i2c_lis;
    protected static byte[] i2cCommBuffer_temp = new byte[16];
    protected static boolean ready = false;
    protected static boolean fullScale = false;

    public void start() {
        if (ready) {
            return;
        }
        i2c_lis  = new I2C(LIS302DL_FILE_NAME,LIS302DL_I2C_ADDR);
        if(!i2c_lis.open()) {
            return; //TODO handle error
        }

        i2cCommBuffer_temp[0] = LIS302DL_I2C_CTRL_REG1;
        i2cCommBuffer_temp[1] = LIS302DL_CTRL_REG1_PD | LIS302DL_CTRL_REG1_XEN | LIS302DL_CTRL_REG1_YEN | LIS302DL_CTRL_REG1_ZEN;
        i2c_lis.write(i2cCommBuffer_temp,2);
        ready= true;
    }

    public void stop() {
        i2c_lis.close();
        ready=false;
    }
}
