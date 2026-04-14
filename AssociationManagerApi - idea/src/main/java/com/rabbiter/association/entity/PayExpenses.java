package com.rabbiter.association.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 数据实体类
 * 消费明细
 */
@TableName(value = "pay_expenses")
public class PayExpenses implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 消费时间
     */
    @TableField(value = "create_time")
    private String createTime;

    /**
     * 消费金额
     */
    @TableField(value = "total")
    private Double total;

    /**
     * 消费标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 消费说明
     */
    @TableField(value = "detail")
    private String detail;

    /**
     * 所属社团
     */
    @TableField(value = "team_id")
    private String teamId;

    /**
     * 记录人
     */
    @TableField(value = "handler_id")
    private String handlerId;

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

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId;
    }

    @Override
    public String toString() {
        return "PayExpenses [id=" + id
                + ", createTime=" + createTime
                + ", total=" + total
                + ", title=" + title
                + ", detail=" + detail
                + ", teamId=" + teamId
                + ", handlerId=" + handlerId
                + "]";
    }
}
