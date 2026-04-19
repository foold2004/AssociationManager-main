package com.rabbiter.association.controller;

import com.rabbiter.association.entity.PayExpenses;
import com.rabbiter.association.entity.Teams;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.handle.CacheHandle;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.service.PayExpensesService;
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

@Controller
@RequestMapping("/payExpenses")
public class PayExpensesController {

    protected static final Logger Log = LoggerFactory.getLogger(PayExpensesController.class);

    @Autowired
    private CacheHandle cacheHandle;

    @Autowired
    private UsersService usersService;

    @Autowired
    private PayExpensesService payExpensesService;

    @Autowired
    private TeamsService teamsService;

    @GetMapping("/page")
    @ResponseBody
    public R getPageInfos(Long pageIndex, Long pageSize, String token, String teamId, String title) {
        Users user = getLoginUser(token);
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        PageData page;
        if (user.getType() == 0) {
            page = payExpensesService.getPageAll(pageIndex, pageSize, teamId, title);
        } else if (user.getType() == 1) {
            page = payExpensesService.getPageByManager(pageIndex, pageSize, user.getId(), teamId, title);
        } else {
            page = payExpensesService.getPageByMember(pageIndex, pageSize, user.getId(), teamId, title);
        }

        return R.successData(page);
    }

    @PostMapping("/add")
    @ResponseBody
    public R addInfo(PayExpenses payExpenses, String token) {
        Users user = getLoginUser(token);
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (user.getType() == 2) {
            return R.warn("学生仅可查看消费明细，不能新增");
        }

        R validResult = validateExpensePermission(user, payExpenses.getTeamId());
        if (validResult != null) {
            return validResult;
        }
        if (ObjectUtils.isEmpty(payExpenses.getTotal()) || payExpenses.getTotal() <= 0) {
            return R.warn("请输入正确的支出金额");
        }
        if (ObjectUtils.isEmpty(payExpenses.getTitle())) {
            return R.warn("请输入支出项目标题");
        }

        payExpenses.setId(IDUtils.makeIDByCurrent());
        payExpenses.setCreateTime(DateUtils.getNowDate());

        Log.info("新增消费明细：{}", payExpenses);
        payExpensesService.add(payExpenses);
        return R.success();
    }

    @PostMapping("/upd")
    @ResponseBody
    public R updInfo(PayExpenses payExpenses, String token) {
        Users user = getLoginUser(token);
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (user.getType() == 2) {
            return R.warn("学生仅可查看消费明细，不能修改");
        }

        PayExpenses old = payExpensesService.getOne(payExpenses.getId());
        if (ObjectUtils.isEmpty(old)) {
            return R.warn("消费明细不存在");
        }

        R validResult = validateExpensePermission(user, old.getTeamId());
        if (validResult != null) {
            return validResult;
        }
        if (ObjectUtils.isEmpty(payExpenses.getTotal()) || payExpenses.getTotal() <= 0) {
            return R.warn("请输入正确的支出金额");
        }
        if (ObjectUtils.isEmpty(payExpenses.getCreateTime())) {
            payExpenses.setCreateTime(old.getCreateTime());
        }

        Log.info("修改消费明细：{}", payExpenses);
        payExpensesService.update(payExpenses);
        return R.success();
    }

    @PostMapping("/del")
    @ResponseBody
    public R delInfo(String id, String token) {
        Users user = getLoginUser(token);
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (user.getType() == 2) {
            return R.warn("学生仅可查看消费明细，不能删除");
        }

        PayExpenses payExpenses = payExpensesService.getOne(id);
        if (ObjectUtils.isEmpty(payExpenses)) {
            return R.warn("消费明细不存在");
        }

        R validResult = validateExpensePermission(user, payExpenses.getTeamId());
        if (validResult != null) {
            return validResult;
        }

        Log.info("删除消费明细，ID={}", id);
        payExpensesService.delete(payExpenses);
        return R.success();
    }

    private Users getLoginUser(String token) {
        if (ObjectUtils.isEmpty(token)) {
            return null;
        }
        return usersService.getOne(cacheHandle.getUserInfoCache(token));
    }

    private R validateExpensePermission(Users operator, String teamId) {
        if (ObjectUtils.isEmpty(teamId)) {
            return R.warn("请选择所属社团");
        }

        Teams team = teamsService.getOne(teamId);
        if (ObjectUtils.isEmpty(team)) {
            return R.warn("社团不存在");
        }
        if (operator.getType() == 1 && !operator.getId().equals(team.getManager())) {
            return R.warn("你只能维护自己负责社团的消费明细");
        }
        return null;
    }
}
