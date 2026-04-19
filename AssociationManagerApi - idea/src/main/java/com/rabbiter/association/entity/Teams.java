package com.rabbiter.association.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 社团信息
 */
@TableName(value = "teams")
public class Teams implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private String id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "create_time")
    private String createTime;

    @TableField(value = "total")
    private Integer total;

    @TableField(value = "manager")
    private String manager;

    @TableField(value = "type_id")
    private String typeId;

    @TableField(value = "intro")
    private String intro;

    /**
     * 社团风貌图片，多个图片地址使用逗号分隔
     */
    @TableField(value = "images")
    private String images;

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Teams [id=" + id
                + ", name=" + name
                + ", createTime=" + createTime
                + ", total=" + total
                + ", manager=" + manager
                + ", typeId=" + typeId
                + ", intro=" + intro
                + ", images=" + images
                + "]";
    }
}
