package com.example.android.uamp.scalised;

import android.content.Context;

import com.example.android.uamp.utils.LogHelper;

import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by Harvey on 9/27/2015.
 */
public class SecCountManager {

    private Context context;
    private SecCount curSecCount = null;

    public SecCountManager(Context context) {
        this.context = context;
    }

    public void startTracking(String currentMediaId, int currentStreamPosition) {
        if (curSecCount != null) {
            throw new IllegalArgumentException("We are currently tracking another Second Count");
        }

        curSecCount = new SecCount(currentMediaId, currentStreamPosition);
    }

    public void endTracking(int currentStreamPosition) {
        if (curSecCount == null) {
            return;
        }
        curSecCount.endSecCount(currentStreamPosition);
        curSecCount.writeToFile(context);
        curSecCount = null;
    }


}

class SecCount {
    private String songId;
    private int startPos;
    private Date startTime;
    private int endPos;
    private Date endTime;

    public SecCount(String songId, int startPos) {
        this.songId = songId;
        this.startPos = startPos;
        this.startTime = new Date();
    }

    public void endSecCount(int endPos) {
        this.endPos = endPos;
        this.endTime = new Date();
    }

    public void writeToFile(Context context) {
        LogHelper.i("SEC_COUNT", "Log Sec Count: Song: " + songId +
                " Pos: " + startPos + " - " + endPos +
                " Time: " + startTime.toString() + " - " + endTime.toString());

        String filename = "sec_counts.txt";
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write((songId + "\n" +
                    startPos + "\n" +
                    endPos + "\n" +
                    startTime.toString() + "\n" +
                    endTime.toString() + "\n" +
                    "===" + "\n").getBytes());
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}