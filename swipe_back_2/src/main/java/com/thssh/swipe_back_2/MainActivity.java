package com.thssh.swipe_back_2;

import android.content.Intent;
import android.os.Bundle;

import com.thssh.swipe_back_2.lib.swipbackhelper.SwipeBackHelper;
import com.thssh.swipe_back_2.ui.login.LoginActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().postDelayed(() -> startActivity(new Intent(this, LoginActivity.class)), 2_000);
    }
}