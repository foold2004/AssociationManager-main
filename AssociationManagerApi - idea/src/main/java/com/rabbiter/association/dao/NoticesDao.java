package com.rabbiter.association.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.rabbiter.association.entity.Notices;

import java.util.List;
import java.util.Map;

/**
 * 数据层处理接口
 * 通知记录
 */
@Repository("noticesDao")
public interface NoticesDao extends BaseMapper<Notices> {

    /**
     * 获取系统通知
     * @return
     */
    @Select("<script>" +
            "SELECT " +
            "id, title, detail, create_time createTime, team_id teamId, is_top isTop " +
            "FROM notices " +
            "WHERE team_id IS NULL " +
            "ORDER BY is_top DESC, create_time DESC " +
            "</script>")
    public List<Notices> qrySysNotices();

    /**
     * 查询指定社团管理员相关通知信息列表
     * @param manId 社团管理ID
     * @return
     */
    @Select("<script>" +
            "SELECT " +
            "id, title, detail, create_time createTime, team_id teamId, is_top isTop " +
            "FROM notices " +
            "WHERE (team_id IS NULL) OR (team_id IN (SELECT team_id FROM members WHERE user_id = #{manId})) " +
            "ORDER BY is_top DESC, create_time DESC " +
            "</script>")
    public List<Notices> qryManNotices(String manId);

    /**
     * 查询指定社团成员相关通知信息列表
     * @param memId 社团成员ID
     * @return
     */
    @Select("<script>" +
            "SELECT " +
            "id, title, detail, create_time createTime, team_id teamId, is_top isTop " +
            "FROM notices " +
            "WHERE (team_id IS NULL) OR (team_id IN (SELECT team_id FROM members WHERE user_id = #{memId})) " +
            "ORDER BY is_top DESC, create_time DESC " +
            "</script>")
    public List<Notices> qryMemNotices(String memId);

    @Select("<script>" +
            "SELECT COUNT(1) " +
            "FROM teams " +
            "WHERE id = #{teamId} AND manager = #{managerId}" +
            "</script>")
    public Long countManagedTeam(@Param("managerId") String managerId,
                                 @Param("teamId") String teamId);

    /**
     * 分页查找全部通知信息
     * @param page 分页参数
     * @param title 通知标题
     * @param teamName 团队名称
     * @return
     */
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
    public Page<Map<String, Object>> qryPageAll(Page<Map<String, Object>> page,
                                                 @Param("title") String title,
                                                 @Param("teamName") String teamName,
                                                 @Param("teamId") String teamId,
                                                 @Param("systemOnly") Integer systemOnly);

    /**
     * 获取指定用户相关的通知信息
     * @param page 分页参数
     * @param userId 指定用户ID
     * @param title 通知标题
     * @param teamName 团队名称
     * @return
     */
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
    public Page<Map<String, Object>> qryPageById(Page<Map<String, Object>> page,
                                                 @Param("userId") String userId,
                                                 @Param("title") String title,
                                                 @Param("teamName") String teamName,
                                                 @Param("teamId") String teamId,
                                                 @Param("systemOnly") Integer systemOnly);

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
    public Page<Map<String, Object>> qryPageByMemberId(Page<Map<String, Object>> page,
                                                       @Param("userId") String userId,
                                                       @Param("title") String title,
                                                       @Param("teamName") String teamName,
                                                       @Param("teamId") String teamId,
                                                       @Param("systemOnly") Integer systemOnly);
}
