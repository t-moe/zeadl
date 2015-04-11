package ch.bfh.android.zeadl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;

import ch.bfh.android.zeadl.impl.gravitydummy.GravityDummyGroup;
import ch.bfh.android.zeadl.impl.temp.TempSensorGroup;

/**
 * Created by timo on 4/4/15.
 */


/**
 * Static class which provides access to all available and active sensor groups.
 * You can activate/deactivate/query all Sensor Groups with this Class
 */
public final class SensorGroupController {

    private final static List<GroupInfo> mSensorGroups = new ArrayList<GroupInfo>();
    private final static List<SensorGroup> mActiveSensorGroups = new ArrayList<SensorGroup>();
    static {
        //Add available sensor groups here
        mSensorGroups.add(new GroupInfo(TempSensorGroup.class));
        mSensorGroups.add(new GroupInfo(GravityDummyGroup.class));

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
            group = gi.getInstance();
            mActiveSensorGroups.add(group);

            synchronized (mListeners) {
                for (UpdateListener listener : mListeners) {
                    listener.onActiveGroupsChanged(new ActiveGroupsChangedEvent(null,ActiveGroupsChangedEvent.Type.GROUP_ACTIVATED, group));
                }
            }

            return group;
        }
        return null;
    }

    /**
     * Deactivates a Sensor Group
     * @param group The instance of the group to deactivate
     * @return bool on success
     */
    public static synchronized final boolean deactivate(SensorGroup group) {
        if(!mActiveSensorGroups.contains(group)) return false;
        mActiveSensorGroups.remove(group);

        synchronized (mListeners) {
            for (UpdateListener listener : mListeners) {
                listener.onActiveGroupsChanged(new ActiveGroupsChangedEvent(null,ActiveGroupsChangedEvent.Type.GROUP_DEACTIVATED, group));
            }
        }


        return true;
    }

    /**
     * Deactivates a Sensor Group
     * @param gi The GroupInfo object retrived from getAvailableGroups()
     * @return bool on success
     */
    public static synchronized final boolean deactivate(GroupInfo gi) {
        SensorGroup group =gi.getInstance();
        if(!mActiveSensorGroups.contains(group)) return false;
        mActiveSensorGroups.remove(group);

        synchronized (mListeners) {
            for (UpdateListener listener : mListeners) {
                listener.onActiveGroupsChanged(new ActiveGroupsChangedEvent(null,ActiveGroupsChangedEvent.Type.GROUP_DEACTIVATED, group));
            }
        }
        return true;
    }

    public static final boolean isActive(GroupInfo gi) {
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

    private static final List<UpdateListener> mListeners = new ArrayList<UpdateListener>();
    public static synchronized void addEventListener(UpdateListener listener)  {
        synchronized (mListeners) {
            mListeners.add(listener);
        }
    }
    public static synchronized void removeEventListener(UpdateListener listener)   {
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }



}
