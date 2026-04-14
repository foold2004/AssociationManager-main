package com.rabbiter.association.service;

import com.rabbiter.association.entity.Interactions;
import com.rabbiter.association.msg.PageData;

public interface InteractionsService extends BaseService<Interactions, String> {

    PageData getPageInfo(Long pageIndex, Long pageSize, Integer userType, String userId, String keyword, String teamName);
}
