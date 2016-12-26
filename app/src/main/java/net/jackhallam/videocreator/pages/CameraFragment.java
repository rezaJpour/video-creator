package net.jackhallam.videocreator.pages;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import net.jackhallam.videocreator.R;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment implements SurfaceHolder.Callback {

    MediaRecorder recorder;
    SurfaceHolder holder;
    SurfaceView cameraView;

    Camera mCamera;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCamera = Camera.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCamera.release();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        recorder = new MediaRecorder();
        cameraView = (SurfaceView) view.findViewById(R.id.surfaceCamera);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        return view;
    }

    private void prepareRecorder() {
        recorder.setPreviewDisplay(holder.getSurface());
        try {
            recorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void surfaceCreated(SurfaceHolder holder) {
        prepareRecorder();
        try {
            mCamera.setPreviewDisplay(cameraView.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        startPreview();
    }

    private void startPreview() {
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size selected = sizes.get(0);
        params.setPreviewSize(selected.width, selected.height);
        mCamera.setDisplayOrientation(0);
        mCamera.setParameters(params);
        mCamera.startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            recorder.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
