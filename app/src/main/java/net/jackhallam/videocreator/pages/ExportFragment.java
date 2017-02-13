package net.jackhallam.videocreator.pages;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.jackhallam.videocreator.MP4UpdateListener;
import net.jackhallam.videocreator.MainActivity;
import net.jackhallam.videocreator.R;
import net.jackhallam.videocreator.tasks.ProjectToMP4Task;
import net.jackhallam.videocreator.tasks.Util;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExportFragment extends Fragment implements MP4UpdateListener {

    private ImageView saveImageView;
    private ImageView uploadImageView;

    private MainActivity mainActivity;

    private boolean isSave = true;

    public ExportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.fragment_export, container, false);

        saveImageView = (ImageView) inflatedView.findViewById(R.id.image_save_to_device);
        saveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSave = true;
                Snackbar.make(mainActivity.findViewById(R.id.project_outer), "Saving Video...", Snackbar.LENGTH_SHORT).show();
                new ProjectToMP4Task(mainActivity).execute(mainActivity.getCurrentVideoProject());
            }
        });
        uploadImageView = (ImageView) inflatedView.findViewById(R.id.image_upload);
        uploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSave = false;
                Snackbar.make(mainActivity.findViewById(R.id.project_outer), "Uploading Video...", Snackbar.LENGTH_SHORT).show();
                new ProjectToMP4Task(mainActivity).execute(mainActivity.getCurrentVideoProject());
            }
        });
        return inflatedView;
    }

    @Override
    public void mp4Updated(File mp4File) {
        saveVideo();
    }

    private void saveVideo() {
        String url = Util.BACKEND_URL+ (isSave ? "out.mp4" : "out.html");
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        mainActivity.registerMP4UpdateListener(this);
    }
}
