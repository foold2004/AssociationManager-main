package com.rabbiter.association.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbiter.association.dao.MembersDao;
import com.rabbiter.association.dao.PayExpensesDao;
import com.rabbiter.association.dao.PayLogsDao;
import com.rabbiter.association.entity.Members;
import com.rabbiter.association.entity.PayLogs;
import com.rabbiter.association.entity.Teams;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.handle.CacheHandle;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.service.PayLogsService;
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

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/payLogs")
public class PayLogsController extends BaseController {

    protected static final Logger Log = LoggerFactory.getLogger(PayLogsController.class);

    @Autowired
    private CacheHandle cacheHandle;

    @Autowired
    private UsersService usersService;

    @Autowired
    private PayLogsService payLogsService;

    @Autowired
    private PayLogsDao payLogsDao;

    @Autowired
    private TeamsService teamsService;

    @Autowired
    private MembersDao membersDao;

    @Autowired
    private PayExpensesDao payExpensesDao;

    @RequestMapping("")
    public String index() {
        return "pages/PayLogs";
    }

    @GetMapping("/info")
    @ResponseBody
    public R getInfo(String id) {
        Log.info("查询指定缴费记录，ID：{}", id);
        return R.successData(payLogsService.getOne(id));
    }

    @GetMapping("/page")
    @ResponseBody
    public R getPageInfos(Long pageIndex, Long pageSize, String token, String teamName, String userName) {
        Users user = getLoginUser(token);
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        PageData page;
        if (user.getType() == 0) {
            page = payLogsService.getPageInfo(pageIndex, pageSize, null, teamName, userName);
        } else if (user.getType() == 1) {
            page = payLogsService.getManPageInfo(pageIndex, pageSize, user.getId(), teamName, userName);
        } else {
            page = payLogsService.getPageInfo(pageIndex, pageSize, user.getId(), teamName, null);
        }

        return R.successData(page);
    }

    @GetMapping("/summary")
    @ResponseBody
    public R getSummary(String token, String teamId) {
        Users user = getLoginUser(token);
        if (ObjectUtils.isEmpty(user)) {
            return R.error("鐧诲綍淇℃伅涓嶅瓨鍦紝璇烽噸鏂扮櫥褰?");
        }

        if (!ObjectUtils.isEmpty(teamId)) {
            Teams team = teamsService.getOne(teamId);
            if (ObjectUtils.isEmpty(team)) {
                return R.warn("绀惧洟淇℃伅涓嶅瓨鍦?");
            }
            if (user.getType() == 1 && !user.getId().equals(team.getManager())) {
                return R.warn("浣犲彧鑳芥煡鐪嬭嚜宸辫礋璐ｇぞ鍥㈢殑璐圭敤鎯呭喌");
            }
            if (user.getType() == 2) {
                QueryWrapper<Members> memberQuery = new QueryWrapper<Members>();
                memberQuery.eq("team_id", teamId).eq("user_id", user.getId());
                if (membersDao.selectCount(memberQuery) <= 0) {
                    return R.warn("浣犲彧鑳芥煡鐪嬭嚜宸卞弬涓庣ぞ鍥㈢殑璐圭敤鎯呭喌");
                }
            }
        }

        Double income;
        Double expense;
        if (user.getType() == 0) {
            income = payLogsDao.sumTotalAll(teamId);
            expense = payExpensesDao.sumTotalAll(teamId);
        } else if (user.getType() == 1) {
            income = payLogsDao.sumTotalByManager(user.getId(), teamId);
            expense = payExpensesDao.sumTotalByManager(user.getId(), teamId);
        } else {
            income = payLogsDao.sumTotalByMember(user.getId(), teamId);
            expense = payExpensesDao.sumTotalByMember(user.getId(), teamId);
        }

        double incomeVal = income == null ? 0 : income;
        double expenseVal = expense == null ? 0 : expense;

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("income", incomeVal);
        result.put("expense", expenseVal);
        result.put("balance", incomeVal - expenseVal);
        return R.successData(result);
    }

    @PostMapping("/add")
    @ResponseBody
    public R addInfo(PayLogs payLogs, String token) {
        Users user = getLoginUser(token);
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (user.getType() == 2) {
            return R.warn("学生仅可查看缴费记录，不能新增");
        }

        R validResult = validatePayLogPermission(user, payLogs.getTeamId(), payLogs.getUserId());
        if (validResult != null) {
            return validResult;
        }
        if (ObjectUtils.isEmpty(payLogs.getTotal()) || payLogs.getTotal() <= 0) {
            return R.warn("请输入正确的缴费金额");
        }

        payLogs.setId(IDUtils.makeIDByCurrent());
        payLogs.setCreateTime(DateUtils.getNowDate());

        Log.info("新增缴费记录：{}", payLogs);
        payLogsService.add(payLogs);
        return R.success();
    }

    @PostMapping("/upd")
    @ResponseBody
    public R updInfo(PayLogs payLogs, String token) {
        Users user = getLoginUser(token);
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (user.getType() == 2) {
            return R.warn("学生仅可查看缴费记录，不能修改");
        }

        PayLogs oldPayLog = payLogsService.getOne(payLogs.getId());
        if (ObjectUtils.isEmpty(oldPayLog)) {
            return R.warn("缴费记录不存在");
        }

        R permissionResult = validateEditPermission(user, oldPayLog.getTeamId());
        if (permissionResult != null) {
            return permissionResult;
        }

        R validResult = validatePayLogPermission(user, payLogs.getTeamId(), payLogs.getUserId());
        if (validResult != null) {
            return validResult;
        }
        if (ObjectUtils.isEmpty(payLogs.getTotal()) || payLogs.getTotal() <= 0) {
            return R.warn("请输入正确的缴费金额");
        }

        if (ObjectUtils.isEmpty(payLogs.getCreateTime())) {
            payLogs.setCreateTime(oldPayLog.getCreateTime());
        }

        Log.info("修改缴费记录：{}", payLogs);
        payLogsService.update(payLogs);
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
            return R.warn("学生仅可查看缴费记录，不能删除");
        }

        PayLogs payLogs = payLogsService.getOne(id);
        if (ObjectUtils.isEmpty(payLogs)) {
            return R.warn("缴费记录不存在");
        }

        R permissionResult = validateEditPermission(user, payLogs.getTeamId());
        if (permissionResult != null) {
            return permissionResult;
        }

        Log.info("删除缴费记录，ID：{}", id);
        payLogsService.delete(payLogs);
        return R.success();
    }

    private Users getLoginUser(String token) {
        if (ObjectUtils.isEmpty(token)) {
            return null;
        }
        return usersService.getOne(cacheHandle.getUserInfoCache(token));
    }

    private R validateEditPermission(Users operator, String teamId) {
        Teams team = teamsService.getOne(teamId);
        if (ObjectUtils.isEmpty(team)) {
            return R.warn("社团信息不存在");
        }
        if (operator.getType() == 1 && !operator.getId().equals(team.getManager())) {
            return R.warn("你只能维护自己负责社团的缴费记录");
        }
        return null;
    }

    private R validatePayLogPermission(Users operator, String teamId, String userId) {
        if (ObjectUtils.isEmpty(teamId)) {
            return R.warn("请选择缴费所属社团");
        }
        if (ObjectUtils.isEmpty(userId)) {
            return R.warn("请选择缴费成员");
        }

        Teams team = teamsService.getOne(teamId);
        if (ObjectUtils.isEmpty(team)) {
            return R.warn("社团信息不存在");
        }
        if (operator.getType() == 1 && !operator.getId().equals(team.getManager())) {
            return R.warn("你只能维护自己负责社团的缴费记录");
        }

        QueryWrapper<Members> wrapper = new QueryWrapper<Members>();
        wrapper.eq("team_id", teamId).eq("user_id", userId);
        Integer memberCount = membersDao.selectCount(wrapper);
        if (memberCount == null || memberCount <= 0) {
            return R.warn("所选成员不在当前社团中");
        }

        return null;
    }
}
