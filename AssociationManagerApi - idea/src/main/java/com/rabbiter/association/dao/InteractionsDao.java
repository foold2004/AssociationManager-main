package com.rabbiter.association.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.association.entity.Interactions;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Map;

@Repository("interactionsDao")
public interface InteractionsDao extends BaseMapper<Interactions> {

    @Select("<script>" +
            "SELECT i.id, i.title, i.content, i.create_time createTime, i.team_id teamId, i.user_id userId, " +
            "u.name userName, t.name teamName " +
            "FROM interactions i " +
            "LEFT JOIN users u ON i.user_id = u.id " +
            "LEFT JOIN teams t ON i.team_id = t.id " +
            "<where>" +
            "<if test='keyword != null and keyword.trim() != &quot;&quot;'> " +
            "(i.title LIKE CONCAT('%', #{keyword}, '%') OR i.content LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='teamName != null and teamName.trim() != &quot;&quot;'> " +
            "AND t.name LIKE CONCAT('%', #{teamName}, '%') " +
            "</if>" +
            "</where>" +
            "ORDER BY i.create_time DESC " +
            "</script>")
    Page<Map<String, Object>> qryPageAll(Page<Map<String, Object>> page,
                                         @Param("keyword") String keyword,
                                         @Param("teamName") String teamName);

    @Select("<script>" +
            "SELECT i.id, i.title, i.content, i.create_time createTime, i.team_id teamId, i.user_id userId, " +
            "u.name userName, t.name teamName " +
            "FROM interactions i " +
            "LEFT JOIN users u ON i.user_id = u.id " +
            "LEFT JOIN teams t ON i.team_id = t.id " +
            "<where>" +
            "(i.team_id IS NULL OR i.team_id IN (SELECT id FROM teams WHERE manager = #{userId})) " +
            "<if test='keyword != null and keyword.trim() != &quot;&quot;'> " +
            "AND (i.title LIKE CONCAT('%', #{keyword}, '%') OR i.content LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='teamName != null and teamName.trim() != &quot;&quot;'> " +
            "AND t.name LIKE CONCAT('%', #{teamName}, '%') " +
            "</if>" +
            "</where>" +
            "ORDER BY i.create_time DESC " +
            "</script>")
    Page<Map<String, Object>> qryPageByManager(Page<Map<String, Object>> page,
                                               @Param("userId") String userId,
                                               @Param("keyword") String keyword,
                                               @Param("teamName") String teamName);

    @Select("<script>" +
            "SELECT i.id, i.title, i.content, i.create_time createTime, i.team_id teamId, i.user_id userId, " +
            "u.name userName, t.name teamName " +
            "FROM interactions i " +
            "LEFT JOIN users u ON i.user_id = u.id " +
            "LEFT JOIN teams t ON i.team_id = t.id " +
            "<where>" +
            "(i.team_id IS NULL OR i.team_id IN (SELECT team_id FROM members WHERE user_id = #{userId})) " +
            "<if test='keyword != null and keyword.trim() != &quot;&quot;'> " +
            "AND (i.title LIKE CONCAT('%', #{keyword}, '%') OR i.content LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='teamName != null and teamName.trim() != &quot;&quot;'> " +
            "AND t.name LIKE CONCAT('%', #{teamName}, '%') " +
            "</if>" +
            "</where>" +
            "ORDER BY i.create_time DESC " +
            "</script>")
    Page<Map<String, Object>> qryPageByMember(Page<Map<String, Object>> page,
                                              @Param("userId") String userId,
                                              @Param("keyword") String keyword,
                                              @Param("teamName") String teamName);
}
