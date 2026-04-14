package com.rabbiter.association.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rabbiter.association.entity.InteractionComments;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("interactionCommentsDao")
public interface InteractionCommentsDao extends BaseMapper<InteractionComments> {

    @Select("SELECT c.id, c.interaction_id interactionId, c.parent_id parentId, c.content, c.create_time createTime, " +
            "c.user_id userId, u.name userName, u.user_name userAccount, pu.name parentUserName, pc.content parentContent " +
            "FROM interaction_comments c " +
            "LEFT JOIN users u ON c.user_id = u.id " +
            "LEFT JOIN interaction_comments pc ON c.parent_id = pc.id " +
            "LEFT JOIN users pu ON pc.user_id = pu.id " +
            "WHERE c.interaction_id = #{interactionId} " +
            "ORDER BY c.create_time ASC")
    List<Map<String, Object>> qryListByInteractionId(@Param("interactionId") String interactionId);
}
