package net.jackhallam.videocreator.pages;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.RuntimeExecutionException;

import net.jackhallam.videocreator.MainActivity;
import net.jackhallam.videocreator.R;

import java.io.InputStream;

import cafe.adriel.androidstreamable.AndroidStreamable;
import cafe.adriel.androidstreamable.callback.NewVideoCallback;
import cafe.adriel.androidstreamable.model.NewVideo;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExportFragment extends Fragment {

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
                try{

                    mainActivity.storeVideo(getResources().openRawResource(R.raw.samplevideo));
                }catch (Exception e){
                    throw new RuntimeException(e);
                }

            }
        });
        uploadImageView = (ImageView) inflatedView.findViewById(R.id.image_upload);
        uploadImageView.setOnClickListener(new UploadVideoClickListener());

        return inflatedView;
    }

    // TODO: Everything is hard coded, but this is the general idea for video upload
    // TODO: Save video locally

    private void uploadVideo() {
        try {
            InputStream is = getResources().openRawResource(getResources().getIdentifier("samplevideo", "raw", getActivity().getPackageName()));
            AndroidStreamable.uploadVideo(is, "Created and uploaded via VideoCreator for Android", new NewVideoCallback() {
                @Override
                public void onSuccess(int statusCode, final NewVideo newVideo) {
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.project_outer), "Upload successful! ", Snackbar.LENGTH_LONG);
                    snackbar.setAction("OPEN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://streamable.com/" + newVideo.getShortCode()));
                            startActivity(browserIntent);
                        }
                    });
                    snackbar.show();
                    uploadImageView.setColorFilter(null);
                }

                @Override
                public void onFailure(int statusCode, Throwable error) {
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.project_outer), "Upload failed!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    uploadImageView.setColorFilter(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class UploadVideoClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.project_outer), "Starting video upload to Streamable.com", Snackbar.LENGTH_LONG);
            snackbar.setAction("CANCEL", new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    uploadImageView.setColorFilter(null);
                }

            });
            snackbar.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_ACTION) {
                        // we are handling this already
                        return;
                    }
                    //start upload now
                    uploadVideo();
                }

                @Override
                public void onShown(Snackbar snackbar) {
                }
            });
            snackbar.show();
            uploadImageView.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.colorAccent));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }
}
