package net.jackhallam.videocreator;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.jackhallam.videocreator.model.Clip;
import net.jackhallam.videocreator.model.VideoThumbnail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackhallam on 12/28/16.
 */

public class TimelineAdapter extends RecyclerView.Adapter {

    private Context mContext;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 52;
    private RecyclerView mListView;
    private List<Bitmap> videoList;
    private List<Clip> deviceVideos;
    private Cursor cursor;
    private VideoPickerAdapter vpAdapter;

    public TimelineAdapter(Context context, RecyclerView recyclerView) {
        mContext = context;
        videoList = new ArrayList<>();
        deviceVideos=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(mContext,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity)mContext,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            // Return only video and image metadata.
            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
            String[] projection = { MediaStore.Video.Thumbnails.DATA};
            CursorLoader cursorL = new CursorLoader(mContext, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                    null, // Return all rows
                    null, null);
            cursor=cursorL.loadInBackground();
            startAsync();
        }
    }

    public void startAsync(){
        new MyAsync().execute(null, null, null);
    }

    private class MyAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            int size = cursor.getCount();
            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(size - i - 1);
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                //use one of overloaded setDataSource() functions to set your data source
                retriever.setDataSource(filePath);
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long totalTime = Long.parseLong(time);
                int sec = (int) (totalTime / 1000) % 60;
                int minutes = (int) ((totalTime / (1000 * 60)) % 60);
                int milli = (int) (totalTime % 1000);
                Bitmap bm = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                if (bm.getWidth() > bm.getHeight()) {
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getHeight(), bm.getHeight());
                } else {
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getWidth());
                }

                // Make the clip
                Clip clip = new Clip();
                clip.setPath(filePath);
                clip.setThumbnail(new VideoThumbnail(bm, minutes, sec, milli));
                clip.setStart(0);
                clip.setEnd(totalTime);
                deviceVideos.add(clip);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            if(vpAdapter!=null){
                Log.d("TIMELINE", "NOT null");
                vpAdapter.notifyDataSetChanged();
            }
        }
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(position%2==1){
            VideoViewHolder holder1 = (VideoViewHolder)holder;
            holder1.video.setImageBitmap(videoList.get((int)Math.floor(position/2)));
            holder1.video.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    videoList.remove((int) Math.floor(position/2));
                    notifyDataSetChanged();
                    return true;
                }
            });
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
        public ImageView video;
        public VideoViewHolder(View itemView) {
            super(itemView);
            video = (ImageView) itemView.findViewById(R.id.videoHolder);
        }
    }

    public class AddViewHolder extends RecyclerView.ViewHolder {
        public AddViewHolder(View itemView, final int position, final RecyclerView.Adapter recAdap) {
            super(itemView);
            itemView.findViewById(R.id.add_video).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.choose_video);

                    View vw = LayoutInflater.from(mContext).inflate(R.layout.video_list_view,null,false);
                    mListView = (RecyclerView) vw.findViewById(R.id.list_view);
                    builder.setView(vw);
                    final AlertDialog ad = builder.create();

                    mListView.setLayoutManager(new GridLayoutManager(mContext, 2));
                    vpAdapter=new VideoPickerAdapter(position, mContext, ad, videoList, recAdap, deviceVideos);
                    mListView.setAdapter(vpAdapter);
                    mListView.setHasFixedSize(true);//purely for speed
                    ad.show();
                }
            });
        }
    }

    public void setCursor(Cursor c){
        cursor = c;
    }

}
