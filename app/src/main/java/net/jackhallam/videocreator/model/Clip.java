package net.jackhallam.videocreator.model;

/**
 * Created by jackhallam on 1/15/17.
 */

public class Clip {
    private VideoClip videoClip;
    private long start;
    private long end;

    public Clip(VideoClip videoClip, long start, long end) {
        this.videoClip = videoClip;
        this.start = start;
        this.end = end;
    }

    public VideoClip getVideoClip() {
        return videoClip;
    }

    public void setVideoClip(VideoClip videoClip) {
        this.videoClip = videoClip;
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
}
