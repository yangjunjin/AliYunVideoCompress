package com.aliyun.alivcsolution;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.aliyun.crop.AliyunCropCreator;
import com.aliyun.crop.struct.CropParam;
import com.aliyun.crop.supply.AliyunICrop;
import com.aliyun.crop.supply.CropCallback;
import com.aliyun.svideo.sdk.external.struct.common.VideoDisplayMode;
import com.aliyun.svideo.sdk.external.struct.common.VideoQuality;
import com.aliyun.svideo.sdk.external.struct.encoder.VideoCodecs;
import com.aliyun.svideo.sdk.external.struct.snap.AliyunSnapVideoParam;

import org.jetbrains.annotations.Nullable;

/**
 * Created by AliYunVideoCropActivity
 * on 2020/1/4
 */
public class AliYunVideoCropActivity extends Activity implements CropCallback {
    private final static String TAG = AliYunVideoCropActivity.class.getSimpleName() + "=======";
    private AliyunICrop mAliyunICrop;
    private MediaPlayer mPlayer = new MediaPlayer();
    private String mOutPutPath = "/storage/emulated/0/DCIM/Camera/myVideo23.mp4";//压缩后视频存放的地址
    private String mInPutPath = "/storage/emulated/0/DCIM/Camera/video.mp4";//视频地址
    private int mRatioMode = AliyunSnapVideoParam.RATIO_MODE_9_16;//宽高比 RATIO_MODE_1_1,RATIO_MODE_3_4，RATIO_MODE_9_16
    private int mResolutionMode = AliyunSnapVideoParam.RESOLUTION_540P;//分辨率 RESOLUTION_360P RESOLUTION_480P RESOLUTION_540P RESOLUTION_720P
    private int mOutPutWidth = 1280;//视频的宽
    private int mOutPutHeight = 720;//视频的高
    private long mEndTime = 283868;//视频的长度

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alivc_crop_activity_video_crop);
        mAliyunICrop = AliyunCropCreator.createCropInstance(this);
        mAliyunICrop.setCropCallback(this);
        calculateWidth();
        calculateTimeHeight();
        findViewById(R.id.btnVideoCompress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCrop();
            }
        });
    }

    /**
     * 计算视频的宽高
     */
    private void calculateWidth() {
        //分辨率（视频宽）
        switch (mResolutionMode) {
            case AliyunSnapVideoParam.RESOLUTION_360P:
                mOutPutWidth = 360;
                break;
            case AliyunSnapVideoParam.RESOLUTION_480P:
                mOutPutWidth = 480;
                break;
            case AliyunSnapVideoParam.RESOLUTION_540P:
                mOutPutWidth = 540;
                break;
            case AliyunSnapVideoParam.RESOLUTION_720P:
                mOutPutWidth = 720;
                break;
        }
        //高
//        if (originalVideoAspectRatio > 0) {
//            mOutPutHeight = (int) (mOutPutWidth * originalVideoAspectRatio);
//        } else {
//            switch (mRatioMode) {
//                case AliyunSnapVideoParam.RATIO_MODE_1_1:
//                    mOutPutHeight = mOutPutWidth;
//                    break;
//                case AliyunSnapVideoParam.RATIO_MODE_3_4:
//                    mOutPutHeight = mOutPutWidth * 4 / 3;
//                    break;
//                case AliyunSnapVideoParam.RATIO_MODE_9_16:
//                    mOutPutHeight = mOutPutWidth * 16 / 9;
//                    break;
//            }
//        }
    }

    /**
     * 初始化参数
     */
    private void calculateTimeHeight() {
        try {
            mPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
                    if (height > 0 && width > 0) {
                        float originalVideoAspectRatio = 1.0f * height / width;//大于1是横屏，小于1是竖屏
                        mOutPutHeight = (int) (mOutPutWidth * originalVideoAspectRatio);
                        Log.e(TAG, "playtime3=width=" + width + "，height=" + height + ",duration=" + mediaPlayer.getDuration() + ",originalVideoAspectRatio=" + originalVideoAspectRatio);
                    }
                    mEndTime = mediaPlayer.getDuration() * 1000;
                }
            });
            mPlayer.setDataSource(mInPutPath);
            mPlayer.prepareAsync();
        } catch (Exception e) {
        }
    }

    /**
     * 开始压缩
     */
    private void startCrop() {
        Log.e(TAG, "playtime2:" + "w=" + mOutPutWidth + ",h=" + mOutPutHeight);
        CropParam cropParam = new CropParam();
        cropParam.setOutputPath(mOutPutPath);//出的路径
        cropParam.setInputPath(mInPutPath);//入的路径
        cropParam.setOutputWidth(mOutPutWidth);
        cropParam.setOutputHeight(mOutPutHeight);

        cropParam.setStartTime(0);
        cropParam.setEndTime(mEndTime);
        cropParam.setScaleMode(VideoDisplayMode.FILL);//裁剪模式FILL（完整显示）,SCALE(会被截取)
        cropParam.setFrameRate(30);//设置帧率（20-->30)
        cropParam.setGop(300);//设置关键帧间隔(1-->300)
        cropParam.setQuality(VideoQuality.HD);//视频质量,SSD极高，HD高，SD中，LD低
        cropParam.setVideoCodec(VideoCodecs.H264_HARDWARE);//H264_HARDWARE,H264_SOFT_OPENH264,H264_SOFT_FFMPEG
        mAliyunICrop.setCropParam(cropParam);
        mAliyunICrop.startCrop();
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
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.release();
        mPlayer = null;
    }
}

