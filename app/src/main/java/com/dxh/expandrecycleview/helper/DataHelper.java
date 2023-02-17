package com.dxh.expandrecycleview.helper;

import static com.dxh.expand_recycleview.expand_recycleView.TreeNodeHelper.constructRootTreeNode;
import static com.dxh.expand_recycleview.expand_recycleView.TreeNodeHelper.getRootExpandTreeNodeList;

import android.graphics.Color;

import com.dxh.expand_recycleview.entity.Title;
import com.dxh.expand_recycleview.expand_recycleView.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XHD
 * Date 2023/02/17
 * Description:
 */
public class DataHelper {

    public static TreeNode<Title> testConstructRootTreeNode() {
        List<TreeNode<Title>> treeNodeList = new ArrayList<>();
        Title title;
        List<TreeNode<Title>> childrenList;
        for (int i = 0; i < 100; i++) {//一级标题
            title = new Title("一级标题" + i, Color.parseColor("#ffffff"), Color.parseColor("#ff0000"));
            treeNodeList.add(new TreeNode<Title>(0, i, title, null));
            for (int j = 0; j < 10; j++) {//二级标题
                childrenList = treeNodeList.get(i).getChildrenList();
                title = new Title("二级标题" + j, Color.parseColor("#ffffff"), Color.parseColor("#00ff00"));
                childrenList.add(new TreeNode<Title>(i, j, title, treeNodeList.get(i)));
                for (int k = 0; k < 10; k++) {//三级标题
                    childrenList = treeNodeList.get(i).getChildrenList().get(j).getChildrenList();
                    title = new Title("三级标题" + k, Color.parseColor("#ffffff"), Color.parseColor("#0000ff"));
                    childrenList.add(new TreeNode<Title>(j, k, title, treeNodeList.get(i).getChildrenList().get(j)));
                    for (int l = 0; l < 10; l++) {//四级标题
                        childrenList = treeNodeList.get(i).getChildrenList().get(j).getChildrenList().get(k).getChildrenList();
                        title = new Title("四级标题" + l, Color.parseColor("#ffffff"), Color.parseColor("#00ffff"));
                        childrenList.add(new TreeNode<Title>(k, l, title, treeNodeList.get(i).getChildrenList().get(j).getChildrenList().get(k)));
                    }
                }
            }
        }
        TreeNode<Title> titleTreeNode = constructRootTreeNode(treeNodeList);
        return titleTreeNode;
    }

    public static List<TreeNode<Title>> testgetRootExpandTreeNodeList() {
        return getRootExpandTreeNodeList(testConstructRootTreeNode());
    }


    public static TreeNode<Title> testgetPhoneTreeNode() {
        List<TreeNode<Title>> treeNodeList = new ArrayList<>();
        Title title;
        List<TreeNode<Title>> childrenList;
        for (int i = 0; i < 100; i++) {//一级标题
            title = new Title("组别" + i, Color.parseColor("#ffffff"), Color.parseColor("#ff0000"));
            treeNodeList.add(new TreeNode<Title>(0, i, title, null));
            treeNodeList.get(i).setExpand(true);
            for (int j = 0; j < 10; j++) {//二级标题
                childrenList = treeNodeList.get(i).getChildrenList();
                title = new Title("联系人" + j, Color.parseColor("#ffffff"), Color.parseColor("#00ff00"));
                childrenList.add(new TreeNode<Title>(i, j, title, treeNodeList.get(i)));
            }
        }
        TreeNode<Title> titleTreeNode = constructRootTreeNode(treeNodeList);
        return titleTreeNode;
    }

    public static List<TreeNode<Title>> testgetPhoneTreeNodeList() {
        return getRootExpandTreeNodeList(testgetPhoneTreeNode());
    }
}
