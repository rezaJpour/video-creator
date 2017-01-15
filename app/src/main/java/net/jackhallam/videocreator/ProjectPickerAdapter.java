package net.jackhallam.videocreator;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by jackhallam on 12/26/16.
 */

public class ProjectPickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private FloatingActionButton fab;
    static final int REQUEST_VIDEO_CAPTURE  = 1;
    private RecyclerView mRecyclerView;

    public ProjectPickerAdapter(final MainActivity mainActivity, RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        fab = mainActivity.getFab(2);
        fab.setOnClickListener(new View.OnClickListener() {
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0: return new TitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.title_project_picker_view, parent, false));
            case 1: return new ProjectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.project_picker_view, parent, false));
            default: return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public class ProjectViewHolder extends RecyclerView.ViewHolder {
        public ProjectViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {
        public TitleViewHolder(View itemView) {
            super(itemView);
        }
    }
}
