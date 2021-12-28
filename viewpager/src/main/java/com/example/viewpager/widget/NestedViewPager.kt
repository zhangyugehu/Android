package com.example.viewpager

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.NestedScrollingChild2
import androidx.core.view.NestedScrollingParent2
import androidx.viewpager.widget.ViewPager

class NestedViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs),
        NestedScrollingParent2, NestedScrollingChild2 {
    constructor(context: Context): this(context, null)

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        println("startNestedScroll")
        return true
    }

    override fun stopNestedScroll(type: Int) {
        println("stopNestedScroll type:$type")
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        println("hasNestedScrollingParent type: $type")
        return true
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int): Boolean {
        println("dispatchNestedScroll")
        return true
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?, type: Int): Boolean {
        println("dispatchNestedPreScroll")
        return true
    }

    // parent2

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        println("onStartNestedScroll")
        return true
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        println("onNestedScrollAccepted")
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        println("onStopNestedScroll")
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        println("onNestedScroll")
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        println("onNestedScroll")
    }
}