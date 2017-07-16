package com.adm.dictionary.entity;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by homiss on 2017/6/13.
 */

public class Setting {

    /**
     * 当前选中题库id
     */
    private Integer groupId;

    private String groupName;

    /**
     * 背题模式：0，顺序 1，随机
     */
    private Integer reciteModel;

    /**
     * 每日背题数
     */
    private Integer reciteNum;

    private Integer sumCount;

    private Integer hasReciteCount;

    public Setting() {
    }

    @Generated
    public Setting(Integer groupId, String groupName, Integer reciteModel, Integer reciteNum) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.reciteModel = reciteModel;
        this.reciteNum = reciteNum;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getReciteModel() {
        return reciteModel;
    }

    public void setReciteModel(Integer reciteModel) {
        this.reciteModel = reciteModel;
    }

    public Integer getReciteNum() {
        return reciteNum;
    }

    public void setReciteNum(Integer reciteNum) {
        this.reciteNum = reciteNum;
    }

    public Integer getSumCount() {
        return sumCount;
    }

    public void setSumCount(Integer sumCount) {
        this.sumCount = sumCount;
    }

    public Integer getHasReciteCount() {
        return hasReciteCount;
    }

    public void setHasReciteCount(Integer hasReciteCount) {
        this.hasReciteCount = hasReciteCount;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "groupId=" + groupId +
                ", groupName=" + groupName +
                ", reciteModel=" + reciteModel +
                ", reciteNum=" + reciteNum +
                '}';
    }
}
