# RecordVideoWu

#### 介绍
这是录制视频+语音识别+人脸识别的demo

#### 软件架构
录制视频用的分为录制音频流和视频流；
1. 利用Android的Camera视频流，开启子线程VideoEncoderThread利用MediaCodec实现视频流的编码，编码为h264格式;
2. 利用百度ai的SDK实现录制音频流并且实现语音识别，开启子线程AudioEncoderThread利用MediaCodec实现音频流的编码;
3. 最后自定义MediaMuxerThread音视频混合线程为音视频流分别添加音视频轨道，并且实时写入到mp4文件;
4. 利用Google自带的FaceDetectionListener实现人脸识别，开启子线程自定义view实现实时人脸框的绘制;

#### 使用说明
详见具体代码

#### 参与贡献
吴庆森

#### 博客地址
https://blog.csdn.net/wuqingsen1/article/details/103799520
