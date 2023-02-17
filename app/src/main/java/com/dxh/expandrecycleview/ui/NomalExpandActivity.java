package com.dxh.expandrecycleview.ui;

import android.graphics.Color;
import android.os.Bundle;
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
import com.dxh.expandrecycleview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 普通recycleView
 */
public class NomalExpandActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName() + "---------->";
    private ExpandRecycleView expandRecycleView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nomal_expand);
        initView();
        List<TreeNode<Title>> treeNodeList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            treeNodeList.add(new TreeNode<Title>(0, 0, new Title("条目" + i, Color.parseColor("#ffffff"), Color.parseColor("#ff0000"))));
        }
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
        };
        recyclerView.setAdapter(treeNodeExpandRecycleViewAdapter);
        treeNodeExpandRecycleViewAdapter.setOnItemClickListner(new ExpandRecycleViewAdapter.OnItemClickListner() {
            @Override
            public void onItemClickListner(View v, int position) {
                Toast.makeText(NomalExpandActivity.this, "" + treeNodeExpandRecycleViewAdapter.mDatas.get(position).getData().titleContent, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        expandRecycleView = (ExpandRecycleView) findViewById(R.id.expandRecycleView);
        recyclerView = expandRecycleView.getmRecyclerView();
    }
}