package com.dxh.expand_recycleview.expand_recycleView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XHD
 * Date 2023/02/09
 * Description:
 */
public class TreeNode<T> {
    private int pId;//父节点
    private int id;//当前节点
    private TreeNode<T> parent;//父亲
    private List<TreeNode<T>> childrenList = new ArrayList<>();//孩子
    private boolean isLeaf;//是否叶子节点
    private int level;//等级 0-n （0可能是根/空节点）
    private boolean isExpand;//是否展开
    private boolean isChecked;//是否选中
    private T data;//当前数据
    private TreeNode<T> lastBrotherTreeNode;
    private TreeNode<T> nextBrotherTreeNode;
    private int itemPosition;
    private int itemHeight;
    private int marginTop;//距离第一个条目顶部的距离

    public TreeNode(int pId, int id, T data) {
        this.pId = pId;
        this.id = id;
        this.data = data;
    }

    public TreeNode(int pId, int id, T data, TreeNode<T> parent) {
        this.pId = pId;
        this.id = id;
        this.data = data;
        this.parent = parent;
    }


    public int getpId() {
        return pId;
    }

    public int getId() {
        return id;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public List<TreeNode<T>> getChildrenList() {
        return childrenList;
    }

    public boolean isLeaf() {
        return childrenList.size() == 0 ? true : false;
    }

    public int getLevel() {
        if (parent == null) {
            return 0;
        }
        return parent.level + 1;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public boolean isParentExpand() {
        if (parent == null) {
            return false;
        }
        return parent.isExpand;
    }

    public boolean isRoot() {
        return parent == null ? true : false;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public void setChildrenList(List<TreeNode<T>> childrenList) {
        this.childrenList = childrenList;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    //1.打开当前2.关闭（所有孩子也关闭）
    public void setExpandCurrentOrCloseAllChild(boolean expand) {
        isExpand = expand;
        if (!isExpand) {
            for (TreeNode<T> tTreeNode : childrenList) {
                tTreeNode.setExpandCurrentOrCloseAllChild(false);
            }
        }
    }

    //关闭所有孩子，但不包括不受约束的节点
    public void closeAllChildWithoutFree() {
        List<Integer> freeLevelList = TreeNodeLevelManager.getInstance().getFreeLevelList();
        boolean isFree = false;
        for (int i = 0; i < freeLevelList.size(); i++) {
            Integer level = freeLevelList.get(i);
            if (this.level == level) {
                isFree = true;
                break;
            }
        }
        if (!isFree) {
            isExpand = false;
        }
        for (TreeNode<T> tTreeNode : childrenList) {
            tTreeNode.closeAllChildWithoutFree();
        }
    }

    //设置唯一展开，其他都关闭
    public void setOneExpandAndCloseOther(boolean expand) {
        if (expand) {//展开
            getRoot().closeAllChildWithoutFree();//关闭所有非根节点的节点
            expandCurrentTreeNode();
        } else {//收起
            setExpandCurrentOrCloseAllChild(expand);
        }
    }

    //展开当前节点
    private void expandCurrentTreeNode() {
        isExpand = true;
        if (getParent() != null) {
            getParent().expandCurrentTreeNode();
        }
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    //只选中一个
    public void setOneCheckedAndNotCheckedOther(boolean checked) {
        getRoot().setAllChildChecked(false);
        isChecked = checked;
    }

    private void setAllChildChecked(boolean checked) {
        isChecked = checked;
        for (TreeNode<T> tTreeNode : childrenList) {
            tTreeNode.setAllChildChecked(checked);
        }
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public TreeNode<T> getNextBrotherTreeNode() {
        int position = id + 1;
        if (parent != null && parent.childrenList.size() > 0 && parent.childrenList.size() > position) {
            return parent.childrenList.get(position);
        }
        return null;
    }

    public TreeNode<T> getLastBrotherTreeNode() {
        int position = id - 1;
        if (position < 0) {
            return null;
        }
        if (parent != null && parent.childrenList.size() > 0 && parent.childrenList.size() > position) {
            return parent.childrenList.get(position);
        }
        return null;
    }

    public TreeNode<T> getRoot() {
        if (getParent() == null) {
            return this;
        } else {
            return getParent().getRoot();
        }
    }

    protected List<Integer> getids(List<Integer> list) {
        if (parent != null) {
            list.add(0, id);
            return parent.getids(list);
        } else {
            return list;
        }
    }

    //只能rootNode 调用此方法 获取展开list
    protected List<TreeNode<T>> getChildExpandTreeNodeList(List<TreeNode<T>> treeNodeList) {
        if (isParentExpand()) {
            level = parent.level + 1;
            setItemPosition(treeNodeList.size());
            int height = TreeNodeLevelManager.getInstance().getHeight(level);
            setItemHeight(height);
            if (treeNodeList.size() > 0) {
                setMarginTop(treeNodeList.get(treeNodeList.size() - 1).marginTop + treeNodeList.get(treeNodeList.size() - 1).getItemHeight());
            }
            treeNodeList.add(this);
        }
        for (TreeNode<T> tTreeNode : childrenList) {
            tTreeNode.getChildExpandTreeNodeList(treeNodeList);
        }
        return treeNodeList;
    }

    public int getItemPosition() {
        return itemPosition;
    }

    public void setItemPosition(int itemPosition) {
        this.itemPosition = itemPosition;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public TreeNode getNextParentOrBrotherTreeNode() {
        TreeNode<T> nextBrotherTreeNode = getNextBrotherTreeNode();
        if (nextBrotherTreeNode == null) {
            if (getParent() != null) {
                return getParent().getNextParentOrBrotherTreeNode();
            }
        } else {
            return nextBrotherTreeNode;
        }
        return null;
    }
}
