package com.example.android.uamp.scalised;

import com.example.android.uamp.utils.LogHelper;

import java.util.Date;

/**
 * Created by Dan on 9/27/2015.
 */
public class SecCountManager {

    private static SecCount curSecCount = null;

    public static void startTracking(String currentMediaId, int currentStreamPosition) {
        if (curSecCount != null) {
            throw new IllegalArgumentException("We are currently tracking another Second Count");
        }

        curSecCount = new SecCount(currentMediaId, currentStreamPosition);
    }

    public static void endTracking(int currentStreamPosition) {
        if (curSecCount == null) {
            return;
        }
        curSecCount.saveSecCount(currentStreamPosition);
        curSecCount = null;
    }


}

class SecCount {
    private String songId;
    private int startPos;
    private Date startTime;

    public SecCount(String songId, int startPos) {
        this.songId = songId;
        this.startPos = startPos;
        this.startTime = new Date();
    }

    public void saveSecCount(int endPos) {
        LogHelper.w("SEC_COUNT", "Log Sec Count: Song: " + songId +
                " Pos: " + startPos + " - " + endPos +
                " Time: " + startTime.toString() + " - " + new Date().toString());
    }

}