package ch.bfh.android.zeadl;

/**
 * Created by timo on 3/24/15.
 */
public interface SensorChannel {
    public String getName();
    public void start();
    public void stop();
    public int getMaximalSampleRate();
    public float getSample();
}