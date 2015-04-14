package ch.bfh.android.zeadl.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import ch.bfh.android.zeadl.R;
import ch.bfh.android.zeadl.sensor.SensorChannel;
import ch.bfh.android.zeadl.sensor.SensorGroup;
import ch.bfh.android.zeadl.sensor.SensorGroupController;

/**
 * Created by timo on 4/11/15.
 */
public class GroupSelectListViewAdapter extends ArrayAdapter<SensorGroupController.GroupInfo> {
    private Activity activity;
    private LayoutInflater inflater=null;

    public GroupSelectListViewAdapter(Activity a) {
        super(a, R.layout.main_list_row, SensorGroupController.getAvailableGroups());
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.group_select_list_row, null);

        final CheckBox cb = (CheckBox)vi.findViewById(R.id.checkBox);

        if(getCount()==0) {
            Log.w("GroupSelectListView", "Unexpected empty list");
            return null;
        }
        final SensorGroupController.GroupInfo groupInfo = getItem(position);
        cb.setText(groupInfo.getName());
        boolean active = SensorGroupController.isActive(groupInfo);
        cb.setChecked(active);

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean active = cb.isChecked();
                if(active) {
                    SensorGroup group = SensorGroupController.activate(groupInfo);

                    //testwise
                    if(group.getActiveChannels().isEmpty()) {
                        for (SensorGroup.ChannelInfo channelInfo : group.getAvailableChannels()) {
                            SensorChannel channel = group.activate(channelInfo);
                            if (channel.getColor() == Color.WHITE) {
                                channel.setColor(Color.argb(255, (int) Math.round(Math.random() * 255f), (int) Math.round(Math.random() * 255f), (int) Math.round(Math.random() * 255f)));
                            }
                        }
                    }
                } else {
                    SensorGroupController.deactivate(groupInfo);
                }
            }
        });

        return vi;
    }

}

