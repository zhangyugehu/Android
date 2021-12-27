package com.thssh.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InnerScrollListener: RecyclerView.OnScrollListener() {

    var isLoading = false
        set(value) {
            if (!value) {
                viewStateListener?.invoke(false, false)
            }
            field = value
        }

    var firstVisiblePosition = -1
    var lastVisiblePosition = -1

    var onRefreshListener: (() -> Unit)? = null
    var onLoadMoreListener: (() -> Unit)? = null
    var viewStateListener: ((isRefresh: Boolean, isLoadMore: Boolean) -> Unit)? = null

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            recyclerView.layoutManager.let {
                if (!isLoading && it != null && it.childCount < it.itemCount) {
                    if (firstVisiblePosition == 0) {
                        isLoading = true
                        viewStateListener?.invoke(true, false)
                        // refresh
                        onRefreshListener?.invoke()
                    } else if (lastVisiblePosition == it.itemCount - 1) {
                        isLoading = true
                        viewStateListener?.invoke(false, true)
                        // loadMore
                        onLoadMoreListener?.invoke()
                    }
                }
            }
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (recyclerView.layoutManager is LinearLayoutManager) {
            lastVisiblePosition = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            firstVisiblePosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
//            println("$firstVisiblePosition - $lastVisiblePosition" )
        } else {
            lastVisiblePosition = -1
            firstVisiblePosition = -1
        }
    }

}

class StateHolder(textView: TextView): RecyclerView.ViewHolder(textView)

class AdapterWrapper<H: RecyclerView.ViewHolder, in T: RecyclerView.Adapter<H>>(private val delegate: T): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val HIDE_STATE = 0
    private val SHOW_REFRESH = 1
    private val SHOW_LOAD_MORE = 2

    private val VIEW_TYPE_REFRESH = -999
    private val VIEW_TYPE_LOAD_MORE = -998
    private val VIEW_ITEM = -997

    var state = HIDE_STATE

    fun showRefreshView(show: Boolean) {
        state = if (show) SHOW_REFRESH else HIDE_STATE
        notifyItemChanged(0)
    }

    fun showLoadMoreView(show: Boolean) {
        state = if (show) SHOW_LOAD_MORE else HIDE_STATE
        notifyItemChanged(itemCount - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_REFRESH) {
            val textView = TextView(parent.context)
            textView.height = 100
            textView.text = "refresh..."
            return StateHolder(textView)
        } else if (viewType == VIEW_TYPE_LOAD_MORE) {
            val textView = TextView(parent.context)
            textView.height = 100
            textView.text = "loading..."
            return StateHolder(textView)
        }
        return delegate.onCreateViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_REFRESH
            delegate.itemCount -> VIEW_TYPE_LOAD_MORE;
            else -> VIEW_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is StateHolder) {
            if (state == HIDE_STATE) {
                holder.itemView.visibility = RecyclerView.GONE
            } else if ((state == SHOW_LOAD_MORE && position == delegate.itemCount) || (state == SHOW_REFRESH && position == 0)) {
                holder.itemView.visibility = RecyclerView.VISIBLE
            }
        } else {
            delegate.onBindViewHolder(holder as H, position - 1)
        }
    }

    override fun getItemCount(): Int {
        return delegate.itemCount + 1
    }
}

class LoadMoreRecyclerView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : RecyclerView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    private val viewStateListener = { isRefresh: Boolean, isLoadMore: Boolean ->
        if (adapter is AdapterWrapper<*, *>) {
            val cast: AdapterWrapper<*, *> = adapter as AdapterWrapper<*, *>
            when {
                isRefresh -> cast.showRefreshView(true)
                isLoadMore -> cast.showLoadMoreView(true)
                else -> {
                    cast.showRefreshView(false)
                    cast.showLoadMoreView(false)
                }
            }
        }
    }

    private val scrollListener by lazy {
        val listener = InnerScrollListener()
        listener.viewStateListener = viewStateListener
        listener
    }

    init {
        addOnScrollListener(scrollListener)
    }

    fun setOnRefreshListener(listener: () -> Unit) {
        scrollListener.onRefreshListener = listener
    }

    fun setOnLoadMoreListener(listener: () -> Unit) {
        scrollListener.onLoadMoreListener = listener
    }

    fun loadCompleted() {
        scrollListener.isLoading = false
    }
}