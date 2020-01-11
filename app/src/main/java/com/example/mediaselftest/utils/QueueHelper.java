package com.example.mediaselftest.utils;


import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.example.mediaselftest.source.MusicProvider;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class QueueHelper {

    public static List<MediaSessionCompat.QueueItem> getPlayingQueue(String mediaID , MusicProvider musicProvider){
        String[] hierarchy = MediaIDHelper.getHierarchy(mediaID);
        if (hierarchy.length!=2) {
            Timber.d("could not build a playing que for this mediaID" + mediaID);
            return null;
        }

        String categoryType = hierarchy[0];
        String categoryValue = hierarchy[1];

        List<MediaMetadataCompat> tracks = musicProvider.getAllRetrievedMetadata();
        if (tracks == null){
            return null;
        }

        return convertToQueue(tracks, hierarchy[0], hierarchy[1]);
    }

    private static List<MediaSessionCompat.QueueItem> convertToQueue(Iterable<MediaMetadataCompat> tracks ,  String... categories){
        List<MediaSessionCompat.QueueItem> queue = new ArrayList<>();
        long id = 0;
        for (MediaMetadataCompat track : tracks){
            String hierarchyAwareMediaID = MediaIDHelper.createMediaID(track.getDescription().getMediaId() , categories);
            MediaMetadataCompat trackCopy = new MediaMetadataCompat.Builder(track).putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID , hierarchyAwareMediaID).build();
            // We don't expect queues to change after created, so we use the item index as the
            // queueId. Any other number unique in the queue would work.
            MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(trackCopy.getDescription(), id++);
            queue.add(item);

        }
        return queue;
    }

    public static int getMusicIndexOnQueue(Iterable<MediaSessionCompat.QueueItem> queue,
                                           String mediaId) {
        int index = 0;
        for (MediaSessionCompat.QueueItem item : queue) {
            if (mediaId.equals(item.getDescription().getMediaId())) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public static int getMusicIndexOnQueue(Iterable<MediaSessionCompat.QueueItem> queue,
                                           long queueId) {
        int index = 0;
        for (MediaSessionCompat.QueueItem item : queue) {
            if (queueId == item.getQueueId()) {
                return index;
            }
            index++;
        }
        return -1;
    }


    public static boolean isIndexPlayable(int index, List<MediaSessionCompat.QueueItem> queue) {
        return (queue != null && index >= 0 && index < queue.size());
    }
}
