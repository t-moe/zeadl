package ch.bfh.android.zeadl.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import ch.bfh.android.zeadl.MainActivity;
import ch.bfh.android.zeadl.R;

/**
 * Created by timo on 4/10/15.
 */
public class ServiceHelper {

    private final Context mContext;
    private final Intent mServiceIntent;
    private LocalService mBoundService;
    private boolean mIsBound;

    public ServiceHelper(Context cont) {
        mContext = cont;
        mServiceIntent= new Intent(mContext, LocalService.class);
    }

    public void Attach() {
        EnsureStarted();
        doBindService();

    }

    public void Detach() {
        doUnbindService();
    }

    public void EnsureStarted(){

        mContext.startService(mServiceIntent);

    }

    public void Stop() {
        Detach();
        mContext.stopService(mServiceIntent);
    }


    private ServiceConnection mConnection = new ServiceConnection() {



        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((LocalService.LocalBinder)service).getService();

            // Tell the user about this for our demo.
            //Toast.makeText(mContext, R.string.local_service_connected,Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
            //Toast.makeText(mContext, R.string.local_service_disconnected, Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        if(mIsBound) return;

        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        mContext.bindService(new Intent(mContext,
                LocalService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            mContext.unbindService(mConnection);
            mIsBound = false;
        }
    }


}
