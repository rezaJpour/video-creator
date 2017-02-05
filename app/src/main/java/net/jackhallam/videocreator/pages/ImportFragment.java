package net.jackhallam.videocreator.pages;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableRow;

import net.jackhallam.videocreator.MainActivity;
import net.jackhallam.videocreator.MyPagerAdapter;
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

    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private MainActivity mainActivity;

    private ProjectPickerAdapter projectPickerAdapter;

    public ImportFragment() {
        // Required empty public constructor
        projectPickerAdapter = new ProjectPickerAdapter();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        mainActivity.getFab(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
                if (intent.resolveActivity(mainActivity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
                }
            }
        });
        mainActivity.getFab(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAndShowAddAlertDialog();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_import, container, false);

        RecyclerView recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recycler_view);

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

    private void createAndShowAddAlertDialog() {
        final VideoProject videoProject = new VideoProject();
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        View inflatedView = mainActivity.getLayoutInflater().inflate(R.layout.dialog_add_project, null, false);
        final EditText titleEditText = (EditText) inflatedView.findViewById(R.id.add_project_text_view);
        builder.setView(inflatedView);
        builder.setNegativeButton(" ", null);
        builder.setPositiveButton(" ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!titleEditText.getText().toString().isEmpty()) {
                    videoProject.setTitle(titleEditText.getText().toString());
                    MainActivity.getVideoProjects().add(videoProject);
                    projectPickerAdapter.notifyDataSetChanged();
                }
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                try {
                    int fortyEightDP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());

                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(fortyEightDP, fortyEightDP);

                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setLayoutParams(layoutParams);

                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setLayoutParams(layoutParams);

                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundResource(R.drawable.ic_check_white_48dp);
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundResource(R.drawable.ic_close_white_48dp);

                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).invalidate();
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).invalidate();

                    alertDialog.getWindow().setBackgroundDrawableResource(R.color.lightBackgroundColor);
                } catch (Exception e) {
                    Log.d("d", e.getMessage());
                }
            }
        });
        alertDialog.show();
    }
}
