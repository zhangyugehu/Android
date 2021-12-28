package com.example.viewpager.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.LinearLayout
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import java.lang.IllegalArgumentException
import kotlin.math.abs

class TwoChildScrollLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent2 {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context): this(context, null)

    private lateinit var topView: View
    private lateinit var contentView: View
    private lateinit var parentHelper: NestedScrollingParentHelper

    private var refreshLayout: SwipeRefreshLayout? = null

    private var topHeight = 0

    init {
        orientation = VERTICAL
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 1) {
            topView = getChildAt(0)
            contentView = getChildAt(1)
            parentHelper = NestedScrollingParentHelper(this)
        } else {
            throw IllegalArgumentException("two child view needed!!!")
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        topHeight = topView.measuredHeight
        if (refreshLayout == null && parent is SwipeRefreshLayout) {
            refreshLayout = parent as SwipeRefreshLayout
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val lp = contentView.layoutParams
        lp.height = measuredHeight
        contentView.layoutParams = lp
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        when (contentView) {
            is RecyclerView -> (contentView as RecyclerView).stopScroll()
            is NestedScrollView -> contentView.stopNestedScroll()
            is ViewPager -> contentView.stopNestedScroll()
            else -> contentView.stopNestedScroll()
        }
        topView.stopNestedScroll()
        val handled = axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
        if (handled && refreshLayout != null && scrollY != 0) {
            refreshLayout?.isEnabled = false
        }
        return handled
    }

    override fun getNestedScrollAxes(): Int {
        return parentHelper.nestedScrollAxes
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        parentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        refreshLayout?.isEnabled = true
        parentHelper.onStopNestedScroll(target, type)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        if (dyUnconsumed > 0) {
            if (target == topView) {
                scrollBy(0, dyUnconsumed)
            }
        }
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        val hideTop = dy > 0 && scrollY < topHeight
        val showTop = dy < 0
                && scrollY > 0
                && !target.canScrollVertically(-1)
                && !contentView.canScrollVertically(-1)
        if (hideTop || showTop) {
            scrollBy(0, dy)
            consumed[1] = dy
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        var fixY = y
        if (fixY < 0) {
            fixY = 0
        }
        if (fixY > topHeight) {
            fixY = topHeight
        }
        super.scrollTo(x, fixY)
    }

}