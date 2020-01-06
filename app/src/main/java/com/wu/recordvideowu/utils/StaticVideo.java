package com.wu.recordvideowu.utils;

import android.os.Environment;

import java.io.File;

/**
 * Name: 吴庆森
 * Date: 2019/8/30
 * Mailbox: 1243411677@qq.com
 * Describe:视频录制静态变量
 */
public class StaticVideo {

    //文件名称
    public static String VoiceName = "wuqingsen";


    //音频名称
    public static String VoiceNamePcm = VoiceName + ".pcm";


    //人脸识别参数
    public static final int UPDATE_FACE_RECT = 0x1000;
    public static final int CAMERA_HAS_STARTED_PREVIEW = 0x1001;

    //Mp4视频储存路径
    public static final String VideoPath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator + VoiceName + ".mp4";

}
