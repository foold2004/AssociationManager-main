package com.rabbiter.association.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.association.dao.ActivitiesDao;
import com.rabbiter.association.dao.ActiveLogsDao;
import com.rabbiter.association.entity.Activities;
import com.rabbiter.association.entity.ActiveLogs;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.service.ActiveLogsService;
import com.rabbiter.association.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service("activeLogsService")
public class ActiveLogsServiceImpl implements ActiveLogsService {

    @Autowired
    private ActiveLogsDao activeLogsDao;

    @Autowired
    private ActivitiesDao activitiesDao;

    @Override
    @Transactional
    public void add(ActiveLogs activeLogs) {
        activeLogsDao.insert(activeLogs);

        if (activeLogs.getStatus() != null && activeLogs.getStatus() == 1) {
            Activities activity = activitiesDao.selectById(activeLogs.getActiveId());
            if (activity != null) {
                activity.setTotal((activity.getTotal() == null ? 0 : activity.getTotal()) + 1);
                activitiesDao.updateById(activity);
            }
        }
    }

    @Override
    @Transactional
    public void update(ActiveLogs activeLogs) {
        ActiveLogs oldLog = activeLogsDao.selectById(activeLogs.getId());
        if (oldLog == null) {
            return;
        }

        Activities activity = activitiesDao.selectById(oldLog.getActiveId());
        Integer oldStatus = oldLog.getStatus() == null ? 0 : oldLog.getStatus();
        Integer newStatus = activeLogs.getStatus() == null ? oldStatus : activeLogs.getStatus();

        if (activity != null) {
          if (oldStatus != 1 && newStatus == 1) {
              activity.setTotal((activity.getTotal() == null ? 0 : activity.getTotal()) + 1);
              activitiesDao.updateById(activity);
          } else if (oldStatus == 1 && newStatus != 1 && activity.getTotal() != null && activity.getTotal() > 0) {
              activity.setTotal(activity.getTotal() - 1);
              activitiesDao.updateById(activity);
          }
        }

        if (newStatus != oldStatus && (newStatus == 1 || newStatus == 2)) {
            activeLogs.setReviewTime(DateUtils.getNowDate());
        }

        activeLogsDao.updateById(activeLogs);
    }

    @Override
    @Transactional
    public void delete(ActiveLogs activeLogs) {
        if (activeLogs == null) {
            return;
        }

        if (activeLogs.getStatus() != null && activeLogs.getStatus() == 1) {
            Activities activity = activitiesDao.selectById(activeLogs.getActiveId());
            if (activity != null && activity.getTotal() != null && activity.getTotal() > 0) {
                activity.setTotal(activity.getTotal() - 1);
                activitiesDao.updateById(activity);
            }
        }
        activeLogsDao.deleteById(activeLogs);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Boolean isActive(String activeId, String userId) {
        QueryWrapper<ActiveLogs> qw = new QueryWrapper<ActiveLogs>();
        qw.eq("active_id", activeId);
        qw.eq("user_id", userId);
        qw.in("status", 0, 1);
        return activeLogsDao.selectCount(qw) <= 0;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ActiveLogs getOne(String id) {
        return activeLogsDao.selectById(id);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Map<String, Object>> getListByActiveId(String activeId) {
        return activeLogsDao.qryApprovedListByActiveId(activeId);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PageData getPageAll(Long pageIndex, Long pageSize, String teamName, String activeName, String userName, Integer status) {
        Page<Map<String, Object>> page = activeLogsDao.qryPageAll(
                new Page<Map<String, Object>>(pageIndex, pageSize), teamName, activeName, userName, status);
        return parsePage(page);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PageData getPageByManager(Long pageIndex, Long pageSize, String managerId, String teamName, String activeName, String userName, Integer status) {
        Page<Map<String, Object>> page = activeLogsDao.qryPageByManager(
                new Page<Map<String, Object>>(pageIndex, pageSize), managerId, teamName, activeName, userName, status);
        return parsePage(page);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PageData getPageByUser(Long pageIndex, Long pageSize, String userId, String teamName, String activeName, Integer status) {
        Page<Map<String, Object>> page = activeLogsDao.qryPageByUser(
                new Page<Map<String, Object>>(pageIndex, pageSize), userId, teamName, activeName, status);
        return parsePage(page);
    }

    private PageData parsePage(Page<Map<String, Object>> page) {
        return new PageData(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }
}
