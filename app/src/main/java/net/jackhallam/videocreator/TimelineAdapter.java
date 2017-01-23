package net.jackhallam.videocreator;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackhallam on 12/28/16.
 */

public class TimelineAdapter extends RecyclerView.Adapter {

    private Context mContext;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 52;
    private ListView mListView;
    private List<Bitmap> videoList;

    public TimelineAdapter(Context context, RecyclerView recyclerView) {
        mContext = context;
        videoList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType%2) {
            case 0:
                return new TimelineAdapter.AddViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.add_edit_timeline_view, parent, false),viewType/2,this);
            case 1:
                return new TimelineAdapter.VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_edit_timeline_view, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(position%2==1){
            VideoViewHolder holder1 = (VideoViewHolder)holder;
            holder1.setImage(position);
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size()*2+1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public VideoViewHolder(View itemView) {
            super(itemView);
        }
        public void setImage(int position){
            ImageView video = (ImageView) itemView.findViewById(R.id.videoHolder);
            video.setImageBitmap(videoList.get((int)Math.floor(position/2)));
        }
    }

    public class AddViewHolder extends RecyclerView.ViewHolder {
        public AddViewHolder(View itemView, final int position, final RecyclerView.Adapter recAdap) {
            super(itemView);
            itemView.findViewById(R.id.add_video).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                    builder.setTitle(R.string.choose_video);
//
//                    View vw = LayoutInflater.from(mContext).inflate(R.layout.video_list_view,null,false);
//                    mListView = (ListView) vw.findViewById(R.id.list_view);
//                    mListView.setLayoutManager(new GridLayoutManager(mContext,2));
//                    String[] projection = { MediaStore.Video.Thumbnails.DATA};
//                    CursorLoader cursor = null;
                    if(ContextCompat.checkSelfPermission(mContext,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions((Activity)mContext,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    } else {
                        String[] projection = { MediaStore.Video.Thumbnails.DATA};
                        CursorLoader cursor = new CursorLoader(mContext, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                                null, // Return all rows
                                null, null);
                        finishMakingView(cursor, position, recAdap);
                    }
//                    builder.setView(vw);
//                    builder.create().show();
                }
            });
        }
    }
    public void finishMakingView(final CursorLoader cursorL, final int index, RecyclerView.Adapter ra){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.choose_video);

        View vw = LayoutInflater.from(mContext).inflate(R.layout.video_list_view,null,false);
        mListView = (ListView) vw.findViewById(R.id.list_view);
        builder.setView(vw);
        final AlertDialog ad = builder.create();

        final Cursor cursor = cursorL.loadInBackground();
//        mListView.setAdapter(new RecyclerView.Adapter<TimelineAdapter.ViewHolder>());
        mListView.setAdapter(new VideoPickerAdapter(cursor, index, mContext, ad, videoList, ra));
        ad.show();
    }
}
