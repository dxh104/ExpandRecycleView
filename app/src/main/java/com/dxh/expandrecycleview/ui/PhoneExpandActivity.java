package com.dxh.expandrecycleview.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
 * 手机联系人吸顶效果
 */
public class PhoneExpandActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName() + "---------->";
    private ExpandRecycleView expandRecycleView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_expand);
        initView();
        TreeNodeLevelManager.getInstance().clearFreeLevel();
        TreeNodeLevelManager.getInstance().setFreeLevel(1);//不会强制关闭,expandOnlyOneGroup
        TreeNodeLevelManager.getInstance().clearLevelHightCache();
        TreeNodeLevelManager.getInstance().putHeight(1, 50);
        TreeNodeLevelManager.getInstance().putHeight(2, 50);
        List<TreeNode<Title>> treeNodeList = DataHelper.testgetPhoneTreeNodeList();
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
            }

            @Override
            protected void changeFixViewData(View view, int position, TreeNode<Title> titleTreeNode,boolean isGetItemHeight) {
                super.changeFixViewData(view, position, titleTreeNode,isGetItemHeight);
                if (view == null) {
                    return;
                }
                tvTitle = (TextView) view.findViewById(R.id.tv_title);
                tvTitle.setText(titleTreeNode.getData().titleContent);
                tvTitle.setTextColor(titleTreeNode.getData().textColor);
                tvTitle.setBackgroundColor(titleTreeNode.getData().background);
            }
        };
        treeNodeExpandRecycleViewAdapter.setmExpandRecycleView(expandRecycleView);
        treeNodeExpandRecycleViewAdapter.setMeasureDataHeightMarginTop(false);//固定高度不用开启测量
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
                Toast.makeText(PhoneExpandActivity.this, treeNode.getData().titleContent, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onItemCheckListner: position=" + position + " ids=" + idsStr + " isChecked=" + isChecked);
            }

            @Override
            public void onItemExpandListner(View v, int position, TreeNode<Title> treeNode, Integer[] ids, boolean isExpand) {
                String idsStr = "";
                for (int i = 0; i < ids.length; i++) {
                    idsStr += ids[i];
                }
                Log.e(TAG, "onItemExpandListner: position=" + position + " ids=" + idsStr + " isExpand=" + isExpand);
            }
        });
    }

    private void initView() {
        expandRecycleView = (ExpandRecycleView) findViewById(R.id.expandRecycleView);
        recyclerView = expandRecycleView.getmRecyclerView();
    }
}