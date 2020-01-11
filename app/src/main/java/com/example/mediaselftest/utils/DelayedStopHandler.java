package com.example.mediaselftest.utils;

import android.os.Handler;
import android.os.Message;

import com.example.mediaselftest.playback.MusicPlayerService;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public class DelayedStopHandler extends Handler {
    private final WeakReference<MusicPlayerService> mWeakReference;

    private DelayedStopHandler(MusicPlayerService service) {
        mWeakReference = new WeakReference<>(service);
    }

    @Override
    public void handleMessage(Message msg) {
        MusicPlayerService service = mWeakReference.get();
        if (service != null && service.playbackManager.getPlayback() != null) {



            if (service.playbackManager.getPlayback().isPlaying()) {
                FireLog.d(TAG, "Ignoring delayed stop since the media player is in use.");
                return;
            }
            Timber.d("Stopping service with delay handler.");
            service.stopSelf();
        }
    }
}
