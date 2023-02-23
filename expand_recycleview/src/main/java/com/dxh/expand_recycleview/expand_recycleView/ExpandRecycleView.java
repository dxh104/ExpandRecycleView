package com.dxh.expand_recycleview.expand_recycleView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author XHD
 * Date 2023/01/04
 * Description:
 */
public class ExpandRecycleView extends FrameLayout {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
    private FrameLayout fixHeadContainerFrameLayout;
    private FrameLayout onMeasureFrameLayout;
    private String TAG = getClass().getSimpleName();


    public ExpandRecycleView(@NonNull Context context) {
        super(context);
    }

    public ExpandRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (mRecyclerView == null) {
            mRecyclerView = new RecyclerView(getContext());
            mRecyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            addView(mRecyclerView);
        }
        fixHeadContainerFrameLayout = new FrameLayout(getContext());
        onMeasureFrameLayout = new FrameLayout(getContext());
        addView(fixHeadContainerFrameLayout);
        addView(onMeasureFrameLayout);
        fixHeadContainerFrameLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        onMeasureFrameLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        onMeasureFrameLayout.setVisibility(INVISIBLE);
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (((ExpandRecycleViewAdapter) mRecyclerView.getAdapter()).isOpenStickyTop()) {
//                    scrollChange();
//                }
//            }
//        });
        if (mRecyclerView.getItemDecorationCount() == 0) {//防止没有启动自动置顶，只刷新适配器
            mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    super.onDraw(c, parent, state);
                    if (((ExpandRecycleViewAdapter) mRecyclerView.getAdapter()).isOpenStickyTop()) {
                        scrollChange();
                    }
                }
            });
        }
    }

    private Map<Integer, HashMap<Integer, View>> fixViewHashMap = new TreeMap<>();

    private void scrollChange() {
        ExpandRecycleViewAdapter adapter = (ExpandRecycleViewAdapter) mRecyclerView.getAdapter();
        List<TreeNode> mDatas = adapter.mDatas;
        List<TreeNode> mExpandDatas = adapter.mExpandDatas;
        int scrollY = adapter.getScrollY();
        int defalutFixTop = 0;
        int level = 1;
        for (int i = 0; i < mExpandDatas.size(); i++) {
            defalutFixTop = 0;
            TreeNode expandTreeNode = mExpandDatas.get(i);
            TreeNode nextParentOrBrotherTreeNode = expandTreeNode.getNextParentOrBrotherTreeNode();
            TreeNode tempExpandTreeNode = expandTreeNode;
            while (tempExpandTreeNode.getParent() != null) {
                TreeNode parent = tempExpandTreeNode.getParent();
                defalutFixTop += parent.getItemHeight();
                tempExpandTreeNode = parent;
            }
            int expandTreeNodeMarginTop = expandTreeNode.getMarginTop();
            int expandTreeNodeItemHeight = expandTreeNode.getItemHeight();
            int nextParentOrBrotherTreeNodeMarginTop = 0;
            int nextParentOrBrotherTreeNodeItemHeight = 0;
            if (nextParentOrBrotherTreeNode != null) {
                nextParentOrBrotherTreeNodeMarginTop = nextParentOrBrotherTreeNode.getMarginTop();
                nextParentOrBrotherTreeNodeItemHeight = nextParentOrBrotherTreeNode.getItemHeight();
            }
            View view = null;
            int y = 0;
            if (nextParentOrBrotherTreeNode != null) {
                //吸顶
                if (expandTreeNodeMarginTop - scrollY <= defalutFixTop &&
                        nextParentOrBrotherTreeNodeMarginTop - scrollY >= expandTreeNodeItemHeight + defalutFixTop) {
//                    if (expandTreeNode.getLevel() != level) {
//                        if (expandTreeNode.getLevel() < level) {
//                            break;
//                        }
//                        continue;
//                    } else {
//                        level++;
//                    }
                    view = getView(adapter, expandTreeNode, true);
                    view.setTag(expandTreeNode.getItemPosition());
                    y = defalutFixTop;
                    view.setY(y);
                    view.setVisibility(VISIBLE);
                    adapter.changeFixViewData(view, expandTreeNode.getItemPosition(), expandTreeNode);
//                    Log.e(TAG, "scrollChange: 吸顶" + expandTreeNode.getItemPosition());
                } else if (nextParentOrBrotherTreeNodeMarginTop - scrollY < expandTreeNodeItemHeight + defalutFixTop &&
                        nextParentOrBrotherTreeNodeMarginTop - scrollY > defalutFixTop) {//吸附
//                    if (expandTreeNode.getLevel() != level) {
//                        if (expandTreeNode.getLevel() < level) {
//                            break;
//                        }
//                        continue;
//                    } else {
//                        level++;
//                    }
                    view = getView(adapter, expandTreeNode, true);
                    view.setTag(expandTreeNode.getItemPosition());
                    y = nextParentOrBrotherTreeNodeMarginTop - scrollY - expandTreeNodeItemHeight;
                    view.setY(y);
                    view.setVisibility(VISIBLE);
                    adapter.changeFixViewData(view, expandTreeNode.getItemPosition(), expandTreeNode);
//                    Log.e(TAG, "scrollChange: 吸附" + expandTreeNode.getItemPosition());
                } else {//消失
                    view = getView(adapter, expandTreeNode, false);
                    if (view.getTag() != null) {
                        int itemPosition = (int) view.getTag();
                        if (itemPosition == expandTreeNode.getItemPosition()) {
                            view.setVisibility(INVISIBLE);
//                            Log.e(TAG, "scrollChange: 消失" + expandTreeNode.getItemPosition());
                        }
                    }
                }
            } else {
                //吸顶
                if (expandTreeNodeMarginTop - scrollY <= defalutFixTop) {
//                    if (expandTreeNode.getLevel() != level) {
//                        if (expandTreeNode.getLevel() < level) {
//                            break;
//                        }
//                        continue;
//                    } else {
//                        level++;
//                    }
                    view = getView(adapter, expandTreeNode, true);
                    view.setTag(expandTreeNode.getItemPosition());
                    y = defalutFixTop;
                    view.setY(y);
                    view.setVisibility(VISIBLE);
                    adapter.changeFixViewData(view, expandTreeNode.getItemPosition(), expandTreeNode);
//                    Log.e(TAG, "scrollChange: 吸顶-" + expandTreeNode.getItemPosition());
                } else {//消失
                    view = getView(adapter, expandTreeNode, false);
                    if (view.getTag() != null) {
                        int itemPosition = (int) view.getTag();
                        if (itemPosition == expandTreeNode.getItemPosition()) {
                            view.setVisibility(INVISIBLE);
//                            Log.e(TAG, "scrollChange: 消失-" + expandTreeNode.getItemPosition());
                        }
                    }
                }
            }

        }

    }

    private View getView(ExpandRecycleViewAdapter adapter, TreeNode treeNode, boolean isUpdateHeight) {
        HashMap<Integer, View> fixHeightViewMap = fixViewHashMap.get(treeNode.getLevel());
        if (fixHeightViewMap == null) {
            fixHeightViewMap = new HashMap<>();
            fixViewHashMap.put(treeNode.getLevel(), fixHeightViewMap);
        }
        View view = fixHeightViewMap.get(treeNode.getItemHeight());
        if (view == null && treeNode.isExpand()) {//addView
            FrameLayout fixHeadContainerFrameLayout = getFixHeadContainerFrameLayout();
            fixHeadContainerFrameLayout.removeAllViews();
            int layoutId = adapter.getLayoutId(adapter.getItemViewType(treeNode.getItemPosition()));
            view = LayoutInflater.from(getContext()).inflate(layoutId, null);
            fixHeightViewMap.put(treeNode.getItemHeight(), view);
            Iterator<Map.Entry<Integer, HashMap<Integer, View>>> iterator = fixViewHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, HashMap<Integer, View>> next = iterator.next();
                HashMap<Integer, View> viewMap = next.getValue();
                Iterator<Map.Entry<Integer, View>> viewMapIterator = viewMap.entrySet().iterator();
                while (viewMapIterator.hasNext()) {
                    Map.Entry<Integer, View> next1 = viewMapIterator.next();
                    fixHeadContainerFrameLayout.addView(next1.getValue(), 0);
                }
            }
            adapter.createFixView(view, treeNode.getItemPosition(), treeNode);
            view.setVisibility(INVISIBLE);
        }
        if (isUpdateHeight) {
            view.getLayoutParams().height = treeNode.getItemHeight();
            view.requestLayout();
        }
        return view;
    }

    public Map<Integer, HashMap<Integer, View>> getFixViewHashMap() {
        return fixViewHashMap;
    }

    public RecyclerView getmRecyclerView() {
        return mRecyclerView;
    }

    public LinearLayoutManager getLinearLayoutManager() {
        return linearLayoutManager;
    }

    public FrameLayout getFixHeadContainerFrameLayout() {
        return fixHeadContainerFrameLayout;
    }

    public FrameLayout getOnMeasureFrameLayout() {
        return onMeasureFrameLayout;
    }
}
