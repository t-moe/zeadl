package ch.bfh.android.zeadl.sensor;

import android.os.Build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

import ch.bfh.android.zeadl.sensor.impl.adc.ADCSensorGroup;
import ch.bfh.android.zeadl.sensor.impl.color.ColorSensorGroup;
import ch.bfh.android.zeadl.sensor.impl.gravity.GravitySensorGroup;
import ch.bfh.android.zeadl.sensor.impl.dummy.DummyGroup;
import ch.bfh.android.zeadl.sensor.impl.temp.TempSensorGroup;

/**
 * Created by timo on 4/4/15.
 */


/**
 * Static class which provides access to all available and active sensor groups.
 * You can activate/deactivate/query all Sensor Groups with this Class
 */
public final class SensorGroupController {

    private final static List<GroupInfo> mSensorGroups = Collections.synchronizedList(new ArrayList<GroupInfo>());
    private final static List<SensorGroup> mActiveSensorGroups = Collections.synchronizedList(new ArrayList<SensorGroup>());
    static {
        //Add available sensor groups here
        if(!Build.FINGERPRINT.startsWith("generic")) { //not in emulator
            mSensorGroups.add(new GroupInfo(TempSensorGroup.class));
            mSensorGroups.add(new GroupInfo(GravitySensorGroup.class));
            mSensorGroups.add(new GroupInfo(ColorSensorGroup.class));
            mSensorGroups.add(new GroupInfo(ADCSensorGroup.class));
        }
        mSensorGroups.add(new GroupInfo(DummyGroup.class));

        //TODO: Find the classes automatically using reflection
    }


    /**
     * Class which holds metadata about a SensorGroup. Pass an instance of this class to activate in order to get the instance of the implementation.
     */
    public static class GroupInfo {
        private GroupInfo(Class cl) {
            _class = cl;
            _instance = null;
            if(!SensorGroup.class.isAssignableFrom(cl))
                throw new IllegalArgumentException();
            if(cl.isAnnotationPresent(DisplayName.class))
            _name = ((DisplayName) cl.getAnnotation(DisplayName.class)).value();
            else
                throw new IllegalArgumentException();
        }

        private boolean create() {
            if(_instance!=null) return true;
            try {
                _instance =  (SensorGroup)_class.newInstance();
                return true;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return false;
        }

        private SensorGroup getInstance(){
            return _instance;
        }

        /**
         * Returns the name of the SensorGroup
         * @return
         */
        public final String getName() {
            return _name;
        }

        private final String _name;
        private final Class _class;
        private SensorGroup _instance;
    }


    /**
     * Activates a Sensor Group.
     * @param gi The GroupInfo object retrived from getAvailableGroups()
     * @return The instance of the activated SensorGroup or null on error
     */
    public static synchronized final SensorGroup activate(GroupInfo gi) {
        SensorGroup group = gi.getInstance();
        if(group!=null && mActiveSensorGroups.contains(group)) return null;
        if(gi.create()) {
            final SensorGroup newGroup = gi.getInstance();
            mActiveSensorGroups.add(newGroup);

            mListeners.fireEvent(new EventListenerCollection.EventFireHelper<UpdateListener>() {
                @Override
                public void foreach(UpdateListener listener) {
                    listener.onActiveGroupsChanged(new ActiveGroupsChangedEvent(null,ActiveGroupsChangedEvent.Type.GROUP_ACTIVATED, newGroup));
                }
            });

            return newGroup;
        }
        return null;
    }

    /**
     * Deactivates a Sensor Group
     * @param group The instance of the group to deactivate
     * @return bool on success
     */
    public static synchronized final boolean deactivate(final SensorGroup group) {
        if(!mActiveSensorGroups.contains(group)) return false;
        mActiveSensorGroups.remove(group);

        mListeners.fireEvent(new EventListenerCollection.EventFireHelper<UpdateListener>() {
            @Override
            public void foreach(UpdateListener listener) {
                listener.onActiveGroupsChanged(new ActiveGroupsChangedEvent(null,ActiveGroupsChangedEvent.Type.GROUP_DEACTIVATED, group));
            }
        });

        return true;
    }

    /**
     * Deactivates a Sensor Group
     * @param gi The GroupInfo object retrived from getAvailableGroups()
     * @return bool on success
     */
    public static synchronized final boolean deactivate(final GroupInfo gi) {
        final SensorGroup group =gi.getInstance();
        if(!mActiveSensorGroups.contains(group)) return false;
        mActiveSensorGroups.remove(group);

        mListeners.fireEvent(new EventListenerCollection.EventFireHelper<UpdateListener>() {
            @Override
            public void foreach(UpdateListener listener) {
                listener.onActiveGroupsChanged(new ActiveGroupsChangedEvent(null,ActiveGroupsChangedEvent.Type.GROUP_DEACTIVATED, group));
            }
        });
        return true;
    }

    public static final void clearAllData() {
        for (Iterator<GroupInfo> iterator = mSensorGroups.iterator(); iterator.hasNext(); ) {
            GroupInfo sensorGroup = iterator.next();
            if(sensorGroup.getInstance()!=null) {
                sensorGroup.getInstance().clearAllDataSegments();
            }
        }
    }

    /**
     * Returns whether or not the given SensorGroup is active
     * @param gi The GroupInfo object retrived from getAvailableGroups()
     * @return true if the SensorGroup is active
     */
    public static final boolean isActive(final GroupInfo gi) {
        return mActiveSensorGroups.contains(gi.getInstance());
    }

    /**
     * Returns all available SensorGroups
     * @return
     */
    public static final List<GroupInfo> getAvailableGroups() {
        return Collections.unmodifiableList(mSensorGroups);
    }

    /**
     * Returns all active SensorGroups
     * @return
     */
    public static final List<SensorGroup> getActiveGroups() {
        return Collections.unmodifiableList(mActiveSensorGroups);
    }



    public static class ActiveGroupsChangedEvent {
        public enum Type {
            GROUP_ACTIVATED,
            GROUP_DEACTIVATED,
        }
        private Type mType;
        private SensorGroup mGroup;
        private ActiveGroupsChangedEvent(final Object source, final Type type, final SensorGroup group) {
            mGroup=group;
            mType=type;
        }
        public final Type getType() {
            return mType;
        }
        public final SensorGroup getGroup () {
            return mGroup;
        }
    }


    public interface UpdateListener extends EventListener {
        public void onActiveGroupsChanged(final ActiveGroupsChangedEvent event);
    }

    private static final EventListenerCollection<UpdateListener> mListeners = new EventListenerCollection<>();
    public static synchronized void addEventListener(final UpdateListener listener)  {
        mListeners.addListener(listener);
    }
    public static synchronized void removeEventListener(final UpdateListener listener) {
        mListeners.removeListener(listener);
    }

    public static synchronized void addWeakEventListener(final UpdateListener listener)  {
        mListeners.addWeakListener(listener);
    }
    public static synchronized void removeWeakEventListener(final UpdateListener listener) {
        mListeners.removeWeakListener(listener);
    }

}
