package ch.bfh.android.zeadl.sensor.impl.gravity;

/**
 * Created by kevin on 11.04.15.
 */
public class I2CGSensChannelY extends I2CGSensChannel {

    private static final char LIS302DL_I2C_Y_REG = 0x2B;
    
    public float getSample() {

        if(!ready) throw new IllegalStateException();

          /* Setup i2c buffer for the configuration register */
        i2cCommBuffer_temp[0] = LIS302DL_I2C_Y_REG;
        int status = i2c_lis.write(i2cCommBuffer_temp, 2);

        /* Read the current acceleration from the LIS302DL device */
        i2c_lis.read(i2cCommBuffer_temp, 1);

        float YForce;
        /* Convert current acceleration to float */
        if (fullScale) {
            YForce = LIS302DL_CAL_FS * i2cCommBuffer_temp[0];
        } else {
            YForce = LIS302DL_CAL_NFS * i2cCommBuffer_temp[0];
        }

        return YForce;
    }

    public String getName() {
        return "GSensY";
    }
}
