package com.example.mediaselftest.playback;

import android.app.Service;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.Toast;


import com.example.mediaselftest.callack.MusicPlayerServiceCallback;

import timber.log.Timber;

public class PlaybackManager implements Playback.Callback {


    private MusicPlayerServiceCallback serviceCallback;
    private QueueManager queueManager;
    private Playback playback;
    private MediaSessionCallback mediaSessionCallback;


    public PlaybackManager(MusicPlayerServiceCallback musicPlayerServiceCallback,
                           QueueManager queueManager, Playback playback) {

        this.mediaSessionCallback = new MediaSessionCallback();
        this.serviceCallback = musicPlayerServiceCallback;
        this.queueManager = queueManager;
        this.playback = playback;
        this.playback.setCallback(this);
    }

    public Playback getPlayback() {
        return playback;
    }

    public MediaSessionCallback getMediaSessionCallback() {
        return mediaSessionCallback;
    }


    public void handlePlayRequest() {
        Timber.d("(++) handlePlayRequest: mState=\" + playback.getState()");

        MediaSessionCompat.QueueItem currentMusic = queueManager.getCurrentMusic();
        if (currentMusic != null) {
            serviceCallback.onPlaybackStart();
            playback.play(currentMusic);
        }
    }

    public void handlePauseRequest() {
        Timber.d("(++) handlePauseRequest: mState=\" + playback.getState()");
        if (playback.isPlaying()) {
            playback.pause();
            serviceCallback.onPlaybackStop();
        }
    }

    /**
     * Handle a request to stop music
     *
     * @param withError Error message in case the stop has an unexpected cause. The error
     *                  message will be set in the PlaybackState and will be visible to
     *                  MediaController clients.
     */
    public void handleStopRequest(String withError) {
        playback.stop(true);
        serviceCallback.onPlaybackStop();
        updatePlaybackState(withError);
    }


    public void updatePlaybackState(String error) {
        long position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
        if (playback != null && playback.isConnected()) {
            position = playback.getCurrentStreamPosition();
        }

        // no inspection resource type
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder().setActions(getAvailableActions());
        int state = playback.getState();

        // if there is an error message, send it to the playback state

        if (error != null) {
            stateBuilder.setErrorMessage(error);
            state = PlaybackStateCompat.STATE_ERROR;
        }

        stateBuilder.setState(state, position, 1.0f, SystemClock.elapsedRealtime());
        // Set the activeQueueItemId if the current index is valid.
        MediaSessionCompat.QueueItem currentMusic = queueManager.getCurrentMusic();
        if (currentMusic != null) {
            stateBuilder.setActiveQueueItemId(currentMusic.getQueueId());
        }

        serviceCallback.onPlaybackStateUpdated(stateBuilder.build());

        if (state == PlaybackStateCompat.STATE_PLAYING ||
                state == PlaybackStateCompat.STATE_PAUSED) {
            serviceCallback.onNotificationRequired();
        }
    }


    /**
     *
     *  Implemeation of the playback.Callback interface
     *
     */
    @Override
    public void onCompletion() {
        // The media player finished playing the current song, so we go ahead
        // and start the next

        if (queueManager.skipQueuePosition(1)){
            handlePlayRequest();
            queueManager.updateMetadata();
        }else {
            // if skipping was not possible, we stop and release the resources
            handleStopRequest(null);
        }
    }

    @Override
    public void onPlaybackStatusChanged(int state) {
        updatePlaybackState(null);
    }

    @Override
    public void onError(String error) {
        updatePlaybackState(error);
    }


    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {

            if (queueManager.getCurrentMusic() == null) {
//                queueManager.setRandomQueue();
                Toast.makeText(((Service) serviceCallback).getApplicationContext(), "no queue", Toast.LENGTH_LONG).show();
            }
            handlePlayRequest();


        }

        @Override
        public void onSkipToQueueItem(long queueId) {
            queueManager.setCurrentQueueItem(queueId);
            queueManager.updateMetadata();
        }

        @Override
        public void onSeekTo(long pos) {
            playback.seekTo((int)pos);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            queueManager.setQueueFromMusic(mediaId);
            handlePlayRequest();
        }

        @Override
        public void onPause() {
            handlePauseRequest();
        }

        @Override
        public void onStop() {
            handleStopRequest(null);
        }

        @Override
        public void onSkipToNext() {
            if (queueManager.skipQueuePosition(1)){
                handlePlayRequest();
            }else {
                handleStopRequest("can't skip");
            }
            queueManager.updateMetadata();
        }

        @Override
        public void onSkipToPrevious() {
            if (queueManager.skipQueuePosition(-1)){
                handlePlayRequest();
            }else {
                handleStopRequest("can't skip");
            }
            queueManager.updateMetadata();
        }
    }


    private long getAvailableActions() {
        long actions = PlaybackStateCompat.ACTION_PLAY_PAUSE |
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH |
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
        if (playback.isPlaying()) {
            actions |= PlaybackStateCompat.ACTION_PAUSE;
        } else {
            actions |= PlaybackStateCompat.ACTION_PLAY;
        }
        return actions;
    }
}
