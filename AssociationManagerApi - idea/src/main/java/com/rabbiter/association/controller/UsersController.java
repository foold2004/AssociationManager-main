package com.rabbiter.association.controller;

import com.rabbiter.association.entity.Users;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.service.UsersService;
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

@Controller
@RequestMapping("/users")
public class UsersController {

    protected static final Logger Log = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private UsersService usersService;

    @GetMapping("/page")
    @ResponseBody
    public R getPageInfos(Long pageIndex, Long pageSize, Users users) {
        Log.info("分页查询系统用户，pageIndex={}, pageSize={}, query={}", pageIndex, pageSize, users);
        PageData page = usersService.getPageInfo(pageIndex, pageSize, users);
        return R.successData(page);
    }

    @GetMapping("/managers")
    @ResponseBody
    public R getManagers() {
        Users query = new Users();
        query.setType(1);
        query.setStatus(1);

        PageData page = usersService.getPageInfo(1L, 200L, query);
        return R.successData(page.getData());
    }

    @PostMapping("/add")
    @ResponseBody
    public R addInfo(Users users) {
        if (usersService.getUserByUserName(users.getUserName()) != null) {
            return R.warn("用户账号已存在，请重新输入");
        }

        users.setId(IDUtils.makeIDByCurrent());
        users.setCreateTime(DateUtils.getNowDate());
        if (users.getStatus() == null) {
            users.setStatus(1);
        }

        Log.info("新增系统用户：{}", users);
        usersService.add(users);
        return R.success();
    }

    @PostMapping("/upd")
    @ResponseBody
    public R updInfo(Users users) {
        Log.info("修改系统用户：{}", users);
        usersService.update(users);
        return R.success();
    }

    @PostMapping("/del")
    @ResponseBody
    public R delInfo(String id) {
        if (!usersService.isRemove(id)) {
            return R.warn("用户存在关联社团，无法移除");
        }

        Log.info("删除系统用户，ID={}", id);
        Users users = usersService.getOne(id);
        usersService.delete(users);
        return R.success();
    }
}
