package net.jackhallam.videocreator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackhallam on 1/15/17.
 */

public class VideoProject {
    private List<Clip> clips;
    private String name;

    public VideoProject() {
        clips = new ArrayList<>();
    }

    public List<Clip> getClips() {
        return clips;
    }

    public void addClip(Clip clip, int i) {
        clips.add(i, clip);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void saveAsVideoFile(Runnable onSuccess, Runnable onFailure) {
        //TODO: save as a video file and call either onSuccess.run() or onFailure.run()
        onSuccess.run();
    }

    public String uploadAsVideoFile(){
        return null;
    }
}
