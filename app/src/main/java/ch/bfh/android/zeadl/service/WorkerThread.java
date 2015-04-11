package ch.bfh.android.zeadl.service;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.bfh.android.zeadl.SensorChannel;
import ch.bfh.android.zeadl.SensorGroup;
import ch.bfh.android.zeadl.SensorGroupController;

/**
 * Created by timo on 4/10/15.
 */
public class WorkerThread extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] params) {
        Log.d("Zeadl Thread","Thread started");
        while(!isCancelled()) {
            //Log.d("Zeadl Thread","Thread working...");

            try {
                Thread.sleep(500,0);
            } catch (InterruptedException e) {
                if(isCancelled()) break;
            }

            for(SensorGroup group : SensorGroupController.getActiveGroups()) {
                SensorGroup.DataSegment segment = group.getLastDataSegment();
                Date time = new Date(); //Timestamp of now
                List<Float> datapoints = new ArrayList<Float>();

                for(SensorChannel channel : segment.getChannels()) {
                    datapoints.add(channel.getSample());
                }

                segment.addEntry(new SensorGroup.DataSegment.Entry(time,datapoints));

            }

        }
        Log.d("Zeadl Thread","Thread stopped");

        return null;
    }
}
