package net.jackhallam.videocreator;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jackhallam.videocreator.pages.ImportFragment;

/**
 * Created by jackhallam on 12/26/16.
 */

public class ProjectPickerAdapter extends RecyclerView.Adapter<ProjectPickerAdapter.ViewHolder> {

    public ProjectPickerAdapter(Context context, RecyclerView recyclerView) {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_picker_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
