package com.meishe.mseffectdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meishe.mseffectdemo.databinding.ActivityMainBinding;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    static {
        System.loadLibrary("mseffectdemo");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},100);
        initData();
        initListener();
    }

    /**
     * 初始化Data
     */
    private void initData() {
        initModel();
    }

    /**
     * 初始化模型文件
     */
    private void initModel() {
        String modelPath = null;
        String licensePath = null;
        String faceModelName = null;
        String className = null;

        modelPath = "/facemodel/facemodel_ms/ms_face_v1.2.2.model";
        faceModelName = "ms_face_v1.2.2.model";
        className = "facemodel/facemodel_ms";
        licensePath = "";


        //初始化人脸识别
        boolean isCopy = FileUtils.copyFileIfNeed(this, faceModelName, className);
        File rootDir = getApplicationContext().getExternalFilesDir(null);
        String destModelDir = rootDir + modelPath;
        Log.d(TAG, "model path:" + destModelDir + "\n   lic path:" + licensePath);
        boolean suc = NvsEffectSdkContext.initHumanDetection(this, destModelDir,
                licensePath,
                NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_FACE_LANDMARK | NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_FACE_ACTION |
                        NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_VIDEO_MODE| NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_IMAGE_MODE );
        // 测试假脸 需要这些dat
        Log.d(TAG, " isCopy:" + isCopy + "   suc :" + suc);
        String fakefacePath = "assets:/facemodel/fakeface.dat";
        boolean fakefaceSuccess = NvsEffectSdkContext.setupHumanDetectionData(NvsEffectSdkContext.HUMAN_DETECTION_DATA_TYPE_FAKE_FACE, fakefacePath);
        Log.e(TAG, "fakefaceSuccess-->" + fakefaceSuccess);
        //美妆模型初始化
        String makeupPath = "assets:/facemodel/makeup.dat";
        boolean makeupSuccess = NvsEffectSdkContext.setupHumanDetectionData(NvsEffectSdkContext.HUMAN_DETECTION_DATA_TYPE_MAKEUP, makeupPath);
        Log.e(TAG, "makeupSuccess-->" + makeupSuccess);
        //全身背景分割初始化
        String segPath = "assets:/facemodel/facemodel_ms/ms_humanseg_v1.0.7.model";
        boolean segSuccess = NvsEffectSdkContext.initHumanDetectionExt(getApplicationContext(),
                segPath, null, NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_SEGMENTATION_BACKGROUND);
        Log.e(TAG, "ms segSuccess-->" + segSuccess);
        //半身背景分割模型，前置摄像头拍摄建议使用半身
        String halfBodyPath = "assets:/facemodel/facemodel_ms/ms_halfbodyseg_v1.0.6.model";
        boolean halfBodySuccess = NvsEffectSdkContext.initHumanDetectionExt(getApplicationContext(),
                halfBodyPath, null, NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_SEGMENTATION_HALF_BODY);
        Log.e(TAG, "ms halfBodySuccess-->" + halfBodySuccess);
    }



    private void initListener() {
        binding.btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,CaptureActivity.class));
            }
        });
    }

    /**
     * A native method that is implemented by the 'mseffectdemo' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}