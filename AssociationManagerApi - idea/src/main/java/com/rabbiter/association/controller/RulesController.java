package com.rabbiter.association.controller;

import com.rabbiter.association.entity.Rules;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.handle.CacheHandle;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.service.RulesService;
import com.rabbiter.association.service.UsersService;
import com.rabbiter.association.utils.DateUtils;
import com.rabbiter.association.utils.IDUtils;
import com.rabbiter.association.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rules")
public class RulesController {

    @Autowired
    private CacheHandle cacheHandle;

    @Autowired
    private UsersService usersService;

    @Autowired
    private RulesService rulesService;

    @GetMapping("/page")
    @ResponseBody
    public R getPage(Long pageIndex, Long pageSize, String token, String title, String teamName) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        PageData page = rulesService.getPageInfo(pageIndex, pageSize, user.getType(), user.getId(), title, teamName);
        return R.successData(page);
    }

    @PostMapping("/add")
    @ResponseBody
    public R add(String token, Rules rules) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (user.getType() == 2) {
            return R.warn("学生用户无发布规则权限");
        }

        if (user.getType() == 1 && StringUtils.isNullOrEmpty(rules.getTeamId())) {
            return R.warn("社团管理员发布规则需指定社团");
        }

        rules.setId(IDUtils.makeIDByCurrent());
        rules.setUserId(user.getId());
        rules.setCreateTime(DateUtils.getNowDate());
        rules.setUpdateTime(DateUtils.getNowDate());
        rulesService.add(rules);
        return R.success();
    }

    @PostMapping("/upd")
    @ResponseBody
    public R upd(String token, Rules rules) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (user.getType() == 2) {
            return R.warn("学生用户无编辑规则权限");
        }

        Rules old = rulesService.getOne(rules.getId());
        if (ObjectUtils.isEmpty(old)) {
            return R.warn("规则记录不存在");
        }
        if (user.getType() == 1 && !user.getId().equals(old.getUserId())) {
            return R.warn("只能编辑自己发布的规则");
        }

        rules.setUpdateTime(DateUtils.getNowDate());
        rulesService.update(rules);
        return R.success();
    }

    @PostMapping("/del")
    @ResponseBody
    public R del(String token, String id) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (user.getType() == 2) {
            return R.warn("学生用户无删除规则权限");
        }

        Rules rules = rulesService.getOne(id);
        if (ObjectUtils.isEmpty(rules)) {
            return R.warn("规则记录不存在");
        }
        if (user.getType() == 1 && !user.getId().equals(rules.getUserId())) {
            return R.warn("只能删除自己发布的规则");
        }

        rulesService.delete(rules);
        return R.success();
    }
}
