package com.aliyun.alivcsolution;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.aliyun.common.utils.ToastUtil;
import com.aliyun.crop.AliyunCropCreator;
import com.aliyun.crop.struct.CropParam;
import com.aliyun.crop.supply.AliyunICrop;
import com.aliyun.crop.supply.CropCallback;
import com.aliyun.svideo.sdk.external.struct.common.VideoDisplayMode;
import com.aliyun.svideo.sdk.external.struct.common.VideoQuality;
import com.aliyun.svideo.sdk.external.struct.encoder.VideoCodecs;

import org.jetbrains.annotations.Nullable;

/**
 * Created by AliYunVideoCropActivity
 * on 2020/1/4
 */
public class AliYunVideoCropActivity extends Activity implements CropCallback {

    private final static String TAG = AliYunVideoCropActivity.class.getSimpleName() + "=======";
    private AliyunICrop crop;
    private VideoQuality quality = VideoQuality.HD;
    private VideoCodecs mVideoCodec = VideoCodecs.H264_HARDWARE;
    private VideoDisplayMode cropMode = VideoDisplayMode.SCALE;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alivc_crop_activity_video_crop);
        crop = AliyunCropCreator.createCropInstance(this);
        crop.setCropCallback(this);

        findViewById(R.id.btnVideoCompress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCrop();
            }
        });
    }

    /**
     * 开始压缩
     */
    String outputPath = "/storage/emulated/0/DCIM/Camera/myVideo1.mp4";
    String path = "/storage/emulated/0/DCIM/Camera/video.mp4";
    int outputWidth = 540;
    int outputHeight = 720;
    int posX=0;
    int posY=0;
    int cropWidth=720;
    int cropHeight=960;
    int mStartTime=0;
    int mEndTime=283868;
    int frameRate=25;
    int gop=5;

    private void startCrop() {
        CropParam cropParam = new CropParam();
        cropParam.setOutputPath(outputPath);//出的路径
        cropParam.setInputPath(path);//入的路径
        cropParam.setOutputWidth(outputWidth);
        cropParam.setOutputHeight(outputHeight);

        Rect cropRect = new Rect(posX, posY, posX + cropWidth, posY + cropHeight);
        cropParam.setCropRect(cropRect);
        cropParam.setStartTime(mStartTime * 1000);
        cropParam.setEndTime(mEndTime * 1000);
        cropParam.setScaleMode(cropMode);
        cropParam.setFrameRate(frameRate);
        cropParam.setGop(gop);
        cropParam.setQuality(quality);
        cropParam.setVideoCodec(mVideoCodec);
        cropParam.setFillColor(Color.BLACK);
        cropParam.setCrf(0);
        crop.setCropParam(cropParam);
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
}

