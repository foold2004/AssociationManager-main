package com.rabbiter.association.service;

import com.rabbiter.association.entity.PayExpenses;
import com.rabbiter.association.msg.PageData;

/**
 * 业务层处理
 * 消费明细
 */
public interface PayExpensesService extends BaseService<PayExpenses, String> {

    /**
     * 系统管理员分页查询消费明细
     * @param pageIndex 当前页码
     * @param pageSize 每页数据量
     * @param teamId 社团ID
     * @param title 消费标题
     * @return
     */
    PageData getPageAll(Long pageIndex, Long pageSize, String teamId, String title);

    /**
     * 社团管理员分页查询消费明细
     * @param pageIndex 当前页码
     * @param pageSize 每页数据量
     * @param userId 管理员ID
     * @param teamId 社团ID
     * @param title 消费标题
     * @return
     */
    PageData getPageByManager(Long pageIndex, Long pageSize, String userId, String teamId, String title);

    /**
     * 学生分页查询消费明细
     * @param pageIndex 当前页码
     * @param pageSize 每页数据量
     * @param userId 用户ID
     * @param teamId 社团ID
     * @param title 消费标题
     * @return
     */
    PageData getPageByMember(Long pageIndex, Long pageSize, String userId, String teamId, String title);
}
