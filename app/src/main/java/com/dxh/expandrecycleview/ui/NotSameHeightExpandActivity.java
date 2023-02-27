package com.dxh.expandrecycleview.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.dxh.expand_recycleview.base.BaseViewHolder;
import com.dxh.expand_recycleview.entity.Title;
import com.dxh.expand_recycleview.expand_recycleView.ExpandRecycleView;
import com.dxh.expand_recycleview.expand_recycleView.ExpandRecycleViewAdapter;
import com.dxh.expand_recycleview.expand_recycleView.TreeNode;
import com.dxh.expand_recycleview.expand_recycleView.TreeNodeLevelManager;
import com.dxh.expandrecycleview.R;
import com.dxh.expandrecycleview.helper.DataHelper;

import java.util.List;

/**
 * 自动吸顶，高度不一致
 */
public class NotSameHeightExpandActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName() + "---------->";
    private ExpandRecycleView expandRecycleView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_same_height_expand);
        initView();
        TreeNodeLevelManager.getInstance().clearFreeLevel();
        TreeNodeLevelManager.getInstance().setFreeLevel(1);
        List<TreeNode<Title>> treeNodeList = DataHelper.testgetRootExpandTreeNodeList();
        ExpandRecycleViewAdapter<TreeNode<Title>> treeNodeExpandRecycleViewAdapter = new ExpandRecycleViewAdapter<TreeNode<Title>>(treeNodeList) {
            private TextView tvTitle;

            @Override
            protected int setItemViewType(List<TreeNode<Title>> mDatas, int position) {
                return 0;
            }

            @Override
            protected int getLayoutId(int viewType) {
                return R.layout.item_group;
            }

            @Override
            protected void initView(BaseViewHolder holder) {
                tvTitle = (TextView) holder.getView(R.id.tv_title);
            }

            @Override
            protected void setListener(BaseViewHolder holder, List<TreeNode<Title>> mDatas) {
            }

            @Override
            protected void bindData(BaseViewHolder holder, int position, TreeNode<Title> titleTreeNode) {
                if (position % 2 == 0) {
                    tvTitle.setText(titleTreeNode.getData().titleContent + titleTreeNode.getData().titleContent + titleTreeNode.getData().titleContent);
                    tvTitle.getLayoutParams().height = 200;
                } else {
                    tvTitle.setText(titleTreeNode.getData().titleContent);
                    tvTitle.getLayoutParams().height = 40;
                }
                tvTitle.requestLayout();
                tvTitle.setTextColor(titleTreeNode.getData().textColor);
                tvTitle.setBackgroundColor(titleTreeNode.getData().background);
//                int tvTitleHeight = 120 - 20 * titleTreeNode.getLevel();
//                tvTitle.getLayoutParams().height = tvTitleHeight;
//                tvTitle.requestLayout();
                if (titleTreeNode.isLeaf()) {
                    if (titleTreeNode.isChecked()) {
                        tvTitle.setTextColor(Color.parseColor("#ffff00"));
                    } else {
                        tvTitle.setTextColor(Color.parseColor("#ffffff"));
                    }
                }
            }

            @Override
            protected void createFixView(View view, int position, TreeNode<Title> titleTreeNode) {
                super.createFixView(view, position, titleTreeNode);
                if (view == null) {
                    return;
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer itemPostition = (Integer) v.getTag();
                        collapseGroup(itemPostition, true);
                    }
                });
            }

            @Override
            protected void changeFixViewData(View view, int position, TreeNode<Title> titleTreeNode,boolean isGetItemHeight) {
                super.changeFixViewData(view, position, titleTreeNode,isGetItemHeight);
                if (view == null) {
                    return;
                }
                tvTitle = (TextView) view.findViewById(R.id.tv_title);
                if (position % 2 == 0) {
                    tvTitle.setText(titleTreeNode.getData().titleContent + titleTreeNode.getData().titleContent + titleTreeNode.getData().titleContent);
                    tvTitle.getLayoutParams().height = 200;
                } else {
                    tvTitle.setText(titleTreeNode.getData().titleContent);
                    tvTitle.getLayoutParams().height = 40;
                }
                tvTitle.requestLayout();
                tvTitle.setTextColor(titleTreeNode.getData().textColor);
                tvTitle.setBackgroundColor(titleTreeNode.getData().background);
                ((View)tvTitle.getParent()).requestLayout();
//                int tvTitleHeight = 120 - 20 * titleTreeNode.getLevel();
//                tvTitle.getLayoutParams().height = tvTitleHeight;
//                tvTitle.requestLayout();
            }
        };
        treeNodeExpandRecycleViewAdapter.setmExpandRecycleView(expandRecycleView);
        treeNodeExpandRecycleViewAdapter.setMeasureDataHeightMarginTop(true);//开启测量
        treeNodeExpandRecycleViewAdapter.setOpenStickyTop(true);//打开吸顶功能（默认开启）
        treeNodeExpandRecycleViewAdapter.refreshExpandData();//更新可展开数据
        recyclerView.setAdapter(treeNodeExpandRecycleViewAdapter);
        treeNodeExpandRecycleViewAdapter.setOnItemTriggerListner(new ExpandRecycleViewAdapter.OnItemTriggerListner<TreeNode<Title>>() {

            @Override
            public void onItemCheckListner(View v, int position, TreeNode<Title> treeNode, Integer[] ids, boolean isChecked) {
                String idsStr = "";
                for (int i = 0; i < ids.length; i++) {
                    idsStr += ids[i];
                }
                Log.e(TAG, "onItemCheckListner: position=" + position + " ids=" + idsStr + " isChecked=" + isChecked);
                treeNode.setOneCheckedAndNotCheckedOther(!isChecked);//取消其他选中，设置当前选中状态
                treeNodeExpandRecycleViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemExpandListner(View v, int position, TreeNode<Title> treeNode, Integer[] ids, boolean isExpand) {
                String idsStr = "";
                for (int i = 0; i < ids.length; i++) {
                    idsStr += ids[i];
                }
                Log.e(TAG, "onItemExpandListner: position=" + position + " ids=" + idsStr + " isExpand=" + isExpand);
                if (isExpand) {
                    treeNodeExpandRecycleViewAdapter.collapseGroup(position, true);
                } else {
                    treeNodeExpandRecycleViewAdapter.expandOnlyOneGroup(position, true);
                }
            }
        });
    }

    private void initView() {
        expandRecycleView = (ExpandRecycleView) findViewById(R.id.expandRecycleView);
        recyclerView = expandRecycleView.getmRecyclerView();
    }
}