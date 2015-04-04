package ch.bfh.android.zeadl;

import android.hardware.Sensor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.bfh.android.zeadl.impl.temp.TempSensorGroup;

public abstract class SensorGroup {
    public abstract String getUnit();


    protected final void addChannel(SensorChannel channel) {
        _sensorChannels.add(new SensorChannelInfo(channel));
    }

    private final List<SensorChannelInfo> _sensorChannels = new ArrayList<SensorChannelInfo>();
    private final List<SensorChannel> _activeSensorChannels = new ArrayList<SensorChannel>();


    public final List<SensorChannelInfo> getAvailableChannels() {
        return Collections.unmodifiableList(_sensorChannels);

    }
    public final List<SensorChannel> getActiveChannels() {
        return Collections.unmodifiableList(_activeSensorChannels);
    }

    public final SensorChannel activate(SensorChannelInfo gi) {
        if(_activeSensorChannels.contains(gi)) return null;
        _activeSensorChannels.add(gi.getInstance());
        return gi.getInstance();

    }
    public final boolean deactivate(SensorChannelInfo gi) {
        if(!_activeSensorChannels.contains(gi.getInstance())) return false;
        _activeSensorChannels.remove(gi.getInstance());
        return true;
    }

    public final boolean deactivate(SensorChannel ch) {
        if(!_activeSensorChannels.contains(ch)) return false;
        _activeSensorChannels.remove(ch);
        return true;
    }

    static class SensorChannelInfo{
        private SensorChannelInfo(SensorChannel ch) {
            _instance = ch;
        }
        public final String getName() {
            return _instance.getName();
        }
        private SensorChannel getInstance() {
            return _instance;
        }
        private SensorChannel _instance;
    }

}
