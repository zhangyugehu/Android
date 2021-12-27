package com.thssh.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.View
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

class LoadMoreRecyclerView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : RecyclerView(context, attrs, defStyleAttr) {

    abstract class Adapter<T, VH: ViewHolder>(private val context: Context, private val data: List<T>): RecyclerView.Adapter<ViewHolder>() {

        private val HIDE_STATE = 0
        private val SHOW_REFRESH = 1
        private val SHOW_LOAD_MORE = 2

        private val VIEW_TYPE_REFRESH = -999
        private val VIEW_TYPE_LOAD_MORE = -998
        private val VIEW_ITEM = -997

        var state = HIDE_STATE

        abstract fun onFixedCreateViewHolder(parent: ViewGroup, viewType: Int): VH
        abstract fun onFixedBindViewHolder(holder: VH, position: Int)

        fun showRefreshView(show: Boolean) {
            state = if (show) SHOW_REFRESH else HIDE_STATE
            notifyItemChanged(0)
        }

        fun showLoadMoreView(show: Boolean) {
            state = if (show) SHOW_LOAD_MORE else HIDE_STATE
            notifyItemChanged(itemCount - 1)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
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
            return onFixedCreateViewHolder(parent, viewType)
        }

        override fun getItemViewType(position: Int): Int {
            return when (position) {
                0 -> VIEW_TYPE_REFRESH
                data.size -> VIEW_TYPE_LOAD_MORE;
                else -> VIEW_ITEM
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (holder is StateHolder) {
                if (state == HIDE_STATE) {
                    holder.itemView.visibility = GONE
                } else if ((state == SHOW_LOAD_MORE && position == data.size) || (state == SHOW_REFRESH && position == 0)) {
                    holder.itemView.visibility = VISIBLE
                }
            } else {
                onFixedBindViewHolder(holder as VH, position - 1)
            }
        }

        override fun getItemCount(): Int {
            return data.size + 1
        }
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    private val viewStateListener = { isRefresh: Boolean, isLoadMore: Boolean ->
        if (adapter is Adapter<*, *>)
            when {
                isRefresh -> {
                    (adapter as Adapter<*, *>).showRefreshView(true)
                    println("add refresh view")
                }
                isLoadMore -> {
                    (adapter as Adapter<*, *>).showLoadMoreView(true)
                    println("add loadMore view")
                }
                else -> {
                    (adapter as Adapter<*, *>).showRefreshView(false)
                    (adapter as Adapter<*, *>).showLoadMoreView(false)
                    println("remove")
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