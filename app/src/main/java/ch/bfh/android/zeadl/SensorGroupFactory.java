package ch.bfh.android.zeadl;

import android.hardware.Sensor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ch.bfh.android.zeadl.impl.temp.TempSensorGroup;

/**
 * Created by timo on 4/4/15.
 */



public final class SensorGroupFactory {

    private final static List<SensorGroupInfo> _sensorGroups = new ArrayList<SensorGroupInfo>();
    private final static List<SensorGroup> _activeSensorGroups = new ArrayList<SensorGroup>();
    static {
        //Add available sensor groups here
        _sensorGroups.add(new SensorGroupInfo(TempSensorGroup.class));

        //TODO: Find the classes automatically using reflection
    }


    static class SensorGroupInfo {
        private SensorGroupInfo(Class cl) {
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

        public final String getName() {
            return _name;
        }

        private final String _name;
        private final Class _class;
        private SensorGroup _instance;
    }



    public static final SensorGroup activate(SensorGroupInfo gi) {
        if(gi.getInstance()!=null && _activeSensorGroups.contains(gi.getInstance())) return null;
        if(gi.create()) {
            _activeSensorGroups.add(gi.getInstance());
            return gi.getInstance();
        }
        return null;
    }

    public static final boolean deactivate(SensorGroup group) {
        if(!_activeSensorGroups.contains(group)) return false;
        _activeSensorGroups.remove(group);
        return true;
    }

    public static final boolean deactivate(SensorGroupInfo gi) {
        if(!_activeSensorGroups.contains(gi.getInstance())) return false;
        _activeSensorGroups.remove(gi.getInstance());
        return true;
    }

    public static final List<SensorGroupInfo> getAvailableGroups() {
        return Collections.unmodifiableList(_sensorGroups);
    }

    public static final List<SensorGroup> getActiveGroups() {
        return Collections.unmodifiableList(_activeSensorGroups);
    }




}