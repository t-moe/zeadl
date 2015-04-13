package ch.bfh.android.zeadl.sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Created by timo on 4/13/15.
 */
public class EventListenerCollection <T extends java.util.EventListener> {
    private List<T> mStrongEventListeners = new ArrayList<>();
    private WeakHashMap<T,Boolean> mWeakHashMap = new WeakHashMap<T, Boolean>();
    private Set<T> mWeakListeners = Collections.newSetFromMap(mWeakHashMap);
    private Object lockObj = new Object();

    public void addListener(T listener) {
        synchronized (lockObj) {
            mStrongEventListeners.add(listener);
        }
    }
    public void removeListener(T listener) {
        synchronized (lockObj) {
            mStrongEventListeners.add(listener);
        }
    }

    public void addWeakListener(T listener) {
        synchronized (lockObj) {
            mWeakHashMap.put(listener,true);
        }
    }

    public void removeWeakListener(T listener) {
        synchronized (lockObj) {
            mWeakHashMap.remove(listener);
        }
    }

    /*public void getCurrentListeners() {
        synchronized (lockObj) {
            ArrayList<T>  list = new ArrayList<>();
            list.addAll(mStrongEventListeners);
            list.addAll(mWeakListeners);
        }
    }*/

    public void fireEvent(EventFireHelper<T> helper) {
        synchronized (lockObj) {
            for (T listener : mStrongEventListeners) {
                helper.foreach(listener);
            }
            for (T listener : mWeakListeners) {
                helper.foreach(listener);
            }
        }
    }

    public interface EventFireHelper<T extends java.util.EventListener> {
        public void foreach(T listener);
    }


}
