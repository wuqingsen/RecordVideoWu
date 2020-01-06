package com.wu.recordvideowu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.wu.recordvideowu.thread.MediaMuxerThread;
import com.wu.recordvideowu.utils.FaceView;
import com.wu.recordvideowu.utils.RecordAiRealUtils;
import com.wu.recordvideowu.utils.StaticVideo;
import com.wu.recordvideowu.utils.VideoFaceUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * wuqingsen on 2019-12-10
 * Mailbox:1243411677@qq.com
 * annotation:录制视频
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RecordVideoRealActivity extends AppCompatActivity implements EventListener, SurfaceHolder.Callback, Camera.PreviewCallback {
    @BindView(R.id.surfaceview_recordvideo_bendi)
    SurfaceView surfaceview;
    @BindView(R.id.btn_start)
    Button btn_start;
    @BindView(R.id.change_camera_dir)
    Button change_camera_dir;
    @BindView(R.id.face_view)
    public FaceView mFaceView;
    @BindView(R.id.tv_text)
    public TextView tv_text;
    //语音识别开始
    public EventManager asr;

    //视频录制
    public SurfaceHolder surfaceHolder;
    public Camera camera;

    //语音识别、音视频合成工具类
    public RecordAiRealUtils aiUtils;

    //人脸识别工具类
    public VideoFaceUtils faceUtils;

    //handler
    public MainHandler mainHandler;

    //是否开始录制：true已经开始
    public boolean isStartRecord = false;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_record_video);
        ButterKnife.bind(this);
        SupportAvcCodec();
        initStart();
    }

    @OnClick({R.id.btn_start, R.id.change_camera_dir})
    void onClice(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                if (!isStartRecord) {
                    //开始录制
                    startView();
                    aiUtils.startRecord();
                    MediaMuxerThread.startMuxer();

                    Message m = mainHandler.obtainMessage();
                    m.what = StaticVideo.CAMERA_HAS_STARTED_PREVIEW;
                    m.sendToTarget();
                } else {
                    //停止录制
                    stopView();
                    aiUtils.stopRecord();
                    MediaMuxerThread.stopMuxer();
                }
                break;
            case R.id.change_camera_dir:
                //切换摄像头
                aiUtils.changeCameraDir();
                break;
        }
    }

    private class MainHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case StaticVideo.UPDATE_FACE_RECT:
                    //绘制人脸框
                    Face[] faces = (Face[]) msg.obj;
                    if (faceUtils != null) {
                        mFaceView.setFaces(faces);
                    }
                    break;
                case StaticVideo.CAMERA_HAS_STARTED_PREVIEW:
                    //开始人脸识别
                    if (faceUtils != null) {
                        faceUtils.startGoogleFaceDetect();
                    }
                    break;
            }
        }
    }

    //初始化
    private void initStart() {
        mContext = RecordVideoRealActivity.this;

        //handler
        mainHandler = new MainHandler();
        //初始化工具类
        aiUtils = new RecordAiRealUtils(RecordVideoRealActivity.this, mContext);
        faceUtils = new VideoFaceUtils(RecordVideoRealActivity.this, mContext);

        //初始化surfaceHolder
        surfaceHolder = surfaceview.getHolder();
        surfaceHolder.addCallback(this);

        // 基于SDK唤醒词集成1.1 初始化EventManager
        asr = EventManagerFactory.create(this, "asr");
        // 基于SDK唤醒词集成1.3 注册输出事件
        asr.registerListener(this);
    }


    @SuppressLint("NewApi")
    private boolean SupportAvcCodec() {
        if (Build.VERSION.SDK_INT >= 18) {
            for (int j = MediaCodecList.getCodecCount() - 1; j >= 0; j--) {
                MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(j);

                String[] types = codecInfo.getSupportedTypes();
                for (int i = 0; i < types.length; i++) {
                    if (types[i].equalsIgnoreCase("video/avc")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //视频流合成视频
        MediaMuxerThread.addVideoFrameData(data);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //创建
        camera = aiUtils.getBackCamera();
        aiUtils.prepareVideo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        MediaMuxerThread.stopMuxer();
        if (faceUtils != null) {
            faceUtils.stopGoogleFaceDetect();
        }
        //销毁
        if (null != camera) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        //识别回调
        String logTxt = "";
        if (params != null) {
            try {
                JSONObject jsonObject = new JSONObject(params);
                if (jsonObject.getString("best_result") != null) {
                    String result = jsonObject.getString("best_result");
                    Log.e("=====语音识别", params);
                    logTxt += "识别成功：" + result;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            tv_text.append("\n" + logTxt);
        }

        if (data != null) {
            MediaMuxerThread.audioThread.encode(data);
//            Log.e("=====data", "音频数据不为null");
        } else {
//            Log.e("=====data", "音频数据为null");
        }
    }

    //开始录制控件设置
    private void startView() {
        isStartRecord = true;
        btn_start.setText("暂停");
        change_camera_dir.setVisibility(View.GONE);
    }

    //结束录制控件设置
    private void stopView() {
        isStartRecord = false;
        btn_start.setText("开始");
        change_camera_dir.setVisibility(View.VISIBLE);
        Toast.makeText(mContext, "录制完成，位置在：" +
                StaticVideo.VideoPath, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asr.send(SpeechConstant.WAKEUP_STOP, "{}", null, 0, 0);
    }

}
