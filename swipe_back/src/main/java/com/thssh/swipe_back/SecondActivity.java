package com.thssh.swipe_back;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.thssh.commonlib.PopHelper;

public class SecondActivity extends BaseActivity {

    private TextView textView;

    private final Runnable delayRun = () -> {
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        textView.setText("Pop");
        PopHelper helper = new PopHelper(textView, Color.BLUE);
        helper.show();
//        textView.postDelayed(helper::dismiss, 10_000);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        textView = new TextView(this);
        getWindow().getDecorView().postDelayed(delayRun, 5_500);
    }

    public void onContentClick(View view) {
        scrollToFinishActivity();
    }

    @Override
    protected void onDestroy() {
        getWindow().getDecorView().removeCallbacks(delayRun);
        super.onDestroy();
    }
}