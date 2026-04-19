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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/teams")
public class TeamsController {

    protected static final Logger Log = LoggerFactory.getLogger(TeamsController.class);

    @Autowired
    private CacheHandle cacheHandle;

    @Autowired
    private UsersService usersService;

    @Autowired
    private TeamsService teamsService;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

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
        Log.info("获取指定社团管理员的社团列表，managerId={}", manId);
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

        Log.info("分页查询社团信息，pageIndex={}, pageSize={}, query={}", pageIndex, pageSize, teams);
        PageData page = teamsService.getPageInfo(pageIndex, pageSize, teams);
        return R.successData(page);
    }

    @PostMapping("/add")
    @ResponseBody
    public R addInfo(String token, Teams teams) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (user.getType() != 0 && user.getType() != 1) {
            return R.warn("当前角色不能创建社团");
        }

        if (user.getType() == 1) {
            teams.setManager(user.getId());
        }

        teams.setId(IDUtils.makeIDByCurrent());
        teams.setCreateTime(DateUtils.getNowDate("yyyy-MM-dd"));
        if (teams.getTotal() == null || teams.getTotal() < 1) {
            teams.setTotal(1);
        }

        Log.info("新增社团信息：{}", teams);
        int count = teamsService.addTeams(teams);
        if (count == 0) {
            return R.error("社团管理员无效，请重新选择");
        }
        return R.successData(teams);
    }

    @PostMapping("/upd")
    @ResponseBody
    public R updInfo(String token, Teams teams) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        Teams oldTeam = teamsService.getOne(teams.getId());
        if (ObjectUtils.isEmpty(oldTeam)) {
            return R.warn("社团不存在");
        }
        if (user.getType() != 0 && !user.getId().equals(oldTeam.getManager())) {
            return R.warn("只能修改自己负责的社团");
        }

        if (user.getType() == 1) {
            teams.setManager(oldTeam.getManager());
        }
        if (teams.getTotal() == null || teams.getTotal() < 1) {
            teams.setTotal(Math.max(oldTeam.getTotal() == null ? 1 : oldTeam.getTotal(), 1));
        }

        Log.info("修改社团信息：{}", teams);
        int count = teamsService.updateTeams(teams);
        if (count == 0) {
            return R.error("社团管理员无效，请重新选择");
        }
        return R.successData(teams);
    }

    @PostMapping("/uploadImage")
    @ResponseBody
    public R uploadImage(String token, String teamId, @RequestParam("file") MultipartFile file) throws IOException {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        Teams team = teamsService.getOne(teamId);
        if (ObjectUtils.isEmpty(team)) {
            return R.warn("社团不存在");
        }
        if (user.getType() != 0 && !user.getId().equals(team.getManager())) {
            return R.warn("只能维护自己负责社团的介绍配图");
        }
        if (file == null || file.isEmpty()) {
            return R.warn("请选择要上传的图片");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return R.warn("仅支持上传图片文件");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            return R.warn("社团介绍图片不能超过 5MB");
        }

        String originalName = file.getOriginalFilename();
        String suffix = ".png";
        if (originalName != null && originalName.lastIndexOf('.') >= 0) {
            suffix = originalName.substring(originalName.lastIndexOf('.'));
        }

        File imageDir = new File(resolveUploadRoot(), "teams");
        if (!imageDir.exists() && !imageDir.mkdirs()) {
            return R.error("社团图片目录创建失败");
        }

        String fileName = teamId + "_" + System.currentTimeMillis() + suffix;
        File target = new File(imageDir, fileName);
        file.transferTo(target);

        return R.successData("/association/uploads/teams/" + fileName);
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

        Log.info("删除社团信息，ID={}", id);
        teamsService.delete(teams);
        return R.success();
    }

    private File resolveUploadRoot() {
        File configured = new File(uploadDir);
        if (configured.isAbsolute()) {
            return configured;
        }
        return new File(System.getProperty("user.dir"), uploadDir);
    }
}
