package ch.bfh.android.zeadl.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import afzkl.development.colorpickerview.dialog.ColorPickerDialog;
import ch.bfh.android.zeadl.R;
import ch.bfh.android.zeadl.sensor.SensorChannel;
import ch.bfh.android.zeadl.sensor.SensorGroup;
import ch.bfh.android.zeadl.sensor.SensorGroupController;

/**
 * Created by timo on 4/11/15.
 */
public class DetailChannelListViewAdapter extends ArrayAdapter<SensorGroup.ChannelInfo> {
    private Activity mActivity;
    private LayoutInflater mInflater =null;
    private SensorGroup mSensorGroup;
    public DetailChannelListViewAdapter(Activity a, SensorGroup group) {
        super(a, R.layout.detail_channels_list_row, group.getAvailableChannels());
        mActivity = a;
        mSensorGroup=group;
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = mInflater.inflate(R.layout.detail_channels_list_row, null);

        final CheckBox nameCb = (CheckBox)vi.findViewById(R.id.channelName);
        final View colorView = vi.findViewById(R.id.channelColor);

        if(getCount()==0) {
            Log.w("DetailChannelListView", "Unexpected empty list");
            return null;
        }

        final SensorGroup.ChannelInfo channelInfo = getItem(position);
        boolean active = mSensorGroup.isActive(channelInfo);

        nameCb.setText(channelInfo.getName());
        nameCb.setChecked(active);
        nameCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean active = nameCb.isChecked();
                if(active) {
                    mSensorGroup.activate(channelInfo);
                } else {
                    mSensorGroup.deactivate(channelInfo);
                }
            }
        });

        colorView.setBackgroundColor(channelInfo.getInstance().getColor());
        colorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ColorPickerDialog colorDialog = new ColorPickerDialog(mActivity, channelInfo.getInstance().getColor());
                colorDialog.setAlphaSliderVisible(false);
                colorDialog.setTitle("Pick a Color for Channel\n"+channelInfo.getName());

                colorDialog.setButton(DialogInterface.BUTTON_POSITIVE, mActivity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       int newColor = colorDialog.getColor();
                       channelInfo.getInstance().setColor(newColor);
                       colorView.setBackgroundColor(newColor);
                   }
                });

                colorDialog.setButton(DialogInterface.BUTTON_NEGATIVE, mActivity.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       //Nothing to do here.
                   }
                });
                colorDialog.show();
            }
        });

        return vi;
    }

}

