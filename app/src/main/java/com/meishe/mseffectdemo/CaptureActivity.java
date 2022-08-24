package com.meishe.mseffectdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.meicam.effect.sdk.NvsARSceneManipulate;
import com.meicam.effect.sdk.NvsAssetPackageManager;
import com.meicam.effect.sdk.NvsEffect;
import com.meicam.effect.sdk.NvsEffectRenderCore;
import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meicam.effect.sdk.NvsRational;
import com.meicam.effect.sdk.NvsVideoEffect;
import com.meicam.effect.sdk.NvsVideoFrameInfo;
import com.meicam.effect.sdk.NvsVideoResolution;
import com.meishe.mseffectdemo.databinding.ActivityCaptureBinding;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CaptureActivity extends AppCompatActivity {

    private ActivityCaptureBinding mBinding;
    private SurfaceHolder mSurfaceHolder;
    private MSVideoChannel mMsVideoChannel;
    private final static String TAG = "CaptureActivity";
    public static final int AspectRatio_9v16 = 4;


    private List<NvsEffect> effects = new ArrayList<>();
    private NvsVideoResolution mCurrentVideoResolution = new NvsVideoResolution();
    private NvsEffectSdkContext mNvsEffectSdkContext;
    private NvsEffectRenderCore mEffectRenderCore;

    private NvsVideoFrameInfo mNvsVideoFrameInfo=new NvsVideoFrameInfo();
    private long mStartPreviewTime;

    private String mFilterId;
    private NvsVideoEffect mFilter;

    private boolean isShowImageView;
    private String mSceneId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*设置全屏*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBinding= ActivityCaptureBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mNvsEffectSdkContext= NvsEffectSdkContext.getInstance();
        mEffectRenderCore = mNvsEffectSdkContext.createEffectRenderCore();

        mEffectRenderCore.initialize(NvsEffectRenderCore.NV_EFFECT_CORE_FLAGS_SUPPORT_8K
                |NvsEffectRenderCore.NV_EFFECT_CORE_FLAGS_CREATE_GLCONTEXT_IF_NEED|
                NvsEffectRenderCore.NV_EFFECT_CORE_FLAGS_IN_SINGLE_GLTHREAD);


        initSurfaceView();

        // 安装资源包
        mFilterId = DataHelper.createStickerItem("assets:/1AACCE79-7EAB-4B2E-AE9B-E53A02AFC055.3.videofx", NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX);

        if (mFilter == null) {
            NvsRational nvsRational = new NvsRational(9,16);
            mFilter = mNvsEffectSdkContext.createVideoEffect(mFilterId, nvsRational);
            effects.add(mFilter);
        }


        mSceneId =  DataHelper.createStickerItem("assets:/9C917EE3-A1B0-4B5D-B50F-9624A6824A6B.arscene", NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ARSCENE);


        NvsRational nvsRational = new NvsRational(9,16);
        NvsVideoEffect nvsVideoEffect = mNvsEffectSdkContext.createVideoEffect("AR Scene", nvsRational);

        NvsARSceneManipulate arSceneManipulate = nvsVideoEffect.getARSceneManipulate();
        //支持的人脸个数，是否需要使用最小的设置
        nvsVideoEffect.setBooleanVal(Constants.MAX_FACES_RESPECT_MIN, true);
        arSceneManipulate.setDetectionMode(NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_IMAGE_MODE);
        arSceneManipulate.setDetectionAutoProbe(true);
        arSceneManipulate.setARSceneCallback(new NvsARSceneManipulate.NvsARSceneManipulateCallback() {
            @Override
            public void notifyFaceBoundingRect(List<NvsARSceneManipulate.NvsFaceBoundingRectInfo> list) {
//                通知人脸道具检测到的人脸的范围 请特别注意:这个函数被调用是在后台的线程,而不是在UI线程.使用请考虑线程安全的问题!!
                        Log.d("lpf","notifyFaceBoundingRect list.size="+list.size());
            }

            @Override
            public void notifyFaceFeatureInfos(List<NvsARSceneManipulate.NvsFaceFeatureInfo> list) {
//                通知人脸道具检测到的人脸特征信息 请特别注意:这个函数被调用是在后台的线程,而不是在UI线程.使用请考虑线程安全的问题!!
                        Log.d("lpf","notifyFaceFeatureInfos list.size="+list.size());
            }

            @Override
            public void notifyCustomAvatarRealtimeResourcesPreloaded(boolean b) {
//                通知捏脸特效实时模式下所需预加载的资源是否已加载
            }

            @Override
            public void notifyDetectionTimeCost(float v) {
//                	通知人脸检测用时
            }

            @Override
            public void notifyTotalTimeCost(float v) {
//    通知总渲染用时。

            }
        });


        nvsVideoEffect.setBooleanVal("Single Buffer Mode", false);
        nvsVideoEffect.setBooleanVal("Beauty Effect", true);
        nvsVideoEffect.setBooleanVal("Beauty Shape", true);
        nvsVideoEffect.setBooleanVal("Advanced Beauty Enable",true);
        nvsVideoEffect.setFloatVal("Beauty Strength", 0.5f);
        nvsVideoEffect.setFloatVal("Beauty Whitening", 0.5f);
        nvsVideoEffect.setFloatVal("Advanced Beauty Intensity", 1.0f);
        nvsVideoEffect.setIntVal("Advanced Beauty Type", 1);
        nvsVideoEffect.setFloatVal("Beauty Reddening", 0.5f);
        nvsVideoEffect.setBooleanVal("Default Beauty Enabled", true);
        nvsVideoEffect.setFloatVal("Default Intensity", 1.0f);
        nvsVideoEffect.setBooleanVal("Default Sharpen Enabled", true);
        nvsVideoEffect.setStringVal("Scene Id", mSceneId);

        effects.add(nvsVideoEffect);


        mBinding.btnStartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.imageView.setVisibility(View.VISIBLE);
                isShowImageView=true;
            }
        });

        mBinding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.imageView.setVisibility(View.GONE);
                isShowImageView=false;
            }
        });

    }

    private void initSurfaceView() {
        mBinding.surfaceview.setKeepScreenOn(true);
        mSurfaceHolder = mBinding.surfaceview.getHolder();
        /*设置surface不需要自己的维护缓存区*/
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(new MSSurfaceCallback());

        mMsVideoChannel = MSVideoChannel.getInstance();

        mMsVideoChannel.setCallback(new MSVideoChannel.Callback() {
            @Override
            public void videoData(byte[] data) {
                if (isShowImageView) {

                    mNvsVideoFrameInfo.frameWidth = mMsVideoChannel.getWidth();
                    mNvsVideoFrameInfo.frameHeight = mMsVideoChannel.getHeight();
                    Log.d(TAG, "------videoData------mNvsVideoFrameInfo.frameWidth="+mNvsVideoFrameInfo.frameWidth+" mNvsVideoFrameInfo.frameHeight="+mNvsVideoFrameInfo.frameHeight);
                    mNvsVideoFrameInfo.displayRotation = 0;
                    mNvsVideoFrameInfo.flipHorizontally = true;
                    mNvsVideoFrameInfo.isRec601=true;
                    mNvsVideoFrameInfo.pixelFormat = NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_NV21;


                    Bitmap pictureBitmap = Bitmap.createBitmap(mNvsVideoFrameInfo.frameWidth,
                            mNvsVideoFrameInfo.frameHeight, Bitmap.Config.ARGB_8888);

                    if (effects.size() > 0) {
                        //前置摄像头  检测方向是270
                        ByteBuffer byteBufferResult = mEffectRenderCore.renderEffects(effects.toArray(new NvsEffect[effects.size()]),
                                data, mNvsVideoFrameInfo, 270,
                                NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_RGBA, false,
                                (System.currentTimeMillis() - mStartPreviewTime) * 1000, 0);

                        if (null != byteBufferResult) {
                            pictureBitmap.copyPixelsFromBuffer(byteBufferResult);
                            Bitmap bitmap = adjustBitmapRotation(pictureBitmap, 90);
                            mBinding.imageView.setImageBitmap(bitmap);
                        }
                    }



//                    Observable.just(effects).map(new Function<List<NvsEffect>, Bitmap>() {
//                        @Override
//                        public Bitmap apply(List<NvsEffect> nvsEffects) throws Exception {
//
//                            Bitmap pictureBitmap = Bitmap.createBitmap(mNvsVideoFrameInfo.frameWidth,
//                                    mNvsVideoFrameInfo.frameHeight, Bitmap.Config.ARGB_8888);
//
//                            if (effects.size() > 0) {
//
//
//                                ByteBuffer byteBufferResult = mEffectRenderCore.renderEffects(effects.toArray(new NvsEffect[effects.size()]),
//                                        data, mNvsVideoFrameInfo, 0,
//                                        NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_RGBA, false,
//                                        (System.currentTimeMillis() - mStartPreviewTime) * 1000, 0);
//
//                                if (null != byteBufferResult) {
//                                    pictureBitmap.copyPixelsFromBuffer(byteBufferResult);
//                                }
//                            }
//
//                            return pictureBitmap;
//                        }
//                    }).observeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread()).doOnNext(new Consumer<Bitmap>() {
//                        @Override
//                        public void accept(Bitmap bitmap) throws Exception {
//                            if (bitmap!=null){
//                                mBinding.imageView.setImageBitmap(bitmap);
//                            }
//                        }
//                    }).doOnError(new Consumer<Throwable>() {
//                        @Override
//                        public void accept(Throwable throwable) throws Exception {
//                            Log.e(TAG, throwable.getMessage());
//                        }
//                    }).subscribe();

                }
            }
        });
    }


    private class MSSurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
            Log.d(TAG, "------surfaceCreated------");
            mMsVideoChannel.openCamera(CaptureActivity.this,mSurfaceHolder);
            mStartPreviewTime = System.currentTimeMillis();
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            Log.d(TAG, "------surfaceChanged------");
            mMsVideoChannel.openCamera(CaptureActivity.this,mSurfaceHolder);
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
            Log.d(TAG, "------surfaceDestroyed------");
            mMsVideoChannel.doStopCamera();
        }
    }


    public Bitmap adjustBitmapRotation(Bitmap bitmap,int orientationDegree){
        Matrix matrix=new Matrix();
        matrix.setRotate(orientationDegree, (float) bitmap.getWidth() / 2,
                (float) bitmap.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree==90){
            targetX = bitmap.getHeight();
            targetY = 0;
        }else{
            targetX = bitmap.getHeight();
            targetY = bitmap.getWidth();
        }

        final float[] values = new float[9];
        matrix.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        matrix.postTranslate(targetX - x1, targetY - y1);

        Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(),
                Bitmap.Config.ARGB_8888);


        Paint paint = new Paint();
        Canvas canvas = new Canvas(canvasBitmap);
        canvas.drawBitmap(bitmap, matrix, paint);

        return canvasBitmap;

    }

}