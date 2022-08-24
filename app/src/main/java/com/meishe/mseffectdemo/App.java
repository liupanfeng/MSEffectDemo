package com.meishe.mseffectdemo;

import android.app.Application;

import com.meicam.effect.sdk.NvsEffectSdkContext;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: lpf
 * @CreateDate: 2022/8/23 下午8:16
 * @Description:
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NvsEffectSdkContext.init(this, "assets:/meishesdk.lic", 0);
    }
}
