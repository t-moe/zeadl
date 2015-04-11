package ch.bfh.android.zeadl;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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


public class MainActivity extends ActionBarActivity {


    private ServiceHelper serviceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.EnsureStarted();

        setContentView(R.layout.activity_main);

        ListView list=(ListView)findViewById(R.id.mainListView);
        final LazyMainListViewAdapter adapter=new LazyMainListViewAdapter(this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //SensorGroup group = adapter.getItem(position);
                Intent i = new Intent(MainActivity.this,DetailActivity.class);
                i.putExtra("sensorGroupId",position);
                startActivity(i);
            }
        });



        //Test: Add some groups and channels

        List<SensorGroup> activeGroups = SensorGroupController.getActiveGroups();
        if(activeGroups.size()==0) {
            List<SensorGroupController.GroupInfo> availableGroups = SensorGroupController.getAvailableGroups();
            for(SensorGroupController.GroupInfo groupInfo: availableGroups) {
                SensorGroup group = SensorGroupController.activate(groupInfo);
                for(SensorGroup.ChannelInfo channelInfo : group.getAvailableChannels()) {
                    SensorChannel channel = group.activate(channelInfo);
                    channel.setColor(Color.argb(255,(int)Math.round(Math.random()*255f),(int)Math.round(Math.random()*255f),(int)Math.round(Math.random()*255f)));
                }
            }

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
            SensorGroupController.GroupInfo groupZero = SensorGroupController.getAvailableGroups().get(0);
            if(!SensorGroupController.isActive(groupZero)) {
                SensorGroupController.activate(groupZero);
            } else {
                SensorGroupController.deactivate(groupZero);
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
