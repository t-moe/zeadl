package ch.bfh.android.zeadl;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


public class DetailActivity extends ActionBarActivity {

    private TableLayout table_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Add the Tabs
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabspec = tabHost.newTabSpec("Graph");
        tabspec.setContent(R.id.tabGraph);
        tabspec.setIndicator("Graph");
        tabHost.addTab(tabspec);

        tabspec = tabHost.newTabSpec("Table");
        tabspec.setContent(R.id.tabTable);
        tabspec.setIndicator("Table");
        tabHost.addTab(tabspec);

        tabspec = tabHost.newTabSpec("Settings");
        tabspec.setContent(R.id.tabSettings);
        tabspec.setIndicator("Settings");
        tabHost.addTab(tabspec);

        // Add Table

        TableRow row = new TableRow(this);
        TableRow anotherRow = new TableRow(this);

        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));
        row.setPadding(0,1,0,1);
        anotherRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));
        anotherRow.setPadding(0,1,0,1);

        TextView tv = new TextView(this);
        TextView tv2 = new TextView(this);
        tv.setText("Column 1  ");
        tv2.setText("Column 2  ");



        row.addView(tv);
        row.addView(tv2);
        //row.addView();


        table_layout = (TableLayout) findViewById(R.id.TableData);
        table_layout.addView(row);






    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
