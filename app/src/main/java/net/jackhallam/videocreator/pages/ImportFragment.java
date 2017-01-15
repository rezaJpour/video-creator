package net.jackhallam.videocreator.pages;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jackhallam.videocreator.MainActivity;
import net.jackhallam.videocreator.ProjectPickerAdapter;
import net.jackhallam.videocreator.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImportFragment extends Fragment {


    public ImportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_import, container, false);

        RecyclerView recyclerView = (RecyclerView) inflatedView.findViewById(R.id.recycler_view);
        ProjectPickerAdapter projectPickerAdapter = new ProjectPickerAdapter((MainActivity) getActivity(), recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(projectPickerAdapter);

        return inflatedView;
    }

}
