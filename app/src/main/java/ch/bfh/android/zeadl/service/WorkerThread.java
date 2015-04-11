package ch.bfh.android.zeadl.service;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ch.bfh.android.zeadl.sensor.SensorChannel;
import ch.bfh.android.zeadl.sensor.SensorGroup;
import ch.bfh.android.zeadl.sensor.SensorGroupController;

/**
 * Created by timo on 4/10/15.
 */
public class WorkerThread extends AsyncTask implements SensorGroupController.UpdateListener, SensorGroup.UpdateListener {

    @Override
    protected Object doInBackground(Object[] params) {
        Log.d("Zeadl Thread","Thread started");
        SensorGroupController.addEventListener(this);
        List<SensorGroup> activeSensorGroups = SensorGroupController.getActiveGroups();
        for(SensorGroup sensorGroup: activeSensorGroups) {
            sensorGroup.addEventListener(this);
        }

        calcUpdateFreq();

        while(!isCancelled()) {
            //Log.d("Zeadl Thread","Thread working...");

            try {
                Thread.sleep(1000/mUpdateFreq,0);
            } catch (InterruptedException e) {
                if(isCancelled()) break;
            }

            int channelIdx=0;
            for(SensorGroup group : SensorGroupController.getActiveGroups()) {
                synchronized (mLockObj) {
                    while (channelIdx >= mUpdateCnt.size()) { //ensure mUpdateCnt obj has the right size
                        mUpdateCnt.add(0);
                    }
                    int div = mUpdateFreq / group.getSampleRate(); //number of times we need to wait
                    int cur = mUpdateCnt.get(channelIdx);
                    if (cur >= div) { //time to update
                        mUpdateCnt.set(channelIdx, 0);

                        SensorGroup.DataSegment segment = group.getLastDataSegment();
                        Date time = new Date(); //Timestamp of now
                        List<Float> datapoints = new ArrayList<Float>();

                        for (SensorChannel channel : segment.getChannels()) {
                            datapoints.add(channel.getSample());
                        }

                        segment.addEntry(new SensorGroup.DataSegment.Entry(time, datapoints));

                    } else { //nah, lets wait
                        mUpdateCnt.set(channelIdx, cur + 1);
                    }
                    channelIdx++;
                }
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
                mUpdateFreq = 1;
            } else {
                mUpdateFreq = lcm(sampleRates);
            }

            Log.d("Zeadl Thread", "New Update Freq: " + mUpdateFreq + " Freqs: " + Arrays.toString(sampleRates));
        }
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
