package com.thssh.swipe_back;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSwipeBackEnable(false);
    }

    public void onGoSecondActivity(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }
}