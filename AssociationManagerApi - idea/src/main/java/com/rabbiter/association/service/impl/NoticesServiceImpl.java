package com.rabbiter.association.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.association.dao.NoticesDao;
import com.rabbiter.association.entity.Notices;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.service.NoticesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service("noticesService")
public class NoticesServiceImpl implements NoticesService {

    @Autowired
    private NoticesDao noticesDao;

    @Override
    @Transactional
    public void add(Notices notices) {
        noticesDao.insert(notices);
    }

    @Override
    @Transactional
    public void update(Notices notices) {
        noticesDao.updateById(notices);
    }

    @Override
    @Transactional
    public void delete(Notices notices) {
        noticesDao.deleteById(notices);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Notices getOne(String id) {
        return noticesDao.selectById(id);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PageData getPageAll(Long pageIndex, Long pageSize, String title, String teamName, String teamId, Integer systemOnly) {
        Page<Map<String, Object>> page = noticesDao.qryPageAll(
                new Page<Map<String, Object>>(pageIndex, pageSize),
                title,
                teamName,
                teamId,
                systemOnly
        );
        return parsePage(page);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PageData getPageById(Long pageIndex, Long pageSize, String userId, String title, String teamName, String teamId, Integer systemOnly) {
        Page<Map<String, Object>> page = noticesDao.qryPageById(
                new Page<Map<String, Object>>(pageIndex, pageSize),
                userId,
                title,
                teamName,
                teamId,
                systemOnly
        );
        return parsePage(page);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PageData getPageByMemberId(Long pageIndex, Long pageSize, String userId, String title, String teamName, String teamId, Integer systemOnly) {
        Page<Map<String, Object>> page = noticesDao.qryPageByMemberId(
                new Page<Map<String, Object>>(pageIndex, pageSize),
                userId,
                title,
                teamName,
                teamId,
                systemOnly
        );
        return parsePage(page);
    }

    private PageData parsePage(Page<Map<String, Object>> page) {
        return new PageData(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }
}
