package com.rabbiter.association.service;

import com.rabbiter.association.entity.Rules;
import com.rabbiter.association.msg.PageData;

public interface RulesService extends BaseService<Rules, String> {

    PageData getPageInfo(Long pageIndex, Long pageSize, Integer userType, String userId, String title, String teamName);
}
