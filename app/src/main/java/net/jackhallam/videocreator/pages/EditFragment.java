package net.jackhallam.videocreator.pages;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jackhallam.videocreator.ProjectPickerAdapter;
import net.jackhallam.videocreator.R;
import net.jackhallam.videocreator.TimelineAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {


    public EditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_edit, container, false);

        RecyclerView recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recycler_view);
        TimelineAdapter timelineAdapter = new TimelineAdapter(getActivity(), recyclerView);
        recyclerView.setAdapter(timelineAdapter);

        return inflatedView;
    }

}
