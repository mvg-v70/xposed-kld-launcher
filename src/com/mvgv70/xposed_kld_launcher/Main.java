package com.mvgv70.xposed_kld_launcher;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Main implements IXposedHookLoadPackage
{
  private static ViewGroup mWorkspace;
  private final static String TAG = "xposed-kld-launcher";
  
  @Override
  public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable 
  {
    // Launcher.onCreate(Bundle)
	  XC_MethodHook onCreate = new XC_MethodHook() {

      @Override
      protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Log.d(TAG,"onCreate");
        // показать версию модуля
        try 
        {
          Activity launcher = (Activity)param.thisObject; 
          Context context = launcher.createPackageContext(getClass().getPackage().getName(), Context.CONTEXT_IGNORE_SECURITY);
          String version = context.getString(R.string.app_version_name);
          Log.d(TAG,"version="+version);
        } catch (NameNotFoundException e) {}
        mWorkspace = (ViewGroup)XposedHelpers.getObjectField(param.thisObject, "mWorkspace");
        if (mWorkspace.getChildCount() > 1)
        {
          View first_screen = mWorkspace.getChildAt(0);
          mWorkspace.removeViewAt(0);
          mWorkspace.addView(first_screen);
        }
      }
    };
    
    // start hooks
    if (!lpparam.packageName.equals("com.android.launcher")) return;
    Log.d(TAG,"package com.android.launcher2");
    XposedHelpers.findAndHookMethod("com.android.launcher2.Launcher", lpparam.classLoader, "onCreate", Bundle.class, onCreate);
    Log.d(TAG,"com.android.launcher2 hook OK");
  }

}
