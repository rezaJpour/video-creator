package net.jackhallam.videocreator;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by laeschjs on 1/22/2017.
 */

public class VideoPickerAdapter extends BaseAdapter {

//    private Bitmap bm;
    private Cursor cursor;
    private int index;
    private Context mContext;
    private AlertDialog ad;
    private List<Bitmap> videoList;
    private RecyclerView.Adapter adap;

    public VideoPickerAdapter(Cursor c, int location, Context con, AlertDialog alert, List<Bitmap> li, RecyclerView.Adapter ra){
        cursor = c;
        index = location;
        mContext = con;
        ad = alert;
        videoList = li;
        adap = ra;
    }

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
        final Bitmap finalBm = bm;
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoList.add(index, finalBm);
                adap.notifyDataSetChanged();
                ad.dismiss();
            }
        });
        return v;
    }
}
