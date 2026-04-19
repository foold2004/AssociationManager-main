package com.rabbiter.association.controller;

import com.rabbiter.association.dao.NoticesDao;
import com.rabbiter.association.dao.TeamsDao;
import com.rabbiter.association.entity.Notices;
import com.rabbiter.association.entity.Teams;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.handle.CacheHandle;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.service.NoticesService;
import com.rabbiter.association.service.UsersService;
import com.rabbiter.association.utils.DateUtils;
import com.rabbiter.association.utils.IDUtils;
import com.rabbiter.association.utils.StringUtils;
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
@RequestMapping("/notices")
public class NoticesController {

    protected static final Logger Log = LoggerFactory.getLogger(NoticesController.class);

    @Autowired
    private CacheHandle cacheHandle;

    @Autowired
    private NoticesService noticesService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private TeamsDao teamsDao;

    @Autowired
    private NoticesDao noticesDao;

    @GetMapping("/page")
    @ResponseBody
    public R getPageInfos(Long pageIndex, Long pageSize, String token, String title, String teamName, String teamId, Integer systemOnly) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        PageData page;
        if (user.getType() == 0) {
            page = noticesService.getPageAll(pageIndex, pageSize, title, teamName, teamId, systemOnly);
        } else if (user.getType() == 1) {
            page = noticesService.getPageById(pageIndex, pageSize, user.getId(), title, teamName, teamId, systemOnly);
        } else {
            page = noticesService.getPageByMemberId(pageIndex, pageSize, user.getId(), title, teamName, teamId, systemOnly);
        }

        return R.successData(page);
    }

    @PostMapping("/add")
    @ResponseBody
    public R addInfo(String token, Notices notices) {
        Users user = null;
        if (StringUtils.isNotNullOrEmpty(token)) {
            user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        }
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        notices.setId(IDUtils.makeIDByCurrent());
        notices.setCreateTime(DateUtils.getNowDate("yyyy-MM-dd"));
        if (StringUtils.isNullOrEmpty(notices.getTeamId())) {
            notices.setTeamId(null);
        }
        if (user.getType() == 1) {
            if (StringUtils.isNullOrEmpty(notices.getTeamId())) {
                return R.warn("社团管理员发布通知时必须选择所属社团");
            }
            Long count = noticesDao.countManagedTeam(user.getId(), notices.getTeamId());
            if (count == null || count <= 0) {
                return R.warn("只能向自己负责的社团发布通知");
            }
        }
        if (notices.getIsTop() == null) {
            notices.setIsTop(0);
        }
        if (notices.getIsTop() == 1 && user.getType() != 0) {
            return R.warn("只有系统管理员可以发布置顶通知");
        }

        Log.info("新增通知：{}", notices);
        noticesService.add(notices);
        return R.success();
    }

    @PostMapping("/upd")
    @ResponseBody
    public R updInfo(String token, Notices notices) {
        Users user = null;
        if (StringUtils.isNotNullOrEmpty(token)) {
            user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        }
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        Notices old = noticesService.getOne(notices.getId());
        if (ObjectUtils.isEmpty(old)) {
            return R.warn("通知记录不存在");
        }
        if (user.getType() == 1) {
            if (StringUtils.isNullOrEmpty(old.getTeamId())) {
                return R.warn("社团管理员不能编辑系统通知");
            }
            Teams team = teamsDao.selectById(old.getTeamId());
            if (ObjectUtils.isEmpty(team) || !user.getId().equals(team.getManager())) {
                return R.warn("只能编辑自己社团的通知");
            }
        }
        if (notices.getIsTop() != null && notices.getIsTop() == 1 && user.getType() != 0) {
            return R.warn("只有系统管理员可以设置置顶");
        }

        Log.info("修改通知：{}", notices);
        noticesService.update(notices);
        return R.success();
    }

    @PostMapping("/top")
    @ResponseBody
    public R topInfo(String token, String id, Integer isTop) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (user.getType() != 0) {
            return R.warn("只有系统管理员可以置顶通知");
        }

        Notices notices = noticesService.getOne(id);
        if (ObjectUtils.isEmpty(notices)) {
            return R.warn("通知记录不存在");
        }

        notices.setIsTop((isTop != null && isTop == 1) ? 1 : 0);
        noticesService.update(notices);
        return R.success();
    }

    @PostMapping("/del")
    @ResponseBody
    public R delInfo(String token, String id) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        Notices notices = noticesService.getOne(id);
        if (ObjectUtils.isEmpty(notices)) {
            return R.warn("通知记录不存在");
        }
        if (user.getType() == 2) {
            return R.warn("学生无权删除通知");
        }
        if (user.getType() == 1) {
            if (StringUtils.isNullOrEmpty(notices.getTeamId())) {
                return R.warn("社团管理员不能删除系统通知");
            }
            Teams team = teamsDao.selectById(notices.getTeamId());
            if (ObjectUtils.isEmpty(team) || !user.getId().equals(team.getManager())) {
                return R.warn("只能删除自己社团的通知");
            }
        }
        noticesService.delete(notices);
        return R.success();
    }
}
