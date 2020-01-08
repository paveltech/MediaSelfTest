package com.example.mediaselftest.source;

import android.support.v4.media.MediaMetadataCompat;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private final CopyOnWriteArrayList<MediaMetadataCompat> mediList;

    ExecutorService executorService = Executors.newSingleThreadExecutor();




}
