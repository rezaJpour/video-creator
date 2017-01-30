package net.jackhallam.videocreator.pages;


import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jackhallam.videocreator.R;
import net.jackhallam.videocreator.TimelineAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {

    private TimelineAdapter mTimelineAdapter;

    public EditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_edit, container, false);

        RecyclerView recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recycler_view);
        mTimelineAdapter = new TimelineAdapter(getActivity(), recyclerView);
        recyclerView.setAdapter(mTimelineAdapter);

        return inflatedView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case TimelineAdapter.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String[] projection = { MediaStore.Video.Thumbnails.DATA};
                    CursorLoader cursorL = new CursorLoader(getContext(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                            null, // Return all rows
                            null, null);
                    Cursor cursor=cursorL.loadInBackground();
                    while(cursorL.isStarted()) {}
                    mTimelineAdapter.setCursor(cursor);
                    mTimelineAdapter.populateVideos();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

}
