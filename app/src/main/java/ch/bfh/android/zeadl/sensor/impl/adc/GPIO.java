package ch.bfh.android.zeadl.sensor.impl.adc;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by kevin on 12.04.15.
 */
public class GPIO {

    /*
     * GPIO Numbers for LED and Button
     */
    private final static int LED_L1 = 61;
    private final static int LED_L2 = 44;
    private final static int LED_L3 = 68;
    private final static int LED_L4 = 67;
    private final static int BUTTON_T1 = 49;
    private final static int BUTTON_T2 = 112;
    private final static int BUTTON_T3 = 51;
    private final static int BUTTON_T4 = 7;

    /*
     * ADC Channels
     */
    public static final ADCChannel ADC_CH0 = new ADCChannel(0);
    public static final ADCChannel ADC_CH1 = new ADCChannel(1);
    public static final ADCChannel ADC_CH2 = new ADCChannel(2);
    public static final ADCChannel ADC_CH3 = new ADCChannel(3);
    public static final ADCChannel ADC_CH4 = new ADCChannel(4);
    public static final ADCChannel ADC_POTI = new ADCChannel(4);
    public static final ADCChannel ADC_CH5 = new ADCChannel(5);
    public static final ADCChannel ADC_CH6 = new ADCChannel(6);
    public static final ADCChannel ADC_CH7 = new ADCChannel(7);
    /*
     * LED
     */
    public static final LED LED1 = new LED(LED_L1);
    public static final LED LED2 = new LED(LED_L2);
    public static final LED LED3 = new LED(LED_L3);
    public static final LED LED4 = new LED(LED_L4);
    /*
     * Button
     */
    public static final BUTTON BUTTON1 = new BUTTON(BUTTON_T1);
    public static final BUTTON BUTTON2 = new BUTTON(BUTTON_T2);
    public static final BUTTON BUTTON3 = new BUTTON(BUTTON_T3);
    public static final BUTTON BUTTON4 = new BUTTON(BUTTON_T4);



    public static class ADCChannel {
        private RandomAccessFile r;

        private static final String SYSFS_ADC = "/sys/bus/iio/devices/iio:device0/in_voltage";

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

    /*
     * Button class
     */
    public static class BUTTON {
        private RandomAccessFile f;

        // path to GPIO
        private static final String BUTTpath = "/sys/class/gpio/gpio";

        /*
         * constructor
         */
        public BUTTON(int gpionumber) {
            try {
                // try to open GPIO pin
                f = new RandomAccessFile(BUTTpath + gpionumber + "/value","r");
            }
            catch (IOException e) {
                e.getStackTrace();
            }
        }

        /*
         * read value of a GPIO
         */
        public int getValue() {
            try {
                f.seek(0);
                int i = Integer.decode(f.readLine());
                return i;
            }
            catch (IOException e) {
                e.getStackTrace();
                return -1;
            }
        }

        /*
         * check if the button is pressed
         */
        public boolean isPressed() {
            return getValue()==0?true:false;
        }
    }


    /*
     * LED class
     */
    public static class LED {
        private FileWriter f;

        // path to GPIO
        private static final String LEDpath = "/sys/class/gpio/gpio";

        public LED (int gpioNumber) {

            try {
                f = new FileWriter(LEDpath + gpioNumber + "/value");
            }
            catch (IOException e) {
                e.getStackTrace();
            }
        }

        public void setValue(int value) {

            char state = value==1?'1':'0';

            try {
                f.write(state);
                f.flush();
            }
            catch (IOException e) {
                e.getStackTrace();
            }
        }

        public void ON() {
            setValue(0);
        }

        public void OFF() {
            setValue(1);
        }
    }
}
