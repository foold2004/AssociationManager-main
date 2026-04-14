package com.rabbiter.association.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.association.dao.PayExpensesDao;
import com.rabbiter.association.entity.PayExpenses;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.service.PayExpensesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service("payExpensesService")
public class PayExpensesServiceImpl implements PayExpensesService {

    @Autowired
    private PayExpensesDao payExpensesDao;

    @Override
    @Transactional
    public void add(PayExpenses payExpenses) {
        payExpensesDao.insert(payExpenses);
    }

    @Override
    @Transactional
    public void update(PayExpenses payExpenses) {
        payExpensesDao.updateById(payExpenses);
    }

    @Override
    @Transactional
    public void delete(PayExpenses payExpenses) {
        payExpensesDao.deleteById(payExpenses);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PayExpenses getOne(String id) {
        return payExpensesDao.selectById(id);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PageData getPageAll(Long pageIndex, Long pageSize, String teamId, String title) {
        Page<Map<String, Object>> page =
                payExpensesDao.qryPageAll(new Page<Map<String, Object>>(pageIndex, pageSize), teamId, title);
        return parsePage(page);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PageData getPageByManager(Long pageIndex, Long pageSize, String userId, String teamId, String title) {
        Page<Map<String, Object>> page =
                payExpensesDao.qryPageByManager(new Page<Map<String, Object>>(pageIndex, pageSize), userId, teamId, title);
        return parsePage(page);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PageData getPageByMember(Long pageIndex, Long pageSize, String userId, String teamId, String title) {
        Page<Map<String, Object>> page =
                payExpensesDao.qryPageByMember(new Page<Map<String, Object>>(pageIndex, pageSize), userId, teamId, title);
        return parsePage(page);
    }

    private PageData parsePage(Page<Map<String, Object>> p) {
        return new PageData(p.getCurrent(), p.getSize(), p.getTotal(), p.getRecords());
    }
}
