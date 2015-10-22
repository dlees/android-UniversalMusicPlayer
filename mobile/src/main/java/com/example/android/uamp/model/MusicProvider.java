package com.example.android.uamp.model;

import android.media.MediaMetadata;

/**
 * Created by Dan on 9/27/2015.
 */
public interface MusicProvider {

    public final String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";

    Iterable<String> getGenres();
    Iterable<String> getAlbums();

    Iterable<MediaMetadata> getMusicsByGenre(String genre);
    Iterable<MediaMetadata> getMusicsByAlbum(String album);

    Iterable<MediaMetadata> searchMusicBySongTitle(String query);

    Iterable<MediaMetadata> searchMusicByAlbum(String query);

    Iterable<MediaMetadata> searchMusicByArtist(String query);

    MediaMetadata getMusic(String musicId);

    void updateMusic(String musicId, MediaMetadata metadata);

    void setFavorite(String musicId, boolean favorite);

    boolean isFavorite(String musicId);

    boolean isInitialized();

    void retrieveMediaAsync(Callback callback);

    public interface Callback {
        void onMusicCatalogReady(boolean success);
    }

}
