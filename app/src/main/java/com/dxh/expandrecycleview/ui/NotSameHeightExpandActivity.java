package com.dxh.expandrecycleview.ui;

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
                tvTitle.setText(titleTreeNode.getData().titleContent);
                tvTitle.setTextColor(titleTreeNode.getData().textColor);
                tvTitle.setBackgroundColor(titleTreeNode.getData().background);
                int tvTitleHeight = 120 - 20 * titleTreeNode.getLevel();
                tvTitle.getLayoutParams().height = tvTitleHeight;
                tvTitle.requestLayout();
            }

            @Override
            protected void changeFixViewData(View view, int position, TreeNode<Title> titleTreeNode) {
                super.changeFixViewData(view, position, titleTreeNode);
                if (view == null) {
                    return;
                }
                tvTitle = (TextView) view.findViewById(R.id.tv_title);
                tvTitle.setText(titleTreeNode.getData().titleContent);
                tvTitle.setTextColor(titleTreeNode.getData().textColor);
                tvTitle.setBackgroundColor(titleTreeNode.getData().background);
                int tvTitleHeight = 120 - 20 * titleTreeNode.getLevel();
                tvTitle.getLayoutParams().height = tvTitleHeight;
                tvTitle.requestLayout();
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
            }

            @Override
            public void onItemExpandListner(View v, int position, TreeNode<Title> treeNode, Integer[] ids, boolean isExpand) {
                String idsStr = "";
                for (int i = 0; i < ids.length; i++) {
                    idsStr += ids[i];
                }
                Log.e(TAG, "onItemExpandListner: position=" + position + " ids=" + idsStr + " isExpand=" + isExpand);
                if (isExpand) {
                    treeNodeExpandRecycleViewAdapter.collapseGroup(position);
                } else {
                    treeNodeExpandRecycleViewAdapter.expandGroup(position);
                }
            }
        });
    }

    private void initView() {
        expandRecycleView = (ExpandRecycleView) findViewById(R.id.expandRecycleView);
        recyclerView = expandRecycleView.getmRecyclerView();
    }
}