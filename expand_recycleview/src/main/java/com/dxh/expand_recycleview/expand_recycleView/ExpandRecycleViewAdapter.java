package com.dxh.expand_recycleview.expand_recycleView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.dxh.expand_recycleview.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author XHD
 * Date 2023/01/18
 * Description:如果需要实现吸顶功能，展开/收起一定要做防快点击处理，否则连续过快点可能会有问题（原因暂时未探究，最少要等notifyDataSetChanged刷新完的100ms后置顶结束）
 */
public abstract class ExpandRecycleViewAdapter<T extends TreeNode> extends RecyclerView.Adapter<BaseViewHolder> {
    public List<T> mDatas = new ArrayList<>();//数据集合
    protected Context mContext;//上下文
    protected OnItemClickListner onItemClickListner;//条目单击事件
    protected OnItemTriggerListner onItemTriggerListner;//条目触发事件
    protected ExpandRecycleView mExpandRecycleView;
    protected String TAG = getClass().getSimpleName();
    protected int currentExpandPosition = -1;
    private boolean isMeasureDataHeightMarginTop = true;//默认测量计算高度
    private boolean isOpenStickyTop = true;//默认吸顶
    protected List<T> mExpandDatas = new ArrayList<>();

    public ExpandRecycleViewAdapter(List<T> datas) {
        if (datas != null) {
            mDatas.addAll(datas);
        }
        setHasStableIds(true);//避免刷新时图片闪烁
    }

    public ExpandRecycleViewAdapter(List<T> datas, ExpandRecycleView expandRecycleView) {
        mExpandRecycleView = expandRecycleView;
        if (datas != null) {
            mDatas.addAll(datas);
        }
        setHasStableIds(true);//避免刷新时图片闪烁
    }

    @Override
    public int getItemViewType(int position) {
        int itemViewType = setItemViewType(mDatas, position);//------设置条目布局类型------
        if (itemViewType != 0) {
            return itemViewType;
        }
        return super.getItemViewType(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(getLayoutId(viewType), parent, false);//------设置条目布局------
        final BaseViewHolder baseViewHolder = new BaseViewHolder(mContext, view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListner != null) {
                    onItemClickListner.onItemClickListner(v, baseViewHolder.getLayoutPosition());
                }
                if (onItemTriggerListner != null) {
                    T t = mDatas.get(baseViewHolder.getLayoutPosition());
                    if (t.isLeaf()) {
                        onItemTriggerListner.onItemCheckListner(v, baseViewHolder.getLayoutPosition(), t, TreeNodeHelper.getids(t), t.isChecked());
                    } else {
                        onItemTriggerListner.onItemExpandListner(v, baseViewHolder.getLayoutPosition(), t, TreeNodeHelper.getids(t), t.isExpand());
                    }
                }
            }
        });
        initView(baseViewHolder);//------初始化控件------
        setListener(baseViewHolder, mDatas);//------设置监听器------
        return baseViewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        T t = mDatas.get(position);
        initView(holder);//------初始化控件------
        bindData(holder, position, t);//------绑定数据------
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 返回ItemView布局类型---子类实现可以在外部用个变量保存返回值，初始化不同布局控件和点击事件可以直接根据这个返回值判断
     *
     * @return
     */
    protected abstract int setItemViewType(List<T> mDatas, int position);

    /**
     * 初始化item布局，获取item
     *
     * @return
     */
    protected abstract int getLayoutId(int viewType);

    /**
     * 初始化控件
     *
     * @param holder
     */
    protected abstract void initView(BaseViewHolder holder);

    /**
     * 设置监听事件
     */
    protected abstract void setListener(BaseViewHolder holder, List<T> mDatas);

    /**
     * 绑定数据
     */
    protected abstract void bindData(BaseViewHolder holder, int position, T t);

    protected void createFixView(View view, int position, T t) {

    }

    /**
     * 如果需要实现吸顶功能，需要在子Adapter中实现这个方法绑定view
     * 如果需要在内部实现点击事件，可以用(int)view.getTag()方法获取position
     *
     * @param view
     * @param position
     * @param t
     */
    protected void changeFixViewData(View view, int position, T t) {

    }

    public void setOnItemClickListner(OnItemClickListner onItemClickListner) {
        this.onItemClickListner = onItemClickListner;
    }

    public void setOnItemTriggerListner(OnItemTriggerListner onItemTriggerListner) {
        this.onItemTriggerListner = onItemTriggerListner;
    }


    public interface OnItemClickListner {
        void onItemClickListner(View v, int position);
    }

    public interface OnItemTriggerListner<T> {
        //条目选中事件
        void onItemCheckListner(View v, int position, T treeNode, Integer[] ids, boolean isChecked);

        //条目展开事件
        void onItemExpandListner(View v, int position, T treeNode, Integer[] ids, boolean isExpand);
    }

    //折叠节点
    public void collapseGroup(int itemPosition) {
        currentExpandPosition = -1;
        T t = mDatas.get(itemPosition);
        mDatas.clear();
        TreeNodeHelper.setExpandCurrentOrCloseAllChild(t, false);
        List rootExpandTreeNodeList = TreeNodeHelper.getRootExpandTreeNodeList(t);
        mDatas.addAll(rootExpandTreeNodeList);
        notifyDataSetChanged();
        int level = t.getLevel();
        int defalutFixTop = 0;
        if (!isMeasureDataHeightMarginTop) {
            for (int i = 1; i < level; i++) {
                int height = TreeNodeLevelManager.getInstance().getHeight(i);
                defalutFixTop += height;
            }
        } else {
            measureDataHeightMarginTop();
            T tempT = t;
            while (tempT.getParent() != null) {
                TreeNode parent = tempT.getParent();
                defalutFixTop += parent.getItemHeight();
                tempT = (T) parent;
            }
        }
        if (mExpandRecycleView != null) {
            Map<Integer, View> fixViewHashMap = mExpandRecycleView.getFixViewHashMap();
            Iterator<Map.Entry<Integer, View>> iterator = fixViewHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, View> next = iterator.next();
                int fixLevel = next.getKey();
                View fixView = next.getValue();
                if (fixView != null && fixLevel >= level) {
                    fixView.setVisibility(View.INVISIBLE);
                }
            }
        }
        refreshExpandData();
        scrollToPositionStickyTop(t.getItemPosition(), defalutFixTop);
    }

    //展开节点 其他节点自动关闭
    public void expandOnlyOneGroup(int itemPosition) {
        currentExpandPosition = itemPosition;
        T t = mDatas.get(itemPosition);
        mDatas.clear();
        TreeNodeHelper.setOneExpandAndCloseOther(t, true);
        List rootExpandTreeNodeList = TreeNodeHelper.getRootExpandTreeNodeList(t);
        mDatas.addAll(rootExpandTreeNodeList);
        notifyDataSetChanged();
        int level = t.getLevel();
        int defalutFixTop = 0;
        if (!isMeasureDataHeightMarginTop) {
            for (int i = 1; i < level; i++) {
                int height = TreeNodeLevelManager.getInstance().getHeight(i);
                defalutFixTop += height;
            }
        } else {
            measureDataHeightMarginTop();
            for (int i = t.getItemPosition(); i >= 0; i--) {
                T findT = mDatas.get(i);
                int findLevel = findT.getLevel();
                int findTItemHeight = findT.getItemHeight();
                if (findLevel < level) {
                    if (findLevel == 1) {
                        defalutFixTop += findTItemHeight;
                        break;
                    }
                    defalutFixTop += findTItemHeight;
                    level--;
                }
            }
        }
        refreshExpandData();
        scrollToPositionStickyTop(t.getItemPosition(), defalutFixTop);
    }

    //展开节点
    public void expandGroup(int itemPosition) {
        currentExpandPosition = itemPosition;
        T t = mDatas.get(itemPosition);
        mDatas.clear();
        TreeNodeHelper.setExpandCurrentOrCloseAllChild(t, true);
        List rootExpandTreeNodeList = TreeNodeHelper.getRootExpandTreeNodeList(t);
        mDatas.addAll(rootExpandTreeNodeList);
        notifyDataSetChanged();
        int level = t.getLevel();
        int defalutFixTop = 0;
        if (!isMeasureDataHeightMarginTop) {
            for (int i = 1; i < level; i++) {
                int height = TreeNodeLevelManager.getInstance().getHeight(i);
                defalutFixTop += height;
            }
        } else {
            measureDataHeightMarginTop();
            for (int i = t.getItemPosition(); i >= 0; i--) {
                T findT = mDatas.get(i);
                int findLevel = findT.getLevel();
                int findTItemHeight = findT.getItemHeight();
                if (findLevel < level) {
                    if (findLevel == 1) {
                        defalutFixTop += findTItemHeight;
                        break;
                    }
                    defalutFixTop += findTItemHeight;
                    level--;
                }
            }
        }
        refreshExpandData();
        scrollToPositionStickyTop(t.getItemPosition(), defalutFixTop);
    }

    //测量条目高度和到第一个条目的距离，并保存到treeNode
    public void measureDataHeightMarginTop() {
        int marginTop = 0;
        for (int position = 0; position < mDatas.size(); position++) {
            T t = mDatas.get(position);
            t.setMarginTop(marginTop);
            int itemHeight = getItemHeight(position);
            t.setItemHeight(itemHeight);
            marginTop += itemHeight;
        }
    }

    //更新展开数据
    public void refreshExpandData() {
        mExpandDatas.clear();
        for (int i = 0; i < mDatas.size(); i++) {
            T treeNode = mDatas.get(i);
            if (treeNode.isExpand()) {
                mExpandDatas.add(treeNode);
            }
        }
    }

    private HashMap<Integer, View> measureViewMap = new HashMap<>();

    private int getItemHeight(int position) {
        FrameLayout onMeasureFrameLayout = mExpandRecycleView.getOnMeasureFrameLayout();
        T t = mDatas.get(position);
        int layoutId = getLayoutId(getItemViewType(position));
        View view = measureViewMap.get(layoutId);
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(layoutId, null);
            onMeasureFrameLayout.addView(view);
            view.setVisibility(View.INVISIBLE);
            measureViewMap.put(layoutId, view);
        }
        changeFixViewData(view, position, t);
        view.requestLayout();
        view.measure(View.MeasureSpec.makeMeasureSpec(mExpandRecycleView.getmRecyclerView().getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int measuredHeight = view.getMeasuredHeight();
        return measuredHeight;
    }

    public int getScrollY() {
        if (mExpandRecycleView != null) {
            int firstCompletelyVisibleItemPosition = mExpandRecycleView.getLinearLayoutManager().findFirstCompletelyVisibleItemPosition();
            View view = mExpandRecycleView.getLinearLayoutManager().findViewByPosition(firstCompletelyVisibleItemPosition);
            int top = view.getTop();
            int marginTop = mDatas.get(firstCompletelyVisibleItemPosition).getMarginTop();
            return marginTop - top;
        }
        return 0;
    }

    public void scrollToPositionStickyTop(int itemPosition, int offsetY) {
        if (mExpandRecycleView != null) {
            mExpandRecycleView.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    mExpandRecycleView.getmRecyclerView().smoothScrollToPosition(itemPosition);
                    mExpandRecycleView.getLinearLayoutManager().scrollToPositionWithOffset(itemPosition, offsetY);
                }
            }, 100);//防止空白
        }
    }

    public void setmExpandRecycleView(ExpandRecycleView mExpandRecycleView) {
        this.mExpandRecycleView = mExpandRecycleView;
    }

    //设置成false将会使用TreeNodeLevelManager 中设置的条目固定高度（需打开页面时自己主动设置），性能有提升
    public void setMeasureDataHeightMarginTop(boolean measureDataHeightMarginTop) {
        isMeasureDataHeightMarginTop = measureDataHeightMarginTop;
    }

    public boolean isOpenStickyTop() {
        return isOpenStickyTop;
    }

    //打开吸顶功能（默认开启）
    public void setOpenStickyTop(boolean openStickyTop) {
        isOpenStickyTop = openStickyTop;
        if (!openStickyTop) {//不吸顶不用开启isMeasureDataHeightMarginTop（测量高度影响性能）
            isMeasureDataHeightMarginTop = false;
        }
    }

    public int getCurrentExpandPosition() {
        return currentExpandPosition;
    }
}
