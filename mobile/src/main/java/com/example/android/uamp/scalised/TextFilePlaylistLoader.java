package com.example.android.uamp.scalised;

import android.media.MediaMetadata;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.example.android.uamp.model.MusicProvider;
import com.example.android.uamp.utils.LogHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 8/9/2017.
 */

public class TextFilePlaylistLoader implements PlaylistLoader {

    public static final String PLAYLIST_PREFIX = "Playlist-";


    private MusicProvider musicProvider;

    public TextFilePlaylistLoader(MusicProvider musicProvider) {
        this.musicProvider = musicProvider;
    }

    @Override
    public List<String> getPlaylists() {
        File[] files = getFilesForPlaylists();
        List<String> playlistNames = new ArrayList<>(files.length);

        for (File file : files) {
            playlistNames.add(file.getName().replace(PLAYLIST_PREFIX,"").replace(".txt", ""));
        }
        return playlistNames;
    }

    private File[] getFilesForPlaylists() {
        File dir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS)));
        return dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(PLAYLIST_PREFIX);
            }
        });
    }

    @Override
    public List<MediaMetadata> getSongsInPlaylist(String playlistName) {

        List<String> filenames = getFilenames(playlistName);

        return getMediaMetadataForFilenames(filenames);
    }


    @NonNull
    private List<String> getFilenames(String playlistName) {
        try {
            FileInputStream fstream = new FileInputStream(getFile(PLAYLIST_PREFIX + playlistName + ".txt"));
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String strLine;

            List<String> mediaIds = new ArrayList<>();
            while ((strLine = br.readLine()) != null)   {
                mediaIds.add(strLine);
            }

            br.close();
            return mediaIds;

        } catch (IOException e) {
            throw new RuntimeException("Can't find file for: " + playlistName, e);
        }
    }

    private List<MediaMetadata> getMediaMetadataForFilenames(List<String> mediaIds) {

        List<MediaMetadata> metadataList = new ArrayList<>();
        for (String mediaId: mediaIds) {
            if (musicProvider.getMusic(mediaId) == null) {
                continue;
            }

            metadataList.add(musicProvider.getMusic(mediaId));
        }
        return metadataList;

    }

    public File getFile(String fileName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), fileName);
        return file;
    }
}
