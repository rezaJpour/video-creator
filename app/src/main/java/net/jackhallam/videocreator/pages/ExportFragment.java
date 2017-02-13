package net.jackhallam.videocreator.pages;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.jackhallam.videocreator.MP4UpdateListener;
import net.jackhallam.videocreator.MainActivity;
import net.jackhallam.videocreator.R;
import net.jackhallam.videocreator.tasks.ProjectToMP4Task;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExportFragment extends Fragment implements MP4UpdateListener {

    private ImageView saveImageView;
    private ImageView uploadImageView;

    private MainActivity mainActivity;


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
                new ProjectToMP4Task(mainActivity).execute(mainActivity.getCurrentVideoProject());
            }
        });
        uploadImageView = (ImageView) inflatedView.findViewById(R.id.image_upload);
        return inflatedView;
    }

    @Override
    public void mp4Updated(File mp4File) {
        saveVideo(mp4File);
    }

    private void saveVideo(File mp4File) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, mp4File.getAbsolutePath());
        shareIntent.setType("*/*");
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        mainActivity.registerMP4UpdateListener(this);
    }
}
