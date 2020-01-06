package com.wu.recordvideowu.utils;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.wu.recordvideowu.RecordVideoRealActivity;

/**
 * wuqingsen on 2019-12-26
 * Mailbox:1243411677@qq.com
 * annotation:视频流人脸识别工具类
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VideoFaceUtils {
    public RecordVideoRealActivity mActivity;
    public Context mContext;
    public Thread view;
    public GoogleFaceDetect googleFaceDetect = null;

    public VideoFaceUtils(RecordVideoRealActivity mActivity, Context mContext) {
        this.mActivity = mActivity;
        this.mContext = mContext;
        googleFaceDetect = new GoogleFaceDetect(mContext, mActivity.mainHandler);
    }

    //开始检测
    public void startGoogleFaceDetect() {
        Camera.Parameters params = mActivity.aiUtils.parameters;
        if (params.getMaxNumDetectedFaces() > 0) {
            if (mActivity.mFaceView != null) {
                mActivity.mFaceView.clearFaces();
                mActivity.mFaceView.setVisibility(View.VISIBLE);
            }
            mActivity.camera.setFaceDetectionListener(googleFaceDetect);
            mActivity.camera.startFaceDetection();
        }
    }

    //停止检测
    public void stopGoogleFaceDetect() {
        Camera.Parameters params = mActivity.aiUtils.parameters;
        if (params.getMaxNumDetectedFaces() > 0) {
            mActivity.camera.setFaceDetectionListener(null);
            mActivity.camera.stopFaceDetection();
            mActivity.mFaceView.clearFaces();
        }
    }

}
