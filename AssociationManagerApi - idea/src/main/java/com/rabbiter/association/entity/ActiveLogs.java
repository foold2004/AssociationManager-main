package com.rabbiter.association.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName(value = "active_logs")
public class ActiveLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private String id;

    @TableField(value = "create_time")
    private String createTime;

    @TableField(value = "active_id")
    private String activeId;

    @TableField(value = "user_id")
    private String userId;

    @TableField(value = "status")
    private Integer status;

    @TableField(value = "review_time")
    private String reviewTime;

    @TableField(value = "review_remark")
    private String reviewRemark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getActiveId() {
        return activeId;
    }

    public void setActiveId(String activeId) {
        this.activeId = activeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(String reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getReviewRemark() {
        return reviewRemark;
    }

    public void setReviewRemark(String reviewRemark) {
        this.reviewRemark = reviewRemark;
    }

    @Override
    public String toString() {
        return "ActiveLogs [id=" + id
                + ", createTime=" + createTime
                + ", activeId=" + activeId
                + ", userId=" + userId
                + ", status=" + status
                + ", reviewTime=" + reviewTime
                + ", reviewRemark=" + reviewRemark
                + "]";
    }
}
