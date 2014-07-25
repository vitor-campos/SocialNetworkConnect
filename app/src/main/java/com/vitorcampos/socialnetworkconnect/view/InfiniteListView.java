package com.vitorcampos.socialnetworkconnect.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.vitorcampos.socialnetworkconnect.adapter.InfiniteLoadAdapter;

import java.util.List;

/**
 * Created by vitorcampos on 24/07/14.
 */
public class InfiniteListView extends ListView implements AbsListView.OnScrollListener {
    private View footer;
    private boolean isLoading;
    private InfiniteLoadListener listener;
    private InfiniteLoadAdapter adapter;

    public InfiniteListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setOnScrollListener(this);
    }

    public InfiniteListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnScrollListener(this);
    }

    public InfiniteListView(Context context) {
        super(context);
        this.setOnScrollListener(this);
    }

    public void setListener(InfiniteLoadListener listener) {
        this.listener = listener;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        if (getAdapter() == null) {
            return;
        }
        if (getAdapter().getCount() == 0) {
            return;
        }
        int actualItem = visibleItemCount + firstVisibleItem;
        if (actualItem >= totalItemCount && !isLoading) {
            this.addFooterView(footer);
            isLoading = true;
            listener.loadData();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}

    public void setLoadingView(int resId) {
        LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = inflater.inflate(resId, null);
        this.addFooterView(footer);

    }


    public void setAdapter(InfiniteLoadAdapter adapter) {
        super.setAdapter(adapter);
        this.adapter = adapter;
        this.removeFooterView(footer);
    }


    public void addNewData(List<FriendListItem> data) {
        this.removeFooterView(footer);

        adapter.addAll(data);
        adapter.notifyDataSetChanged();
        isLoading = false;
    }


    public InfiniteLoadListener setListener() {
        return listener;
    }


    public static interface InfiniteLoadListener {
        public void loadData() ;
    }
}
