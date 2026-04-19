package com.rabbiter.association.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbiter.association.dao.ActiveLogsDao;
import com.rabbiter.association.dao.MembersDao;
import com.rabbiter.association.entity.ActiveLogs;
import com.rabbiter.association.entity.Activities;
import com.rabbiter.association.entity.Members;
import com.rabbiter.association.entity.Teams;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.handle.CacheHandle;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.service.ActiveLogsService;
import com.rabbiter.association.service.ActivitiesService;
import com.rabbiter.association.service.TeamsService;
import com.rabbiter.association.service.UsersService;
import com.rabbiter.association.utils.DateUtils;
import com.rabbiter.association.utils.IDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/activeLogs")
public class ActiveLogsController {

    protected static final Logger Log = LoggerFactory.getLogger(ActiveLogsController.class);

    @Autowired
    private CacheHandle cacheHandle;

    @Autowired
    private UsersService usersService;

    @Autowired
    private ActiveLogsService activeLogsService;

    @Autowired
    private ActiveLogsDao activeLogsDao;

    @Autowired
    private MembersDao membersDao;

    @Autowired
    private ActivitiesService activitiesService;

    @Autowired
    private TeamsService teamsService;

    @GetMapping("/list")
    @ResponseBody
    public R getList(String activeId) {
        return R.successData(activeLogsService.getListByActiveId(activeId));
    }

    @GetMapping("/page")
    @ResponseBody
    public R getPageInfos(Long pageIndex, Long pageSize, String token, String teamName, String activeName, String userName, Integer status) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (user == null) {
            return R.error("登录信息不存在，请重新登录");
        }

        PageData page;
        if (user.getType() == 0) {
            page = activeLogsService.getPageAll(pageIndex, pageSize, teamName, activeName, userName, status);
        } else if (user.getType() == 1) {
            page = activeLogsService.getPageByManager(pageIndex, pageSize, user.getId(), teamName, activeName, userName, status);
        } else {
            page = activeLogsService.getPageByUser(pageIndex, pageSize, user.getId(), teamName, activeName, status);
        }
        return R.successData(page);
    }

    @GetMapping("/myStatuses")
    @ResponseBody
    public R getMyStatuses(String token) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (user == null) {
            return R.error("登录信息不存在，请重新登录");
        }

        QueryWrapper<ActiveLogs> wrapper = new QueryWrapper<ActiveLogs>();
        wrapper.eq("user_id", user.getId());
        wrapper.orderByDesc("create_time");
        List<ActiveLogs> logs = activeLogsDao.selectList(wrapper);

        Map<String, Integer> statusMap = new HashMap<String, Integer>();
        for (ActiveLogs log : logs) {
            if (!statusMap.containsKey(log.getActiveId())) {
                statusMap.put(log.getActiveId(), log.getStatus());
            }
        }
        return R.successData(statusMap);
    }

    @PostMapping("/add")
    @ResponseBody
    public R addInfo(String token, ActiveLogs activeLogs) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (user == null) {
            return R.error("登录信息不存在，请重新登录");
        }

        Activities activity = activitiesService.getOne(activeLogs.getActiveId());
        if (activity == null) {
            return R.warn("活动不存在");
        }

        Teams team = teamsService.getOne(activity.getTeamId());
        boolean isManager = false;
        boolean isMember = false;

        if (user.getType() == 1) {
            isManager = team != null && user.getId().equals(team.getManager());

            QueryWrapper<Members> memberWrapper = new QueryWrapper<Members>();
            memberWrapper.eq("team_id", activity.getTeamId());
            memberWrapper.eq("user_id", user.getId());
            isMember = membersDao.selectCount(memberWrapper) > 0;

            if (!isManager && !isMember) {
                return R.warn("社团管理员只能报名自己已加入或负责的社团活动");
            }
        } else if (user.getType() != 2) {
            return R.warn("当前角色无法提交活动报名");
        }

        if (!activeLogsService.isActive(activeLogs.getActiveId(), user.getId())) {
            return R.warn("你已经提交过该活动的报名申请");
        }
        if (activity.getMaxTotal() != null && activity.getTotal() != null && activity.getTotal() >= activity.getMaxTotal()) {
            return R.warn("活动人数已满");
        }
        if (hasDeadlinePassed(activity.getEnrollEndTime())) {
            return R.warn("活动报名已截止");
        }

        activeLogs.setId(IDUtils.makeIDByCurrent());
        activeLogs.setUserId(user.getId());
        activeLogs.setCreateTime(DateUtils.getNowDate());

        if (user.getType() == 1 && isManager) {
            activeLogs.setStatus(1);
            activeLogs.setReviewTime(DateUtils.getNowDate());
            activeLogs.setReviewRemark("社团管理员直接加入本社团活动");
        } else {
            activeLogs.setStatus(0);
            activeLogs.setReviewTime(null);
            activeLogs.setReviewRemark(null);
        }

        Log.info("新增活动报名申请：{}", activeLogs);
        activeLogsService.add(activeLogs);

        if (user.getType() == 1 && isManager) {
            return R.successMsg("已加入本社团活动");
        }
        if (user.getType() == 1 && isMember) {
            return R.successMsg("报名申请已提交，等待对方社团管理员审核");
        }
        return R.successMsg("报名申请已提交，等待社团管理员审核");
    }

    @PostMapping("/upd")
    @ResponseBody
    public R updInfo(String token, ActiveLogs activeLogs) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (user == null) {
            return R.error("登录信息不存在，请重新登录");
        }

        ActiveLogs oldLog = activeLogsService.getOne(activeLogs.getId());
        if (oldLog == null) {
            return R.warn("报名记录不存在");
        }
        Activities activity = activitiesService.getOne(oldLog.getActiveId());
        if (activity == null) {
            return R.warn("活动不存在");
        }

        if (user.getType() == 1) {
            Teams team = teamsService.getOne(activity.getTeamId());
            if (team == null || !user.getId().equals(team.getManager())) {
                return R.warn("只能审批自己社团的活动报名");
            }
        } else if (user.getType() != 0) {
            return R.warn("当前身份不能审批活动报名");
        }

        if (activeLogs.getStatus() != null && activeLogs.getStatus() == 1) {
            boolean wasApproved = oldLog.getStatus() != null && oldLog.getStatus() == 1;
            if (activity.getMaxTotal() != null && activity.getTotal() != null && activity.getTotal() >= activity.getMaxTotal() && !wasApproved) {
                return R.warn("活动人数已满，无法通过更多报名");
            }
        }

        activeLogsService.update(activeLogs);
        return R.success();
    }

    @PostMapping("/del")
    @ResponseBody
    public R delInfo(String token, String id) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (user == null) {
            return R.error("登录信息不存在，请重新登录");
        }

        ActiveLogs activeLogs = activeLogsService.getOne(id);
        if (activeLogs == null) {
            return R.warn("报名记录不存在");
        }
        Activities activity = activitiesService.getOne(activeLogs.getActiveId());
        if (activity == null) {
            return R.warn("活动不存在");
        }

        if (user.getType() == 2) {
            if (!user.getId().equals(activeLogs.getUserId())) {
                return R.warn("只能取消自己的报名");
            }
        } else if (user.getType() == 1) {
            Teams team = teamsService.getOne(activity.getTeamId());
            boolean canManage = team != null && user.getId().equals(team.getManager());
            boolean isSelf = user.getId().equals(activeLogs.getUserId());
            if (!canManage && !isSelf) {
                return R.warn("只能取消自己的报名，或管理本社团活动的报名记录");
            }
        } else if (user.getType() != 0) {
            return R.warn("当前身份不能删除报名记录");
        }

        activeLogsService.delete(activeLogs);
        return R.success();
    }

    private boolean hasDeadlinePassed(String enrollEndTime) {
        if (ObjectUtils.isEmpty(enrollEndTime)) {
            return false;
        }
        try {
            return DateUtils.parseDate(enrollEndTime, DateUtils.DATETIME_DEFAULT_FORMAT)
                    .before(DateUtils.parseDate(DateUtils.getNowDate(), DateUtils.DATETIME_DEFAULT_FORMAT));
        } catch (ParseException error) {
            return false;
        }
    }
}
