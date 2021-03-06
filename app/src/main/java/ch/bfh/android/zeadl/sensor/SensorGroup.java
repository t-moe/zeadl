package ch.bfh.android.zeadl.sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

public abstract class SensorGroup {


    /**
     * Returns the Unit of the Sensor Channel. E.g. "Degree Celsius" or "V"
     * @return
     */
    public abstract String getUnit();

    /**
     * Returns the maximal available sample rate. See also: setSampleRate
     * @return sample rate in samples per hour!
     */
    public abstract int getMaximalSampleRate();



    private int mSampleRate=3600; //one sample per second


    protected  SensorGroup() {
        if(mSampleRate>getMaximalSampleRate()) {
            mSampleRate = getMaximalSampleRate();
        }
        mDataSegments.add(new DataSegment(this,new ArrayList<SensorChannel>()));
    }

    /**
     * Returns the current configured samplerate
     * @return sample rate in samples per hour!
     */
    public final int getSampleRate() {
        return mSampleRate;
    }

    /**
     * Sets the current sample Rate. Throws an exception if the passed sample rate is out of bounds
     * @param rate The desired number of samples per hour!
     */
    public final synchronized void setSampleRate(int rate) {
        if(rate<=0 || rate > getMaximalSampleRate()) {
            throw new IllegalArgumentException();
        }
        if(rate==mSampleRate) return;
        final int oldSampleRate = mSampleRate;
        mSampleRate = rate;
        mListeners.fireEvent(new EventListenerCollection.EventFireHelper<UpdateListener>() {
            @Override
            public void foreach(UpdateListener listener) {
                listener.onSampleRateChanged(new SampleRateChangedEvent(SensorGroup.this,oldSampleRate,mSampleRate));
            }
        });
    }


    /**
     * Class which holds Data for all SensorChannels of a SensorGroup during a certain interval.
     * If you add or remove SensorChannels a new DataSegment will be created
     */
    public static class DataSegment {
        private final List<SensorChannel> mChannels;
        private final List<Entry> mEntries;
        private final SensorGroup mSource;

        private DataSegment(SensorGroup source, List<SensorChannel> channels) {
            mChannels = channels;
            mEntries = new ArrayList<Entry>();
            mSource=source;
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
        public final synchronized void addEntry(final Entry e) {
            if(e.getChannelData().size()!=mChannels.size())
                throw new IllegalArgumentException();
            mEntries.add(e);
            mListeners.fireEvent(new EventListenerCollection.EventFireHelper<EntryAddedListener>() {
                @Override
                public void foreach(EntryAddedListener listener) {
                    listener.onEntryAdded(new EntryAddedEvent(DataSegment.this,e));
                }
            });
        }

        public final SensorGroup getSensorGroup() {
            return mSource;
        }

        public static class EntryAddedEvent extends EventObject {
            private final  Entry mEntry;
            private EntryAddedEvent(Object source, final Entry entry) {
                super(source);
                mEntry = entry;
            }
            public final Entry getEntry() {
                return mEntry;
            }
        }
        public interface EntryAddedListener  extends EventListener {
            public void onEntryAdded(final EntryAddedEvent event);
        }

        private final EventListenerCollection<EntryAddedListener> mListeners = new EventListenerCollection<>();
        public synchronized void addEventListener(final EntryAddedListener listener)  {
            mListeners.addListener(listener);
        }
        public synchronized void removeEventListener(final EntryAddedListener listener) {
            mListeners.removeListener(listener);
        }

        public synchronized void addWeakEventListener(final EntryAddedListener listener)  {
            mListeners.addWeakListener(listener);
        }
        public synchronized void removeWeakEventListener(final EntryAddedListener listener) {
            mListeners.removeWeakListener(listener);
        }



        /**
         * Class which holds DataPoints of all channels of a SensorGroup for one single point in time
         */
        public static class Entry {
            private final Date mTime;
            private final List<Float> mDataList;

            /**
             * Construct a new Entry
             * @param time The timestamp of the entry
             * @param data The Data of the single channels
             */
            public Entry(final Date time, final List<Float> data) {
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

   private final  List<DataSegment> mDataSegments = new ArrayList<DataSegment>();

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
            if(mDataSegments.isEmpty()) return null;
            return mDataSegments.get(mDataSegments.size() - 1);
        }
    }

    /**
     * Clears all data segments
     */
    public final void clearAllDataSegments() {
        final DataSegment newSegment = new DataSegment(this,new ArrayList<>(mActiveSensorChannels)); //COPY channnels into new datasegment
        mDataSegments.clear();
        mDataSegments.add(newSegment);
        mListeners.fireEvent(new EventListenerCollection.EventFireHelper<UpdateListener>() {
            @Override
            public void foreach(UpdateListener listener) {
                listener.onDataSegmentAdded(new DataSegmentAddedEvent(SensorGroup.this,newSegment));
            }
        });
    }

    /**
     * Adds a new Channel to the available channels. Call this method in the constructor of your SensorGroup implementation
     * @param channel
     */
    protected final void addChannel(SensorChannel channel) {
        mSensorChannels.add(new ChannelInfo(channel));
    }

    private final List<ChannelInfo> mSensorChannels = Collections.synchronizedList(new ArrayList<ChannelInfo>());
    private final List<SensorChannel> mActiveSensorChannels = Collections.synchronizedList(new ArrayList<SensorChannel>());


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
     * Returns whether or not the given SensorGroup is active
     * @param ci The ChannelInfo object retrived from getAvailableChannel()
     * @return true if the SensorChannel is active
     */
    public final boolean isActive(ChannelInfo ci) {
        return mActiveSensorChannels.contains(ci.getInstance());
    }


    /**
     * Activates a Sensor Channel.
     * @param ci The ChannelInfo object retrived from getAvailableChannel()
     * @return The instance of the activated SensorChannel or null on error
     */
    public synchronized final SensorChannel activate(ChannelInfo ci) {
        if(mActiveSensorChannels.contains(ci)) return null;
        final SensorChannel channel =ci.getInstance();
        channel.start();
        mActiveSensorChannels.add(ci.getInstance());
        mListeners.fireEvent(new EventListenerCollection.EventFireHelper<UpdateListener>() {
            @Override
            public void foreach(UpdateListener listener) {
                listener.onActiveChannelsChanged(new ActiveChannelsChangedEvent(SensorGroup.this, ActiveChannelsChangedEvent.Type.CHANNEL_ACTIVATED, channel));
            }
        });

        synchronized (mDataSegments) {
            if(!mActiveSensorChannels.isEmpty()) {
                final DataSegment newSegment = new DataSegment(this,new ArrayList<>(mActiveSensorChannels)); //COPY channnels into new datasegment
                mDataSegments.clear(); //Testwise, to save memory. TODO: remove this line
                mDataSegments.add(newSegment);
                mListeners.fireEvent(new EventListenerCollection.EventFireHelper<UpdateListener>() {
                    @Override
                    public void foreach(UpdateListener listener) {
                        listener.onDataSegmentAdded(new DataSegmentAddedEvent(SensorGroup.this,newSegment));
                    }
                });
            }
        }
        return channel;

    }

    /**
     * Deactivates a Sensor Channel
     * @param ci The ChannelInfo object retrived from getAvailableChannel()
     * @return bool on success
     */
    public synchronized final boolean deactivate(ChannelInfo ci) {
        final SensorChannel channel =ci.getInstance();
        if(!mActiveSensorChannels.contains(channel)) return false;
        channel.stop();
        mActiveSensorChannels.remove(channel);
        mListeners.fireEvent(new EventListenerCollection.EventFireHelper<UpdateListener>() {
            @Override
            public void foreach(UpdateListener listener) {
                listener.onActiveChannelsChanged(new ActiveChannelsChangedEvent(SensorGroup.this, ActiveChannelsChangedEvent.Type.CHANNEL_DEACTIVATED, channel));
            }
        });

        synchronized (mDataSegments) {
            if(!mActiveSensorChannels.isEmpty()) {
                final DataSegment newSegment = new DataSegment(this,new ArrayList<>(mActiveSensorChannels)); //COPY channnels into new datasegment
                mDataSegments.clear(); //Testwise, to save memory. TODO: remove this line
                mDataSegments.add(newSegment);
                mListeners.fireEvent(new EventListenerCollection.EventFireHelper<UpdateListener>() {
                    @Override
                    public void foreach(UpdateListener listener) {
                        listener.onDataSegmentAdded(new DataSegmentAddedEvent(SensorGroup.this,newSegment));
                    }
                });
            }
        }
        return true;
    }

    /**
     * Deactivates a Sensor Channel
     * @param ch The instance of the channel to deactivate
     * @return bool on success
     */
    public synchronized final boolean deactivate(final SensorChannel ch) {
        if(!mActiveSensorChannels.contains(ch)) return false;
        ch.stop();
        mActiveSensorChannels.remove(ch);
        mListeners.fireEvent(new EventListenerCollection.EventFireHelper<UpdateListener>() {
            @Override
            public void foreach(UpdateListener listener) {
                listener.onActiveChannelsChanged(new ActiveChannelsChangedEvent(SensorGroup.this, ActiveChannelsChangedEvent.Type.CHANNEL_DEACTIVATED, ch));
            }
        });

        synchronized (mDataSegments) {
            if(!mActiveSensorChannels.isEmpty()) {
                final DataSegment newSegment = new DataSegment(this,new ArrayList<>(mActiveSensorChannels)); //COPY channnels into new datasegment
                mDataSegments.clear(); //Testwise, to save memory. TODO: remove this line
                mDataSegments.add(newSegment);
                mListeners.fireEvent(new EventListenerCollection.EventFireHelper<UpdateListener>() {
                    @Override
                    public void foreach(UpdateListener listener) {
                        listener.onDataSegmentAdded(new DataSegmentAddedEvent(SensorGroup.this,newSegment));
                    }
                });
            }
        }
        return true;
    }


    /**
     * Class which holds metadata about a SensorChannel. Pass an instance of this class to activate in order to get the instance of the implementation.
     */
    public static class ChannelInfo {
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

        //This method should probably be private..
        public SensorChannel getInstance() {
            return mInstance;
        }
        private SensorChannel mInstance;
    }



    public static class ActiveChannelsChangedEvent extends EventObject {
        public enum Type {
            CHANNEL_ACTIVATED,
            CHANNEL_DEACTIVATED,
        }
        private Type mType;
        private SensorChannel mChannel;
        private ActiveChannelsChangedEvent(final Object source, final Type type, final SensorChannel channel) {
            super(source);
            mChannel=channel;
            mType=type;
        }
        public final Type getType() {
            return mType;
        }
        public final SensorChannel getChannel () {
            return mChannel;
        }
    }

    public static class DataSegmentAddedEvent extends EventObject {
        private DataSegment mDataSegment;
        private DataSegmentAddedEvent(final Object source, final DataSegment segment) {
            super(source);
            mDataSegment = segment;
        }
        public final DataSegment getDataSegment() {
            return mDataSegment;
        }
    }


    public static class SampleRateChangedEvent extends EventObject {
        private int mOldSampleRate;
        private int mNewSampleRate;
        private SampleRateChangedEvent(final Object source, final int oldSampleRate, final int newSampleRate) {
            super(source);
            mOldSampleRate=oldSampleRate;
            mNewSampleRate=newSampleRate;
        }
        public final int getOldSampleRate() {
            return mOldSampleRate;
        }
        public final int getNewSampleRate() {
            return mNewSampleRate;
        }
    }

    public interface UpdateListener extends EventListener {
        public void onActiveChannelsChanged(final ActiveChannelsChangedEvent event);
        public void onDataSegmentAdded(final DataSegmentAddedEvent event);
        public void onSampleRateChanged(final SampleRateChangedEvent event);
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
