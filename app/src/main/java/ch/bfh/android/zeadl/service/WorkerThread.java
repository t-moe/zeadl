package ch.bfh.android.zeadl.service;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import ch.bfh.android.zeadl.sensor.SensorChannel;
import ch.bfh.android.zeadl.sensor.SensorGroup;
import ch.bfh.android.zeadl.sensor.SensorGroupController;

/**
 * Created by timo on 4/10/15.
 */
public class WorkerThread extends AsyncTask implements SensorGroupController.UpdateListener, SensorGroup.UpdateListener {

    private Thread mThread;

    @Override
    protected Object doInBackground(Object[] params) {
        Log.d("Zeadl Thread","Thread started");
        mThread = Thread.currentThread();
        SensorGroupController.addEventListener(this);
        List<SensorGroup> activeSensorGroups = SensorGroupController.getActiveGroups();
        for(SensorGroup sensorGroup: activeSensorGroups) {
            sensorGroup.addEventListener(this);
        }

        calcUpdateFreq();

        while(!isCancelled()) {
            //Log.d("Zeadl Thread","Thread working...");

            long startTime =  System.currentTimeMillis();

            int channelIdx=0;

            for (Iterator<SensorGroup> it = SensorGroupController.getActiveGroups().iterator(); it.hasNext(); ) {
                SensorGroup group = it.next();
                synchronized (mLockObj) {
                    while (channelIdx >= mUpdateCnt.size()) { //ensure mUpdateCnt obj has the right size
                        mUpdateCnt.add(0);
                    }
                    int div = mUpdateFreq / group.getSampleRate() -1; //number of times we need to wait
                    int cur = mUpdateCnt.get(channelIdx);
                    if (cur >= div) { //time to update
                        mUpdateCnt.set(channelIdx, 0);

                        SensorGroup.DataSegment segment = group.getLastDataSegment();
                        Date time = new Date(); //Timestamp of now
                        List<Float> datapoints = new ArrayList<Float>();

                        /*
                        //Debugging
                        if(!segment.getEntries().isEmpty()) {
                            long milidiff = time.getTime() - segment.getEntries().get(segment.getEntries().size() - 1).getTime().getTime();
                            Log.d("worker", "Sample rate " + group.getSampleRate() + " timediff " + milidiff);
                        }*/

                        for (Iterator<SensorChannel> iterator = segment.getChannels().iterator(); iterator.hasNext(); ) {
                            SensorChannel channel = iterator.next();
                            datapoints.add(channel.getSample());
                        }

                        segment.addEntry(new SensorGroup.DataSegment.Entry(time, datapoints));

                    } else { //nah, lets wait
                        mUpdateCnt.set(channelIdx, cur + 1);
                    }
                    channelIdx++;
                }
            }

            int timerFreq= mUpdateFreq; //Copy to ensure we use the same value for the following calculations
            int sleepTime = 3600*1000/timerFreq;
            long elapsedMillis = System.currentTimeMillis() - startTime;
            sleepTime-=elapsedMillis;
            //if(mUpdateFreq!=1) Log.d("worker","elapsed "+elapsedMillis);
            if(sleepTime<=0) continue;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                if(isCancelled()) break;
            }

        }

        SensorGroupController.removeEventListener(this);
        for(SensorGroup sensorGroup: activeSensorGroups) {
            sensorGroup.addEventListener(this);
        }

        Log.d("Zeadl Thread","Thread stopped");

        return null;
    }


    //greatest common factor
    private static int GCF(int a, int b) {
        if (b == 0) return a;
        else return (GCF (b, a % b));
    }

    //least common multiple
    private static int lcm(int a, int b)
    {
        return a * (b / GCF(a, b));
    }

    private static int lcm(int[] input)
    {
        int result = input[0];
        for(int i = 1; i < input.length; i++) result = lcm(result, input[i]);
        return result;
    }

    private int mUpdateFreq;
    private Object mLockObj = new Object();
    private List<Integer> mUpdateCnt = new ArrayList<Integer>();

    private synchronized void calcUpdateFreq() {
        synchronized (mLockObj) {
            List<SensorGroup> sensorGroups = SensorGroupController.getActiveGroups();
            int sampleRates[] = new int[sensorGroups.size()];
            int i = 0;
            for (SensorGroup sensorGroup : sensorGroups) {
                sampleRates[i++] = sensorGroup.getSampleRate();
            }
            if (sampleRates.length == 0) {
                mUpdateFreq = 3600; //1 sample per second
            } else {
                mUpdateFreq = lcm(sampleRates);
            }

            Log.d("Zeadl Thread", "New Update Freq: " + mUpdateFreq + " Freqs: " + Arrays.toString(sampleRates));
        }
        mThread.interrupt(); //force thread to quit sleep

    }


    @Override
    public void onActiveGroupsChanged(SensorGroupController.ActiveGroupsChangedEvent event) {

        if(event.getType()== SensorGroupController.ActiveGroupsChangedEvent.Type.GROUP_ACTIVATED) {
            event.getGroup().addEventListener(this);
        } else {
            event.getGroup().removeEventListener(this);
        }
        calcUpdateFreq();
    }

    @Override
    public void onActiveChannelsChanged(SensorGroup.ActiveChannelsChangedEvent event) {
        //not needed. we will detect the new channels in the next update
    }

    @Override
    public void onDataSegmentAdded(SensorGroup.DataSegmentAddedEvent event) {
        //not needed
    }

    @Override
    public void onSampleRateChanged(SensorGroup.SampleRateChangedEvent event) {
        calcUpdateFreq();
    }
}
