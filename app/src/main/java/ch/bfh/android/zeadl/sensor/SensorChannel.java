package ch.bfh.android.zeadl.sensor;


import android.graphics.Color;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

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
        if(color==mColor) return;
        final int oldColor = mColor;
        mColor = color;
        mListeners.fireEvent(new EventListenerCollection.EventFireHelper<UpdateListener>() {
            @Override
            public void foreach(UpdateListener listener) {
                listener.onColorChanged(new ColorChangedEvent(SensorChannel.this, oldColor, mColor));
            }
        });
    }

    public static class ColorChangedEvent extends EventObject {
        private int mOldColor;
        private int mNewColor;
        private ColorChangedEvent(final Object source, final int oldColor, final int newColor) {
            super(source);
            mOldColor=oldColor;
            mNewColor=newColor;
        }
        public final int getOldColor() {
            return mOldColor;
        }
        public final int getNewColor() {
            return mNewColor;
        }
    }

    public interface UpdateListener extends EventListener {
        public void onColorChanged(final ColorChangedEvent event);
    }

    private final EventListenerCollection<UpdateListener> mListeners = new EventListenerCollection<>();
    public synchronized void addEventListener(final UpdateListener listener)  {
        mListeners.addListener(listener);
    }
    public synchronized void removeEventListener(final UpdateListener listener) {
        mListeners.removeListener(listener);
    }

    public synchronized void addWeakEventListener(final UpdateListener listener)  {
        mListeners.addWeakListener(listener);
    }
    public synchronized void removeWeakEventListener(final UpdateListener listener) {
        mListeners.removeWeakListener(listener);
    }

}
