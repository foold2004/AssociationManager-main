package com.rabbiter.association.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.association.entity.Rules;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Map;

@Repository("rulesDao")
public interface RulesDao extends BaseMapper<Rules> {

    @Select("<script>" +
            "SELECT r.id, r.title, r.content, r.create_time createTime, r.update_time updateTime, " +
            "r.team_id teamId, r.user_id userId, u.name userName, t.name teamName " +
            "FROM rules r " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "LEFT JOIN teams t ON r.team_id = t.id " +
            "<where> " +
            "<if test='title != null and title.trim() != &quot;&quot;'> " +
            "r.title LIKE CONCAT('%', #{title}, '%') " +
            "</if> " +
            "<if test='teamName != null and teamName.trim() != &quot;&quot;'> " +
            "AND t.name LIKE CONCAT('%', #{teamName}, '%') " +
            "</if> " +
            "</where> " +
            "ORDER BY r.create_time DESC " +
            "</script>")
    Page<Map<String, Object>> qryPageAll(Page<Map<String, Object>> page,
                                         @Param("title") String title,
                                         @Param("teamName") String teamName);

    @Select("<script>" +
            "SELECT r.id, r.title, r.content, r.create_time createTime, r.update_time updateTime, " +
            "r.team_id teamId, r.user_id userId, u.name userName, t.name teamName " +
            "FROM rules r " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "LEFT JOIN teams t ON r.team_id = t.id " +
            "<where> " +
            "(r.team_id IS NULL OR r.team_id IN (SELECT id FROM teams WHERE manager = #{userId})) " +
            "<if test='title != null and title.trim() != &quot;&quot;'> " +
            "AND r.title LIKE CONCAT('%', #{title}, '%') " +
            "</if> " +
            "<if test='teamName != null and teamName.trim() != &quot;&quot;'> " +
            "AND t.name LIKE CONCAT('%', #{teamName}, '%') " +
            "</if> " +
            "</where> " +
            "ORDER BY r.create_time DESC " +
            "</script>")
    Page<Map<String, Object>> qryPageByManager(Page<Map<String, Object>> page,
                                               @Param("userId") String userId,
                                               @Param("title") String title,
                                               @Param("teamName") String teamName);

    @Select("<script>" +
            "SELECT r.id, r.title, r.content, r.create_time createTime, r.update_time updateTime, " +
            "r.team_id teamId, r.user_id userId, u.name userName, t.name teamName " +
            "FROM rules r " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "LEFT JOIN teams t ON r.team_id = t.id " +
            "<where> " +
            "(r.team_id IS NULL OR r.team_id IN (SELECT team_id FROM members WHERE user_id = #{userId})) " +
            "<if test='title != null and title.trim() != &quot;&quot;'> " +
            "AND r.title LIKE CONCAT('%', #{title}, '%') " +
            "</if> " +
            "<if test='teamName != null and teamName.trim() != &quot;&quot;'> " +
            "AND t.name LIKE CONCAT('%', #{teamName}, '%') " +
            "</if> " +
            "</where> " +
            "ORDER BY r.create_time DESC " +
            "</script>")
    Page<Map<String, Object>> qryPageByMember(Page<Map<String, Object>> page,
                                              @Param("userId") String userId,
                                              @Param("title") String title,
                                              @Param("teamName") String teamName);
}
