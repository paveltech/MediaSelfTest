package com.example.mediaselftest.playback;

import android.support.v4.media.session.MediaSessionCompat;

public interface Playback {

    void play(MediaSessionCompat.QueueItem item);

    void pause();

    void stop(boolean notifyListeners);

    int getState();

    boolean isConnected();

    boolean isPlaying();

    void seekTo(long position);

    long getCurrentStreamPosition();


    void setCallback(Callback callback);

    interface Callback {
        void onCompletion();

        void onPlaybackStatusChanged(int state);

        void onError(String error);

    }
}
