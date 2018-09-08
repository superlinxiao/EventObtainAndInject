package com.linxiao.motioneventobtain.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;


/**
 * ---------------------------------------------------------------
 * Author: ZhaoYidong
 * Create: 2017/7/3 15:27
 * ---------------------------------------------------------------
 * Describe:
 * ---------------------------------------------------------------
 * Changes:
 * ---------------------------------------------------------------
 * 2017/7/3 15 : Create by ZhaoYidong
 * ---------------------------------------------------------------
 */
public class MyApplication extends Application {
  private static MyApplication sApplication = null;

  @Override
  public void onCreate() {
    super.onCreate();
    sApplication = this;
  }

  public static MyApplication getApplication() {
    return sApplication;
  }

  /**
   * 获取应用的版本号
   *
   * @return
   */
  private static int getVersionCode(Context context) {
    int code = 0;
    try {
      PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      code = info.versionCode;
      return code;
    } catch (Exception e) {
    }
    return code;

  }
}
