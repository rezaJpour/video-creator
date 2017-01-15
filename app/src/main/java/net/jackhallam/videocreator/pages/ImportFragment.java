package net.jackhallam.videocreator.pages;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jackhallam.videocreator.MainActivity;
import net.jackhallam.videocreator.ProjectPickerAdapter;
import net.jackhallam.videocreator.R;
import net.jackhallam.videocreator.model.VideoProject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImportFragment extends Fragment {

    static private final int REQUEST_VIDEO_CAPTURE  = 1;
    private List<VideoProject> videoProjects = new ArrayList<>(); //TODO: get using firebase
    private MainActivity mainActivity;

    public ImportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        mainActivity.getFab(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (intent.resolveActivity(mainActivity.getPackageManager()) != null) {
                    mainActivity.startActivityForResult(intent, REQUEST_VIDEO_CAPTURE );
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_import, container, false);

        RecyclerView recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recycler_view);
        ProjectPickerAdapter projectPickerAdapter = new ProjectPickerAdapter(mainActivity, recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(projectPickerAdapter);

        return inflatedView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();

            View coordinator = mainActivity.findViewById(R.id.project_outer);
            Snackbar snackbar = Snackbar.make(coordinator, "Got the video", Snackbar.LENGTH_LONG);
            snackbar.show();
            //TODO: do something with the Uri
        }
    }
}
