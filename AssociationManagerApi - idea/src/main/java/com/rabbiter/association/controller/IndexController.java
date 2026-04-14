package com.rabbiter.association.controller;

import com.rabbiter.association.entity.Notices;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.handle.CacheHandle;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.msg.PageData;
import com.rabbiter.association.service.NoticesService;
import com.rabbiter.association.service.UsersService;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class IndexController extends BaseController {

    private static final Logger Log = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private UsersService usersService;

    @Autowired
    private CacheHandle cacheHandle;

    @Autowired
    private NoticesService noticesService;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @GetMapping("/sys/notices")
    @ResponseBody
    public R getNoticeList(String token) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        List<Notices> list;
        if (user.getType() == 0) {
            list = noticesService.getSysNotices();
        } else if (user.getType() == 1) {
            list = noticesService.getManNotices(user.getId());
        } else {
            list = noticesService.getMemNotices(user.getId());
        }
        return R.successData(list);
    }

    @GetMapping("/demoAccounts")
    @ResponseBody
    public R getDemoAccounts() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        appendDemoUsers(result, 0, 1);
        appendDemoUsers(result, 1, 2);
        appendDemoUsers(result, 2, 3);

        return R.successData(result);
    }

    @PostMapping("/login")
    @ResponseBody
    public R login(String userName, String passWord) {
        Log.info("用户登录，用户名：{}", userName);

        Users user = usersService.getUserByUserName(userName);
        if (user == null) {
            return R.error("输入的用户名不存在");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            return R.error("当前账号已停用，请联系系统管理员");
        }
        if (!passWord.equals(user.getPassWord().trim())) {
            return R.error("输入的密码错误");
        }

        String token = IDUtils.makeIDByUUID();
        cacheHandle.addUserCache(token, user.getId());
        return R.success("登录成功", token);
    }

    @RequestMapping("/exit")
    @ResponseBody
    public R exit(String token) {
        Log.info("用户退出系统并移除登录信息");
        cacheHandle.removeUserCache(token);
        return R.success();
    }

    @GetMapping("/info")
    @ResponseBody
    public R info(String token) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        return R.successData(user);
    }

    @PostMapping("/info")
    @ResponseBody
    public R info(String token, Users user) {
        Users current = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(current)) {
            return R.error("登录信息不存在，请重新登录");
        }

        Log.info("修改个人资料：userId={}", current.getId());

        if (!ObjectUtils.isEmpty(user.getName())) {
            current.setName(user.getName());
        }
        if (!ObjectUtils.isEmpty(user.getGender())) {
            current.setGender(user.getGender());
        }
        if (user.getAge() != null) {
            current.setAge(user.getAge());
        }
        if (!ObjectUtils.isEmpty(user.getPhone())) {
            current.setPhone(user.getPhone());
        }
        if (!ObjectUtils.isEmpty(user.getAddress())) {
            current.setAddress(user.getAddress());
        }
        if (!ObjectUtils.isEmpty(user.getAvatar())) {
            current.setAvatar(user.getAvatar());
        }

        usersService.update(current);
        return R.successData(current);
    }

    @PostMapping("/avatar")
    @ResponseBody
    public R avatar(String token, @RequestParam("file") MultipartFile file) throws IOException {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (file == null || file.isEmpty()) {
            return R.warn("请选择要上传的头像图片");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return R.warn("仅支持上传图片文件");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            return R.warn("头像图片不能超过 2MB");
        }

        String originalName = file.getOriginalFilename();
        String suffix = ".png";
        if (originalName != null && originalName.lastIndexOf(".") >= 0) {
            suffix = originalName.substring(originalName.lastIndexOf("."));
        }

        File avatarDir = new File(resolveUploadRoot(), "avatars");
        if (!avatarDir.exists() && !avatarDir.mkdirs()) {
            return R.error("头像目录创建失败");
        }

        if (user.getAvatar() != null && user.getAvatar().contains("/uploads/avatars/")) {
            String oldName = user.getAvatar().substring(user.getAvatar().lastIndexOf("/") + 1);
            File oldFile = new File(avatarDir, oldName);
            if (oldFile.exists()) {
                oldFile.delete();
            }
        }

        String fileName = user.getId() + "_" + System.currentTimeMillis() + suffix;
        File target = new File(avatarDir, fileName);
        File parent = target.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            return R.error("头像保存目录创建失败");
        }
        file.transferTo(target);

        user.setAvatar("/association/uploads/avatars/" + fileName);
        usersService.update(user);
        return R.successData(user);
    }

    @RequestMapping("/checkPwd")
    @ResponseBody
    public R checkPwd(String oldPwd, String token) {
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (ObjectUtils.isEmpty(oldPwd)) {
            return R.warn("请输入当前密码");
        }
        if (oldPwd.equals(user.getPassWord())) {
            return R.success();
        }
        return R.warn("原始密码和输入密码不一致");
    }

    @PostMapping("/pwd")
    @ResponseBody
    public R pwd(String token, String password, String oldPwd) {
        Log.info("修改用户密码");
        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if (ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }
        if (ObjectUtils.isEmpty(password)) {
            return R.warn("请输入新密码");
        }
        if (password.length() < 6) {
            return R.warn("新密码长度不能少于 6 位");
        }
        if (!ObjectUtils.isEmpty(oldPwd) && !oldPwd.equals(user.getPassWord())) {
            return R.warn("当前密码输入不正确");
        }
        if (password.equals(user.getPassWord())) {
            return R.warn("新密码不能与当前密码相同");
        }
        user.setPassWord(password);
        usersService.update(user);
        return R.success();
    }

    private File resolveUploadRoot() {
        File configured = new File(uploadDir);
        if (configured.isAbsolute()) {
            return configured;
        }
        return new File(System.getProperty("user.dir"), uploadDir);
    }

    private void appendDemoUsers(List<Map<String, Object>> result, Integer type, long limit) {
        Users query = new Users();
        query.setType(type);
        query.setStatus(1);
        PageData pageData = usersService.getPageInfo(1L, 50L, query);
        List<?> rawList = pageData.getData();
        if (rawList == null) {
            return;
        }

        long count = 0L;
        for (Object item : rawList) {
            if (count >= limit) {
                break;
            }
            if (!(item instanceof Map)) {
                continue;
            }
            Map<?, ?> source = (Map<?, ?>) item;
            LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("id", source.get("id"));
            row.put("userName", source.get("userName"));
            row.put("name", source.get("name"));
            row.put("type", source.get("type"));
            row.put("roleLabel", getRoleLabel(source.get("type")));
            result.add(row);
            count += 1;
        }
    }

    private String getRoleLabel(Object type) {
        if (type == null) {
            return "未分配身份";
        }
        Integer value = Integer.valueOf(String.valueOf(type));
        if (value == 0) {
            return "系统管理员";
        }
        if (value == 1) {
            return "社团管理员";
        }
        return "学生";
    }
}
