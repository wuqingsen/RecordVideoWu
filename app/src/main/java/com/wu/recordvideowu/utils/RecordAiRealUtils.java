package com.wu.recordvideowu.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.baidu.aip.asrwakeup3.core.mini.AutoCheck;
import com.baidu.speech.asr.SpeechConstant;
import com.wu.recordvideowu.RecordVideoRealActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * wuqingsen on 2019-12-12
 * Mailbox:1243411677@qq.com
 * annotation:百度语音识别，语音视频合成，工具类
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RecordAiRealUtils {
    private RecordVideoRealActivity mActivity;
    private Context mContext;
    public int cameraDir = Camera.CameraInfo.CAMERA_FACING_FRONT;
    public Camera.Parameters parameters;
    //    public int width = 1280;
//    public int height = 720;
    public int width = 1920;
    public int height = 1080;
    private String BOT_ID = "26435";

    public RecordAiRealUtils(RecordVideoRealActivity mActivity, Context mContext) {
        this.mActivity = mActivity;
        this.mContext = mContext;
    }

    //准备录制
    public void prepareVideo() {
        if (mActivity.camera != null) {
            try {
                mActivity.camera.setPreviewCallback(mActivity);
                mActivity.camera.setDisplayOrientation(0);//旋转角度
                if (parameters == null) {
                    parameters = mActivity.camera.getParameters();
                }
                parameters = mActivity.camera.getParameters();
                parameters.setPreviewFormat(ImageFormat.NV21);
                parameters.setPreviewSize(width, height);
                mActivity.camera.setParameters(parameters);
                mActivity.camera.setPreviewDisplay(mActivity.surfaceHolder);
                mActivity.camera.startPreview();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //开始录制
    public void startRecord() {
        startVoice();
    }

    //停止录制
    public void stopRecord() {
        stopVoice();
    }

    //音频录制开始
    private void startVoice() {
        // 基于SDK唤醒词集成第2.1 设置唤醒的输入参数
        Map<String, Object> params = new TreeMap<String, Object>();
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.PID, 15364); // Unit  2.0 固定pid,仅支持中文普通话
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音

        params.put(SpeechConstant.ACCEPT_AUDIO_DATA, true);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "guoshou.pcm";
        params.put(SpeechConstant.OUT_FILE, path);

        //保存录音文件
//        params.put(SpeechConstant.ACCEPT_AUDIO_DATA, true);
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + StaticVideo.VoiceNamePcm;
//        params.put(SpeechConstant.OUT_FILE, path);
        params.put(SpeechConstant.BOT_SESSION_LIST, unitParams());

        // 复制此段可以自动检测错误
        (new AutoCheck(mContext.getApplicationContext(), new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainErrorMessage(); // autoCheck.obtainAllMessage();
                        mActivity.tv_text.append(message + "\n");
                        ; // 可以用下面一行替代，在logcat中查看代码
                        // Log.w("AutoCheckMessage", message);
                    }
                }
            }
        },false)).checkAsr(params);
        String json = null; // 这里可以替换成你需要测试的json
        json = new JSONObject(params).toString();
        mActivity.asr.send(SpeechConstant.ASR_START, json, null, 0, 0);
        Log.e("=====音频录制开始时间", getTime());
    }

    //音频录制停止
    private void stopVoice() {
        mActivity.asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0); //
        Log.e("=====音频录制停止时间", getTime());
    }

    //获取摄像头
    public Camera getBackCamera() {
        Camera c = null;
        try {
            c = Camera.open(cameraDir); // 0后置，1前置
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    //改变摄像头方向
    public void changeCameraDir() {
        if (mActivity.camera != null) {
            mActivity.camera.setPreviewCallback(null);
            mActivity.camera.stopPreview();
            mActivity.camera.lock();
            mActivity.camera.release();
            mActivity.camera = null;
        }
        if (cameraDir == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mActivity.camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            cameraDir = Camera.CameraInfo.CAMERA_FACING_BACK;
            //人脸识别设置摄像头
            mActivity.mFaceView.setCameraId(cameraDir);
        } else {
            mActivity.camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            cameraDir = Camera.CameraInfo.CAMERA_FACING_FRONT;
            //人脸识别设置摄像头
            mActivity.mFaceView.setCameraId(cameraDir);

        }
        prepareVideo();
    }

    public String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    private JSONArray unitParams() {
        JSONArray json = new JSONArray();
        try {
            JSONObject bot = new JSONObject();
            bot.put("bot_id",BOT_ID);
            bot.put("bot_session_id","");
            bot.put("bot_session","");
            json.put(bot);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
