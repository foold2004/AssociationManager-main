package com.rabbiter.association.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbiter.association.dao.ApplyLogsDao;
import com.rabbiter.association.dao.MembersDao;
import com.rabbiter.association.entity.ApplyLogs;
import com.rabbiter.association.entity.Teams;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.handle.CacheHandle;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.service.ApplyLogsService;
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

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/applyLogs")
public class ApplyLogsController {

    protected static final Logger Log = LoggerFactory.getLogger(ApplyLogsController.class);

    @Autowired
    private CacheHandle cacheHandle;

    @Autowired
    private UsersService usersService;

    @Autowired
    private ApplyLogsService applyLogsService;

    @Autowired
    private TeamsService teamsService;

    @Autowired
    private MembersDao membersDao;

    @Autowired
    private ApplyLogsDao applyLogsDao;

    @GetMapping("/page")
    @ResponseBody
    public R getPageInfos(Long pageIndex, Long pageSize, String token, String teamName, String userName) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        if (user.getType() == 0) {
            PageData page = applyLogsService.getPageInfo(pageIndex, pageSize, null, teamName, userName);
            return R.successData(page);
        }

        if (user.getType() == 1) {
            PageData page = applyLogsService.getManPageInfo(pageIndex, pageSize, user.getId(), teamName, userName);
            return R.successData(page);
        }

        PageData page = applyLogsService.getPageInfo(pageIndex, pageSize, user.getId(), teamName, null);
        return R.successData(page);
    }

    @GetMapping("/myPendingTeamIds")
    @ResponseBody
    public R getMyPendingTeamIds(String token) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        QueryWrapper<ApplyLogs> wrapper = new QueryWrapper<ApplyLogs>();
        wrapper.eq("user_id", user.getId());
        wrapper.eq("status", 0);

        List<String> teamIds = applyLogsDao.selectList(wrapper)
                .stream()
                .map(ApplyLogs::getTeamId)
                .collect(Collectors.toList());

        return R.successData(teamIds);
    }

    @PostMapping("/add")
    @ResponseBody
    public R addInfo(String token, ApplyLogs applyLogs) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        QueryWrapper<com.rabbiter.association.entity.Members> memberWrapper =
                new QueryWrapper<com.rabbiter.association.entity.Members>();
        memberWrapper.eq("user_id", user.getId());
        memberWrapper.eq("team_id", applyLogs.getTeamId());
        if (membersDao.selectCount(memberWrapper) > 0) {
            return R.warn("你已经加入该社团，无需重复申请");
        }

        if (!applyLogsService.isApply(user.getId(), applyLogs.getTeamId())) {
            return R.warn("申请审核中，请耐心等待");
        }

        applyLogs.setId(IDUtils.makeIDByCurrent());
        applyLogs.setUserId(user.getId());
        applyLogs.setCreateTime(DateUtils.getNowDate());

        Log.info("添加申请记录：{}", applyLogs);
        applyLogsService.add(applyLogs);
        return R.success();
    }

    @PostMapping("/upd")
    @ResponseBody
    public R updInfo(String token, ApplyLogs applyLogs) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        ApplyLogs existing = applyLogsService.getOne(applyLogs.getId());
        if (ObjectUtils.isEmpty(existing)) {
            return R.warn("申请记录不存在");
        }

        if (user.getType() == 1) {
            Teams team = teamsService.getOne(existing.getTeamId());
            if (ObjectUtils.isEmpty(team) || !user.getId().equals(team.getManager())) {
                return R.warn("只能审批自己负责社团的入团申请");
            }
        } else if (user.getType() != 0) {
            return R.warn("当前角色不能审批入团申请");
        }

        Log.info("修改申请记录：{}", applyLogs);
        applyLogsService.update(applyLogs);
        return R.success();
    }
}
