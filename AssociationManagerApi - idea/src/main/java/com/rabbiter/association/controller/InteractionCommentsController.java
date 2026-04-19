package com.rabbiter.association.controller;

import com.rabbiter.association.entity.InteractionComments;
import com.rabbiter.association.entity.Interactions;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.handle.CacheHandle;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.service.InteractionCommentsService;
import com.rabbiter.association.service.InteractionsService;
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
@RequestMapping("/interactionComments")
public class InteractionCommentsController {

    @Autowired
    private CacheHandle cacheHandle;

    @Autowired
    private UsersService usersService;

    @Autowired
    private InteractionsService interactionsService;

    @Autowired
    private InteractionCommentsService interactionCommentsService;

    @GetMapping("/list")
    @ResponseBody
    public R getList(String token, String interactionId) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (StringUtils.isNullOrEmpty(interactionId)) {
            return R.warn("互动主题不能为空");
        }
        return R.successData(interactionCommentsService.getListByInteractionId(interactionId));
    }

    @PostMapping("/add")
    @ResponseBody
    public R add(String token, InteractionComments interactionComments) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (StringUtils.isNullOrEmpty(interactionComments.getInteractionId())) {
            return R.warn("互动主题不能为空");
        }
        if (StringUtils.isNullOrEmpty(interactionComments.getContent())) {
            return R.warn("评论内容不能为空");
        }

        Interactions interactions = interactionsService.getOne(interactionComments.getInteractionId());
        if (ObjectUtils.isEmpty(interactions)) {
            return R.warn("互动主题不存在");
        }

        if (!StringUtils.isNullOrEmpty(interactionComments.getParentId())) {
            InteractionComments parent = interactionCommentsService.getOne(interactionComments.getParentId());
            if (ObjectUtils.isEmpty(parent) || !interactionComments.getInteractionId().equals(parent.getInteractionId())) {
                return R.warn("回复目标不存在或不属于当前主题");
            }
        } else {
            interactionComments.setParentId(null);
        }

        interactionComments.setId(IDUtils.makeIDByCurrent());
        interactionComments.setUserId(user.getId());
        interactionComments.setCreateTime(DateUtils.getNowDate());
        interactionCommentsService.add(interactionComments);
        return R.success();
    }

    @PostMapping("/del")
    @ResponseBody
    public R del(String token, String id) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        InteractionComments interactionComments = interactionCommentsService.getOne(id);
        if (ObjectUtils.isEmpty(interactionComments)) {
            return R.warn("评论不存在");
        }
        if (user.getType() != 0 && !user.getId().equals(interactionComments.getUserId())) {
            return R.warn("只能删除自己发布的评论");
        }

        interactionCommentsService.delete(interactionComments);
        return R.success();
    }
}
