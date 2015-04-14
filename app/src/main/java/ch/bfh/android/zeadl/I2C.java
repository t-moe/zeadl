/*
 ***************************************************************************
 * \brief   Embedded Android I2C Exercise 5.2
 *	        Native basic i2c communication interface
 *	        Only a minimal error handling is implemented.
 * \file    I2C.java
 * \version 1.0
 * \date    06.03.2014
 * \author  Martin Aebersold
 *
 * \remark  Last Modifications:
 * \remark  V1.0, AOM1, 06.03.2014
 * \remark  V2.0, lati1, 07,04.2015
 ***************************************************************************
 */

package ch.bfh.android.zeadl;

import java.io.IOException;

/***************************************************************************
 * This is an I2C operation class
 **************************************************************************/

public class I2C
{

    private int mFileHandle;
    private String mDeviceName;
    private int mI2CAdress;
    public I2C(String devicename, int i2c_address) {
        mFileHandle= -1;
        mDeviceName = devicename;
        mI2CAdress = i2c_address;
    }


    /**

     */
    public native boolean open();

    /**
     * @param buffer
     * @param length
     *
     * @return Number of bytes read
     */
    public native int read(byte buffer[], int length);

    /**
     * @param buffer
     * @param length
     *
     * @return Number of bytes written
     */
    public native int write(byte buffer[], int length);

    /**
     *
     * @return -
     */
    public native void close();

    static
    {
        System.loadLibrary("i2c");
    }
}