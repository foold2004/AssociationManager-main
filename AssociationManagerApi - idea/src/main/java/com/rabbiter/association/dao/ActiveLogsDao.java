package com.rabbiter.association.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.association.entity.ActiveLogs;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("activeLogsDao")
public interface ActiveLogsDao extends BaseMapper<ActiveLogs> {

    @Select("<script>" +
            "SELECT al.id, al.create_time createTime, al.active_id activeId, al.user_id userId, al.status, " +
            "al.review_time reviewTime, al.review_remark reviewRemark, " +
            "a.name activeName, a.team_id teamId, t.name teamName, t.manager managerId, " +
            "u.name userName, u.phone userPhone " +
            "FROM active_logs al " +
            "JOIN activities a ON al.active_id = a.id " +
            "JOIN teams t ON a.team_id = t.id " +
            "JOIN users u ON al.user_id = u.id " +
            "<where>" +
            "<if test='teamName != null and teamName.trim() != &quot;&quot; '>" +
            "AND t.name LIKE CONCAT('%', #{teamName}, '%') " +
            "</if>" +
            "<if test='activeName != null and activeName.trim() != &quot;&quot; '>" +
            "AND a.name LIKE CONCAT('%', #{activeName}, '%') " +
            "</if>" +
            "<if test='userName != null and userName.trim() != &quot;&quot; '>" +
            "AND u.name LIKE CONCAT('%', #{userName}, '%') " +
            "</if>" +
            "<if test='status != null'>" +
            "AND al.status = #{status} " +
            "</if>" +
            "</where>" +
            "ORDER BY al.create_time DESC" +
            "</script>")
    Page<Map<String, Object>> qryPageAll(Page<Map<String, Object>> page,
                                         @Param("teamName") String teamName,
                                         @Param("activeName") String activeName,
                                         @Param("userName") String userName,
                                         @Param("status") Integer status);

    @Select("<script>" +
            "SELECT al.id, al.create_time createTime, al.active_id activeId, al.user_id userId, al.status, " +
            "al.review_time reviewTime, al.review_remark reviewRemark, " +
            "a.name activeName, a.team_id teamId, t.name teamName, t.manager managerId, " +
            "u.name userName, u.phone userPhone " +
            "FROM active_logs al " +
            "JOIN activities a ON al.active_id = a.id " +
            "JOIN teams t ON a.team_id = t.id " +
            "JOIN users u ON al.user_id = u.id " +
            "<where>" +
            "t.manager = #{managerId} " +
            "<if test='teamName != null and teamName.trim() != &quot;&quot; '>" +
            "AND t.name LIKE CONCAT('%', #{teamName}, '%') " +
            "</if>" +
            "<if test='activeName != null and activeName.trim() != &quot;&quot; '>" +
            "AND a.name LIKE CONCAT('%', #{activeName}, '%') " +
            "</if>" +
            "<if test='userName != null and userName.trim() != &quot;&quot; '>" +
            "AND u.name LIKE CONCAT('%', #{userName}, '%') " +
            "</if>" +
            "<if test='status != null'>" +
            "AND al.status = #{status} " +
            "</if>" +
            "</where>" +
            "ORDER BY al.create_time DESC" +
            "</script>")
    Page<Map<String, Object>> qryPageByManager(Page<Map<String, Object>> page,
                                               @Param("managerId") String managerId,
                                               @Param("teamName") String teamName,
                                               @Param("activeName") String activeName,
                                               @Param("userName") String userName,
                                               @Param("status") Integer status);

    @Select("<script>" +
            "SELECT al.id, al.create_time createTime, al.active_id activeId, al.user_id userId, al.status, " +
            "al.review_time reviewTime, al.review_remark reviewRemark, " +
            "a.name activeName, a.team_id teamId, t.name teamName, t.manager managerId, " +
            "u.name userName, u.phone userPhone " +
            "FROM active_logs al " +
            "JOIN activities a ON al.active_id = a.id " +
            "JOIN teams t ON a.team_id = t.id " +
            "JOIN users u ON al.user_id = u.id " +
            "<where>" +
            "al.user_id = #{userId} " +
            "<if test='teamName != null and teamName.trim() != &quot;&quot; '>" +
            "AND t.name LIKE CONCAT('%', #{teamName}, '%') " +
            "</if>" +
            "<if test='activeName != null and activeName.trim() != &quot;&quot; '>" +
            "AND a.name LIKE CONCAT('%', #{activeName}, '%') " +
            "</if>" +
            "<if test='status != null'>" +
            "AND al.status = #{status} " +
            "</if>" +
            "</where>" +
            "ORDER BY al.create_time DESC" +
            "</script>")
    Page<Map<String, Object>> qryPageByUser(Page<Map<String, Object>> page,
                                            @Param("userId") String userId,
                                            @Param("teamName") String teamName,
                                            @Param("activeName") String activeName,
                                            @Param("status") Integer status);

    @Select("SELECT al.id, al.create_time createTime, al.active_id activeId, al.user_id userId, al.status, " +
            "u.name userName, u.gender userGender, u.phone userPhone " +
            "FROM active_logs al JOIN users u ON al.user_id = u.id " +
            "WHERE al.active_id = #{activeId} AND al.status = 1 " +
            "ORDER BY al.create_time DESC")
    List<Map<String, Object>> qryApprovedListByActiveId(@Param("activeId") String activeId);
}
