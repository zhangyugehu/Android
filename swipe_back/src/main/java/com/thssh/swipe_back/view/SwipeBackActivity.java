package com.thssh.swipe_back.view;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.thssh.swipe_back.R;

public class SwipeBackActivity extends AppCompatActivity implements SwipeBackActivityBase {
    private SwipeBackActivityHelper mHelper;
    protected boolean mmHelperFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mHelper = new SwipeBackActivityHelper(this);
        if(this.mmHelperFlag) {
            this.mHelper.onActivityCreate();
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (this.mmHelperFlag) {
            this.mHelper.onPostCreate();
        }

    }

    @Override
    public <T extends View> T findViewById(int id) {
        T v = super.findViewById(id);
        return v != null ? v : this.mHelper.findViewById(id);
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return this.mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        this.getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        this.getSwipeBackLayout().scrollToFinishActivity();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.default_transfer_in_top, R.anim.default_transfer_in_bottom);
    }

    @Override
    public void registerActivityLifecycleCallbacks(@NonNull Application.ActivityLifecycleCallbacks callback) {
        super.registerActivityLifecycleCallbacks(callback);
        overridePendingTransition(R.anim.default_transfer_in_top, R.anim.default_transfer_in_bottom);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.default_transfer_out_bottom,R.anim.default_transfer_out_top);
    }
}
