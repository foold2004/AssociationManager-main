package com.rabbiter.association.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbiter.association.dao.ActivitiesDao;
import com.rabbiter.association.dao.ActiveLogsDao;
import com.rabbiter.association.dao.ApplyLogsDao;
import com.rabbiter.association.dao.MembersDao;
import com.rabbiter.association.dao.NoticesDao;
import com.rabbiter.association.dao.PayExpensesDao;
import com.rabbiter.association.dao.PayLogsDao;
import com.rabbiter.association.dao.TeamTypesDao;
import com.rabbiter.association.dao.TeamsDao;
import com.rabbiter.association.dao.UsersDao;
import com.rabbiter.association.entity.Activities;
import com.rabbiter.association.entity.ActiveLogs;
import com.rabbiter.association.entity.ApplyLogs;
import com.rabbiter.association.entity.Members;
import com.rabbiter.association.entity.Notices;
import com.rabbiter.association.entity.PayExpenses;
import com.rabbiter.association.entity.PayLogs;
import com.rabbiter.association.entity.TeamTypes;
import com.rabbiter.association.entity.Teams;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.service.TeamsService;
import com.rabbiter.association.utils.DateUtils;
import com.rabbiter.association.utils.IDUtils;
import com.rabbiter.association.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("teamsService")
public class TeamsServiceImpl implements TeamsService {

    @Autowired
    private UsersDao usersDao;

    @Autowired
    private TeamTypesDao teamTypesDao;

    @Autowired
    private TeamsDao teamsDao;

    @Autowired
    private MembersDao membersDao;

    @Autowired
    private NoticesDao noticesDao;

    @Autowired
    private ActivitiesDao activitiesDao;

    @Autowired
    private ActiveLogsDao activeLogsDao;

    @Autowired
    private ApplyLogsDao applyLogsDao;

    @Autowired
    private PayLogsDao payLogsDao;

    @Autowired
    private PayExpensesDao payExpensesDao;

    @Override
    @Transactional
    public Integer addTeams(Teams teams) {
        Integer count = usersDao.selectCount(
                new QueryWrapper<Users>().eq("id", teams.getManager()).eq("type", 1)
        );
        if (count == 0) {
            return 0;
        }

        teamsDao.insert(teams);
        ensureManagerMembership(teams.getId(), teams.getManager());
        return 1;
    }

    @Override
    @Transactional
    public Integer updateTeams(Teams teams) {
        Integer count = usersDao.selectCount(
                new QueryWrapper<Users>().eq("id", teams.getManager()).eq("type", 1)
        );
        if (count == 0) {
            return 0;
        }

        Teams oldTeam = teamsDao.selectById(teams.getId());
        update(teams);

        if (oldTeam != null && !teams.getManager().equals(oldTeam.getManager())) {
            ensureManagerMembership(teams.getId(), teams.getManager());
        }
        return 1;
    }

    @Override
    public void add(Teams teams) {
    }

    @Override
    @Transactional
    public void update(Teams teams) {
        teamsDao.updateById(teams);
    }

    @Override
    @Transactional
    public void delete(Teams teams) {
        QueryWrapper<Notices> noticeWrapper = new QueryWrapper<Notices>();
        noticeWrapper.eq("team_id", teams.getId());
        noticesDao.delete(noticeWrapper);

        QueryWrapper<PayLogs> payWrapper = new QueryWrapper<PayLogs>();
        payWrapper.eq("team_id", teams.getId());
        payLogsDao.delete(payWrapper);

        QueryWrapper<PayExpenses> expenseWrapper = new QueryWrapper<PayExpenses>();
        expenseWrapper.eq("team_id", teams.getId());
        payExpensesDao.delete(expenseWrapper);

        QueryWrapper<ApplyLogs> applyWrapper = new QueryWrapper<ApplyLogs>();
        applyWrapper.eq("team_id", teams.getId());
        applyLogsDao.delete(applyWrapper);

        QueryWrapper<Members> memberWrapper = new QueryWrapper<Members>();
        memberWrapper.eq("team_id", teams.getId());
        membersDao.delete(memberWrapper);

        QueryWrapper<Activities> activityWrapper = new QueryWrapper<Activities>();
        activityWrapper.eq("team_id", teams.getId());
        for (Activities activity : activitiesDao.selectList(activityWrapper)) {
            QueryWrapper<ActiveLogs> logWrapper = new QueryWrapper<ActiveLogs>();
            logWrapper.eq("active_id", activity.getId());
            activeLogsDao.delete(logWrapper);
        }
        activitiesDao.delete(activityWrapper);

        teamsDao.deleteById(teams);

        QueryWrapper<Teams> teamWrapper = new QueryWrapper<Teams>();
        teamWrapper.eq("manager", teams.getManager());
        if (teamsDao.selectCount(teamWrapper) <= 0) {
            Users user = usersDao.selectById(teams.getManager());
            user.setType(2);
            usersDao.updateById(user);
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Teams getOne(String id) {
        return teamsDao.selectById(id);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Teams> getAll() {
        QueryWrapper<Teams> qw = new QueryWrapper<Teams>();
        qw.orderByDesc("create_time");
        return teamsDao.selectList(qw);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Teams> getListByManId(String manId) {
        QueryWrapper<Teams> qw = new QueryWrapper<Teams>();
        qw.eq("manager", manId);
        qw.orderByDesc("create_time");
        return teamsDao.selectList(qw);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PageData getPageInfo(Long pageIndex, Long pageSize, Teams teams) {
        QueryWrapper<Teams> qw = new QueryWrapper<Teams>();

        if (StringUtils.isNotNullOrEmpty(teams.getName())) {
            qw.like("name", teams.getName());
        }
        if (StringUtils.isNotNullOrEmpty(teams.getTypeId())) {
            qw.eq("type_id", teams.getTypeId());
        }
        if (StringUtils.isNotNullOrEmpty(teams.getManager())) {
            qw.eq("manager", teams.getManager());
        }

        qw.orderByDesc("create_time");
        Page<Teams> page = teamsDao.selectPage(new Page<Teams>(pageIndex, pageSize), qw);
        return parsePage(page);
    }

    public PageData parsePage(Page<Teams> p) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        for (Teams teams : p.getRecords()) {
            Map<String, Object> temp = new HashMap<String, Object>();
            temp.put("id", teams.getId());
            temp.put("name", teams.getName());
            temp.put("createTime", teams.getCreateTime());
            temp.put("total", teams.getTotal());
            temp.put("intro", teams.getIntro());
            temp.put("images", teams.getImages());

            Users user = usersDao.selectById(teams.getManager());
            temp.put("manager", teams.getManager());
            temp.put("managerName", user.getName());
            temp.put("managerPhone", user.getPhone());
            temp.put("managerAddress", user.getAddress());

            TeamTypes teamType = teamTypesDao.selectById(teams.getTypeId());
            temp.put("typeId", teams.getTypeId());
            temp.put("typeName", teamType.getName());
            result.add(temp);
        }

        return new PageData(p.getCurrent(), p.getSize(), p.getTotal(), result);
    }

    private void ensureManagerMembership(String teamId, String managerId) {
        QueryWrapper<Members> memberQuery = new QueryWrapper<Members>();
        memberQuery.eq("team_id", teamId).eq("user_id", managerId);
        if (membersDao.selectCount(memberQuery) <= 0) {
            Members member = new Members();
            member.setId(IDUtils.makeIDByCurrent());
            member.setUserId(managerId);
            member.setTeamId(teamId);
            member.setCreateTime(DateUtils.getNowDate());
            membersDao.insert(member);
        }
    }
}
