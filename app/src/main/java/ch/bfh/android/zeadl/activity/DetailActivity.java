package ch.bfh.android.zeadl.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

import ch.bfh.android.zeadl.R;
import ch.bfh.android.zeadl.sensor.SensorGroup;
import ch.bfh.android.zeadl.sensor.SensorGroupController;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int SensorGroupId = extras.getInt("sensorGroupId");
            SensorGroup group = SensorGroupController.getActiveGroups().get(SensorGroupId);

            //The SensorGroup instance contains all the infos you need. The methods are documented.

            TextView textView = (TextView) findViewById(R.id.testtext);
            textView.setText(group.getName());
        }


    }



}
