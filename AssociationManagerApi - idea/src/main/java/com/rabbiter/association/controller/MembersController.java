package com.rabbiter.association.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbiter.association.dao.MembersDao;
import com.rabbiter.association.entity.Members;
import com.rabbiter.association.entity.Teams;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.handle.CacheHandle;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.service.MembersService;
import com.rabbiter.association.service.TeamsService;
import com.rabbiter.association.service.UsersService;
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
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/members")
public class MembersController {

    protected static final Logger Log = LoggerFactory.getLogger(MembersController.class);

    @Autowired
    private CacheHandle cacheHandle;

    @Autowired
    private UsersService usersService;

    @Autowired
    private MembersService membersService;

    @Autowired
    private MembersDao membersDao;

    @Autowired
    private TeamsService teamsService;

    @GetMapping("/page")
    @ResponseBody
    public R getPageInfos(Long pageIndex, Long pageSize, String token, String teamName, String userName) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        if (user.getType() == 0) {
            PageData page = membersService.getPageAll(pageIndex, pageSize, teamName, userName);
            return R.successData(page);
        }

        PageData page = membersService.getPageByManId(pageIndex, pageSize, user.getId(), teamName, userName);
        return R.successData(page);
    }

    @GetMapping("/myTeamIds")
    @ResponseBody
    public R getMyTeamIds(String token) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        QueryWrapper<Members> wrapper = new QueryWrapper<Members>();
        wrapper.eq("user_id", user.getId());

        List<String> teamIds = membersDao.selectList(wrapper)
                .stream()
                .map(Members::getTeamId)
                .collect(Collectors.toList());

        return R.successData(teamIds);
    }

    @GetMapping("/options")
    @ResponseBody
    public R getMemberOptions(String token, String teamId) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (ObjectUtils.isEmpty(teamId)) {
            return R.warn("请选择社团");
        }

        Teams team = teamsService.getOne(teamId);
        if (ObjectUtils.isEmpty(team)) {
            return R.warn("社团信息不存在");
        }

        if (user.getType() == 2) {
            return R.warn("当前身份不能查看成员选项");
        }
        if (user.getType() == 1 && !user.getId().equals(team.getManager())) {
            return R.warn("你只能查看自己负责社团的成员");
        }

        List<Map<String, Object>> options = membersDao.qryMemberOptionsByTeamId(teamId);
        return R.successData(options);
    }

    @PostMapping("/del")
    @ResponseBody
    public R delInfo(String id) {
        Members members = membersService.getOne(id);
        if (members == null) {
            return R.warn("成员记录不存在");
        }

        if (membersService.isManager(members.getTeamId(), members.getUserId())) {
            return R.warn("社团管理员无法移除");
        }

        Log.info("删除成员信息，ID={}", id);
        membersService.delete(members);
        return R.success();
    }
}
