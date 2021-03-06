package com.example.android.uamp.model;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadata;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.example.android.uamp.utils.LogHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by Dan on 9/28/2015.
 */
public class LocalMusicProvider implements MusicProvider {
    private static final String TAG = LogHelper.makeLogTag(LocalMusicProvider.class);

    private final Context context;
    private Map<String, List<MediaMetadata>> musicListByArtist;
    private Map<String, List<MediaMetadata>> musicListByAlbum;
    private final Map<String, MediaMetadata> mMusicListByMediaId; // MediaId is the path starting at the Music Directory
    private final Set<String> mFavoriteTracks;

    public LocalMusicProvider(Context context) {
        musicListByArtist = new HashMap<>();
        musicListByAlbum = new HashMap<>();
        mMusicListByMediaId = new HashMap<>();
        mFavoriteTracks = Collections.newSetFromMap(new HashMap<String, Boolean>());
        this.context = context;

        getAllSongsFromSDCARD();
    }

    public void getAllSongsFromSDCARD()
    {
        String[] STAR = { "*" };
        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor cursor = context.getContentResolver().query(allsongsuri, STAR, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String song_name = cursor.getString(
                            cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));

                    int song_id = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media._ID));

                    String fullpath = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));

                    String album_name = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    int album_id = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                    String artist_name = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    int artist_id = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));

                    String mediaID = fullpath.replace("/storage/emulated/0/Music/","");

                            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    MediaMetadata metadata = new MediaMetadata.Builder()
                            .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, mediaID)
                            .putString(CUSTOM_METADATA_TRACK_SOURCE, data)
                            .putString(MediaMetadata.METADATA_KEY_ALBUM, album_name)
                            .putString(MediaMetadata.METADATA_KEY_ARTIST, artist_name)
                            .putLong(MediaMetadata.METADATA_KEY_DURATION, cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)))
                            .putString(MediaMetadata.METADATA_KEY_GENRE, "Country")
                            .putString(MediaMetadata.METADATA_KEY_TITLE, song_name)
                            .putLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER, cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)))
                                    .build();

                    if (!musicListByArtist.containsKey(artist_name)) {
                        musicListByArtist.put(artist_name, new ArrayList<MediaMetadata>());
                    }
                    musicListByArtist.get(artist_name).add(metadata);

                    if (!musicListByAlbum.containsKey(album_name)) {
                        musicListByAlbum.put(album_name, new ArrayList<MediaMetadata>());
                    }
                    musicListByAlbum.get(album_name).add(metadata);

                    mMusicListByMediaId.put(mediaID, metadata);

                } while (cursor.moveToNext());

            }
            cursor.close();
        }
    }

    @Override
    public Iterable<String> getArtists() {
        return sortSet(musicListByArtist.keySet());

    }

    @NonNull
    private Iterable<String> sortSet(Set<String> set) {
        List<String> artists = new ArrayList<>(set);
        Collections.sort(artists);
        return artists;
    }

    /**
     * Get music tracks of the given genre
     *
     */
    @Override
    public Iterable<MediaMetadata> getMusicsByArtist(String artist) {
        if (!musicListByArtist.containsKey(artist)) {
            return Collections.emptyList();
        }
        return musicListByArtist.get(artist);
    }


    @Override
    public Iterable<String> getAlbums() {
        return sortSet(musicListByAlbum.keySet());

    }

    /**
     * Get music tracks of the given album
     *
     */
    @Override
    public Iterable<MediaMetadata> getMusicsByAlbum(String album) {
        if (!musicListByAlbum.containsKey(album)) {
            return Collections.emptyList();
        }
        return musicListByAlbum.get(album);
    }
    /**
     * Very basic implementation of a search that filter music tracks with title containing
     * the given query.
     *
     */
    @Override
    public Iterable<MediaMetadata> searchMusicBySongTitle(String query) {
        return searchMusic(MediaMetadata.METADATA_KEY_TITLE, query);
    }

    /**
     * Very basic implementation of a search that filter music tracks with album containing
     * the given query.
     *
     */
    @Override
    public Iterable<MediaMetadata> searchMusicByAlbum(String query) {
        return searchMusic(MediaMetadata.METADATA_KEY_ALBUM, query);
    }

    /**
     * Very basic implementation of a search that filter music tracks with artist containing
     * the given query.
     *
     */
    @Override
    public Iterable<MediaMetadata> searchMusicByArtist(String query) {
        return searchMusic(MediaMetadata.METADATA_KEY_ARTIST, query);
    }

    Iterable<MediaMetadata> searchMusic(String metadataField, String query) {
        ArrayList<MediaMetadata> result = new ArrayList<>();
        query = query.toLowerCase(Locale.US);
        for (MediaMetadata track : mMusicListByMediaId.values()) {
            if (track.getString(metadataField).toLowerCase(Locale.US)
                    .contains(query)) {
                result.add(track);
            }
        }
        return result;
    }

    @Override
    public MediaMetadata getMusic(String mediaId) {
        return mMusicListByMediaId.get(mediaId);
    }

    @Override
    public void updateMusic(String musicId, MediaMetadata metadata) {

    }

    @Override
    public void setFavorite(String musicId, boolean favorite) {

    }

    @Override
    public boolean isFavorite(String musicId) {
        return false;
    }

    @Override
    public boolean isInitialized() {
        return true;
    }

    @Override
    public void retrieveMediaAsync(Callback callback) {
    }
}
