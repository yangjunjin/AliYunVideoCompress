package com.aliyun.alivcsolution;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aliyun.crop.AliyunCropCreator;
import com.aliyun.crop.struct.CropParam;
import com.aliyun.crop.supply.AliyunICrop;
import com.aliyun.crop.supply.CropCallback;
import com.aliyun.svideo.sdk.external.struct.common.VideoDisplayMode;
import com.aliyun.svideo.sdk.external.struct.common.VideoQuality;
import com.aliyun.svideo.sdk.external.struct.encoder.VideoCodecs;
import com.aliyun.svideo.sdk.external.struct.snap.AliyunSnapVideoParam;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by AliYunVideoCropActivity
 * on 2020/1/4
 */
public class AliYunVideoCropActivity extends Activity implements CropCallback, MediaPlayer.OnVideoSizeChangedListener {

    private final static String TAG = AliYunVideoCropActivity.class.getSimpleName() + "=======";
    private AliyunICrop crop;
    private int ratioMode = AliyunSnapVideoParam.RATIO_MODE_9_16;//宽高比 RATIO_MODE_1_1,RATIO_MODE_3_4，RATIO_MODE_9_16
    private int resolutionMode = AliyunSnapVideoParam.RESOLUTION_540P;//分辨率 RESOLUTION_360P RESOLUTION_480P RESOLUTION_540P RESOLUTION_540P
    private MediaPlayer mPlayer = new MediaPlayer();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alivc_crop_activity_video_crop);
        crop = AliyunCropCreator.createCropInstance(this);
        crop.setCropCallback(this);
        getPlayTime(mInPutPath);
        getWidthHeight();
        mPlayer.setOnVideoSizeChangedListener(this);
        try {
            mPlayer.setDataSource(mInPutPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        findViewById(R.id.btnVideoCompress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCrop();
            }
        });
    }

    private void getWidthHeight() {
        switch (resolutionMode) {
            case AliyunSnapVideoParam.RESOLUTION_360P:
                outputWidth = 360;
                break;
            case AliyunSnapVideoParam.RESOLUTION_480P:
                outputWidth = 480;
                break;
            case AliyunSnapVideoParam.RESOLUTION_540P:
                outputWidth = 540;
                break;
            case AliyunSnapVideoParam.RESOLUTION_720P:
                outputWidth = 720;
                break;
        }
        switch (ratioMode) {
            case AliyunSnapVideoParam.RATIO_MODE_1_1:
                outputHeight = outputWidth;
                break;
            case AliyunSnapVideoParam.RATIO_MODE_3_4:
                outputHeight = outputWidth * 4 / 3;
                break;
            case AliyunSnapVideoParam.RATIO_MODE_9_16:
                outputHeight = outputWidth * 16 / 9;
                break;
        }
    }

    /**
     * 初始化参数
     */
    private void getPlayTime(String mUri) {
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                HashMap<String, String> headers = null;
                if (headers == null) {
                    headers = new HashMap<>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
//                mmr.setDataSource(mUri, headers);
                mmr.setDataSource(mUri);
            }
            String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(秒)
            String width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
            String height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高

            if (!TextUtils.isEmpty(duration)) mEndTime = Long.parseLong(duration) * 1000;
            if (!TextUtils.isEmpty(width)) outputWidth = Integer.parseInt(width);
            if (!TextUtils.isEmpty(height)) outputHeight = Integer.parseInt(height);
            Log.e(TAG, "playtime1:" + duration + ",w=" + outputWidth + ",h=" + outputHeight);
        } catch (Exception ex) {
            Log.e(TAG, "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
    }

    /**
     * 开始压缩
     */
    String mOutPutPath = "/storage/emulated/0/DCIM/Camera/myVideo23.mp4";
    String mInPutPath = "/storage/emulated/0/DCIM/Camera/video.mp4";
    int outputWidth = 540;
    int outputHeight = 720;
    long mEndTime = 283868;

    private void startCrop() {
        Log.e(TAG, "playtime2:" + ",w=" + outputWidth + ",h=" + outputHeight);
        CropParam cropParam = new CropParam();
        cropParam.setOutputPath(mOutPutPath);//出的路径
        cropParam.setInputPath(mInPutPath);//入的路径
        cropParam.setOutputWidth(outputWidth);
        cropParam.setOutputHeight(outputHeight);

        cropParam.setStartTime(0);
        cropParam.setEndTime(mEndTime);
        cropParam.setScaleMode(VideoDisplayMode.SCALE);//裁剪模式FILL,SCALE
        cropParam.setFrameRate(25);//设置帧率
        cropParam.setGop(250);//设置关键帧间隔
        cropParam.setQuality(VideoQuality.HD);//视频质量,SSD极高，HD高，SD中，LD低
        cropParam.setVideoCodec(VideoCodecs.H264_HARDWARE);//H264_HARDWARE,H264_SOFT_OPENH264,H264_SOFT_FFMPEG
        crop.setCropParam(cropParam);
        crop.startCrop();
    }

    @Override
    public void onProgress(final int percent) {
        Log.d(TAG, "onProgress : " + percent);
    }

    @Override
    public void onError(final int code) {
        Log.d(TAG, "onError : " + code);
    }

    @Override
    public void onComplete(long duration) {
        Log.d(TAG, "onComplete : " + duration);
    }

    //取消完成
    @Override
    public void onCancelComplete() {
        Log.d(TAG, "onCancelComplete : ");
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
        Log.e(TAG,"视频的宽="+width+"，height="+height);
    }
}

