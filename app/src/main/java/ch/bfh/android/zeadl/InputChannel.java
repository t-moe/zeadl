package ch.bfh.android.zeadl;

/**
 * Created by timo on 3/24/15.
 */
public interface InputChannel {
    void start();
    void stop();
    int getMaximalSampleRate();
    String getUnit();
    double getSample();
}
