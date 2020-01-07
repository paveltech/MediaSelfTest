package com.example.mediaselftest.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.media.MediaBrowserCompat;

public class MediaItemWrapper implements Parcelable {

    private MediaBrowserCompat.MediaItem mediaItem;
    private String typeTitle;
    private int category;

    public MediaItemWrapper(String typeTitle){
        this.typeTitle = typeTitle;
    }

    public MediaItemWrapper(int category , MediaBrowserCompat.MediaItem mediaItem){
        this.category = category;
        this.mediaItem = mediaItem;
    }

    public MediaBrowserCompat.MediaItem getMediaItem() {
        return mediaItem;
    }

    public int getCategory() {
        return category;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    protected MediaItemWrapper(Parcel in) {
        mediaItem = in.readParcelable(MediaBrowserCompat.MediaItem.class.getClassLoader());
        typeTitle = in.readString();
        category = in.readInt();
    }

    public static final Creator<MediaItemWrapper> CREATOR = new Creator<MediaItemWrapper>() {
        @Override
        public MediaItemWrapper createFromParcel(Parcel in) {
            return new MediaItemWrapper(in);
        }

        @Override
        public MediaItemWrapper[] newArray(int size) {
            return new MediaItemWrapper[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mediaItem, flags);
        dest.writeString(typeTitle);
        dest.writeInt(category);
    }
}
