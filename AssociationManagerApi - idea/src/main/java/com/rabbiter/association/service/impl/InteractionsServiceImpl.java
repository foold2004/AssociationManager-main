package com.rabbiter.association.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.association.dao.InteractionsDao;
import com.rabbiter.association.entity.Interactions;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.service.InteractionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service("interactionsService")
public class InteractionsServiceImpl implements InteractionsService {

    @Autowired
    private InteractionsDao interactionsDao;

    @Override
    @Transactional
    public void add(Interactions interactions) {
        interactionsDao.insert(interactions);
    }

    @Override
    @Transactional
    public void update(Interactions interactions) {
        interactionsDao.updateById(interactions);
    }

    @Override
    @Transactional
    public void delete(Interactions interactions) {
        interactionsDao.deleteById(interactions);
    }

    @Override
    public Interactions getOne(String id) {
        return interactionsDao.selectById(id);
    }

    @Override
    public PageData getPageInfo(Long pageIndex, Long pageSize, Integer userType, String userId, String keyword, String teamName) {
        Page<Map<String, Object>> page;
        if (userType == 0) {
            page = interactionsDao.qryPageAll(new Page<Map<String, Object>>(pageIndex, pageSize), keyword, teamName);
        } else if (userType == 1) {
            page = interactionsDao.qryPageByManager(new Page<Map<String, Object>>(pageIndex, pageSize), userId, keyword, teamName);
        } else {
            page = interactionsDao.qryPageByMember(new Page<Map<String, Object>>(pageIndex, pageSize), userId, keyword, teamName);
        }

        return new PageData(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }
}
