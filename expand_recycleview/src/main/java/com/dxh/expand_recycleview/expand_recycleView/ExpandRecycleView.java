package com.dxh.expand_recycleview.expand_recycleView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (((ExpandRecycleViewAdapter) mRecyclerView.getAdapter()).isOpenStickyTop()) {
                    scrollChange();
                }
            }
        });
//        if (mRecyclerView.getItemDecorationCount() == 0) {
//            mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//                @Override
//                public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//                    super.onDraw(c, parent, state);
//                    if (((ExpandRecycleViewAdapter) mRecyclerView.getAdapter()).isOpenStickyTop()) {
//                        scrollChange();
//                    }
//                }
//            });
//        }
    }

    private Map<Integer, View> fixViewHashMap = new HashMap<>();

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
            View view = getView(adapter, expandTreeNode);
            int y = 0;
            if (nextParentOrBrotherTreeNode != null) {
                //吸顶
                if (expandTreeNodeMarginTop - scrollY <= defalutFixTop &&
                        nextParentOrBrotherTreeNodeMarginTop - scrollY >= expandTreeNodeItemHeight + defalutFixTop) {
                    if (expandTreeNode.getLevel() != level) {
                        if (expandTreeNode.getLevel() > level) {
                            break;
                        }
                        continue;
                    } else {
                        level++;
                    }
                    view.setTag(expandTreeNode.getItemPosition());
                    y = defalutFixTop;
                    view.setY(y);
                    view.setVisibility(VISIBLE);
                    adapter.changeFixViewData(view, expandTreeNode.getItemPosition(), expandTreeNode);
//                    Log.e(TAG, "scrollChange: 吸顶" + expandTreeNode.getItemPosition());
                } else if (nextParentOrBrotherTreeNodeMarginTop - scrollY < expandTreeNodeItemHeight + defalutFixTop &&
                        nextParentOrBrotherTreeNodeMarginTop - scrollY > defalutFixTop) {//吸附
                    if (expandTreeNode.getLevel() != level) {
                        if (expandTreeNode.getLevel() > level) {
                            break;
                        }
                        continue;
                    } else {
                        level++;
                    }
                    view.setTag(expandTreeNode.getItemPosition());
                    y = nextParentOrBrotherTreeNodeMarginTop - scrollY - expandTreeNodeItemHeight;
                    view.setY(y);
                    view.setVisibility(VISIBLE);
                    adapter.changeFixViewData(view, expandTreeNode.getItemPosition(), expandTreeNode);
//                    Log.e(TAG, "scrollChange: 吸附" + expandTreeNode.getItemPosition());
                } else {//消失
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
                    if (expandTreeNode.getLevel() != level) {
                        if (expandTreeNode.getLevel() > level) {
                            break;
                        }
                        continue;
                    } else {
                        level++;
                    }
                    view.setTag(expandTreeNode.getItemPosition());
                    y = defalutFixTop;
                    view.setY(y);
                    view.setVisibility(VISIBLE);
                    adapter.changeFixViewData(view, expandTreeNode.getItemPosition(), expandTreeNode);
//                    Log.e(TAG, "scrollChange: 吸顶-" + expandTreeNode.getItemPosition());
                } else {//消失
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

    private View getView(ExpandRecycleViewAdapter adapter, TreeNode treeNode) {
        View view = fixViewHashMap.get(treeNode.getLevel());
        if (view == null && treeNode.isExpand()) {//addView
            FrameLayout fixHeadContainerFrameLayout = getFixHeadContainerFrameLayout();
            int layoutId = adapter.getLayoutId(adapter.getItemViewType(treeNode.getItemPosition()));
            view = LayoutInflater.from(getContext()).inflate(layoutId, null);
            fixHeadContainerFrameLayout.addView(view, 0);
            fixViewHashMap.put(treeNode.getLevel(), view);
        }
        view.getLayoutParams().height = treeNode.getItemHeight();
        view.requestLayout();
        return view;
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
