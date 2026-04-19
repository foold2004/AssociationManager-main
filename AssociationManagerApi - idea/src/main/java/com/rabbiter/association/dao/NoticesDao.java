package com.rabbiter.association.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.association.entity.Notices;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("noticesDao")
public interface NoticesDao extends BaseMapper<Notices> {

    @Select("<script>" +
            "SELECT COUNT(1) " +
            "FROM teams " +
            "WHERE id = #{teamId} AND manager = #{managerId}" +
            "</script>")
    Long countManagedTeam(@Param("managerId") String managerId, @Param("teamId") String teamId);

    @Select("<script>" +
            "SELECT " +
            "n.id, n.title, n.detail, n.create_time createTime, n.team_id teamId, n.is_top isTop, t.name teamName " +
            "FROM notices n LEFT JOIN teams t ON n.team_id = t.id " +
            "<where>" +
            "<if test='teamName != null and teamName.trim() != &quot;&quot; '>" +
            "t.name LIKE CONCAT('%', #{teamName}, '%') " +
            "</if>" +
            "<if test='teamId != null and teamId.trim() != &quot;&quot; '>" +
            "AND n.team_id = #{teamId} " +
            "</if>" +
            "<if test='systemOnly != null and systemOnly == 1'>" +
            "AND n.team_id IS NULL " +
            "</if>" +
            "<if test='title != null and title.trim() != &quot;&quot; '>" +
            "AND n.title LIKE CONCAT('%', #{title}, '%') " +
            "</if>" +
            "</where>" +
            "ORDER BY n.is_top DESC, n.create_time DESC " +
            "</script>")
    Page<Map<String, Object>> qryPageAll(
            Page<Map<String, Object>> page,
            @Param("title") String title,
            @Param("teamName") String teamName,
            @Param("teamId") String teamId,
            @Param("systemOnly") Integer systemOnly
    );

    @Select("<script>" +
            "SELECT " +
            "n.id, n.title, n.detail, n.create_time createTime, n.team_id teamId, n.is_top isTop, t.name teamName " +
            "FROM notices n LEFT JOIN teams t ON n.team_id = t.id " +
            "<where>" +
            "(n.team_id IS NULL OR n.team_id IN (SELECT id FROM teams WHERE manager = #{userId})) " +
            "<if test='teamName != null and teamName.trim() != &quot;&quot; '>" +
            "AND t.name LIKE CONCAT('%', #{teamName}, '%') " +
            "</if>" +
            "<if test='teamId != null and teamId.trim() != &quot;&quot; '>" +
            "AND n.team_id = #{teamId} " +
            "</if>" +
            "<if test='systemOnly != null and systemOnly == 1'>" +
            "AND n.team_id IS NULL " +
            "</if>" +
            "<if test='title != null and title.trim() != &quot;&quot; '>" +
            "AND n.title LIKE CONCAT('%', #{title}, '%') " +
            "</if>" +
            "</where>" +
            "ORDER BY n.is_top DESC, n.create_time DESC " +
            "</script>")
    Page<Map<String, Object>> qryPageById(
            Page<Map<String, Object>> page,
            @Param("userId") String userId,
            @Param("title") String title,
            @Param("teamName") String teamName,
            @Param("teamId") String teamId,
            @Param("systemOnly") Integer systemOnly
    );

    @Select("<script>" +
            "SELECT " +
            "n.id, n.title, n.detail, n.create_time createTime, n.team_id teamId, n.is_top isTop, t.name teamName " +
            "FROM notices n LEFT JOIN teams t ON n.team_id = t.id " +
            "<where>" +
            "(n.team_id IS NULL OR n.team_id IN (SELECT team_id FROM members WHERE user_id = #{userId})) " +
            "<if test='teamName != null and teamName.trim() != &quot;&quot; '>" +
            "AND t.name LIKE CONCAT('%', #{teamName}, '%') " +
            "</if>" +
            "<if test='teamId != null and teamId.trim() != &quot;&quot; '>" +
            "AND n.team_id = #{teamId} " +
            "</if>" +
            "<if test='systemOnly != null and systemOnly == 1'>" +
            "AND n.team_id IS NULL " +
            "</if>" +
            "<if test='title != null and title.trim() != &quot;&quot; '>" +
            "AND n.title LIKE CONCAT('%', #{title}, '%') " +
            "</if>" +
            "</where>" +
            "ORDER BY n.is_top DESC, n.create_time DESC " +
            "</script>")
    Page<Map<String, Object>> qryPageByMemberId(
            Page<Map<String, Object>> page,
            @Param("userId") String userId,
            @Param("title") String title,
            @Param("teamName") String teamName,
            @Param("teamId") String teamId,
            @Param("systemOnly") Integer systemOnly
    );
}
