package ch.bfh.android.zeadl;

import android.provider.ContactsContract;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public abstract class SensorGroup {


    /**
     * Returns the Unit of the Sensor Channel. E.g. "Degree Celsius" or "V"
     * @return
     */
    public abstract String getUnit();

    /**
     * Returns the maximal available sample rate. See also: setSampleRate
     * @return
     */
    public abstract int getMaximalSampleRate();



    private int mSampleRate=10;

    /**
     * Returns the current configured sample Rate
     * @return
     */
    public final int getSampleRate() {
        return mSampleRate;
    }

    /**
     * Sets the current sample Rate. Throws an exception if the passed sample rate is out of bounds
     * @param rate The desired rate
     */
    public final synchronized void setSampleRate(int rate) {
        if(rate<=0 || rate > getMaximalSampleRate()) {
            throw new IllegalArgumentException();
        }
        mSampleRate = rate;
    }


    /**
     * Class which holds Data for all SensorChannels of a SensorGroup during a certain interval.
     * If you add or remove SensorChannels a new DataSegment will be created
     */
    static class DataSegment {
        private final List<SensorChannel> mChannels;
        private final List<Entry> mEntries;

        private DataSegment(List<SensorChannel> channels) {
            mChannels = channels;
            mEntries = new ArrayList<Entry>();
        }

        /**
         * Returns the list of Channels in this DataSegment
         * @return
         */
        public final List<SensorChannel> getChannels() {
            return Collections.unmodifiableList(mChannels);
        }

        /**
         * Returns the list of entries in this DataSegment
         * @return
         */
        public final List<Entry> getEntries() {
            return Collections.unmodifiableList(mEntries);
        }

        /**
         * Appends a new Entry to the DataSegment
         * @param e The new entry to add. Must values must correspond to the number of channels of this segment
         */
        public final synchronized  void addEntry(Entry e) {
            if(e.getChannelData().size()!=mChannels.size())
                throw new IllegalArgumentException();
            mEntries.add(e);
        }

        /**
         * Class which holds DataPoints of all channels of a SensorGroup for one single point in time
         */
        static class Entry {
            private final Date mTime;
            private final List<Float> mDataList;

            /**
             * Construct a new Entry
             * @param time The timestamp of the entry
             * @param data The Data of the single channels
             */
            public Entry(Date time, List<Float> data) {
                mDataList = data;
                mTime = time;
            }

            /**
             * Returns the Time of this Entry
             * @return
             */
            public final Date getTime() {
                return mTime;
            }

            /**
             * Returns the data of the channels of this entry
             * @return
             */
            public final List<Float> getChannelData(){
                return Collections.unmodifiableList(mDataList);
            }
        }

    }

   private List<DataSegment> mDataSegments = new ArrayList<DataSegment>();

    /**
     * Returns all available DataSegments
     * @return
     */
    public final List<DataSegment> getDataSegments() {
        return Collections.unmodifiableList(mDataSegments);
    }

    /**
     * Returns the latest DataSegment. Append New Data here
     * @return
     */
    public final DataSegment getLastDataSegment() {
        synchronized (mDataSegments) {
            return mDataSegments.get(mDataSegments.size() - 1);
        }
    }

    /**
     * Adds a new Channel to the available channels. Call this method in the constructor of your SensorGroup implementation
     * @param channel
     */
    protected final void addChannel(SensorChannel channel) {
        mSensorChannels.add(new ChannelInfo(channel));
    }

    private final List<ChannelInfo> mSensorChannels = new ArrayList<ChannelInfo>();
    private final List<SensorChannel> mActiveSensorChannels = new ArrayList<SensorChannel>();


    /**
     * Returns the name of the SensorGroup
     * @return
     */
    public final String getName() {
        return this.getClass().getAnnotation(DisplayName.class).value();
    }

    /**
     * Returns all available SensorChannels of the SensorGroup
     * @return
     */
    public final List<ChannelInfo> getAvailableChannels() {
        return Collections.unmodifiableList(mSensorChannels);

    }

    /**
     * Returns all active SensorChannels of the SensorGroup
     * @return
     */
    public final List<SensorChannel> getActiveChannels() {
        return Collections.unmodifiableList(mActiveSensorChannels);
    }


    /**
     * Activates a Sensor Channel.
     * @param ci The ChannelInfo object retrived from getAvailableChannel()
     * @return The instance of the activated SensorChannel or null on error
     */
    public synchronized final SensorChannel activate(ChannelInfo ci) {
        if(mActiveSensorChannels.contains(ci)) return null;
        ci.getInstance().start();
        mActiveSensorChannels.add(ci.getInstance());
        synchronized (mDataSegments) {
            mDataSegments.add(new DataSegment(mActiveSensorChannels));
        }
        return ci.getInstance();

    }

    /**
     * Deactivates a Sensor Channel
     * @param ci The ChannelInfo object retrived from getAvailableChannel()
     * @return bool on success
     */
    public synchronized final boolean deactivate(ChannelInfo ci) {
        if(!mActiveSensorChannels.contains(ci.getInstance())) return false;
        ci.getInstance().stop();
        mActiveSensorChannels.remove(ci.getInstance());
        synchronized (mDataSegments) {
            mDataSegments.add(new DataSegment(mActiveSensorChannels));
        }
        return true;
    }

    /**
     * Deactivates a Sensor Channel
     * @param ch The instance of the channel to deactivate
     * @return bool on success
     */
    public synchronized final boolean deactivate(SensorChannel ch) {
        if(!mActiveSensorChannels.contains(ch)) return false;
        ch.stop();
        mActiveSensorChannels.remove(ch);
        synchronized (mDataSegments) {
            mDataSegments.add(new DataSegment(mActiveSensorChannels));
        }
        return true;
    }


    /**
     * Class which holds metadata about a SensorChannel. Pass an instance of this class to activate in order to get the instance of the implementation.
     */
    static class ChannelInfo {
        private ChannelInfo(SensorChannel ch) {
            mInstance = ch;
        }
        /**
         * Returns the name of the SensorChannel
         * @return
         */
        public final String getName() {
            return mInstance.getName();
        }
        private SensorChannel getInstance() {
            return mInstance;
        }
        private SensorChannel mInstance;
    }

}
