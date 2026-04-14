package com.rabbiter.association.controller;

import com.rabbiter.association.entity.Teams;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.handle.CacheHandle;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.msg.R;
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

@Controller
@RequestMapping("/teams")
public class TeamsController extends BaseController {

    protected static final Logger Log = LoggerFactory.getLogger(TeamsController.class);

    @Autowired
    private CacheHandle cacheHandle;

    @Autowired
    private UsersService usersService;

    @Autowired
    private TeamsService teamsService;

    @RequestMapping("")
    public String index() {
        return "pages/Teams";
    }

    @GetMapping("/info")
    @ResponseBody
    public R getInfo(String id) {
        Log.info("查询指定社团信息，ID：{}", id);
        return R.successData(teamsService.getOne(id));
    }

    @GetMapping("/all")
    @ResponseBody
    public R getAll() {
        Log.info("获取全部社团");
        List<Teams> list = teamsService.getAll();
        return R.successData(list);
    }

    @GetMapping("/man")
    @ResponseBody
    public R getListByManId(String manId) {
        Log.info("获取指定社团管理员相关的社团列表");
        List<Teams> list = teamsService.getListByManId(manId);
        return R.successData(list);
    }

    @GetMapping("/page")
    @ResponseBody
    public R getPageInfos(Long pageIndex, Long pageSize, String token, Teams teams) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        Log.info("分页查询社团信息，当前页码：{}，每页数量：{}，查询条件：{}", pageIndex, pageSize, teams);
        PageData page = teamsService.getPageInfo(pageIndex, pageSize, teams);
        return R.successData(page);
    }

    @PostMapping("/add")
    @ResponseBody
    public R addInfo(Teams teams) {
        teams.setId(IDUtils.makeIDByCurrent());
        teams.setCreateTime(DateUtils.getNowDate("yyyy-MM-dd"));

        Log.info("新增社团信息：{}", teams);
        int count = teamsService.addTeams(teams);
        if (count == 0) {
            return R.error("社团管理员无效，请重新选择");
        }
        return R.success();
    }

    @PostMapping("/upd")
    @ResponseBody
    public R updInfo(Teams teams) {
        Log.info("修改社团信息：{}", teams);
        int count = teamsService.updateTeams(teams);
        if (count == 0) {
            return R.error("社团管理员无效，请重新选择");
        }
        return R.success();
    }

    @PostMapping("/del")
    @ResponseBody
    public R delInfo(String token, String id) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        Teams teams = teamsService.getOne(id);
        if (ObjectUtils.isEmpty(teams)) {
            return R.warn("社团不存在");
        }
        if (user.getType() == 1 && !user.getId().equals(teams.getManager())) {
            return R.warn("只能解散自己负责的社团");
        }
        if (user.getType() != 0 && user.getType() != 1) {
            return R.warn("当前角色不能解散社团");
        }

        Log.info("删除社团信息，ID：{}", id);
        teamsService.delete(teams);
        return R.success();
    }

    @PostMapping("/assignManager")
    @ResponseBody
    public R assignManager(String token, String teamId, String managerId) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (user.getType() != 0) {
            return R.warn("仅系统管理员可分配社团管理员");
        }

        Teams teams = teamsService.getOne(teamId);
        if (ObjectUtils.isEmpty(teams)) {
            return R.warn("社团不存在");
        }
        teams.setManager(managerId);

        Integer count = teamsService.updateTeams(teams);
        if (count == 0) {
            return R.warn("指定管理员账号无效");
        }
        return R.success();
    }
}
