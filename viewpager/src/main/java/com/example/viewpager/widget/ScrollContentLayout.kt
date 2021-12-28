package com.example.viewpager.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.widget.LinearLayout
import android.widget.Scroller
import androidx.core.view.NestedScrollingChild2
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.ViewCompat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ScrollContentLayout(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
    : LinearLayout(context, attrs, defStyleAttr), NestedScrollingChild2 {
    constructor(context: Context?, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context?): this(context, null)

    private var lastX = -1
    private var lastY = -1
    private var velocityTracker: VelocityTracker? = null
    private val childHelper = NestedScrollingChildHelper(this)
    var lastFlingX = -1
    var lastFlingY = -1
    var offset = IntArray(2)
    var consumed = IntArray(2)
    var flingConsumed = IntArray(2)
    private var isFling = false

    var minFlingVelocity = 0
    var maxFlingVelocity = 0
    var scroller: Scroller

    init {
        isNestedScrollingEnabled = true
        val config = ViewConfiguration.get(context)
        minFlingVelocity = config.scaledMinimumFlingVelocity
        maxFlingVelocity = config.scaledMaximumFlingVelocity
        scroller = Scroller(context)
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return childHelper.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        return childHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return childHelper.hasNestedScrollingParent(type)
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int): Boolean {
        var fixDxUnconsumed = dxUnconsumed
        var fixDyUnconsumed = dyUnconsumed
        if (orientation == VERTICAL) {
            fixDxUnconsumed = 0
        } else {
            fixDyUnconsumed = 0
        }
        return childHelper.dispatchNestedScroll(dxConsumed, dyConsumed, fixDxUnconsumed, fixDyUnconsumed, offsetInWindow, type)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?, type: Int): Boolean {
        var fixDx = dx
        var fixDy = dy
        if (orientation == VERTICAL) {
            fixDx = 0
        } else {
            fixDy = 0
        }
        return childHelper.dispatchNestedPreScroll(fixDx, fixDy, consumed, offsetInWindow, type)
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        childHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return childHelper.isNestedScrollingEnabled
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        cancelFling()
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
        velocityTracker?.addMovement(event)
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                consumed[0] = 0
                consumed[1] = 0
                offset[0] = 0
                offset[1] = 0
                lastX = event.x.toInt()
                lastY = event.y.toInt()

                if (orientation == VERTICAL) {
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)
                } else {
                    startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL, ViewCompat.TYPE_TOUCH)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val curX = event.x.toInt()
                val curY = event.y.toInt()

                var dx = lastX - curX
                var dy = lastY - curY

                if (dispatchNestedPreScroll(dx, dy, consumed, offset, ViewCompat.TYPE_TOUCH)) {
                    println(consumed.contentToString() + offset.contentToString())
                    dx -= consumed[0]
                    dy -= consumed[1]
                }

                var consumedX = 0
                var consumedY = 0
                if (orientation == VERTICAL) {
                    consumedY = childConsumedY(dy)
                } else {
                    consumedX = childConsumedX(dx)
                }

                dispatchNestedScroll(consumedX, consumedY, dx - consumedX, dy - consumedY, null, ViewCompat.TYPE_TOUCH)

                lastX = curX
                lastY = curY
            }
            MotionEvent.ACTION_UP -> upOrCancel(event)
            MotionEvent.ACTION_CANCEL -> upOrCancel(event)
        }
        return true
    }

    private fun upOrCancel(event: MotionEvent?) {
        stopNestedScroll(ViewCompat.TYPE_TOUCH)

//        if (velocityTracker != null) {
//            velocityTracker?.computeCurrentVelocity(1000, maxFlingVelocity.toFloat())
//            fling(velocityTracker?.xVelocity?.toInt() ?:0, velocityTracker?.yVelocity?.toInt() ?:0)
//            velocityTracker?.clear()
//        }
        lastY = -1
        lastX = -1
    }

    override fun computeScroll() {
        if (isFling && scroller.computeScrollOffset()) {
            // 获取scroller计算出的当前滚动距离
            val x = scroller.currX
            val y = scroller.currY
            // 计算滚动偏移量，起始坐标-当前坐标
            var dx: Int = lastFlingX - x
            var dy: Int = lastFlingY - y
            lastFlingX = x
            lastFlingY = y

            // 处理消耗滚动偏移量逻辑同ACTION_MOVE（触摸类型为非用户触摸）
            if (dispatchNestedPreScroll(dx, dy, flingConsumed, null, ViewCompat.TYPE_NON_TOUCH)) {
                dx -= flingConsumed[0]
                dy -= flingConsumed[1]
            }
            var flingX = 0
            var flingY = 0
            // 自身或子view处理fling
            if (orientation == VERTICAL) {
                flingX = childFlingX(dx)
            } else {
                flingY = childFlingY(dy)
            }
            dispatchNestedScroll(
                flingX,
                flingY,
                dx - flingX,
                dy - flingY,
                null,
                ViewCompat.TYPE_NON_TOUCH
            )

            // 触发再次执行computeScroll()
            postInvalidate()
        } else {
            stopNestedScroll(ViewCompat.TYPE_NON_TOUCH)
            cancelFling()
        }
    }

    private fun fling(velocityX: Int, velocityY: Int): Boolean {
        if (abs(velocityX) < minFlingVelocity && abs(velocityY) < minFlingVelocity) {
            // 加速度过小，则不进行fling
            return false
        }

        // 通知parent根据滑动方向和滑动类型进行启用嵌套滑动

        // 通知parent根据滑动方向和滑动类型进行启用嵌套滑动
        if (orientation == VERTICAL) {
            startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH)
        } else {
            startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL, ViewCompat.TYPE_NON_TOUCH)
        }

        // 限制加速度值范围不超过maxFlingVelocity

        // 限制加速度值范围不超过maxFlingVelocity
        doFling(
            max(-maxFlingVelocity, min(velocityX, maxFlingVelocity)),
            max(-maxFlingVelocity, min(velocityY, maxFlingVelocity))
        )
        return true
    }

    private fun doFling(velocityX: Int, velocityY: Int) {
//        isFling = true
        // 将加速度值交由scroller计算
        // 将加速度值交由scroller计算
        scroller.fling(
            0,
            0,
            velocityX,
            velocityY,
            Int.MIN_VALUE,
            Int.MAX_VALUE,
            Int.MIN_VALUE,
            Int.MAX_VALUE
        )
        // 触发执行computeScroll()
        // 触发执行computeScroll()
        postInvalidate()
    }

    private fun cancelFling() {
        isFling = false
        lastFlingX = 0
        lastFlingY = 0
    }

    private fun childConsumedX(dx: Int): Int {
//        println("childConsumedX $dx")
        return 0
    }

    private fun childConsumedY(dy: Int): Int {
//        println("childConsumedY $dy")
        return 0
    }

    private fun childFlingY(dy: Int): Int {
//        println("childFlingY $dy")
        return 0
    }

    private fun childFlingX(dx: Int): Int {
//        println("childFlingX dx")
        return 0
    }
}