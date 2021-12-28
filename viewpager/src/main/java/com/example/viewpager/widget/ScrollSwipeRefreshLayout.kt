package com.example.viewpager.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.abs

class ScrollSwipeRefreshLayout(context: Context, attrs: AttributeSet?) :
    SwipeRefreshLayout(context, attrs) {
        constructor(context: Context): this(context, null)

    var lastX = 0F
    var lastY = 0F
    var isHorizontal = false
    var touchSlop = 0

    init {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isHorizontal = false
                lastX = ev.x
                lastY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (isHorizontal) {
                    return false
                }
                val dx = abs(ev.x - lastX)
                val dy = abs(ev.y - lastY)
                if (dx > touchSlop && dx > dy) {
                    isHorizontal = true
                    return false
                }
            }
            MotionEvent.ACTION_UP -> {
                isHorizontal = false
            }
            MotionEvent.ACTION_CANCEL -> {
                isHorizontal = false
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}