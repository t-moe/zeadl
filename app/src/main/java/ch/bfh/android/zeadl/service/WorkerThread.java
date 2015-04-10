package ch.bfh.android.zeadl.service;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by timo on 4/10/15.
 */
public class WorkerThread extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] params) {
        Log.d("Zeadl Thread","Thread started");
        while(!isCancelled()) {
            Log.d("Zeadl Thread","Thread working...");

            try {
                Thread.sleep(500,0);
            } catch (InterruptedException e) {
                if(isCancelled()) break;
            }







        }
        Log.d("Zeadl Thread","Thread stopped");

        return null;
    }
}
