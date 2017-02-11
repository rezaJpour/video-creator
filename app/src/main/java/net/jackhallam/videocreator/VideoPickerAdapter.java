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

import net.jackhallam.videocreator.model.Clip;
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
    private List<Clip> videoList;
    private RecyclerView.Adapter adap;
    private List<VideoThumbnail> deviceVideos;

    public VideoPickerAdapter(int location, Context con, AlertDialog alert, List<Clip> li, RecyclerView.Adapter ra, List<VideoThumbnail> videosOnDevice){
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

        // Make the clip
        final Clip clip = new Clip();
        clip.setPath(myVideo.getFilePath());
        clip.setStart(0);
        clip.setEnd(myVideo.getTotalTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoList.add(index, clip);
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
