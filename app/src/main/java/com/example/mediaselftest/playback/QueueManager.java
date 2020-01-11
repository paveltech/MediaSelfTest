package com.example.mediaselftest.playback;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.example.mediaselftest.source.MusicProvider;
import com.example.mediaselftest.utils.MediaIDHelper;
import com.example.mediaselftest.utils.QueueHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QueueManager {

    private MusicProvider musicProvider;
    private MetadataUpdateListener metadataUpdateListener;

    //
    private List<MediaSessionCompat.QueueItem> playingQueue;
    private int currentIndex;

    public QueueManager(MusicProvider musicProvider , MetadataUpdateListener metadataUpdateListener){
        this.metadataUpdateListener = metadataUpdateListener;
        this.musicProvider = musicProvider;
        playingQueue = Collections.synchronizedList(new ArrayList<MediaSessionCompat.QueueItem>());
        currentIndex = 0;
    }

    public boolean isSameBrowingCategory(String mediaId){
        String[] newBrowseHierarchy = MediaIDHelper.getHierarchy(mediaId);
        MediaSessionCompat.QueueItem current = getCurrentMusic();
        if (current==null){
            return false;
        }
        String[] currentBrowseHierarchy = MediaIDHelper.getHierarchy(current.getDescription().getMediaId());
        return Arrays.equals(newBrowseHierarchy, currentBrowseHierarchy);
    }

    protected void setCurrentQueue(List<MediaSessionCompat.QueueItem> newQueue,
                                   String initialMediaId) {
        playingQueue = newQueue;
        int index = 0;
        if (initialMediaId != null) {
            index = QueueHelper.getMusicIndexOnQueue(playingQueue, initialMediaId);
        }
        currentIndex = Math.max(index, 0);
        metadataUpdateListener.onQueueUpdated(newQueue);
    }

    private void setCurrentQueueIndex(int index) {
        if (index >= 0 && index < playingQueue.size()) {
            currentIndex = index;
            metadataUpdateListener.onCurrentQueueIndexUpdated(currentIndex);
        }
    }

    public boolean setCurrentQueueItem(long queueId) {
        // set the current index on queue from the queue Id:
        int index = QueueHelper.getMusicIndexOnQueue(playingQueue, queueId);
        setCurrentQueueIndex(index);
        return index >= 0;
    }

    public boolean setCurrentQueueItem(String mediaId) {
        // set the current index on queue from the music Id:
        int index = QueueHelper.getMusicIndexOnQueue(playingQueue, mediaId);
        setCurrentQueueIndex(index);
        return index >= 0;
    }

    public MediaSessionCompat.QueueItem getCurrentMusic() {
        if (!QueueHelper.isIndexPlayable(currentIndex, playingQueue)) {
            return null;
        }
        return playingQueue.get(currentIndex);
    }

    public boolean skipQueuePosition(int amount){
        int index = currentIndex+amount;
        if (index<0){
            // skip backwards before the first song will keep you on the first song
            index = 0;
        }else {
            // skip forwards when in last song will cycle back to start of the queue
            index %= playingQueue.size();
        }
        if (!QueueHelper.isIndexPlayable(index , playingQueue)){
            return false;
        }
        currentIndex = index;
        return true;
    }


    public void setQueueFromMusic(String mediaId) {
        // The mediaId used here is not the unique musicId. This one comes from the
        // MediaBrowser, and is actually a "hierarchy-aware mediaID": a concatenation of
        // the hierarchy in MediaBrowser and the actual unique musicID. This is necessary
        // so we can build the correct playing queue, based on where the track was
        // selected from.
        boolean canReuseQueue = false;
        if (isSameBrowingCategory(mediaId)) {
            canReuseQueue = setCurrentQueueItem(mediaId);
        }
        if (!canReuseQueue) {
            setCurrentQueue(QueueHelper.getPlayingQueue(mediaId, musicProvider), mediaId);
        }
        updateMetadata();
    }

    public interface MetadataUpdateListener{
        void onMetadataChanged(MediaMetadataCompat metadata);
        void onMetadataRetrieveError();
        void onCurrentQueueIndexUpdated(int queueIndex);
        void onQueueUpdated(List<MediaSessionCompat.QueueItem> newQueue);
    }

    public void updateMetadata() {
        MediaSessionCompat.QueueItem currentMusic = getCurrentMusic();
        if (currentMusic == null || currentMusic.getDescription() == null
                || currentMusic.getDescription().getMediaId() == null) {
            metadataUpdateListener.onMetadataRetrieveError();
            return;
        }
        final String musicId = MediaIDHelper.extractMusicIDFromMediaID(currentMusic.getDescription().getMediaId());


        MediaMetadataCompat metadata = musicProvider.getMusic(musicId);
        if (metadata == null) {
            throw new IllegalArgumentException("Invalid musicId " + musicId);
        }

        metadataUpdateListener.onMetadataChanged(metadata);
    }
}
