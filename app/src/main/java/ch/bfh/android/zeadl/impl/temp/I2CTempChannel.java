package ch.bfh.android.zeadl.impl.temp;

import java.io.IOException;
import java.nio.ByteBuffer;

import ch.bfh.android.zeadl.I2C;
import ch.bfh.android.zeadl.SensorChannel;

/**
 * Created by timo on 3/24/15.
 */
public class I2CTempChannel extends SensorChannel {

  /* MCP9800 Register pointers */
  private static final char MCP9800_TEMP   = 0x00;      /* Ambient Temperature Register */
  private static final char MCP9800_CONFIG = 0x01;      /* Sensor Configuration Register */

  /* Sensor Configuration Register Bits */
  private static final char MCP9800_12_BIT = 0x60;

  /* i2c Address of MCP9802 device */
  private static final char MCP9800_I2C_ADDR = 0x48;

  /* i2c device file name */
  private static final String MCP9800_FILE_NAME = "/dev/i2c-3";

   private I2C i2c_temp;
   private  byte[] i2cCommBuffer_temp = new byte[16];
   private boolean ready = false;

    @Override
    public String getName() {
        return "Board Temp";
    }

    @Override
    public void start() {
       i2c_temp  = new I2C(MCP9800_FILE_NAME,MCP9800_I2C_ADDR);
        if(!i2c_temp.open()) {
            return; //TODO handle error
        }
        ready= true;
    }

    @Override
    public void stop() {
        i2c_temp.close();
        ready=false;
    }

    @Override
    public float getSample() {
         if(!ready) throw new IllegalStateException();

          /* Setup i2c buffer for the configuration register */
         i2cCommBuffer_temp[0] = MCP9800_CONFIG;
         i2cCommBuffer_temp[1] = MCP9800_12_BIT;
         int status = i2c_temp.write(i2cCommBuffer_temp, 2);

        /* Setup mcp9800 register to read the temperature */
         i2cCommBuffer_temp[0] =MCP9800_TEMP;
         i2c_temp.write(i2cCommBuffer_temp, 1);

        /* Read the current temperature from the mcp9800 device */
         i2c_temp.read(i2cCommBuffer_temp, 2);

        /* Assemble the temperature values */
         int temp =ByteBuffer.wrap(i2cCommBuffer_temp,0,2).getShort();
         //int temp = ((((int)i2cCommBuffer_temp[0] & 0xFF) << 8) | ((int)i2cCommBuffer_temp[1]& 0xFF));
         temp = temp >> 4;

        /* Convert current temperature to float */
         float tempC = 1.0f * temp * 0.0625f;

         return tempC;
    }
}
