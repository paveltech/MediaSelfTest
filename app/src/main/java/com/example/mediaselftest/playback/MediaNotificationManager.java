package com.example.mediaselftest.playback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MediaNotificationManager extends BroadcastReceiver {

    public static final String ACTION_PAUSE = "com.firekernel.player.pause";
    public static final String ACTION_PLAY = "com.firekernel.player.play";
    public static final String ACTION_PREV = "com.firekernel.player.prev";
    public static final String ACTION_NEXT = "com.firekernel.player.next";
    public static final String ACTION_STOP = "com.firekernel.player.stop";

    /// channel id
    private static final String CHANNEL_ID = "com.firekernel.player.MUSIC_CHANNEL_ID";


    private static final int NOTIFICATION_ID = 1 << 2;
    private static final int REQUEST_CODE = 1 << 3;

    private final MusicPlayerService service;

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
