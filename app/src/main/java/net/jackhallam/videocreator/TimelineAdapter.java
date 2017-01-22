package net.jackhallam.videocreator;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by jackhallam on 12/28/16.
 */

public class TimelineAdapter extends RecyclerView.Adapter {

    private Context mContext;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 52;
    private ListView mListView;

    public TimelineAdapter(Context context, RecyclerView recyclerView) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new TimelineAdapter.AddViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.add_edit_timeline_view, parent, false));
            case 1:
                return new TimelineAdapter.VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_edit_timeline_view, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 31;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }


    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public VideoViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class AddViewHolder extends RecyclerView.ViewHolder {
        public AddViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.add_video).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.choose_video);

                    View vw = LayoutInflater.from(mContext).inflate(R.layout.video_list_view,null,false);
                    mListView = (ListView) vw.findViewById(R.id.list_view);
                    String[] projection = { MediaStore.Video.Thumbnails.DATA};
                    CursorLoader cursor = null;
                    if(ContextCompat.checkSelfPermission(mContext,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions((Activity)mContext,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    } else {
                        cursor = new CursorLoader(mContext, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                                null, // Return all rows
                                null, null);
                        finishMakingView(cursor);
                    }
                    builder.setView(vw);
                    builder.create().show();
                }
            });
        }
    }
    public void finishMakingView(final CursorLoader cursorL){
        final Cursor cursor = cursorL.loadInBackground();
        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return cursor.getCount();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View contentView, ViewGroup parent) {
                View v = null;
                if(contentView == null){
                    v = LayoutInflater.from(mContext).inflate(R.layout.video_view,parent,false);
                }else{
                    v = contentView;
                }
                ImageView iv = (ImageView) v.findViewById(R.id.thumbnail);
                TextView tv = (TextView) v.findViewById(R.id.video_path);
                cursor.moveToPosition(cursor.getCount()-1-i);
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                //use one of overloaded setDataSource() functions to set your data source
                retriever.setDataSource(filePath);
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                double timeInMillisec = Long.parseLong(time );
                timeInMillisec=timeInMillisec/1000.0;
                tv.setText(timeInMillisec+"");
                Bitmap bm = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                if(bm.getWidth()>bm.getHeight()){
                    bm = Bitmap.createBitmap(bm, 600, 0, 600, 600);
                } else {
                    bm = Bitmap.createBitmap(bm, 0, 0, 600, 600);
                }
                iv.setImageBitmap(bm);
                return v;
            }
        });
    }
}
