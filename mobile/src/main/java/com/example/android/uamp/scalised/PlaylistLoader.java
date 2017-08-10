package com.example.android.uamp.scalised;

import android.media.MediaMetadata;

import java.util.List;

/**
 * Created by Dan on 8/9/2017.
 */

public interface PlaylistLoader {

    List<String> getPlaylists();

    List<MediaMetadata> getSongsInPlaylist(String playlistName);

}
