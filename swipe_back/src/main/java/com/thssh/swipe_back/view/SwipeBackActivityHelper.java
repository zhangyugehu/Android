package com.thssh.swipe_back.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.thssh.swipe_back.BuildConfig;
import com.thssh.swipe_back.R;

import java.lang.reflect.Method;

public class SwipeBackActivityHelper {
    private Activity mActivity;

    private SwipeBackLayout mSwipeBackLayout;

    public SwipeBackActivityHelper(Activity activity) {
        mActivity = activity;
    }

    @SuppressWarnings("deprecation")
    public void onActivityCreate() {
        if (BuildConfig.DEBUG) Log.d("SwipeBackLayoutTag", "onActivityCreate: ");
        mActivity.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        mActivity.getWindow().getDecorView().setBackgroundDrawable(null);
        mSwipeBackLayout = (SwipeBackLayout) LayoutInflater.from(mActivity).inflate(R.layout.swipeback_layout, null);
        mSwipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {
                if (state == SwipeBackLayout.STATE_IDLE && scrollPercent == 0) {
                    if (BuildConfig.DEBUG) Log.d("SwipeBackLayoutTag", "convertActivityFromTranslucent: ");

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                        mActivity.setTranslucent(false);
//                    } else {
//                        convertActivityFromTranslucent();
//                    }
                }
            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
                if (BuildConfig.DEBUG) Log.d("SwipeBackLayoutTag", "convertActivityToTranslucent: ");

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    mActivity.setTranslucent(true);
//                } else {
//                    convertActivityToTranslucent();
//                }
            }

            @Override
            public void onScrollOverThreshold() {

            }
        });
    }

    public void onPostCreate() {
        if (BuildConfig.DEBUG) Log.d("SwipeBackLayoutTag", "onPostCreate: ");
        mSwipeBackLayout.attachToActivity(mActivity);
        convertActivityFromTranslucent();
    }

    public <T extends View> T findViewById(int id) {
        if (mSwipeBackLayout != null) {
            return mSwipeBackLayout.findViewById(id);
        }
        return null;
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} to a fullscreen opaque
     * Activity.
     * <p>
     * Call this whenever the background of a translucent Activity has changed
     * to become opaque. Doing so will allow the {@link android.view.Surface} of
     * the Activity behind to be released.
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public void convertActivityFromTranslucent() {
        try {
            //最后一个参数使用了不准确的变量类型的 varargs 方法的非 varargs 调用 null 改为new  Class[ 0 ]，new  Object[]{}
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent", new Class[0]);
            method.setAccessible(true);
            method.invoke(mActivity, new Object[]{});
        } catch (Throwable t) {
        }
    }

    /**
     * Convert a translucent themed Activity
     * {@link android.R.attr#windowIsTranslucent} back from opaque to
     * translucent following a call to {@link #convertActivityFromTranslucent()}
     * .
     * <p>
     * Calling this allows the Activity behind this one to be seen again. Once
     * all such Activities have been redrawn
     * <p>
     * This call has no effect on non-translucent activities or on activities
     * with the {@link android.R.attr#windowIsFloating} attribute.
     */
    public void convertActivityToTranslucent() {
        try {
            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method method = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz,
                    ActivityOptions.class);
            method.setAccessible(true);
            method.invoke(mActivity, new Object[]{null, null});
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
