package com.example.android.uamp.scalised;

import android.content.Context;
import android.media.MediaMetadata;
import android.os.Environment;

import com.example.android.uamp.utils.LogHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by Daniel on 9/27/2015.
 */
public class SecCountManager {
    private SecCount curSecCount = null;

    public void startTracking(MediaMetadata mediaMetadata, int currentStreamPosition) {
        if (curSecCount != null) {
            LogHelper.e("SEC_COUNT", "We are currently tracking another SEC_COUNT");
            throw new IllegalArgumentException("We are currently tracking another Second Count");
        }

        curSecCount = new SecCount(mediaMetadata.getString(MediaMetadata.METADATA_KEY_MEDIA_ID), currentStreamPosition);
    }

    public void endTracking(int currentStreamPosition) {
        if (curSecCount == null) {
            return;
        }
        curSecCount.endSecCount(currentStreamPosition);
        curSecCount.writeToFile();
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

    public File getFile(String fileName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), fileName);
        return file;
    }

    public void writeToFile() {
        LogHelper.w("SEC_COUNT", "Log Sec Count: Song: " + songId +
                " Pos: " + startPos + " - " + endPos +
                " Time: " + startTime.toString() + " - " + endTime.toString());


        String filename = "sec_counts.txt";
        FileOutputStream outputStream;


        try {
            FileWriter fileWriter = new FileWriter(getFile("secCount.txt"), true);
            fileWriter.append((songId + "\n" +
                    startPos + "\n" +
                    endPos + "\n" +
                    startTime.toString() + "\n" +
                    endTime.toString() + "\n" +
                    "===" + "\n"));
            fileWriter.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogHelper.e("SEC_COUNT", "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the manifest?");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}