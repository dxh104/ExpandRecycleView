package com.dxh.expand_recycleview.expand_recycleView;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XHD
 * Date 2023/02/10
 * Description:
 */
public class TreeNodeLevelManager {
    private static TreeNodeLevelManager instance = new TreeNodeLevelManager();
    private List<Integer> freeLevelList = new ArrayList<>();
    private Map<Integer, Integer> levelHightHashMap = new HashMap<>();

    public static TreeNodeLevelManager getInstance() {
        return instance;
    }

    //设置自由等级，不被getChildExpandTreeNodeList方法约束（设置唯一展开会影响到）
    public void setFreeLevel(int... level) {
        freeLevelList.clear();
        for (int i = 0; i < level.length; i++) {
            freeLevelList.add(level[i]);
        }
    }

    public void clearFreeLevel() {
        freeLevelList.clear();
    }

    public List<Integer> getFreeLevelList() {
        return freeLevelList;
    }

    //如果多级菜单，每个菜单高度确定切固定就可以传入
    public void putHeight(int level, int height) {
        levelHightHashMap.put(level, height);
    }

    @SuppressLint("NewApi")
    public int getHeight(int level) {
        return levelHightHashMap.getOrDefault(level, 0);
    }

    public void clearLevelHightCache() {
        levelHightHashMap.clear();
    }
}
