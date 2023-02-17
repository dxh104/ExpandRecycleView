package com.dxh.expand_recycleview.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by XHD on 2020/08/12
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> views;
    private final Context context;

    public BaseViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        this.views = new SparseArray<View>();
    }

    public <T extends View> T getView(int id) {
        View view = views.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            views.put(id, view);
        }
        return (T) view;
    }
}
