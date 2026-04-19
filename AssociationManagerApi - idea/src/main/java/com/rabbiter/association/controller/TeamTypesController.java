package com.rabbiter.association.controller;

import com.rabbiter.association.entity.TeamTypes;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.service.TeamTypesService;
import com.rabbiter.association.utils.DateUtils;
import com.rabbiter.association.utils.IDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/teamTypes")
public class TeamTypesController {

    protected static final Logger Log = LoggerFactory.getLogger(TeamTypesController.class);

    @Autowired
    private TeamTypesService teamTypesService;

    @GetMapping("/all")
    @ResponseBody
    public R getAll() {
        Log.info("查看全部社团类型");
        List<TeamTypes> list = teamTypesService.getAll();
        return R.successData(list);
    }

    @GetMapping("/page")
    @ResponseBody
    public R getPageInfos(Long pageIndex, Long pageSize, TeamTypes teamTypes) {
        Log.info("分页查询社团类型，pageIndex={}, pageSize={}, query={}", pageIndex, pageSize, teamTypes);
        PageData page = teamTypesService.getPageInfo(pageIndex, pageSize, teamTypes);
        return R.successData(page);
    }

    @PostMapping("/add")
    @ResponseBody
    public R addInfo(TeamTypes teamTypes) {
        teamTypes.setId(IDUtils.makeIDByCurrent());
        teamTypes.setCreateTime(DateUtils.getNowDate());

        Log.info("新增社团类型：{}", teamTypes);
        teamTypesService.add(teamTypes);
        return R.success();
    }

    @PostMapping("/upd")
    @ResponseBody
    public R updInfo(TeamTypes teamTypes) {
        Log.info("修改社团类型：{}", teamTypes);
        teamTypesService.update(teamTypes);
        return R.success();
    }

    @PostMapping("/del")
    @ResponseBody
    public R delInfo(String id) {
        if (!teamTypesService.isRemove(id)) {
            return R.warn("存在关联社团，无法移除");
        }

        Log.info("删除社团类型，ID={}", id);
        TeamTypes teamTypes = teamTypesService.getOne(id);
        teamTypesService.delete(teamTypes);
        return R.success();
    }
}
