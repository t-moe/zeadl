package ch.bfh.android.zeadl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.bfh.android.zeadl.impl.temp.TempSensorGroup;

/**
 * Created by timo on 4/4/15.
 */


/**
 * Static class which provides access to all available and active sensor groups.
 * You can activate/deactivate/query all Sensor Groups with this Class
 */
public final class SensorGroupFactory {

    private final static List<GroupInfo> _sensorGroups = new ArrayList<GroupInfo>();
    private final static List<SensorGroup> _activeSensorGroups = new ArrayList<SensorGroup>();
    static {
        //Add available sensor groups here
        _sensorGroups.add(new GroupInfo(TempSensorGroup.class));

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
        if(gi.getInstance()!=null && _activeSensorGroups.contains(gi.getInstance())) return null;
        if(gi.create()) {
            _activeSensorGroups.add(gi.getInstance());
            return gi.getInstance();
        }
        return null;
    }

    /**
     * Deactivates a Sensor Group
     * @param group The instance of the group to deactivate
     * @return bool on success
     */
    public static synchronized final boolean deactivate(SensorGroup group) {
        if(!_activeSensorGroups.contains(group)) return false;
        _activeSensorGroups.remove(group);
        return true;
    }

    /**
     * Deactivates a Sensor Group
     * @param gi The GroupInfo object retrived from getAvailableGroups()
     * @return bool on success
     */
    public static synchronized final boolean deactivate(GroupInfo gi) {
        if(!_activeSensorGroups.contains(gi.getInstance())) return false;
        _activeSensorGroups.remove(gi.getInstance());
        return true;
    }

    /**
     * Returns all available SensorGroups
     * @return
     */
    public static final List<GroupInfo> getAvailableGroups() {
        return Collections.unmodifiableList(_sensorGroups);
    }

    /**
     * Returns all active SensorGroups
     * @return
     */
    public static final List<SensorGroup> getActiveGroups() {
        return Collections.unmodifiableList(_activeSensorGroups);
    }
}
