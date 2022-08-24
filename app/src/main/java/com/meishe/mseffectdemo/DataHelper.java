package com.meishe.mseffectdemo;


import com.meicam.effect.sdk.NvsAssetPackageManager;
import com.meicam.effect.sdk.NvsEffectSdkContext;


public class DataHelper {


    public static String createStickerItem(String itemPath, int type) {
        StringBuilder sceneId = new StringBuilder();
        int ret = NvsEffectSdkContext.getInstance().getAssetPackageManager().installAssetPackage(itemPath, null, type, true, sceneId);
        if (ret == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR
                || ret == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_ALREADY_INSTALLED) {
            return sceneId.toString();
        } else if (ret == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_UPGRADE_VERSION) {
            ret = NvsEffectSdkContext.getInstance().getAssetPackageManager().upgradeAssetPackage(itemPath, null, type, true, sceneId);
            if (ret != NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR) {
                return null;
            } else {
                return sceneId.toString();
            }
        } else {
            return null;
        }
    }


}
