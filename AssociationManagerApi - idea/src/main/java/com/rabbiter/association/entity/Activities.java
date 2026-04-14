package com.rabbiter.association.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName(value = "activities")
public class Activities implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private String id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "comm")
    private String comm;

    @TableField(value = "detail")
    private String detail;

    @TableField(value = "ask")
    private String ask;

    @TableField(value = "total")
    private Integer total;

    @TableField(value = "max_total")
    private Integer maxTotal;

    @TableField(value = "active_time")
    private String activeTime;

    @TableField(value = "enroll_end_time")
    private String enrollEndTime;

    @TableField(value = "team_id")
    private String teamId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComm() {
        return comm;
    }

    public void setComm(String comm) {
        this.comm = comm;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAsk() {
        return ask;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public String getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(String activeTime) {
        this.activeTime = activeTime;
    }

    public String getEnrollEndTime() {
        return enrollEndTime;
    }

    public void setEnrollEndTime(String enrollEndTime) {
        this.enrollEndTime = enrollEndTime;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    @Override
    public String toString() {
        return "Activities [id=" + id
                + ", name=" + name
                + ", comm=" + comm
                + ", detail=" + detail
                + ", ask=" + ask
                + ", total=" + total
                + ", maxTotal=" + maxTotal
                + ", activeTime=" + activeTime
                + ", enrollEndTime=" + enrollEndTime
                + ", teamId=" + teamId
                + "]";
    }
}
