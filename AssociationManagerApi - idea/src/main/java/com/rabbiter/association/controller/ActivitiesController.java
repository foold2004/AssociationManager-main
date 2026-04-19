package com.rabbiter.association.controller;

import com.rabbiter.association.entity.Activities;
import com.rabbiter.association.entity.Teams;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.handle.CacheHandle;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.service.ActivitiesService;
import com.rabbiter.association.service.TeamsService;
import com.rabbiter.association.service.UsersService;
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

@Controller
@RequestMapping("/activities")
public class ActivitiesController {

    protected static final Logger Log = LoggerFactory.getLogger(ActivitiesController.class);

    @Autowired
    private CacheHandle cacheHandle;

    @Autowired
    private UsersService usersService;

    @Autowired
    private ActivitiesService activitiesService;

    @Autowired
    private TeamsService teamsService;

    @GetMapping("/page")
    @ResponseBody
    public R getPageInfos(Long pageIndex, Long pageSize, String token, String teamName, String activeName) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        PageData page;
        if (user.getType() == 0) {
            page = activitiesService.getPageAll(pageIndex, pageSize, activeName, teamName);
        } else {
            page = activitiesService.getPageByUserId(pageIndex, pageSize, user.getId(), activeName, teamName);
        }
        return R.successData(page);
    }

    @PostMapping("/add")
    @ResponseBody
    public R addInfo(String token, Activities activities) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (user.getType() != 1) {
            return R.warn("只有社团管理员可以发布活动");
        }
        if (ObjectUtils.isEmpty(activities.getTeamId()) || ObjectUtils.isEmpty(activities.getName())) {
            return R.warn("请填写完整的活动信息");
        }
        Teams team = teamsService.getOne(activities.getTeamId());
        if (ObjectUtils.isEmpty(team) || !user.getId().equals(team.getManager())) {
            return R.warn("只能为自己负责的社团创建活动");
        }
        if (ObjectUtils.isEmpty(activities.getEnrollEndTime())) {
            return R.warn("请设置报名截止时间");
        }
        if (activities.getMaxTotal() == null || activities.getMaxTotal() <= 0) {
            return R.warn("报名人数上限必须大于 0");
        }

        activities.setId(IDUtils.makeIDByCurrent());
        activities.setTotal(1);
        Log.info("新增活动信息：{}", activities);
        activitiesService.add(activities);
        return R.success();
    }

    @PostMapping("/upd")
    @ResponseBody
    public R updInfo(String token, Activities activities) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        Activities old = activitiesService.getOne(activities.getId());
        if (ObjectUtils.isEmpty(old)) {
            return R.warn("活动不存在");
        }
        if (user.getType() != 0) {
            Teams team = teamsService.getOne(old.getTeamId());
            if (user.getType() != 1 || ObjectUtils.isEmpty(team) || !user.getId().equals(team.getManager())) {
                return R.warn("当前角色不能修改该活动");
            }
        }
        if (activities.getMaxTotal() == null || activities.getMaxTotal() <= 0) {
            return R.warn("报名人数上限必须大于 0");
        }

        if (activities.getTotal() == null || activities.getTotal() < 1) {
            activities.setTotal(old.getTotal());
        }

        Log.info("修改活动信息：{}", activities);
        activitiesService.update(activities);
        return R.success();
    }

    @PostMapping("/del")
    @ResponseBody
    public R delInfo(String token, String id) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        Activities activities = activitiesService.getOne(id);
        if (ObjectUtils.isEmpty(activities)) {
            return R.warn("活动不存在");
        }
        if (user.getType() == 1) {
            Teams team = teamsService.getOne(activities.getTeamId());
            if (ObjectUtils.isEmpty(team) || !user.getId().equals(team.getManager())) {
                return R.warn("只能删除自己社团的活动");
            }
        } else if (user.getType() != 0) {
            return R.warn("当前角色不能删除活动");
        }

        Log.info("删除活动信息，ID={}", id);
        activitiesService.delete(activities);
        return R.success();
    }
}
