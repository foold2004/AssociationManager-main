package com.rabbiter.association.controller;

import com.rabbiter.association.entity.Interactions;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.handle.CacheHandle;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.service.InteractionsService;
import com.rabbiter.association.service.UsersService;
import com.rabbiter.association.utils.DateUtils;
import com.rabbiter.association.utils.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/interactions")
public class InteractionsController extends BaseController {

    @Autowired
    private CacheHandle cacheHandle;

    @Autowired
    private UsersService usersService;

    @Autowired
    private InteractionsService interactionsService;

    @GetMapping("/page")
    @ResponseBody
    public R getPage(Long pageIndex, Long pageSize, String token, String keyword, String teamName) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        PageData page = interactionsService.getPageInfo(pageIndex, pageSize, user.getType(), user.getId(), keyword, teamName);
        return R.successData(page);
    }

    @PostMapping("/add")
    @ResponseBody
    public R add(String token, Interactions interactions) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        interactions.setId(IDUtils.makeIDByCurrent());
        interactions.setUserId(user.getId());
        interactions.setCreateTime(DateUtils.getNowDate());
        interactionsService.add(interactions);
        return R.success();
    }

    @PostMapping("/del")
    @ResponseBody
    public R del(String token, String id) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        Interactions interactions = interactionsService.getOne(id);
        if (ObjectUtils.isEmpty(interactions)) {
            return R.warn("互动记录不存在");
        }

        if (user.getType() != 0 && !user.getId().equals(interactions.getUserId())) {
            return R.warn("只能删除自己发布的互动信息");
        }

        interactionsService.delete(interactions);
        return R.success();
    }
}
