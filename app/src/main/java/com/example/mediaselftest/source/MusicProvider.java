package com.example.mediaselftest.source;

import android.os.AsyncTask;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import com.example.mediaselftest.utils.MediaIDHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

import static com.example.mediaselftest.utils.MediaIDHelper.MEDIA_ID_TRACKS_ALL;

public class MusicProvider {

    // only playable music list, gets updated only when medialist is playable

    /**
     * The design of the CopyOnWriteArrayList uses an interesting technique
     * to make it thread-safe without a need for synchronization.
     * When we are using any of the modify methods – such as add()
     * or remove() – the whole content of the CopyOnWriteArrayList
     * is copied into the new internal copy.
     */

    private final CopyOnWriteArrayList<MediaMetadataCompat> musicList;

    // media list contains browsable + playable media items
    private final CopyOnWriteArrayList<MediaMetadataCompat> mediaList;

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    private MusicProviderSource remoteSouce;

    public MusicProvider() {
        this(new RemoteSource());
    }

    public MusicProvider(MusicProviderSource remoteSource) {
        this.remoteSouce = remoteSource;
        musicList = new CopyOnWriteArrayList<>();
        mediaList = new CopyOnWriteArrayList<>();

    }

    public static MusicProvider getInstance() {
        return LazyHolder.INSTANCE;
    }


    private static class LazyHolder {
        public static final MusicProvider INSTANCE = new MusicProvider();
    }

    public void retrieveMediaAsync(final String mediaId, final Callback callback) {
        // Asynchronously load the music catalog in a separate thread
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return retrieveMedia(mediaId);
            }

            @Override
            protected void onPostExecute(Boolean initialized) {
                if (callback != null) {
                    callback.onMusicCatalogReady(initialized);
                }
            }
        }.executeOnExecutor(executorService);
    }



    private synchronized boolean retrieveMedia(String mediaID) {
        boolean initialized = false;
        mediaList.clear();
        try {
            Iterator<MediaMetadataCompat> tracks = remoteSouce.iterator(mediaID);
            while (tracks.hasNext()) {
                MediaMetadataCompat mediaMetadataCompat = tracks.next();
                mediaList.add(mediaMetadataCompat);
            }
            initialized = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return initialized;
    }

    public List<MediaBrowserCompat.MediaItem> getChildren(String mediaID) {
        List<MediaBrowserCompat.MediaItem> mediaItemList = new ArrayList<>();
        musicList.addAll(mediaList);

        for (MediaMetadataCompat metadata : getAllRetrievedMetadata()) {
            mediaItemList.add(createTracksMediaItem(metadata));
        }
        return mediaItemList;

    }


    public List<MediaMetadataCompat> getAllRetrievedMetadata() {
        ArrayList<MediaMetadataCompat> result = new ArrayList<>();
        for (MediaMetadataCompat track : mediaList) {
            result.add(track);
        }
        return result;
    }

    private MediaBrowserCompat.MediaItem createTracksMediaItem(MediaMetadataCompat metadata) {
        // Since mediaMetadata fields are immutable, we need to create a copy, so we
        // can set a hierarchy-aware mediaID. We will need to know the media hierarchy
        // when we get a onPlayFromMusicID call, so we can create the proper queue based
        // on where the music was selected from (by artist, by genre, random, etc)


        String hierarchyAwareMediaID = MediaIDHelper.createMediaID(metadata.getDescription().getMediaId(), "", MEDIA_ID_TRACKS_ALL);
        Timber.d("hierarch " + hierarchyAwareMediaID);

        MediaMetadataCompat copy = new MediaMetadataCompat.Builder(metadata).putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, hierarchyAwareMediaID).build();

        return new MediaBrowserCompat.MediaItem(copy.getDescription(),
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);

    }

    public interface Callback {
        void onMusicCatalogReady(boolean success);
    }


}
