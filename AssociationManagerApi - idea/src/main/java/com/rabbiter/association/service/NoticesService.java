package com.rabbiter.association.service;

import com.rabbiter.association.entity.Notices;
import com.rabbiter.association.msg.PageData;

public interface NoticesService extends BaseService<Notices, String> {

    PageData getPageAll(Long pageIndex, Long pageSize, String title, String teamName, String teamId, Integer systemOnly);

    PageData getPageById(Long pageIndex, Long pageSize, String userId, String title, String teamName, String teamId, Integer systemOnly);

    PageData getPageByMemberId(Long pageIndex, Long pageSize, String userId, String title, String teamName, String teamId, Integer systemOnly);
}
