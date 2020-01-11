package com.example.mediaselftest.playback;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import com.example.mediaselftest.R;
import com.example.mediaselftest.callack.MusicPlayerServiceCallback;
import com.example.mediaselftest.source.MusicProvider;
import com.example.mediaselftest.utils.DelayedStopHandler;

import java.util.List;

public class MusicPlayerService extends MediaBrowserServiceCompat implements MusicPlayerServiceCallback {


    public static final String ACTION_CMD = "com.firekernel.player.ACTION_CMD";
    public static final String CMD_NAME = "CMD_NAME";
    public static final String CMD_PAUSE = "CMD_PAUSE";



    // Delay stopSelf by using a handler.
    private static final int STOP_DELAY = 10 * 1000; //10 seconds
    private final DelayedStopHandler delayedStopHandler = new DelayedStopHandler(this);
    private MusicProvider musicProvider;
    private PlaybackManager playbackManager;
    private MediaSessionCompat mediaSessionCompat;
    private MediaNotificationManager mediaNotificationManager;

    private QueueManager.MetadataUpdateListener metadataUpdateListener = new QueueManager.MetadataUpdateListener() {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            mediaSessionCompat.setMetadata(metadata);
        }

        @Override
        public void onMetadataRetrieveError() {
            playbackManager.updatePlaybackState(getString(R.string.error_no_metadata));
        }

        @Override
        public void onCurrentQueueIndexUpdated(int queueIndex) {
            playbackManager.handlePlayRequest();
        }

        @Override
        public void onQueueUpdated(List<MediaSessionCompat.QueueItem> newQueue) {
            mediaSessionCompat.setQueue(newQueue);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mediaSessionCompat = new MediaSessionCompat(this, MusicPlayerService.class.getSimpleName());
        setSessionToken(mediaSessionCompat.getSessionToken());

        musicProvider = new MusicProvider().getInstance();
        QueueManager queueManager = new QueueManager(musicProvider, metadataUpdateListener);
        Mediapla
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    @Override
    public void onPlaybackStart() {

    }

    @Override
    public void onNotificationRequired() {

    }

    @Override
    public void onPlaybackStop() {

    }

    @Override
    public void onPlaybackStateUpdated(PlaybackStateCompat newState) {

    }
}
