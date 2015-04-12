package ch.bfh.android.zeadl.sensor.impl.adc;

import ch.bfh.android.zeadl.sensor.SensorChannel;

/**
 * Created by kevin on 12.04.15.
 */
public class FsAdcChannel extends SensorChannel {

    public static enum ADCChannelSrc {ADC0,ADC1,ADC2,ADC3,ADC4,POTI};

    private ADCChannelSrc channel;

    public FsAdcChannel(ADCChannelSrc channel) {
        this.channel = channel;
    }

    public float getSample() {
        float val;
        switch (channel) {
            case ADC0:
                val = GPIO.ADC_CH0.getValue() * 100 /4095;
                break;
            case ADC1:
                val = GPIO.ADC_CH1.getValue() * 100 /4095;
                break;
            case ADC2:
                val = GPIO.ADC_CH2.getValue() * 100 /4095;
                break;
            case ADC3:
                val = GPIO.ADC_CH3.getValue() * 100 /4095;
                break;
            case ADC4:
                val = GPIO.ADC_CH4.getValue() * 100 /4095;
                break;
            case POTI:
                val = GPIO.ADC_POTI.getValue() * 100 /4095;
                break;
            default:
                val = GPIO.ADC_POTI.getValue() * 100 /4095;
                break;
        }
        return val;
    }

    public String getName() {
        String val;
        switch (channel) {
            case ADC0:
                val = "ADC0";
                break;
            case ADC1:
                val = "ADC1";
                break;
            case ADC2:
                val = "ADC2";
                break;
            case ADC3:
                val = "ADC3";
                break;
            case ADC4:
                val = "ADC4";
                break;
            case POTI:
                val = "POTI";
                break;
            default:
                val = "POTI";
                break;
        }
        return val;
    }

    public void start() {
    }

    public void stop() {
    }
}
