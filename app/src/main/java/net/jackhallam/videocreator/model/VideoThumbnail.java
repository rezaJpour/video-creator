package net.jackhallam.videocreator.model;

import android.graphics.Bitmap;

/**
 * Created by laeschjs on 1/29/2017.
 */

public class VideoThumbnail {

    private Bitmap bitmap;
    private int mins;
    private int secs;
    private int milli;

    public VideoThumbnail(Bitmap b, int min, int sec, int mil){
        bitmap = b;
        mins = min;
        secs = sec;
        milli = mil;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getMins() {
        return mins;
    }

    public int getSecs() {
        return secs;
    }

    public int getMilli() {
        return milli;
    }
}
