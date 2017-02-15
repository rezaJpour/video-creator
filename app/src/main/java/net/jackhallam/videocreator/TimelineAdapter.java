package net.jackhallam.videocreator;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import net.jackhallam.videocreator.model.Clip;
import net.jackhallam.videocreator.model.VideoThumbnail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by jackhallam on 12/28/16.
 */

public class TimelineAdapter extends RecyclerView.Adapter {

    private Context mContext;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 52;
    private RecyclerView mListView;
    private List<Clip> videoList;
    private List<VideoThumbnail> deviceVideos;
    private List<String> videoKeyList;
    private Cursor cursor;
    private VideoPickerAdapter vpAdapter;
    private DatabaseReference projectRef;
    private ChildEventListener cel;
    private boolean initialLoad;

    // This map is purely used for speed
    private Map<String, Bitmap> map;

    public TimelineAdapter(Context context, RecyclerView recyclerView) {
        mContext = context;
        ((MainActivity) context).setTimeLineAdapter(this);
        initialLoad=true;
        videoList = new LinkedList<>();
        deviceVideos=new ArrayList<>();
        videoKeyList = new LinkedList<>();
        map = new HashMap<>();
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

                deviceVideos.add(new VideoThumbnail(bm, minutes, sec, milli, filePath, totalTime));

                map.put(filePath,bm);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            if(vpAdapter!=null){
                Log.d("TIMELINE", "NOT null");
                vpAdapter.notifyDataSetChanged();
            }
            reorder();
            notifyDataSetChanged();
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
            final Clip clip = videoList.get((int)Math.floor(position/2));
            holder1.video.setImageBitmap(map.get(clip.getPath()));
            holder1.video.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    projectRef.child(clip.getKey()).removeValue();
                    return true;
                }
            });
            holder1.video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    View vw = LayoutInflater.from(mContext).inflate(R.layout.edit_video_view,null,false);
                    builder.setView(vw);

                    final FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
                    retriever.setDataSource(clip.getPath());

                    final ImageView startFrame = (ImageView) vw.findViewById(R.id.startFrame);
                    final ImageView endFrame = (ImageView) vw.findViewById(R.id.endFrame);

                    // get seekbar from view
                    final CrystalRangeSeekbar rangeSeekbar = (CrystalRangeSeekbar) vw.findViewById(R.id.rangeSeekbar);
                    rangeSeekbar.setMinValue(0).setMaxValue((int)(clip.getLengthOfClip()/1000));

                    rangeSeekbar.setMinStartValue((int)(clip.getStart()/1000)).setMaxStartValue((int)(clip.getEnd()/1000)).apply();

                    Log.d("d", "CLIP START: "+clip.getStart()+", CLIP END: "+clip.getEnd()+" ------------------");

                    final long[] vals = new long[2];
                    vals[0] = -1;
                    vals[1] = -1;

                    // set listener
                    rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
                        @Override
                        public void valueChanged(Number minValue, Number maxValue) {
                            if(vals[0] != minValue.longValue()) {
                                Log.d("MIN",minValue.longValue()+"");
                                startFrame.setImageBitmap(retriever.getFrameAtTime(minValue.longValue()*1000000,
                                        MediaMetadataRetriever.OPTION_CLOSEST));
                                startFrame.invalidate();
                                vals[0] = minValue.longValue();
                            }
                            if(vals[1] != maxValue.longValue()) {
                                Log.d("MAX",maxValue.longValue()+"");
                                endFrame.setImageBitmap(retriever.getFrameAtTime(maxValue.longValue()*1000000,
                                        MediaMetadataRetriever.OPTION_CLOSEST));
                                endFrame.invalidate();
                                vals[1] = maxValue.longValue();
                            }
                        }
                    });

                    builder.setNegativeButton(" ", null);
                    builder.setPositiveButton(" ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            clip.setStart(rangeSeekbar.getSelectedMinValue().longValue()*1000);
                            clip.setEnd(rangeSeekbar.getSelectedMaxValue().longValue()*1000);
                            projectRef.child(clip.getKey()).setValue(clip);
                        }
                    });
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            try {
                                int fortyEightDP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, mContext.getResources().getDisplayMetrics());

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

    public void setProjectRef(DatabaseReference dr){
        videoList = new LinkedList<>();
        videoKeyList = new LinkedList<>();
        projectRef = dr;
        if(cel != null) {
            projectRef.removeEventListener(cel);
        }
        cel = new ClipsChildEventListener();
        projectRef.addChildEventListener(cel);
    }

    public void reorder(){
        initialLoad=false;
        List<Clip> newList = new LinkedList<>();
        List<String> newKeyList = new LinkedList<>();
        String current = "";
        for(int i = 0; i < videoList.size(); i++){
            for(int j = 0; j < videoList.size();j++){
                Clip c = videoList.get(j);
                if(c.getNextClipKey().equals(current)){
                    current = c.getKey();
                    newList.add(0, videoList.get(j));
                    newKeyList.add(0, videoKeyList.get(j));
                }
            }
        }
        videoList = newList;
        videoKeyList = newKeyList;
    }

    private class ClipsChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Clip clip = dataSnapshot.getValue(Clip.class);
            clip.setKey(dataSnapshot.getKey());
            String next = clip.getNextClipKey();
            if(initialLoad){
                videoList.add(clip);
                videoKeyList.add(clip.getKey());
            } else {
                if (next.equals("") && videoList.size() != 0) {
                    Clip prev = videoList.get(videoList.size() - 1);
                    prev.setNextClipKey(dataSnapshot.getKey());
                    projectRef.child(prev.getKey()).setValue(prev);
                    videoList.add(clip);
                    videoKeyList.add(clip.getKey());
                } else if (videoList.size() == 0) {
                    videoList.add(clip);
                    videoKeyList.add(dataSnapshot.getKey());
                } else {
                    int index = videoKeyList.indexOf(next);
                    if (index > 0) {
                        Clip prev = videoList.get(index - 1);
                        prev.setNextClipKey(dataSnapshot.getKey());
                        projectRef.child(prev.getKey()).setValue(prev);
                    }
                    // TODO
                    if (index >= 0) {
                        videoList.add(index, clip);
                        videoKeyList.add(index, dataSnapshot.getKey());
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Clip clip = dataSnapshot.getValue(Clip.class);
            clip.setKey(dataSnapshot.getKey());
            for (int i = 0; i < videoList.size(); i++) {
                if (dataSnapshot.getKey().equals(videoList.get(i).getKey())) {
                    Log.d("d", "CHILD CHANGED ------------------------------------------------- ");
                    videoList.set(i, clip);
                    notifyDataSetChanged();
                    return;
                }
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            for (int i = 0; i < videoList.size(); i++) {
                if (dataSnapshot.getKey().equals(videoList.get(i).getKey())) {
                    Clip remove = videoList.get(i);
                    if(i>0){
                        Clip prev = videoList.get(i-1);
                        prev.setNextClipKey(remove.getNextClipKey());
                        projectRef.child(prev.getKey()).setValue(prev);
                    }

                    videoList.remove(i);
                    videoKeyList.remove(i);
                    notifyDataSetChanged();
                    return;
                }
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    }

    public void addClip(Clip clip){
        projectRef.push().setValue(clip);
    }

    public List<Clip> getVideoList() {
        return videoList;
    }
}
