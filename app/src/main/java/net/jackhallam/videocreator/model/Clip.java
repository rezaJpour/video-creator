package net.jackhallam.videocreator.model;

import com.google.firebase.database.Exclude;

/**
 * Created by jackhallam on 1/15/17.
 */

public class Clip {
    private String key;
    private String path;
    private long start;
    private long end;
    private long lengthOfClip;
    private String nextClipKey;

    public Clip() {
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getNextClipKey() {
        return nextClipKey;
    }

    public void setNextClipKey(String nextClipKey) {
        this.nextClipKey = nextClipKey;
    }

    public long getLengthOfClip() {
        return lengthOfClip;
    }

    public void setLengthOfClip(long lengthOfClip) {
        this.lengthOfClip = lengthOfClip;
    }
}
