package ch.bfh.android.zeadl.activity;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;

import ch.bfh.android.zeadl.sensor.SensorGroup;

import static android.app.PendingIntent.getActivity;

/**
 * Created by adrian on 13.04.15.
 */
public class FileClass {

    //public FileClass(){}


    public boolean checkExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }

    public boolean checkExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getStorageDir(String filename) {
        File file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                filename);
        if (!file.mkdirs()) {
            Log.e("TableSave", "Directory not created");
        }
        return file;
    }

    private final String mFilenameBase = "/sdcard/DCIM/ZeadlData";



    public String saveSegment(SensorGroup.DataSegment segment) throws IOException
    {
        if(checkExternalStorageWritable()) {
            String filename = mFilenameBase + System.currentTimeMillis()+".txt";
            BufferedWriter bfw = new BufferedWriter(new FileWriter(filename));
            SimpleDateFormat df = new SimpleDateFormat("dd hh:mm:ss:SSS");

            bfw.write("Time, ");
            for (int i = 0; i < segment.getChannels().size(); i++) {
                bfw.write(segment.getChannels().get(i).getName());
                bfw.write(", ");
            }

            for (int i = 0; i < segment.getEntries().size(); i++) {
                bfw.newLine();
                bfw.write(df.format(segment.getEntries().get(i).getTime()));
                for (int j = 0; j < segment.getChannels().size(); j++) {
                    bfw.write(segment.getEntries().get(i).getChannelData().get(j) + ", ");
                }
            }

            bfw.close();
            return filename;
        }
        return "";
    }


}
