/*
  AssociationManager 全新重置 SQL

  作用：
  1. 彻底删除旧数据库 associationmanager
  2. 按当前后端代码重建全部表结构
  3. 导入一套全新的中文演示数据

  说明：
  - 这是覆盖式重置脚本，不会和旧数据混在一起
  - 执行完成后，数据库里只保留本脚本生成的数据

  推荐登录账号：
  - 系统管理员：sys_admin / 123456
  - 社团管理员：club_robot / 123456
  - 社团管理员：club_photo / 123456
  - 社团管理员：club_volunteer / 123456
  - 社团管理员：student_wangyx / 123456
  - 普通成员：student_chenzr / 123456
  - 普通成员：student_liym / 123456
  - 普通成员：student_tangny / 123456
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS `associationmanager`;
CREATE DATABASE `associationmanager`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `associationmanager`;

CREATE TABLE `users` (
  `id` char(13) NOT NULL,
  `user_name` varchar(32) NOT NULL,
  `pass_word` varchar(64) NOT NULL,
  `name` varchar(32) DEFAULT NULL,
  `gender` varchar(8) DEFAULT NULL,
  `age` int DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `status` int NOT NULL DEFAULT 1,
  `create_time` char(19) NOT NULL,
  `type` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_user_name` (`user_name`),
  KEY `idx_users_type_status` (`type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `team_types` (
  `id` char(13) NOT NULL,
  `name` varchar(32) NOT NULL,
  `create_time` char(19) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `teams` (
  `id` char(13) NOT NULL,
  `name` varchar(64) NOT NULL,
  `create_time` char(19) NOT NULL,
  `total` int NOT NULL DEFAULT 0,
  `manager` char(13) NOT NULL,
  `type_id` char(13) NOT NULL,
  `intro` text,
  `images` text,
  PRIMARY KEY (`id`),
  KEY `idx_teams_manager` (`manager`),
  KEY `idx_teams_type` (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `members` (
  `id` char(13) NOT NULL,
  `create_time` char(19) NOT NULL,
  `team_id` char(13) NOT NULL,
  `user_id` char(13) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_members_team_user` (`team_id`, `user_id`),
  KEY `idx_members_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `apply_logs` (
  `id` char(13) NOT NULL,
  `status` int NOT NULL DEFAULT 0,
  `create_time` char(19) NOT NULL,
  `team_id` char(13) NOT NULL,
  `user_id` char(13) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_apply_logs_team` (`team_id`),
  KEY `idx_apply_logs_user` (`user_id`),
  KEY `idx_apply_logs_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `activities` (
  `id` char(13) NOT NULL,
  `name` varchar(80) NOT NULL,
  `comm` text NOT NULL,
  `detail` text NOT NULL,
  `ask` text NOT NULL,
  `total` int NOT NULL DEFAULT 0,
  `max_total` int NOT NULL DEFAULT 0,
  `active_time` char(19) NOT NULL,
  `enroll_end_time` char(19) NOT NULL,
  `team_id` char(13) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_activities_team` (`team_id`),
  KEY `idx_activities_time` (`active_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `active_logs` (
  `id` char(13) NOT NULL,
  `create_time` char(19) NOT NULL,
  `active_id` char(13) NOT NULL,
  `user_id` char(13) NOT NULL,
  `status` int NOT NULL DEFAULT 0,
  `review_time` char(19) DEFAULT NULL,
  `review_remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_active_logs_active` (`active_id`),
  KEY `idx_active_logs_user` (`user_id`),
  KEY `idx_active_logs_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `notices` (
  `id` char(13) NOT NULL,
  `title` varchar(80) NOT NULL,
  `detail` text NOT NULL,
  `create_time` char(19) NOT NULL,
  `team_id` char(13) DEFAULT NULL,
  `is_top` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_notices_team` (`team_id`),
  KEY `idx_notices_top_time` (`is_top`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `rules` (
  `id` char(13) NOT NULL,
  `title` varchar(80) NOT NULL,
  `content` text NOT NULL,
  `create_time` char(19) NOT NULL,
  `update_time` char(19) NOT NULL,
  `team_id` char(13) DEFAULT NULL,
  `user_id` char(13) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_rules_team` (`team_id`),
  KEY `idx_rules_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `interactions` (
  `id` char(13) NOT NULL,
  `title` varchar(100) NOT NULL,
  `content` text NOT NULL,
  `create_time` char(19) NOT NULL,
  `team_id` char(13) DEFAULT NULL,
  `user_id` char(13) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_interactions_team` (`team_id`),
  KEY `idx_interactions_user` (`user_id`),
  KEY `idx_interactions_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `interaction_comments` (
  `id` char(13) NOT NULL,
  `interaction_id` char(13) NOT NULL,
  `parent_id` char(13) DEFAULT NULL,
  `content` text NOT NULL,
  `create_time` char(19) NOT NULL,
  `user_id` char(13) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_interaction_comments_interaction` (`interaction_id`),
  KEY `idx_interaction_comments_parent` (`parent_id`),
  KEY `idx_interaction_comments_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `pay_logs` (
  `id` char(13) NOT NULL,
  `create_time` char(19) NOT NULL,
  `total` decimal(10,2) NOT NULL,
  `team_id` char(13) NOT NULL,
  `user_id` char(13) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_pay_logs_team` (`team_id`),
  KEY `idx_pay_logs_user` (`user_id`),
  KEY `idx_pay_logs_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `pay_expenses` (
  `id` char(13) NOT NULL,
  `create_time` char(19) NOT NULL,
  `total` decimal(10,2) NOT NULL,
  `title` varchar(128) NOT NULL,
  `detail` text NOT NULL,
  `team_id` char(13) NOT NULL,
  `handler_id` char(13) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_pay_expenses_team` (`team_id`),
  KEY `idx_pay_expenses_handler` (`handler_id`),
  KEY `idx_pay_expenses_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `users` (`id`, `user_name`, `pass_word`, `name`, `gender`, `age`, `phone`, `address`, `avatar`, `status`, `create_time`, `type`) VALUES
('1760010000001', 'sys_admin', '123456', '顾明哲', '男', 29, '13811230001', '杭州市西湖区学院路 88 号', 'avatar_h', 1, '2026-04-01 09:10:00', 0),
('1760010000003', 'club_robot', '123456', '周砚洲', '男', 24, '13811230003', '杭州市拱墅区莫干山路 689 号', 'avatar_d', 1, '2026-04-01 09:18:00', 1),
('1760010000004', 'club_photo', '123456', '林嘉霏', '女', 22, '13811230004', '杭州市滨江区江南大道 518 号', 'avatar_i', 1, '2026-04-01 09:24:00', 1),
('1760010000005', 'club_volunteer', '123456', '程一帆', '男', 23, '13811230005', '杭州市上城区解放东路 197 号', 'avatar_g', 1, '2026-04-01 09:27:00', 1),
('1760010000011', 'student_wangyx', '123456', '王语棠', '女', 20, '13922180011', '杭州师范大学仓前校区 5 号楼', '/association/uploads/avatars/1760010000011_1776394096636.png', 1, '2026-04-01 09:30:00', 1),
('1760010000012', 'student_chenzr', '123456', '陈知润', '男', 21, '13922180012', '杭州电子科技大学下沙校区 7 号楼', 'avatar_b', 1, '2026-04-01 09:33:00', 2),
('1760010000013', 'student_liym', '123456', '李聿绵', '女', 19, '13922180013', '浙江工业大学朝晖校区 2 号楼', 'avatar_c', 1, '2026-04-01 09:36:00', 2),
('1760010000014', 'student_heyc', '123456', '何奕辰', '男', 20, '13922180014', '浙江工商大学下沙校区 4 号楼', 'avatar_d', 1, '2026-04-01 09:40:00', 2),
('1760010000015', 'student_xumq', '123456', '徐沐晴', '女', 21, '13922180015', '中国计量大学启明生活区 8 号楼', 'avatar_e', 1, '2026-04-01 09:45:00', 2),
('1760010000016', 'student_pengxl', '123456', '彭星澜', '男', 20, '13922180016', '浙江理工大学生活二区 3 号楼', 'avatar_f', 1, '2026-04-01 09:49:00', 2),
('1760010000017', 'student_tangny', '123456', '唐宁语', '女', 19, '13922180017', '浙江传媒学院钱塘校区 11 号楼', 'avatar_j', 1, '2026-04-01 09:54:00', 2),
('1760010000018', 'student_luqf', '123456', '陆清帆', '男', 22, '13922180018', '浙江大学紫金港校区丹阳青溪 17 舍', 'avatar_h', 1, '2026-04-01 09:58:00', 2),
('1760010000019', 'student_linyr', '123456', '林悠然', '女', 20, '13922180019', '杭州电子科技大学圣光机宿舍区', 'avatar_i', 1, '2026-04-01 10:05:00', 2),
('1760010000020', 'student_sunhl', '123456', '孙和林', '男', 21, '13922180020', '浙江工商大学金沙港生活园区', 'avatar_b', 1, '2026-04-01 10:12:00', 2),
('1760010000021', 'student_qinmn', '123456', '秦沐宁', '女', 20, '13922180021', '杭州师范大学诚园 9 号楼', 'avatar_e', 1, '2026-04-01 10:18:00', 2),
('1760010000022', 'student_yuao', '123456', '袁安鸥', '男', 22, '13922180022', '浙江工业大学屏峰校区东苑 6 号楼', 'avatar_g', 1, '2026-04-01 10:24:00', 2),
('1760010000023', 'student_zhqf', '123456', '郑清芙', '女', 19, '13922180023', '浙江财经大学学博园 2 幢', 'avatar_c', 1, '2026-04-01 10:31:00', 2),
('1760010000024', 'student_gyl', '123456', '顾奕澜', '男', 20, '13922180024', '杭州电子科技大学下沙校区 3 号楼', 'avatar_a', 1, '2026-04-01 10:36:00', 2);

INSERT INTO `team_types` (`id`, `name`, `create_time`) VALUES
('1760020000001', '学术科技类', '2026-04-01 11:00:00'),
('1760020000002', '文化艺术类', '2026-04-01 11:05:00'),
('1760020000003', '公益实践类', '2026-04-01 11:10:00'),
('1775026450414', '媒体新闻类', '2026-04-01 11:15:00');

INSERT INTO `teams` (`id`, `name`, `create_time`, `total`, `manager`, `type_id`, `intro`, `images`) VALUES
('1760030000001', '星火机器人社', '2026-04-01 12:00:00', 4, '1760010000003', '1760020000001', '以机器人调试、智能车训练和比赛实践为主，适合喜欢动手与技术攻关的同学。', 'https://picsum.photos/seed/robot-team-1/1200/800,https://picsum.photos/seed/robot-team-2/1200/800'),
('1760030000002', '光影摄影社', '2026-04-01 12:10:00', 5, '1760010000004', '1760020000002', '以摄影采风、后期分享和校园视觉表达为主，适合喜欢拍照和记录生活的同学。', 'https://picsum.photos/seed/photo-team-1/1200/800,https://picsum.photos/seed/photo-team-2/1200/800'),
('1760030000003', '校园青年志愿者协会', '2026-04-01 12:20:00', 4, '1760010000005', '1760020000003', '以公益服务、志愿实践和校园互助为主，强调行动力与协作能力。', 'https://picsum.photos/seed/volunteer-team-1/1200/800,https://picsum.photos/seed/volunteer-team-2/1200/800'),
('1775027273819', '新媒体社', '2026-04-01 12:30:00', 4, '1760010000011', '1775026450414', '以推文、采访、短视频和内容运营为主，适合喜欢表达和创作的同学。', 'https://picsum.photos/seed/media-team-1/1200/800,https://picsum.photos/seed/media-team-2/1200/800');

INSERT INTO `members` (`id`, `create_time`, `team_id`, `user_id`) VALUES
('1760040000001', '2026-04-02 09:00:00', '1760030000001', '1760010000003'),
('1760040000002', '2026-04-02 09:05:00', '1760030000001', '1760010000012'),
('1760040000003', '2026-04-02 09:10:00', '1760030000001', '1760010000014'),
('1760040000004', '2026-04-02 09:15:00', '1760030000001', '1760010000018'),
('1760040000005', '2026-04-02 09:20:00', '1760030000002', '1760010000004'),
('1760040000006', '2026-04-02 09:25:00', '1760030000002', '1760010000015'),
('1760040000007', '2026-04-02 09:30:00', '1760030000002', '1760010000019'),
('1760040000008', '2026-04-02 09:35:00', '1760030000002', '1760010000024'),
('1760040000009', '2026-04-02 09:40:00', '1760030000002', '1760010000011'),
('1760040000010', '2026-04-02 09:45:00', '1760030000003', '1760010000005'),
('1760040000011', '2026-04-02 09:50:00', '1760030000003', '1760010000016'),
('1760040000012', '2026-04-02 09:55:00', '1760030000003', '1760010000020'),
('1760040000013', '2026-04-02 10:00:00', '1760030000003', '1760010000023'),
('1760040000014', '2026-04-02 10:05:00', '1775027273819', '1760010000011'),
('1760040000015', '2026-04-02 10:10:00', '1775027273819', '1760010000013'),
('1760040000016', '2026-04-02 10:15:00', '1775027273819', '1760010000021'),
('1760040000017', '2026-04-02 10:20:00', '1775027273819', '1760010000022');

INSERT INTO `apply_logs` (`id`, `status`, `create_time`, `team_id`, `user_id`) VALUES
('1775109200001', 0, '2026-04-10 18:10:00', '1760030000001', '1760010000017'),
('1775109200002', 1, '2026-04-11 09:20:00', '1760030000002', '1760010000021'),
('1775109200003', 1, '2026-04-11 11:30:00', '1775027273819', '1760010000024'),
('1775109200004', 2, '2026-04-12 14:00:00', '1775027273819', '1760010000023'),
('1775109200005', 0, '2026-04-13 16:40:00', '1760030000003', '1760010000019');

INSERT INTO `activities` (`id`, `name`, `comm`, `detail`, `ask`, `total`, `max_total`, `active_time`, `enroll_end_time`, `team_id`) VALUES
('1760050000001', '智能车调试夜场', '围绕巡线稳定性进行集中调试。', '活动包括器材检查、程序烧录、赛道测试和复盘讨论。', '请自带笔记本电脑并提前安装驱动。', 1, 12, '2026-05-18 19:00:00', '2026-05-17 18:00:00', '1760030000001'),
('1760050000002', '校园巡线挑战赛', '通过分组挑战赛检验训练成果。', '活动包含规则说明、熟悉赛道、正式比赛和赛后复盘。', '仅限社团成员报名，建议两人一组。', 1, 20, '2026-05-26 09:30:00', '2026-05-25 18:00:00', '1760030000001'),
('1760050000003', '春日校园摄影采风', '围绕春日校园主题进行外拍。', '活动在图书馆广场集合，之后前往几处校园取景点，最后统一分享作品。', '建议携带相机或手机稳定器。', 2, 10, '2026-05-19 15:30:00', '2026-05-18 20:00:00', '1760030000002'),
('1760050000004', '夜景长曝光工作坊', '面向摄影社成员开展夜景拍摄训练。', '先做器材说明，再到校外取景，最后返回看片讨论。', '请尽量携带三脚架并注意夜间安全。', 1, 15, '2026-05-21 18:40:00', '2026-05-20 18:00:00', '1760030000002'),
('1760050000005', '旧书换绿植公益集市', '通过旧书捐赠倡导低碳校园生活。', '协会会在广场设置公益摊位，安排登记、引导、秩序维护等工作。', '请提前熟悉流程并佩戴工作证。', 1, 20, '2026-05-20 11:00:00', '2026-05-19 18:00:00', '1760030000003'),
('1775028955202', '光影之间新媒体创作分享会', '邀请校内创作者分享内容创作经验。', '流程包括主题分享、案例拆解、手机剪辑演示和自由问答。', '仅限新媒体社成员报名，请提前到场。', 2, 15, '2026-05-22 14:30:00', '2026-05-21 18:00:00', '1775027273819'),
('1775030000001', '媒体交流', '分享选题策划和短视频脚本经验。', '以圆桌讨论和小组互评的形式展开，重点交流校园内容创作方法。', '新媒体社成员优先，摄影社成员可申请旁听。', 1, 5, '2026-05-21 15:30:59', '2026-05-20 12:00:00', '1775027273819');

INSERT INTO `active_logs` (`id`, `create_time`, `active_id`, `user_id`, `status`, `review_time`, `review_remark`) VALUES
('1775200000001', '2026-05-10 18:20:00', '1760050000001', '1760010000003', 1, '2026-05-10 18:20:00', '发起人默认参加'),
('1775200000002', '2026-05-11 09:50:00', '1760050000002', '1760010000003', 1, '2026-05-11 09:50:00', '发起人默认参加'),
('1775200000003', '2026-05-12 11:00:00', '1760050000003', '1760010000004', 1, '2026-05-12 11:00:00', '发起人默认参加'),
('1775200000004', '2026-05-12 13:20:00', '1760050000003', '1760010000011', 0, NULL, NULL),
('1775200000005', '2026-05-13 10:30:00', '1760050000004', '1760010000004', 1, '2026-05-13 10:30:00', '发起人默认参加'),
('1775200000006', '2026-05-13 15:20:00', '1760050000005', '1760010000005', 1, '2026-05-13 15:20:00', '发起人默认参加'),
('1775200000007', '2026-05-14 15:35:55', '1775028955202', '1760010000011', 1, '2026-05-14 15:35:55', '社团管理员自主加入活动'),
('1775200000008', '2026-05-15 16:10:00', '1775028955202', '1760010000024', 1, '2026-05-15 18:00:00', '审核通过'),
('1775200000009', '2026-05-16 11:17:23', '1775030000001', '1760010000011', 1, '2026-05-16 11:17:23', '发起人默认参加');

INSERT INTO `notices` (`id`, `title`, `detail`, `create_time`, `team_id`, `is_top`) VALUES
('1760080000001', '关于五一后活动安排的说明', '五一假期结束后请各社团及时确认活动时间并同步更新到后台。', '2026-05-01 09:00:00', NULL, 1),
('1760080000002', '社团经费报销材料提醒', '提交报销时请同步上传小票照片、活动说明和负责人确认信息。', '2026-05-02 10:15:00', NULL, 0),
('1760080000003', '评论区文明交流倡议', '请围绕主题发言，禁止人身攻击、恶意引战、广告刷屏和泄露隐私。', '2026-05-03 08:30:00', NULL, 1),
('1760080000004', '机器人社本周训练安排', '本周三和周五晚上七点开放实验室，重点进行巡线程序调试。', '2026-05-10 19:00:00', '1760030000001', 0),
('1760080000005', '摄影社作品征集通知', '本周开始征集春日校园主题作品，优秀作品将在线下展板展示。', '2026-05-11 14:20:00', '1760030000002', 0),
('1760080000006', '新媒体社活动分工说明', '本周分享会将按主持、记录、现场拍摄和后期整理四组分工。', '2026-05-12 16:00:00', '1775027273819', 0);

INSERT INTO `rules` (`id`, `title`, `content`, `create_time`, `update_time`, `team_id`, `user_id`) VALUES
('1760090000001', '平台使用规范', '所有账号都需要妥善保管密码，涉及审核操作时请保留必要说明。', '2026-05-01 09:00:00', '2026-05-01 09:00:00', NULL, '1760010000001'),
('1760090000002', '经费报销基本要求', '经费申请需要有活动方案和对应凭证，金额必须和记录保持一致。', '2026-05-01 10:00:00', '2026-05-01 10:00:00', NULL, '1760010000001'),
('1760090000003', '机器人社实验室守则', '焊台和热风枪使用后必须断电归位，借用器材请当天登记。', '2026-05-08 19:30:00', '2026-05-08 19:30:00', '1760030000001', '1760010000003'),
('1760090000004', '摄影社借设备规范', '借用相机、镜头和三脚架需要提前登记，如遇损坏请第一时间上报。', '2026-05-09 13:10:00', '2026-05-09 13:10:00', '1760030000002', '1760010000004'),
('1760090000005', '新媒体社发布流程', '每篇推文和视频发布前都需要经过至少一次校对和一次封面确认。', '2026-05-10 17:20:00', '2026-05-12 11:00:00', '1775027273819', '1760010000011');

INSERT INTO `interactions` (`id`, `title`, `content`, `create_time`, `team_id`, `user_id`) VALUES
('1775300000001', '迎新宣发物料应该怎么分工', '这学期迎新活动比较多，海报、短视频、推文和现场拍摄应该怎么分工更高效？', '2026-05-09 10:00:00', NULL, '1760010000001'),
('1775300000002', '机器人社招新海报风格征集', '想做一版更偏技术感的海报，大家更喜欢深色电路板风还是明亮科技感？', '2026-05-10 20:15:00', '1760030000001', '1760010000003'),
('1775300000003', '摄影社春拍路线推荐', '本周末准备组织春拍，大家有没有适合下午拍摄且人少光线好的路线推荐？', '2026-05-11 13:40:00', '1760030000002', '1760010000004'),
('1775300000004', '新媒体社短视频脚本互助贴', '最近在做校园人物采访短视频，想收集一些前十秒更抓人的脚本写法。', '2026-05-12 15:15:00', '1775027273819', '1760010000011');

INSERT INTO `interaction_comments` (`id`, `interaction_id`, `parent_id`, `content`, `create_time`, `user_id`) VALUES
('1775310000001', '1775300000001', NULL, '建议先拆成内容组、视觉组和现场组，每组只保留一个总对接人。', '2026-05-09 10:20:00', '1760010000011'),
('1775310000002', '1775300000001', '1775310000001', '这个分法很实用，我建议再补一个素材归档负责人。', '2026-05-09 10:35:00', '1760010000001'),
('1775310000003', '1775300000002', NULL, '我投深色电路板风，和机器人社的气质更贴近。', '2026-05-10 20:40:00', '1760010000014'),
('1775310000004', '1775300000003', NULL, '可以走图书馆外侧到湖心亭这条线，下午逆光比较柔和。', '2026-05-11 14:05:00', '1760010000019'),
('1775310000005', '1775300000004', NULL, '前十秒可以先抛一个有冲突感的问题，这样更容易抓住注意力。', '2026-05-12 15:40:00', '1760010000022'),
('1775310000006', '1775300000004', '1775310000005', '这个开头很有画面感，如果再配一点环境声会更好。', '2026-05-12 15:52:00', '1760010000013');

INSERT INTO `pay_logs` (`id`, `create_time`, `total`, `team_id`, `user_id`) VALUES
('1775400000001', '2026-05-01 10:00:00', 120.00, '1760030000001', '1760010000012'),
('1775400000002', '2026-05-02 10:00:00', 120.00, '1760030000001', '1760010000014'),
('1775400000003', '2026-05-03 10:00:00', 120.00, '1760030000001', '1760010000018'),
('1775400000004', '2026-05-01 11:00:00', 80.00, '1760030000002', '1760010000004'),
('1775400000005', '2026-05-02 11:00:00', 80.00, '1760030000002', '1760010000015'),
('1775400000006', '2026-05-03 11:00:00', 80.00, '1760030000002', '1760010000019'),
('1775400000007', '2026-05-01 12:00:00', 120.00, '1760030000003', '1760010000005'),
('1775400000008', '2026-05-02 12:00:00', 60.00, '1760030000003', '1760010000016'),
('1775400000009', '2026-05-03 12:00:00', 60.00, '1760030000003', '1760010000020'),
('1775400000010', '2026-05-02 10:00:00', 70.00, '1775027273819', '1760010000011'),
('1775400000011', '2026-05-03 10:00:00', 50.00, '1775027273819', '1760010000013'),
('1775400000012', '2026-05-04 10:00:00', 70.00, '1775027273819', '1760010000021');

INSERT INTO `pay_expenses` (`id`, `create_time`, `total`, `title`, `detail`, `team_id`, `handler_id`) VALUES
('1775500000001', '2026-05-10 15:00:00', 90.00, '机器人器材维修', '更换两套损耗严重的传感器支架并补充焊锡丝。', '1760030000001', '1760010000003'),
('1775500000002', '2026-05-18 18:30:00', 40.00, '比赛材料采购', '购买赛道胶带、扎带和调试记录本。', '1760030000001', '1760010000003'),
('1775500000003', '2026-05-12 13:20:00', 70.00, '摄影外拍交通补贴', '用于春拍踩点往返交通和少量设备搬运费用。', '1760030000002', '1760010000004'),
('1775500000004', '2026-05-22 17:10:00', 50.00, '摄影展板打印', '打印社团作品展宣传海报和展板样张。', '1760030000002', '1760010000004'),
('1775500000005', '2026-05-15 09:40:00', 80.00, '公益集市物资采购', '购买贴纸、签字笔、饮用水和桌牌。', '1760030000003', '1760010000005'),
('1775500000006', '2026-05-20 16:20:00', 30.00, '活动海报打印', '创作分享会现场易拉宝和桌面提示牌打印。', '1775027273819', '1760010000011');

SET FOREIGN_KEY_CHECKS = 1;

