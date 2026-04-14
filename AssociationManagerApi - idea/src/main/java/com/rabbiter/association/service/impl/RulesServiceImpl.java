package com.rabbiter.association.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.association.dao.RulesDao;
import com.rabbiter.association.entity.Rules;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.service.RulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service("rulesService")
public class RulesServiceImpl implements RulesService {

    @Autowired
    private RulesDao rulesDao;

    @Override
    @Transactional
    public void add(Rules rules) {
        rulesDao.insert(rules);
    }

    @Override
    @Transactional
    public void update(Rules rules) {
        rulesDao.updateById(rules);
    }

    @Override
    @Transactional
    public void delete(Rules rules) {
        rulesDao.deleteById(rules);
    }

    @Override
    public Rules getOne(String id) {
        return rulesDao.selectById(id);
    }

    @Override
    public PageData getPageInfo(Long pageIndex, Long pageSize, Integer userType, String userId, String title, String teamName) {
        Page<Map<String, Object>> page;
        if (userType == 0) {
            page = rulesDao.qryPageAll(new Page<Map<String, Object>>(pageIndex, pageSize), title, teamName);
        } else if (userType == 1) {
            page = rulesDao.qryPageByManager(new Page<Map<String, Object>>(pageIndex, pageSize), userId, title, teamName);
        } else {
            page = rulesDao.qryPageByMember(new Page<Map<String, Object>>(pageIndex, pageSize), userId, title, teamName);
        }

        return new PageData(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }
}
