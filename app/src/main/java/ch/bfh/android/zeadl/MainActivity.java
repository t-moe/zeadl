package ch.bfh.android.zeadl;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.util.Date;
import java.util.List;

import ch.bfh.android.zeadl.service.ServiceHelper;


public class MainActivity extends ActionBarActivity implements SensorGroup.UpdateListener, SensorGroup.DataSegment.EntryAddedListener {


    private ServiceHelper serviceHelper;


    @Override
    public void onEntryAdded(SensorGroup.DataSegment.EntryAddedEvent event) {

    }

    @Override
    public void onActiveChannelsChanged(SensorGroup.ActiveChannelsChangedEvent event) {

    }

    @Override
    public void onDataSegmentAdded(SensorGroup.DataSegmentAddedEvent event) {
        event.getDataSegment().addEventListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.EnsureStarted();

        setContentView(R.layout.activity_main);

        ListView list=(ListView)findViewById(R.id.mainListView);
        LazyMainListViewAdapter adapter=new LazyMainListViewAdapter(this);
        list.setAdapter(adapter);


        //Test: Add some groups and channels

        SensorGroup tempGroup;
        List<SensorGroup> activeGroups = SensorGroupController.getActiveGroups();
        if(activeGroups.size()==0) {
            List<SensorGroupController.GroupInfo> strs = SensorGroupController.getAvailableGroups();
            tempGroup= SensorGroupController.activate(strs.get(0));
            tempGroup.addEventListener(this);
        } else
        {
            tempGroup = activeGroups.get(0);
        }
        SensorGroupController.getActiveGroups();

        SensorChannel ch1tmp;
        SensorChannel ch2tmp;
        List<SensorChannel> tempChannels = tempGroup.getActiveChannels();
        if(tempChannels.size()==0) {
            List<SensorGroup.ChannelInfo> avails = tempGroup.getAvailableChannels();
            ch1tmp = tempGroup.activate(avails.get(0));
            ch2tmp = tempGroup.activate(avails.get(1));
        } else {
            ch1tmp = tempChannels.get(0);
            ch2tmp = tempChannels.get(1);
        }

        Log.d("MainActivity","On create");
    }


    public MainActivity() {
        Log.d("MainActivity","Ctor");
    }

    @Override
    protected  void onRestart() {
        super.onRestart();
        Log.d("MainActivity","On  restart");
    }

    @Override
    protected  void onStart() {
        super.onStart();
        serviceHelper.Attach();
        Log.d("MainActivity","On start");
    }

    @Override
    protected  void onStop() {
        super.onStop();
        serviceHelper.Detach();
        Log.d("MainActivity","On stop");
    }
    @Override
    protected  void onPause() {
        super.onPause();
        Log.d("MainActivity","On pause");
    }

    @Override
    protected  void onResume() {
        super.onResume();
        Log.d("MainActivity","On resume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            serviceHelper.Stop();
            this.finish();
            return true;
        } else if (id==R.id.action_test) {
            if(SensorGroupController.getActiveGroups().isEmpty()) {
                SensorGroupController.activate(SensorGroupController.getAvailableGroups().get(0));
            } else {
                SensorGroupController.deactivate(SensorGroupController.getAvailableGroups().get(0));
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        Log.d("MainActivity","On destroy");
        super.onDestroy();
    }

}
