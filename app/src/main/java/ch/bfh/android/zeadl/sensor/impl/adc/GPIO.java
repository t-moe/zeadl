package ch.bfh.android.zeadl.sensor.impl.adc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by kevin on 12.04.15.
 */
public class GPIO {

    public static final ADCChannel ADC_CH0 = new ADCChannel(0);
    public static final ADCChannel ADC_CH1 = new ADCChannel(1);
    public static final ADCChannel ADC_CH2 = new ADCChannel(2);
    public static final ADCChannel ADC_CH3 = new ADCChannel(3);
    public static final ADCChannel ADC_CH4 = new ADCChannel(4);
    public static final ADCChannel ADC_POTI = ADC_CH4;

    private static final String SYSFS_ADC = "/sys/bus/iio/devices/iio:device0/in_voltage";

    public static class ADCChannel {
        private RandomAccessFile r;

        public ADCChannel(int adc_channel) {
            try {
                String path = SYSFS_ADC + adc_channel + "_raw";
                r = new RandomAccessFile(path,"r");
            }
            catch (FileNotFoundException e) {
                e.getStackTrace();
            }
        }

        public int getValue() {
            try {
                r.seek(0);
                int i = Integer.decode(r.readLine());
                return i;
            }
            catch (IOException e) {
                e.getStackTrace();
                return -1;
            }
        }
    }
}
