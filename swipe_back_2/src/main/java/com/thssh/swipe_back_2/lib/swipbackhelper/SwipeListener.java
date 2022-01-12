package com.thssh.swipe_back_2.lib.swipbackhelper;

public interface SwipeListener {
        void onScroll(float percent,int px);
        void onEdgeTouch();
        /**
         * Invoke when scroll percent over the threshold for the first time
         */
        void onScrollToClose();
    }