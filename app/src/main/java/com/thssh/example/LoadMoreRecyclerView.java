package com.thssh.example;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LoadMoreRecyclerView extends RecyclerView {

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnRequestListener extends OnRefreshListener, OnLoadMoreListener{

    }

    private OnLoadMoreListener loadMoreListener;
    private OnRefreshListener refreshListener;

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.loadMoreListener = listener;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.refreshListener = listener;
    }

    public void setOnRequestListener(OnRequestListener listener) {
        setOnLoadMoreListener(listener);
        setOnRefreshListener(listener);
    }

    public LoadMoreRecyclerView(@NonNull Context context) {
        super(context);
        initView(context, null, 0);
    }

    public LoadMoreRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public LoadMoreRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs, defStyle);
    }

    final class InnerScrollListener extends RecyclerView.OnScrollListener {

        private boolean isLoading;
        private int firstVisibleItemPosition, lastVisibleItemPosition;

        private RecyclerView.LayoutManager layoutManager;

        public RecyclerView.LayoutManager getLinearLayoutManager() {
            if (layoutManager == null) {
                layoutManager = getLayoutManager();
            }
            return layoutManager;
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            RecyclerView.LayoutManager layoutManager = getLinearLayoutManager();
            if (newState == RecyclerView.SCROLL_STATE_IDLE && layoutManager != null) {
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if (visibleItemCount < totalItemCount && !isLoading) {
                    if (firstVisibleItemPosition == 0) {
                        isLoading = true;
                        if (refreshListener != null) refreshListener.onRefresh();
                    }else if (lastVisibleItemPosition == totalItemCount - 1) {
                        isLoading = true;
                        if (loadMoreListener != null) loadMoreListener.onLoadMore();
                    }
                }
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (getLayoutManager() instanceof LinearLayoutManager) {
                lastVisibleItemPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
                firstVisibleItemPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
            } else {
                firstVisibleItemPosition = -1;
                lastVisibleItemPosition = -1;
            }
        }
    }

    public void loadingCompleted() {
        getScrollListener().isLoading = false;
    }

    private InnerScrollListener mScrollListener;

    InnerScrollListener getScrollListener() {
        if (mScrollListener == null) {
            mScrollListener = new InnerScrollListener();
        }
        return mScrollListener;
    }


    private void initView(Context context, AttributeSet attrs, int defStyle) {
        addOnScrollListener(getScrollListener());
    }


}
