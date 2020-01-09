package com.aliyun.alivcsolution;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.aliyun.crop.supply.CropCallback;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = AliYunVideoCropActivity.class.getSimpleName() + "=======";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tvCompress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoCompressUtil.getInstance(MainActivity.this, "/storage/emulated/0/DCIM/Camera/video.mp4", new CropCallback() {
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

                }).start();
            }
        });
    }
}
