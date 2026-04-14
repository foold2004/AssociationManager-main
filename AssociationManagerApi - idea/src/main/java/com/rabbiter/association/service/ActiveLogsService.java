package com.rabbiter.association.service;

import com.rabbiter.association.entity.ActiveLogs;
import com.rabbiter.association.msg.PageData;

import java.util.List;
import java.util.Map;

public interface ActiveLogsService extends BaseService<ActiveLogs, String> {

    Boolean isActive(String activeId, String userId);

    List<Map<String, Object>> getListByActiveId(String activeId);

    PageData getPageAll(Long pageIndex, Long pageSize, String teamName, String activeName, String userName, Integer status);

    PageData getPageByManager(Long pageIndex, Long pageSize, String managerId, String teamName, String activeName, String userName, Integer status);

    PageData getPageByUser(Long pageIndex, Long pageSize, String userId, String teamName, String activeName, Integer status);
}
