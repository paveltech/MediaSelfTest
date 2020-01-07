package com.example.mediaselftest.source;

import android.support.v4.media.MediaMetadataCompat;

import java.util.Iterator;

public interface MusicProviderSource {

    public Iterator<MediaMetadataCompat> iterator(String mediaID);
}
