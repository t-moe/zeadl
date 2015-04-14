package ch.bfh.android.zeadl.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import ch.bfh.android.zeadl.R;


public class GroupSelectActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_select);

        final ListView listView= (ListView) findViewById(R.id.groupSelectListView);
        final GroupSelectListViewAdapter adapter = new GroupSelectListViewAdapter(this);
        listView.setAdapter(adapter);
    }
}
