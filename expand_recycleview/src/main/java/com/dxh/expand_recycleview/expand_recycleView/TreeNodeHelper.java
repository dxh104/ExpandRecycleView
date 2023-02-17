package com.dxh.expand_recycleview.expand_recycleView;

import android.graphics.Color;

import com.dxh.expand_recycleview.entity.Title;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XHD
 * Date 2023/02/09
 * Description:
 */
public class TreeNodeHelper {
    /**
     * 根据ids 获取TreeNode
     *
     * @param treeNode
     * @param ids
     * @param <T>
     * @return
     */
    public static <T> TreeNode<T> getTargetTreeNode(TreeNode<T> treeNode, int... ids) {
        treeNode = treeNode.getRoot();
        for (int i = 0; i < ids.length; i++) {
            int id = ids[i];
            if (treeNode.getChildrenList().size() > id) {
                treeNode = treeNode.getChildrenList().get(id);
            } else {
                treeNode = null;
                break;
            }
        }
        return treeNode;
    }

    //1.打开当前2.关闭（所有孩子也关闭）
    public static <T> void setExpandCurrentOrCloseAllChild(TreeNode<T> treeNode, boolean expand) {
        treeNode.setExpandCurrentOrCloseAllChild(expand);
    }

    //设置唯一展开，其他都关闭
    public static <T> void setOneExpandAndCloseOther(TreeNode<T> treeNode, boolean expand) {
        treeNode.setOneExpandAndCloseOther(expand);
    }

    //只选中一个/全部不选中
    public static <T> void setOneCheckedAndNotCheckedOther(TreeNode<T> treeNode, boolean checked) {
        treeNode.setOneCheckedAndNotCheckedOther(checked);
    }

    //获取ids
    public static <T> Integer[] getids(TreeNode<T> treeNode) {
        List<Integer> ids = treeNode.getids(new ArrayList<>());
        return ids.toArray(new Integer[ids.size()]);
    }

    /**
     * 所有子树合并成一个树
     *
     * @param treeNodeList
     * @param <T>          根节点（空）
     * @return
     */
    public static <T> TreeNode<T> constructRootTreeNode(List<TreeNode<T>> treeNodeList) {
        TreeNode<T> tTreeNode = new TreeNode<>(0, 0, null, null);
        tTreeNode.setExpand(true);
        List<TreeNode<T>> childrenList = tTreeNode.getChildrenList();
        for (int i = 0; i < treeNodeList.size(); i++) {
            TreeNode<T> treeNode = treeNodeList.get(i);
            treeNode.setpId(0);
            treeNode.setId(i);
            treeNode.setParent(tTreeNode);
            childrenList.add(treeNode);
        }
        return tTreeNode;
    }

    //获取root下所有展开的list
    public static <T> List<TreeNode<T>> getRootExpandTreeNodeList(TreeNode<T> tTreeNode) {
        TreeNode<T> root = tTreeNode.getRoot();
        root.setExpand(true);
        List<TreeNode<T>> childExpandTreeNodeList = root.getChildExpandTreeNodeList(new ArrayList<>());
        return childExpandTreeNodeList;
    }

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

}
