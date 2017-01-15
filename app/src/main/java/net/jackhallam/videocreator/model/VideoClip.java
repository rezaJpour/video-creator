package net.jackhallam.videocreator.model;

/**
 * Created by jackhallam on 1/15/17.
 */

public class VideoClip {
    private String path;

    public VideoClip(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
