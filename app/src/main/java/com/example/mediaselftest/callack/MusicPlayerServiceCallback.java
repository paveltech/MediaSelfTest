package com.example.mediaselftest.callack;

import android.support.v4.media.session.PlaybackStateCompat;

public interface MusicPlayerServiceCallback {
    void onPlaybackStart();

    void onNotificationRequired();

    void onPlaybackStop();

    void onPlaybackStateUpdated(PlaybackStateCompat newState);
}
