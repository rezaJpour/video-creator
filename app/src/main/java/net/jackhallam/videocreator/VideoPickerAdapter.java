package net.jackhallam.videocreator;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.jackhallam.videocreator.model.VideoThumbnail;

import java.util.List;

/**
 * Created by laeschjs on 1/22/2017.
 */

public class VideoPickerAdapter extends RecyclerView.Adapter<VideoPickerAdapter.ViewHolder> {

//    private Bitmap bm;
//    private Cursor cursor;
    private int index;
    private Context mContext;
    private AlertDialog ad;
    private List<Bitmap> videoList;
    private RecyclerView.Adapter adap;
    private List<VideoThumbnail> deviceVideos;

    public VideoPickerAdapter(int location, Context con, AlertDialog alert, List<Bitmap> li, RecyclerView.Adapter ra, List<VideoThumbnail> videosOnDevice){
        index = location;
        mContext = con;
        ad = alert;
        videoList = li;
        adap = ra;
        deviceVideos = videosOnDevice;
    }

    @Override
    public VideoPickerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.video_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoPickerAdapter.ViewHolder holder, int position) {
        ImageView iv = holder.image;
        TextView tv = holder.time;
        VideoThumbnail myVideo= deviceVideos.get(position);

        int min = myVideo.getMins();
        int sec = myVideo.getSecs();
        int mil = myVideo.getMilli();
        tv.setText(min + ":" + sec + "." + mil);

        Bitmap bm = myVideo.getBitmap();
        iv.setImageBitmap(bm);
        final Bitmap finalBm = bm;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoList.add(index, finalBm);
                adap.notifyDataSetChanged();
                ad.dismiss();
            }
        });
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return deviceVideos.size();
    }

//    @Override
//    public View getView(int i, View contentView, ViewGroup parent) {
//        View v = null;
//        if(contentView == null){
//            v = LayoutInflater.from(mContext).inflate(R.layout.video_view,parent,false);
//        }else{
//            v = contentView;
//        }
//        ImageView iv = (ImageView) v.findViewById(R.id.thumbnail);
//        TextView tv = (TextView) v.findViewById(R.id.video_path);
//        cursor.moveToPosition(cursor.getCount()-1-i);
//        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        //use one of overloaded setDataSource() functions to set your data source
//        retriever.setDataSource(filePath);
//        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//        double timeInMillisec = Long.parseLong(time );
//        timeInMillisec=timeInMillisec/1000.0;
//        tv.setText(timeInMillisec+"");
//        Bitmap bm = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
//        if(bm.getWidth()>bm.getHeight()){
//            bm = Bitmap.createBitmap(bm, 0, 0, bm.getHeight(), bm.getHeight());
//        } else {
//            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getWidth());
//        }
//        iv.setImageBitmap(bm);
//        final Bitmap finalBm = bm;
//        v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                videoList.add(index, finalBm);
//                adap.notifyDataSetChanged();
//                ad.dismiss();
//            }
//        });
//        return v;
//    }

    public class ViewHolder  extends RecyclerView.ViewHolder{

        private ImageView image;
        private TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.thumbnail);
            time = (TextView) itemView.findViewById(R.id.video_path);
        }
    }
}
