package ch.bfh.android.zeadl.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ch.bfh.android.zeadl.R;
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
        final MainListViewAdapter adapter=new MainListViewAdapter(this);
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

        switch(id) {
            case R.id.action_exit:
                serviceHelper.Stop();
                this.finish();
                return true;
            case R.id.action_groups:
                startActivity(new Intent(this,GroupSelectActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        Log.d("MainActivity","On destroy");
        super.onDestroy();
    }

}
