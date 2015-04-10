package ch.bfh.android.zeadl;


import android.graphics.Color;

/**
 * Created by timo on 3/24/15.
 */


/**
 * Class which represents a Sensor (Channel), Each SensorChannel belongs to a SensorGroup
 */
public abstract class SensorChannel {
    /**
     * Returns the Name of the Channel (e.g. Ambient Temperature)
     * @return
     */
    public abstract String getName();

    /**
     * Prepares the Channel for data retrival
     */
    public abstract void start();

    /**
     * Tells the channel that no more data will be retrieved
     */
    public abstract void stop();

    /**
     * Gets a Sample from the Channel
     * @return Current value of the channel in the unit of SensorGroup.getUnit()
     */
    public abstract float getSample();


    private int mColor = Color.CYAN;

    /**
     * Returns the current rendering Color of the channel
     * @return
     */
    public final int getColor() {
        return mColor;
    }

    /**
     * Sets the rendering color of the channel
     * @param color color to use for drawings
     */
    public final synchronized void setColor(int color) {
        mColor = color;
    }
}
