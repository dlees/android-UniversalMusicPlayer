package com.example.android.uamp.model;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadata;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

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
    private Map<String, List<MediaMetadata>> mMusicListByGenre;
    private Map<String, List<MediaMetadata>> mMusicListByAlbum;
    private final Map<String, MediaMetadata> mMusicListById;
    private final Set<String> mFavoriteTracks;

    public LocalMusicProvider(Context context) {
        mMusicListByGenre = new HashMap<>();
        mMusicListByAlbum = new HashMap<>();
        mMusicListById = new HashMap<>();
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

                    if (!mMusicListByGenre.containsKey(artist_name)) {
                        mMusicListByGenre.put(artist_name, new ArrayList<MediaMetadata>());
                    }
                    mMusicListByGenre.get(artist_name).add(metadata);

                    if (!mMusicListByAlbum.containsKey(album_name)) {
                        mMusicListByAlbum.put(album_name, new ArrayList<MediaMetadata>());
                    }
                    mMusicListByAlbum.get(album_name).add(metadata);

                    mMusicListById.put(mediaID, metadata);

                } while (cursor.moveToNext());

            }
            cursor.close();
        }
    }

    @Override
    public Iterable<String> getGenres() {
        return mMusicListByGenre.keySet();

    }

    /**
     * Get music tracks of the given genre
     *
     */
    @Override
    public Iterable<MediaMetadata> getMusicsByGenre(String genre) {
        if (!mMusicListByGenre.containsKey(genre)) {
            return Collections.emptyList();
        }
        return mMusicListByGenre.get(genre);
    }


    @Override
    public Iterable<String> getAlbums() {
        return mMusicListByAlbum.keySet();

    }

    /**
     * Get music tracks of the given album
     *
     */
    @Override
    public Iterable<MediaMetadata> getMusicsByAlbum(String album) {
        if (!mMusicListByAlbum.containsKey(album)) {
            return Collections.emptyList();
        }
        return mMusicListByAlbum.get(album);
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
        for (MediaMetadata track : mMusicListById.values()) {
            if (track.getString(metadataField).toLowerCase(Locale.US)
                    .contains(query)) {
                result.add(track);
            }
        }
        return result;
    }

    @Override
    public MediaMetadata getMusic(String musicId) {
        return mMusicListById.get(musicId);
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
