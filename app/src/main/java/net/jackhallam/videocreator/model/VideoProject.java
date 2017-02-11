package net.jackhallam.videocreator.model;

import com.google.firebase.database.Exclude;

/**
 * Created by jackhallam on 1/15/17.
 */

public class VideoProject {
    private String key;
    private String title;
//    private List<Clip> clips;

    public VideoProject() {
//        clips = new ArrayList<>();
        title = "";
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public List<Clip> getClips() {
//        return clips;
//    }
//
//    public void setClips(List<Clip> clips) {
//        this.clips = clips;
//    }
}
