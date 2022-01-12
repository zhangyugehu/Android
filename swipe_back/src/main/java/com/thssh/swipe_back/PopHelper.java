package com.thssh.swipe_back;

import static android.content.Context.WINDOW_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;

public class PopHelper {
    private final Context context;
    private final LinearLayout wrapLayout;

    private WindowManager windowManager;

    public PopHelper(View contentView, int backgroundColor) {
        this.context = contentView.getContext();
        wrapLayout = new LinearLayout(context);
        wrapLayout.setBackgroundColor(backgroundColor);
        int minHeight = 0;
        int statusBarHeight = getStatusBarHeight(context);
        if (context instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) context).getSupportActionBar();
            minHeight = supportActionBar != null ? supportActionBar.getHeight() : getActionBarHeight((AppCompatActivity) context);
        } else if (context instanceof Activity) {
            android.app.ActionBar actionBar = ((Activity) context).getActionBar();
            minHeight = actionBar != null ? actionBar.getHeight() : getActionBarHeight((Activity) context);
        }
        wrapLayout.setMinimumHeight(minHeight + statusBarHeight);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = statusBarHeight;
        wrapLayout.setPadding(14, 0, 14, 0);
        wrapLayout.addView(contentView, layoutParams);
    }

    public void show() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION);
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        params.gravity = Gravity.TOP;
        params.windowAnimations = R.style.topTipsAnim;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        getWindowManager().addView(wrapLayout, params);
    }

    public void dismiss() {
        getWindowManager().removeViewImmediate(wrapLayout);
    }

    private WindowManager getWindowManager() {
        if (windowManager == null) {
            windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        }
        return windowManager;
    }


    private int statusBarHeight;
    public int getStatusBarHeight(@NonNull Context context) {
        if (statusBarHeight != 0) {
            return statusBarHeight;
        }
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelOffset(resId);
        }
        if (statusBarHeight <= 0) {
            Class<?> c = null;
            Object obj = null;
            Field field = null;
            int x = 0;
            try {
                c = Class.forName("com.android.internal.R$dimen");
                obj = c.newInstance();
                field = c.getField("status_bar_height");
                x = Integer.parseInt(field.get(obj).toString());
                statusBarHeight = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return statusBarHeight;
    }



    public int getActionBarHeight(Activity a) {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (a.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, a.getResources().getDisplayMetrics());
        }
        return actionBarHeight;

    }
}
