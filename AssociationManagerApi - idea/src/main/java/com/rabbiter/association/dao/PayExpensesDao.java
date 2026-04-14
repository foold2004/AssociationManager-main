package com.rabbiter.association.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.association.entity.PayExpenses;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 数据层处理接口
 * 消费明细
 */
@Repository("payExpensesDao")
public interface PayExpensesDao extends BaseMapper<PayExpenses> {

    @Select("<script>" +
            "SELECT " +
            "pe.id, pe.create_time createTime, pe.total, pe.title, pe.detail, pe.team_id teamId, pe.handler_id handlerId, " +
            "t.name teamName, u.name handlerName " +
            "FROM pay_expenses pe " +
            "LEFT JOIN teams t ON pe.team_id = t.id " +
            "LEFT JOIN users u ON pe.handler_id = u.id " +
            "<where> " +
            "<if test='teamId != null and teamId.trim() != &quot;&quot;'> " +
            "pe.team_id = #{teamId} " +
            "</if> " +
            "<if test='title != null and title.trim() != &quot;&quot;'> " +
            "AND pe.title LIKE CONCAT('%', #{title}, '%') " +
            "</if> " +
            "</where> " +
            "ORDER BY pe.create_time DESC " +
            "</script>")
    Page<Map<String, Object>> qryPageAll(Page<Map<String, Object>> page,
                                         @Param("teamId") String teamId,
                                         @Param("title") String title);

    @Select("<script>" +
            "SELECT " +
            "pe.id, pe.create_time createTime, pe.total, pe.title, pe.detail, pe.team_id teamId, pe.handler_id handlerId, " +
            "t.name teamName, u.name handlerName " +
            "FROM pay_expenses pe " +
            "LEFT JOIN teams t ON pe.team_id = t.id " +
            "LEFT JOIN users u ON pe.handler_id = u.id " +
            "<where> " +
            "pe.team_id IN (SELECT id FROM teams WHERE manager = #{userId}) " +
            "<if test='teamId != null and teamId.trim() != &quot;&quot;'> " +
            "AND pe.team_id = #{teamId} " +
            "</if> " +
            "<if test='title != null and title.trim() != &quot;&quot;'> " +
            "AND pe.title LIKE CONCAT('%', #{title}, '%') " +
            "</if> " +
            "</where> " +
            "ORDER BY pe.create_time DESC " +
            "</script>")
    Page<Map<String, Object>> qryPageByManager(Page<Map<String, Object>> page,
                                               @Param("userId") String userId,
                                               @Param("teamId") String teamId,
                                               @Param("title") String title);

    @Select("<script>" +
            "SELECT " +
            "pe.id, pe.create_time createTime, pe.total, pe.title, pe.detail, pe.team_id teamId, pe.handler_id handlerId, " +
            "t.name teamName, u.name handlerName " +
            "FROM pay_expenses pe " +
            "LEFT JOIN teams t ON pe.team_id = t.id " +
            "LEFT JOIN users u ON pe.handler_id = u.id " +
            "<where> " +
            "pe.team_id IN (SELECT team_id FROM members WHERE user_id = #{userId}) " +
            "<if test='teamId != null and teamId.trim() != &quot;&quot;'> " +
            "AND pe.team_id = #{teamId} " +
            "</if> " +
            "<if test='title != null and title.trim() != &quot;&quot;'> " +
            "AND pe.title LIKE CONCAT('%', #{title}, '%') " +
            "</if> " +
            "</where> " +
            "ORDER BY pe.create_time DESC " +
            "</script>")
    Page<Map<String, Object>> qryPageByMember(Page<Map<String, Object>> page,
                                              @Param("userId") String userId,
                                              @Param("teamId") String teamId,
                                              @Param("title") String title);

    @Select("<script>" +
            "SELECT IFNULL(SUM(pe.total), 0) " +
            "FROM pay_expenses pe " +
            "<where> " +
            "<if test='teamId != null and teamId.trim() != &quot;&quot;'> " +
            "pe.team_id = #{teamId} " +
            "</if> " +
            "</where> " +
            "</script>")
    Double sumTotalAll(@Param("teamId") String teamId);

    @Select("<script>" +
            "SELECT IFNULL(SUM(pe.total), 0) " +
            "FROM pay_expenses pe " +
            "<where> " +
            "pe.team_id IN (SELECT id FROM teams WHERE manager = #{userId}) " +
            "<if test='teamId != null and teamId.trim() != &quot;&quot;'> " +
            "AND pe.team_id = #{teamId} " +
            "</if> " +
            "</where> " +
            "</script>")
    Double sumTotalByManager(@Param("userId") String userId, @Param("teamId") String teamId);

    @Select("<script>" +
            "SELECT IFNULL(SUM(pe.total), 0) " +
            "FROM pay_expenses pe " +
            "<where> " +
            "pe.team_id IN (SELECT team_id FROM members WHERE user_id = #{userId}) " +
            "<if test='teamId != null and teamId.trim() != &quot;&quot;'> " +
            "AND pe.team_id = #{teamId} " +
            "</if> " +
            "</where> " +
            "</script>")
    Double sumTotalByMember(@Param("userId") String userId, @Param("teamId") String teamId);
}
