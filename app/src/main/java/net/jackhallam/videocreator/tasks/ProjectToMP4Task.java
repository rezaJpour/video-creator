package net.jackhallam.videocreator.tasks;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import net.jackhallam.videocreator.MainActivity;
import net.jackhallam.videocreator.model.Clip;
import net.jackhallam.videocreator.model.VideoProject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by jackhallam on 2/10/17.
 */

public class ProjectToMP4Task extends AsyncTask<VideoProject, Void, File> {

    private MainActivity mainActivity;

    public ProjectToMP4Task(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    protected File doInBackground(VideoProject... videoProjects) {
        try {
            MultipartUtility multipart = new MultipartUtility(Util.BACKEND_URL + "createVideoProject", "UTF-8");
            multipart.addFormField("videoProject", new Gson().toJson(videoProjects[0]));
            List<Clip> clips = videoProjects[0].getClips();
            for (Clip clip : clips) {
                File videoFile = new File(clip.getPath());
                multipart.addFilePart(clip.getKey(), videoFile);
            }

            List<String> response = multipart.finish();
            String videoURL = response.get(0);


            ContextWrapper cw = new ContextWrapper(mainActivity);
            File directory = cw.getDir("", Context.MODE_PRIVATE);


            URL url = new URL(videoURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();


            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream input = httpURLConnection.getInputStream();
            OutputStream output = new FileOutputStream(directory.getAbsolutePath() + "/video.mp4");

            byte data[] = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                output.write(data, 0, count);
            }

            File videoFile = new File(directory.getAbsolutePath() + "/video.mp4");

            return videoFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        mainActivity.mp4Updated(file);
    }
}
