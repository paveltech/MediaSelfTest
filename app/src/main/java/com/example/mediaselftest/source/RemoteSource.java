package com.example.mediaselftest.source;

import android.support.v4.media.MediaMetadataCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import timber.log.Timber;

public class RemoteSource implements MusicProviderSource{

    protected static final String CATALOG_URL = "http://storage.googleapis.com/automotive-media/music.json";
    private static final String JSON_MUSIC = "music";
    private static final String JSON_TITLE = "title";
    private static final String JSON_ALBUM = "album";
    private static final String JSON_ARTIST = "artist";
    private static final String JSON_GENRE = "genre";
    private static final String JSON_SOURCE = "source";
    private static final String JSON_IMAGE = "image";
    private static final String JSON_TRACK_NUMBER = "trackNumber";
    private static final String JSON_TOTAL_TRACK_COUNT = "totalTrackCount";
    private static final String JSON_DURATION = "duration";


    @Override
    public Iterator<MediaMetadataCompat> iterator(String mediaId) {
        try {
            int slashPos = CATALOG_URL.lastIndexOf('/');
            String path = CATALOG_URL.substring(0, slashPos + 1);
            JSONObject jsonObj = fetchJSONFromUrl(CATALOG_URL);


            ArrayList<MediaMetadataCompat> tracks = new ArrayList<>();
            if (jsonObj != null) {
                JSONArray jsonTracks = jsonObj.getJSONArray(JSON_MUSIC);
                if (jsonTracks != null) {
                    for (int j = 0; j < jsonTracks.length(); j++) {
                        tracks.add(buildFromJSON(jsonTracks.getJSONObject(j), path));
                    }
                }
            }
            return tracks.iterator();
        } catch (JSONException e) {
            Timber.d("Could not retrieve music list");
            throw new RuntimeException("Could not retrieve music list", e);
        }
    }


    /**
     *  Building json from server response
     *  here json add into the MediaMetadataCompact
     *
     *
     */

    private MediaMetadataCompat buildFromJSON(JSONObject json , String basePath) throws JSONException{

    }




    /**
     * fetch data from server
     */
    private JSONObject fetchJSONFromUrl(String urlString) throws JSONException {
        BufferedReader reader = null;
        try {
            URLConnection urlConnection = new URL(urlString).openConnection();
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream(), "iso-8859-1"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return new JSONObject(sb.toString());
        } catch (JSONException e) {
            throw e;
        } catch (Exception e) {
            Timber.d("Failed to parse the json for media list");
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}
