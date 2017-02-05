package net.jackhallam.videocreator;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.jackhallam.videocreator.model.VideoProject;

import java.util.List;


/**
 * Created by jackhallam on 12/26/16.
 */

public class ProjectPickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProjectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.project_picker_view, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ProjectViewHolder projectViewHolder = (ProjectViewHolder) holder;
        projectViewHolder.projectNameTextView.setText(MainActivity.getVideoProjects().get(position).getTitle());
        projectViewHolder.projectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return MainActivity.getVideoProjects().size();
    }

    public class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView projectNameTextView;
        View projectLayout;

        public ProjectViewHolder(View itemView) {
            super(itemView);
            projectNameTextView = (TextView) itemView.findViewById(R.id.project_title);
            projectLayout = itemView.findViewById(R.id.project_layout);
        }
    }
}
