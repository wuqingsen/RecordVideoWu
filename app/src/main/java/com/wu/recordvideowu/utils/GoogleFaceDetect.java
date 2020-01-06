package com.wu.recordvideowu.utils;


import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * wuqingsen on 2019-12-31
 * Mailbox:1243411677@qq.com
 * annotation:google人脸识别
 */
public class GoogleFaceDetect implements Camera.FaceDetectionListener {
    private static final String TAG = "YanZi";
    private Context mContext;
    private Handler mHander;

    public GoogleFaceDetect(Context c, Handler handler) {
        mContext = c;
        mHander = handler;
    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        // TODO Auto-generated method stub

        Log.i(TAG, "onFaceDetection...");
        if (faces != null) {
            Message m = mHander.obtainMessage();
            m.what = StaticVideo.UPDATE_FACE_RECT;
            m.obj = faces;
            m.sendToTarget();
        }
    }

/*	private Rect getPropUIFaceRect(Rect r){
		Log.i(TAG, "人脸检测  = " + r.flattenToString());
		Matrix m = new Matrix();
		boolean mirror = false;
		m.setScale(mirror ? -1 : 1, 1);
		Point p = DisplayUtil.getScreenMetrics(mContext);
		int uiWidth = p.x;
		int uiHeight = p.y;
		m.postScale(uiWidth/2000f, uiHeight/2000f);
		int leftNew = (r.left + 1000)*uiWidth/2000;
		int topNew = (r.top + 1000)*uiHeight/2000;
		int rightNew = (r.right + 1000)*uiWidth/2000;
		int bottomNew = (r.bottom + 1000)*uiHeight/2000;

		return new Rect(leftNew, topNew, rightNew, bottomNew);
	}*/

}
