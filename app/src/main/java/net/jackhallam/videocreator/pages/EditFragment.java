package net.jackhallam.videocreator.pages;


import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import net.jackhallam.videocreator.MP4UpdateListener;
import net.jackhallam.videocreator.MainActivity;
import net.jackhallam.videocreator.R;
import net.jackhallam.videocreator.TimelineAdapter;
import net.jackhallam.videocreator.tasks.ProjectToMP4Task;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment implements MP4UpdateListener {

    private TimelineAdapter mTimelineAdapter;
    private MainActivity mainActivity;

    private ImageView playButton;
    private VideoView videoView;

    private boolean downloadingVideo = false;

    public EditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_edit, container, false);

        RecyclerView recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recycler_view);
        mTimelineAdapter = new TimelineAdapter(getActivity(), recyclerView);
        recyclerView.setAdapter(mTimelineAdapter);

        playButton = (ImageView) inflatedView.findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playVideo();
            }
        });
        videoView = (VideoView) inflatedView.findViewById(R.id.edit_page_video_view);

        return inflatedView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case TimelineAdapter.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String[] projection = {MediaStore.Video.Thumbnails.DATA};
                    CursorLoader cursorL = new CursorLoader(getContext(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                            null, // Return all rows
                            null, null);
                    Cursor cursor = cursorL.loadInBackground();
                    mTimelineAdapter.setCursor(cursor);
                    mTimelineAdapter.startAsync();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        mTimelineAdapter.setProjectRef(mainActivity.getVideoProjectDatabaseReference().child("clips"));
        mainActivity.registerMP4UpdateListener(this);
    }

    public void setProjectRef() {
        mTimelineAdapter.setProjectRef(mainActivity.getVideoProjectDatabaseReference().child("clips"));
    }

    private void playVideo() {
        downloadingVideo = true;
        Toast.makeText(mainActivity, "Loading...", Toast.LENGTH_SHORT).show();
        new ProjectToMP4Task(mainActivity).execute(mainActivity.getCurrentVideoProject());
    }

    @Override
    public void mp4Updated(final File mp4File) {
        if (!downloadingVideo) {
            return;
        }
        downloadingVideo = false;

        if (mainActivity.getCurrentPage() != 2) {
            return;
        }

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    videoView.setVideoURI(Uri.fromFile(mp4File));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                videoView.start();
            }
        });
    }
}
