-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: associationmanager
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `active_logs`
--

DROP TABLE IF EXISTS `active_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `active_logs` (
  `id` char(13) NOT NULL COMMENT '记录ID',
  `create_time` char(19) NOT NULL COMMENT '报名时间',
  `active_id` char(13) NOT NULL COMMENT '活动编号',
  `user_id` char(13) NOT NULL COMMENT '报名用户',
  `status` int NOT NULL DEFAULT '0' COMMENT '报名状态：0待审核 1已通过 2已驳回',
  `review_time` char(19) DEFAULT NULL COMMENT '审核时间',
  `review_remark` varchar(255) DEFAULT NULL COMMENT '审核备注',
  PRIMARY KEY (`id`),
  KEY `active_id` (`active_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `active_logs_ibfk_1` FOREIGN KEY (`active_id`) REFERENCES `activities` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `active_logs_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='活动报名记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `active_logs`
--

LOCK TABLES `active_logs` WRITE;
/*!40000 ALTER TABLE `active_logs` DISABLE KEYS */;
INSERT INTO `active_logs` VALUES ('1760060000001','2026-03-12 20:12:00','1760050000001','1760010000012',1,'2026-03-12 20:12:00','历史数据补齐'),('1760060000002','2026-03-13 09:25:00','1760050000001','1760010000014',1,'2026-03-13 09:25:00','历史数据补齐'),('1760060000003','2026-03-13 21:40:00','1760050000001','1760010000018',1,'2026-03-13 21:40:00','历史数据补齐'),('1760060000004','2026-03-15 11:20:00','1760050000002','1760010000011',1,'2026-03-15 11:20:00','历史数据补齐'),('1760060000005','2026-03-16 17:36:00','1760050000002','1760010000017',1,'2026-03-16 17:36:00','历史数据补齐'),('1760060000006','2026-03-18 14:08:00','1760050000003','1760010000013',1,'2026-03-18 14:08:00','历史数据补齐'),('1760060000007','2026-03-18 18:55:00','1760050000003','1760010000015',1,'2026-03-18 18:55:00','历史数据补齐'),('1760060000010','2026-03-25 18:06:00','1760050000005','1760010000020',1,'2026-04-01 19:17:16','审核通过'),('1760060000011','2026-03-25 21:14:00','1760050000005','1760010000022',1,'2026-03-25 21:14:00','历史数据补齐'),('1760060000012','2026-03-26 16:43:00','1760050000006','1760010000019',1,'2026-03-26 16:43:00','历史数据补齐'),('1760060000013','2026-03-26 17:12:00','1760050000006','1760010000023',1,'2026-03-26 17:12:00','历史数据补齐'),('1760060000014','2026-03-26 18:25:00','1760050000006','1760010000024',1,'2026-03-26 18:25:00','历史数据补齐'),('1760060000015','2026-03-27 09:03:00','1760050000007','1760010000021',1,'2026-03-27 09:03:00','历史数据补齐'),('1760060000016','2026-03-27 12:40:00','1760050000007','1760010000024',1,'2026-03-27 12:40:00','历史数据补齐'),('1774927554776','2026-03-31 11:25:54','1760050000005','1760010000012',1,'2026-03-31 11:25:54','历史数据补齐'),('1775028955209','2026-04-01 15:35:55','1775028955202','1760010000011',1,'2026-04-01 15:35:55','历史数据补齐'),('1775031551770','2026-04-01 16:19:11','1775031551765','1760010000004',1,'2026-04-01 16:19:11','历史数据补齐'),('1775108686079','2026-04-02 13:44:46','1775031551765','1760010000011',1,'2026-04-02 13:44:46','社团管理员自主加入活动'),('1775108717733','2026-04-02 13:45:17','1760050000007','1760010000011',1,'2026-04-02 13:45:17','社团管理员自主加入活动'),('1775108730952','2026-04-02 13:45:30','1760050000006','1760010000011',1,'2026-04-02 13:45:30','社团管理员自主加入活动'),('1775108793095','2026-04-02 13:46:33','1775031551765','1760010000024',1,'2026-04-02 13:51:23','审核通过'),('1775200000001','2026-04-02 14:20:00','1760050000001','1760010000003',1,'2026-04-02 14:20:00','历史数据修正：补齐发起社团管理员默认参加'),('1775200000002','2026-04-02 14:21:00','1760050000005','1760010000003',1,'2026-04-02 14:21:00','历史数据修正：补齐发起社团管理员默认参加'),('1775200000003','2026-04-02 14:22:00','1760050000002','1760010000004',1,'2026-04-02 14:22:00','历史数据修正：补齐发起社团管理员默认参加'),('1775200000004','2026-04-02 14:23:00','1760050000006','1760010000004',1,'2026-04-02 14:23:00','历史数据修正：补齐发起社团管理员默认参加'),('1775200000005','2026-04-02 14:24:00','1760050000003','1760010000005',1,'2026-04-02 14:24:00','历史数据修正：补齐发起社团管理员默认参加'),('1775200000006','2026-04-02 14:25:00','1760050000007','1760010000005',1,'2026-04-02 14:25:00','历史数据修正：补齐发起社团管理员默认参加');
/*!40000 ALTER TABLE `active_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `activities`
--

DROP TABLE IF EXISTS `activities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activities` (
  `id` char(13) NOT NULL COMMENT '记录ID',
  `name` varchar(60) NOT NULL COMMENT '活动名称',
  `comm` varchar(1600) NOT NULL COMMENT '活动概述',
  `detail` varchar(1600) NOT NULL COMMENT '活动详情',
  `ask` varchar(1600) NOT NULL COMMENT '活动要求',
  `total` int NOT NULL COMMENT '报名人数',
  `active_time` char(19) NOT NULL COMMENT '活动时间',
  `team_id` char(13) NOT NULL COMMENT '发布社团',
  `max_total` int NOT NULL DEFAULT '30' COMMENT '报名人数上限',
  `enroll_end_time` char(19) NOT NULL DEFAULT '2026-12-31 23:59:59' COMMENT '报名截止时间',
  PRIMARY KEY (`id`),
  KEY `team_id` (`team_id`),
  CONSTRAINT `activities_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='活动信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activities`
--

LOCK TABLES `activities` WRITE;
/*!40000 ALTER TABLE `activities` DISABLE KEYS */;
INSERT INTO `activities` VALUES ('1760050000001','机器人创意挑战赛','围绕校园生活场景完成智能小车任务设计。','活动分为硬件调试、现场展示和答辩三个环节，参赛成员需共同完成小车循迹与避障任务。','请自备笔记本电脑，熟悉 Arduino 或树莓派基础开发，活动当天需提前 20 分钟签到。',4,'2026-03-28 14:00:00','1760030000001',12,'2026-03-27 18:00:00'),('1760050000002','春日校园摄影采风','围绕“春日校园”主题进行外拍与作品分享。','活动将在图书馆广场集合，随后分组前往樱花步道、操场和湖心亭取景，结束后统一进行作品点评。','建议携带相机或手机稳定器，穿着便于步行的服装，注意设备防护。',3,'2026-03-30 15:30:00','1760030000002',10,'2026-03-29 20:00:00'),('1760050000003','敬老院志愿服务日','组织社员前往西溪敬老服务中心开展陪伴和便民服务。','服务内容包括环境整理、智能手机使用讲解、文艺陪伴和健康信息登记，活动结束后统一返校。','请穿着志愿者马甲，遵守服务纪律，不得单独行动。',3,'2026-04-02 08:30:00','1760030000003',16,'2026-04-01 18:00:00'),('1760050000005','智能车夜间调试开放日','为挑战赛前的最终联调预留实验室开放时段。','社团将开放创新实验室供各小组测试视觉识别、红外循迹和避障程序，并安排高年级成员值班答疑。','进入实验室请穿平底鞋，不得携带饮料靠近工作台，测试结束后做好设备归位。',4,'2026-03-29 18:30:00','1760030000001',8,'2026-03-29 12:00:00'),('1760050000006','城市光影长曝光工作坊','面向摄影社成员开展长曝光与夜景构图训练。','工作坊先进行半小时器材说明，再前往运河边拍摄城市夜景，最后回到活动教室统一看片。','请携带三脚架，提前确认相机电量，夜间外出注意结伴同行。',5,'2026-04-08 18:40:00','1760030000002',15,'2026-04-07 18:00:00'),('1760050000007','旧书换绿植公益集市','通过旧书捐赠和绿植兑换，倡导低碳校园生活。','协会将在食堂外广场设置公益摊位，招募志愿者负责物资登记、秩序维护和现场引导。','请提前熟悉摊位流程，保持沟通礼貌，活动期间需全程佩戴工作证。',4,'2026-04-10 11:00:00','1760030000003',20,'2026-04-09 18:00:00'),('1775028955202',' 「光影之间」新媒体创作分享会','本次分享会邀请资深新媒体创作者与大家面对面交流，从选题策划、拍摄技巧到后期剪辑，全方位拆解爆款内容背后的创作逻辑。无论你是新媒体小白还是已有一定基础的创作者，都能在这里找到灵感与答案。','14:00-14:30 签到入场 & 暖场互动\n\n14:30-15:30 主题分享：如何打造一篇10w+校园推文（主讲人：新媒体社前主编 李思远）\n\n15:30-16:00 实操演示：手机剪辑快速出片技巧\n\n16:00-16:30 圆桌讨论：AI时代新媒体人的新机遇\n\n16:30-17:00 自由交流 & 现场答疑','仅限新媒体社团员报名，需出示学生证签到入场；\n\n请提前10分钟到场签到，迟到超过15分钟名额将禁止入场；\n\n活动期间请将手机调至静音模式；\n\n建议自带笔记本或平板，方便记录创作干货。',1,'2026-04-02 13:30:00','1775027273819',15,'2026-04-02 00:00:00'),('1775031551765','「光影寻踪」校园摄影大赛','用镜头捕捉校园的动人瞬间，用光影讲述属于我们的故事。本次摄影大赛以「光影寻踪」为主题，鼓励大家走出宿舍，发现校园中容易被忽略的美好角落、动人光影与温暖瞬间。无论手机还是相机，只要有一颗热爱摄影的心，我们都欢迎你的投稿！','15:00-15:30 签到 & 摄影作品展示\n\n15:30-16:00 主题分享：手机摄影的三大核心技巧（构图、光线、色彩）\n\n16:00-16:30 现场实操：校园内实地拍摄练习\n\n16:30-17:00 作品点评 & 互动答疑\n\n活动现场还有机会获得摄影社定制明信片一套！名额有限，快来报名吧～','作品必须为本人原创，不得抄袭或盗用他人作品；\n\n拍摄地点限校园内，内容积极向上；\n\n每人限投3幅作品，单幅或组图均可；\n\n提交格式：JPG/PNG，文件大小不低于2M；\n\n需附上作品名称及简短说明（50字以内）',3,'2026-04-15 00:00:00','1760030000002',3,'2026-04-09 00:00:00');
/*!40000 ALTER TABLE `activities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `apply_logs`
--

DROP TABLE IF EXISTS `apply_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `apply_logs` (
  `id` char(13) NOT NULL COMMENT '记录ID',
  `status` int NOT NULL COMMENT '处理状态',
  `create_time` char(19) NOT NULL COMMENT '申请时间',
  `team_id` char(13) NOT NULL COMMENT '申请社团',
  `user_id` char(13) NOT NULL COMMENT '申请用户',
  PRIMARY KEY (`id`),
  KEY `team_id` (`team_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `apply_logs_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `apply_logs_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='入团申请记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `apply_logs`
--

LOCK TABLES `apply_logs` WRITE;
/*!40000 ALTER TABLE `apply_logs` DISABLE KEYS */;
INSERT INTO `apply_logs` VALUES ('1760070000001',0,'2026-03-24 16:20:00','1760030000002','1760010000015'),('1760070000003',1,'2026-03-10 10:18:00','1760030000003','1760010000016'),('1760070000004',0,'2026-03-26 13:42:00','1760030000001','1760010000013'),('1760070000006',2,'2026-03-21 20:26:00','1760030000002','1760010000020'),('1774927635140',2,'2026-03-31 11:27:15','1760030000003','1760010000012'),('1774928548073',1,'2026-03-31 11:42:28','1760030000003','1760010000011'),('1775029093080',1,'2026-04-01 15:38:13','1775027273819','1760010000024'),('1775035739328',1,'2026-04-01 17:28:59','1775027273819','1760010000022'),('1775109021073',0,'2026-04-02 13:50:21','1775027273819','1760010000023');
/*!40000 ALTER TABLE `apply_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `interaction_comments`
--

DROP TABLE IF EXISTS `interaction_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `interaction_comments` (
  `id` char(13) NOT NULL,
  `interaction_id` char(13) NOT NULL,
  `parent_id` char(13) DEFAULT NULL,
  `content` text NOT NULL,
  `create_time` char(19) NOT NULL,
  `user_id` char(13) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_ic_interaction` (`interaction_id`),
  KEY `idx_ic_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `interaction_comments`
--

LOCK TABLES `interaction_comments` WRITE;
/*!40000 ALTER TABLE `interaction_comments` DISABLE KEYS */;
INSERT INTO `interaction_comments` VALUES ('1760120000001','1760110000001',NULL,'我可以负责演示部分，之前在课程答辩里做过类似展示。','2026-03-26 19:02:00','1760010000012'),('1760120000002','1760110000001',NULL,'如果需要，我可以帮忙整理讲解稿和时间轴。','2026-03-26 19:18:00','1760010000018'),('1760120000003','1760110000002',NULL,'推荐先看 B 站上“影像小满”的 Lightroom 入门系列，比较适合新手。','2026-03-26 21:20:00','1760010000011'),('1760120000004','1760110000003',NULL,'留言墙这个想法不错，还可以加一个旧物捐赠角，和环保主题结合起来。','2026-03-27 09:48:00','1760010000013'),('1760120000005','1760110000004',NULL,'支持，评论区氛围真的会影响大家愿不愿意继续交流。','2026-03-27 11:15:00','1760010000015'),('1760120000006','1760110000005',NULL,'我普通话等级是二甲，可以先录一段样音给大家听听。','2026-03-27 15:02:00','1760010000023'),('1760120000007','1760110000006',NULL,'我投“校园日常”一票，故事感会更强，也更适合展览叙事。','2026-03-27 16:30:00','1760010000019'),('1760120000008','1760110000006',NULL,'夜跑记录也很有氛围，如果能结合长曝光作品会很出彩。','2026-03-27 16:44:00','1760010000024'),('1775020769769','1760110000004','','支持，评论区氛围会影响学生体验的','2026-04-01 13:19:29','1760010000011'),('1775020819929','1760110000006','','我投\"春日光影\"一票，展示春日风光','2026-04-01 13:20:19','1760010000011'),('1775042080507','1760110000004','1760120000005','哈哈','2026-04-01 19:14:40','1760010000011');
/*!40000 ALTER TABLE `interaction_comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `interactions`
--

DROP TABLE IF EXISTS `interactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `interactions` (
  `id` char(13) NOT NULL,
  `title` varchar(128) NOT NULL,
  `content` longtext NOT NULL,
  `create_time` char(19) NOT NULL,
  `team_id` char(13) DEFAULT NULL,
  `user_id` char(13) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_interactions_team` (`team_id`),
  KEY `idx_interactions_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `interactions`
--

LOCK TABLES `interactions` WRITE;
/*!40000 ALTER TABLE `interactions` DISABLE KEYS */;
INSERT INTO `interactions` VALUES ('1760110000001','本周机器人挑战赛还缺一名演示同学','目前机器人创意挑战赛 A 组还需要一位负责现场演示讲解的同学，如果对讲解流程熟悉，欢迎今晚十点前在评论区留言报名。','2026-03-26 18:40:00','1760030000001','1760010000003'),('1760110000002','有没有适合新手的校园摄影修图教程推荐','我刚加入摄影社，想系统学一学 Lightroom 和手机修图的基础流程，如果有大家常用的教程或博主，欢迎推荐。','2026-03-26 21:05:00','1760030000002','1760010000017'),('1760110000003','清明前公益活动创意征集','志愿者协会想在清明前夕做一次校园文明倡议活动，目前考虑设置留言墙和旧书漂流点，欢迎大家提出更好的创意。','2026-03-27 09:20:00','1760030000003','1760010000005'),('1760110000004','评论区文明交流倡议','最近交流互动模块启用了评论功能，希望大家围绕主题理性表达观点，尊重不同意见，共同维护舒适的社团交流氛围。','2026-03-27 11:00:00',NULL,'1760010000001'),('1760110000005','校史短视频旁白想找普通话好的同学','融媒体创作社正在筹备一支面向新生的校史短视频，目前还缺一位普通话标准、声音有辨识度的旁白同学，欢迎自荐或推荐。','2026-03-27 14:25:00','1760030000004','1760010000002'),('1760110000006','摄影社四月展览主题想听听大家建议','我们计划在四月中旬做一次小型作品展，暂定主题在“校园日常”“春日光影”“夜跑记录”之间，大家更想拍哪一种？','2026-03-27 16:12:00','1760030000002','1760010000004');
/*!40000 ALTER TABLE `interactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `members`
--

DROP TABLE IF EXISTS `members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `members` (
  `id` char(13) NOT NULL COMMENT '记录ID',
  `create_time` char(19) NOT NULL COMMENT '入团时间',
  `team_id` char(13) NOT NULL COMMENT '加入社团',
  `user_id` char(13) NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`),
  KEY `team_id` (`team_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `members_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `members_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社团成员';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `members`
--

LOCK TABLES `members` WRITE;
/*!40000 ALTER TABLE `members` DISABLE KEYS */;
INSERT INTO `members` VALUES ('1760040000001','2026-02-18 18:10:00','1760030000001','1760010000012'),('1760040000002','2026-02-20 12:20:00','1760030000001','1760010000014'),('1760040000003','2026-03-02 15:30:00','1760030000001','1760010000018'),('1760040000004','2026-03-11 16:08:00','1760030000001','1760010000020'),('1760040000005','2026-03-18 19:30:00','1760030000001','1760010000022'),('1760040000006','2026-02-22 16:00:00','1760030000002','1760010000011'),('1760040000007','2026-03-01 14:30:00','1760030000002','1760010000017'),('1760040000008','2026-03-09 17:10:00','1760030000002','1760010000019'),('1760040000009','2026-03-12 18:45:00','1760030000002','1760010000023'),('1760040000010','2026-03-18 11:20:00','1760030000002','1760010000024'),('1760040000011','2026-02-19 17:00:00','1760030000003','1760010000013'),('1760040000012','2026-02-27 13:20:00','1760030000003','1760010000015'),('1760040000013','2026-03-03 10:10:00','1760030000003','1760010000016'),('1760040000014','2026-03-07 09:26:00','1760030000003','1760010000021'),('1760040000015','2026-03-16 20:03:00','1760030000003','1760010000024'),('1775025169573','2026-04-01 14:32:49','1760030000003','1760010000011'),('1775027273825','2026-04-01 15:07:53','1775027273819','1760010000011'),('1775029103018','2026-04-01 15:38:23','1775027273819','1760010000024'),('1775035779959','2026-04-01 17:29:39','1775027273819','1760010000022');
/*!40000 ALTER TABLE `members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notices`
--

DROP TABLE IF EXISTS `notices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notices` (
  `id` char(13) NOT NULL COMMENT '记录ID',
  `title` varchar(128) NOT NULL COMMENT '通知标题',
  `detail` text NOT NULL COMMENT '通知内容',
  `create_time` char(10) NOT NULL COMMENT '发布时间',
  `team_id` char(13) DEFAULT NULL COMMENT '发布社团',
  `is_top` int NOT NULL DEFAULT '0' COMMENT '是否置顶：0否 1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知公告';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notices`
--

LOCK TABLES `notices` WRITE;
/*!40000 ALTER TABLE `notices` DISABLE KEYS */;
INSERT INTO `notices` VALUES ('1760080000001','关于清明假期社团活动安排的说明','清明假期前后，校内社团活动原则上以校内场地为主。如需外出采风或开展志愿服务，请至少提前三天完成活动报备，并明确带队负责人。','2026-03-24',NULL,1),('1760080000002','社团经费报销材料提交提醒','请各社团于 2026 年 3 月 31 日前提交本月活动票据、签到表与活动总结，逾期将顺延至下月统一处理。系统管理员将在 4 月第一周集中审核。','2026-03-25',NULL,0),('1760080000003','机器人创意挑战赛分组名单公布','本周六的机器人创意挑战赛已完成分组，请参赛同学于周五晚前完成代码和电池检查，比赛当天 13:40 到创新实验室签到。','2026-03-26','1760030000001',0),('1760080000004','春日校园摄影采风路线更新','因樱花步道周边人流较大，本次采风新增艺术楼连廊与湖心平台两个取景点，集合地点仍为图书馆广场。','2026-03-26','1760030000002',0),('1760080000005','敬老院志愿服务注意事项','请报名同学于活动前一天领取志愿者胸牌，并准备一段简短自我介绍，方便与老人交流互动。协会将统一安排往返交通。','2026-03-27','1760030000003',0),('1760080000007','评论区文明交流倡议','为维护良好的交流环境，保障评论区健康、有序运行，请大家坚持文明发言、理性讨论，禁止辱骂、人身攻击、低俗刷屏及散布不实信息。管理员将对违规内容及时处理。','2026-03-27',NULL,1),('1760080000008','摄影社四月器材借用值班表','四月起器材借用室将在每周二、周四 18:30 至 20:30 开放，值班成员名单已在社群公告同步发布。','2026-03-27','1760030000002',0),('1760080000009','旧书换绿植公益集市招募说明','欢迎有活动主持、物资整理、海报设计经验的同学加入本次公益集市筹备组。招募截止到 4 月 3 日晚 21:00。','2026-03-27','1760030000003',0),('1775029193801','新媒体社成立啦','📢 号外号外！新媒体社正式成立啦！\n终于等到你！热爱新媒体的朋友们看过来👇\n🎬 你是否——\n喜欢用镜头记录生活？\n想学习公众号排版和运营？\n对短视频创作充满热情？\n渴望有一个展示自己作品的平台？\n✨ 新媒体社 通通满足你！\n我们是一个全新的社团，专注做校园里最酷的内容创作！不管你是小白还是大佬，只要你对新媒体感兴趣，我们都欢迎你！\n首批招募限30人，手慢无！\n📝 报名方式：\n现能在社团页面找到并申请\n⏰ 报名截止：4月25日\n有任何问题欢迎私信咨询～\n新媒体社，等你来发光！🌟','2026-04-01','1775027273819',0);
/*!40000 ALTER TABLE `notices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pay_logs`
--

DROP TABLE IF EXISTS `pay_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pay_logs` (
  `id` char(13) NOT NULL COMMENT '记录ID',
  `create_time` char(19) NOT NULL COMMENT '缴费时间',
  `total` double NOT NULL COMMENT '缴费金额',
  `team_id` char(13) NOT NULL COMMENT '收费社团',
  `user_id` char(13) NOT NULL COMMENT '缴费用户',
  PRIMARY KEY (`id`),
  KEY `team_id` (`team_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `pay_logs_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `pay_logs_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='费用记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pay_logs`
--

LOCK TABLES `pay_logs` WRITE;
/*!40000 ALTER TABLE `pay_logs` DISABLE KEYS */;
INSERT INTO `pay_logs` VALUES ('1760090000001','2026-03-02 18:10:00',30,'1760030000001','1760010000012'),('1760090000002','2026-03-04 19:20:00',30,'1760030000001','1760010000014'),('1760090000003','2026-03-06 12:05:00',25,'1760030000002','1760010000011'),('1760090000004','2026-03-08 15:16:00',20,'1760030000003','1760010000013'),('1760090000005','2026-03-08 15:18:00',20,'1760030000003','1760010000015'),('1760090000007','2026-03-11 18:40:00',25,'1760030000002','1760010000019'),('1760090000008','2026-03-12 19:18:00',25,'1760030000002','1760010000023'),('1760090000009','2026-03-14 10:26:00',20,'1760030000003','1760010000021'),('1775025260080','2026-04-01 14:34:20',10,'1760030000003','1760010000011'),('1775026327461','2026-04-01 14:52:07',10,'1760030000002','1760010000011'),('1775041991769','2026-04-01 19:13:11',10,'1775027273819','1760010000024'),
('1775042100001','2026-04-03 10:10:00',80,'1760030000001','1760010000018'),
('1775042100002','2026-04-03 10:20:00',90,'1760030000002','1760010000024'),
('1775042100003','2026-04-03 10:30:00',120,'1760030000003','1760010000016'),
('1775042100004','2026-04-03 10:40:00',110,'1775027273819','1760010000022');
/*!40000 ALTER TABLE `pay_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pay_expenses`
--

DROP TABLE IF EXISTS `pay_expenses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pay_expenses` (
  `id` char(13) NOT NULL COMMENT '记录ID',
  `create_time` char(19) NOT NULL COMMENT '消费时间',
  `total` double NOT NULL COMMENT '消费金额',
  `title` varchar(128) NOT NULL COMMENT '消费标题',
  `detail` varchar(600) NOT NULL COMMENT '消费说明',
  `team_id` char(13) NOT NULL COMMENT '所属社团',
  `handler_id` char(13) NOT NULL COMMENT '记录人',
  PRIMARY KEY (`id`),
  KEY `team_id` (`team_id`),
  KEY `handler_id` (`handler_id`),
  CONSTRAINT `pay_expenses_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `pay_expenses_ibfk_2` FOREIGN KEY (`handler_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消费明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pay_expenses`
--

LOCK TABLES `pay_expenses` WRITE;
/*!40000 ALTER TABLE `pay_expenses` DISABLE KEYS */;
INSERT INTO `pay_expenses` VALUES
('1760130000001','2026-03-05 18:30:00',120,'机器人零件采购','用于竞赛电机、轮胎和传感器补充','1760030000001','1760010000003'),
('1760130000002','2026-03-10 20:10:00',80,'场地布置物料','海报、指示牌与展板打印','1760030000002','1760010000004'),
('1760130000003','2026-03-18 10:40:00',150,'志愿服务物资','爱心礼包和便民工具补给','1760030000003','1760010000005'),
('1760130000004','2026-03-28 21:15:00',60,'摄影外拍交通','外拍路线交通与场地沟通费用','1760030000002','1760010000004'),
('1775026500001','2026-04-01 16:25:00',95,'新媒体活动道具','采访卡片、补光灯配件采购','1775027273819','1760010000011'),
('1775035800001','2026-04-02 10:05:00',45,'志愿者胸牌','新增胸牌和证件挂绳','1760030000003','1760010000005');
/*!40000 ALTER TABLE `pay_expenses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rules`
--

DROP TABLE IF EXISTS `rules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rules` (
  `id` char(13) NOT NULL,
  `title` varchar(128) NOT NULL,
  `content` longtext NOT NULL,
  `create_time` char(19) NOT NULL,
  `update_time` char(19) NOT NULL,
  `team_id` char(13) DEFAULT NULL,
  `user_id` char(13) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_rules_team` (`team_id`),
  KEY `idx_rules_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rules`
--

LOCK TABLES `rules` WRITE;
/*!40000 ALTER TABLE `rules` DISABLE KEYS */;
INSERT INTO `rules` VALUES ('1760100000001','社团公共空间使用规范','为保障公共活动教室与器材室的正常使用，所有社团须在活动结束后完成场地整理、设备归位和电源检查。夜间活动结束时间原则上不晚于 21:30。','2026-03-20 09:00:00','2026-03-20 09:00:00',NULL,'1760010000001'),('1760100000002','智能机器人社新成员培训制度','新成员入社后需在两周内完成一次焊接基础培训和一次编程环境配置培训。缺席两次及以上将延期参与正式项目组。','2026-03-21 19:30:00','2026-03-21 19:30:00','1760030000001','1760010000003'),('1760100000003','光影摄影社器材借用办法','相机、电池、存储卡等器材需在借用表登记后方可带离工作室。借用周期原则上不超过三天，归还时需由值班同学核验设备状态。','2026-03-22 13:20:00','2026-03-22 13:20:00','1760030000002','1760010000004'),('1760100000004','青年志愿者协会服务纪律','参与校外志愿服务时应统一着装、统一签到，不得擅自离队。活动中需尊重服务对象隐私，不得私自拍摄并公开发布未授权影像。','2026-03-23 10:10:00','2026-03-23 10:10:00','1760030000003','1760010000005'),('1760100000005','融媒体创作社选题评审流程','每周例会前需提交选题卡，内容包括主题、采访对象、预计时长和发布渠道。通过初审后方可进入脚本打磨与拍摄排期。','2026-03-24 18:40:00','2026-03-24 18:40:00','1760030000004','1760010000002'),('1760100000006','评论区管理规范','为维护良好的交流环境，保障评论区健康、有序运行，现制定如下管理规范：\n1. 文明发言，禁止发布辱骂、人身攻击、歧视性言论等不当内容。\n2. 鼓励围绕主题进行理性交流，禁止发布虚假信息、恶意引导或传播不实内容。\n3. 严禁发布涉政敏感、不良信息、广告推广、诈骗信息及其他违反法律法规的内容。\n4. 不得恶意刷屏、重复发帖、使用低俗或不雅语言，保持评论区整洁有序。\n5. 鼓励理性讨论，允许不同意见，但不得进行恶意争吵或刻意引战。\n6. 对违规评论，管理员有权删除；情节严重者，将限制或封禁账号。','2026-03-27 10:00:00','2026-03-27 10:00:00',NULL,'1760010000001');
/*!40000 ALTER TABLE `rules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team_types`
--

DROP TABLE IF EXISTS `team_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team_types` (
  `id` char(13) NOT NULL COMMENT '记录ID',
  `name` varchar(20) NOT NULL COMMENT '类型名称',
  `create_time` char(19) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社团类型';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team_types`
--

LOCK TABLES `team_types` WRITE;
/*!40000 ALTER TABLE `team_types` DISABLE KEYS */;
INSERT INTO `team_types` VALUES ('1760020000001','学术科技类','2026-01-06 09:00:00'),('1760020000002','文化艺术类','2026-01-06 09:05:00'),('1760020000003','公益实践类','2026-01-06 09:10:00'),('1775026450414','媒体新闻类','2026-04-01 14:54:10');
/*!40000 ALTER TABLE `team_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teams`
--

DROP TABLE IF EXISTS `teams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teams` (
  `id` char(13) NOT NULL COMMENT '记录ID',
  `name` varchar(20) NOT NULL COMMENT '社团名称',
  `create_time` char(10) NOT NULL COMMENT '成立时间',
  `total` int NOT NULL COMMENT '社团人数',
  `manager` char(13) NOT NULL COMMENT '社团负责人',
  `type_id` char(13) NOT NULL COMMENT '社团类型',
  `intro` text COMMENT '社团介绍',
  PRIMARY KEY (`id`),
  KEY `type_id` (`type_id`),
  CONSTRAINT `teams_ibfk_1` FOREIGN KEY (`type_id`) REFERENCES `team_types` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社团信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teams`
--

LOCK TABLES `teams` WRITE;
/*!40000 ALTER TABLE `teams` DISABLE KEYS */;
INSERT INTO `teams` VALUES ('1760030000001','智能机器人社','2026-01-08',5,'1760010000003','1760020000001','智能机器人社面向热爱机器人竞赛、嵌入式开发与智能硬件实践的同学，日常开展小车循迹、机械臂控制、传感器调试与竞赛项目训练。适合愿意动手、喜欢团队协作、希望把创意做成真实作品的同学加入。'),('1760030000002','光影摄影社','2026-01-08',5,'1760010000004','1760020000002','光影摄影社聚焦校园摄影、视觉表达与后期制作，定期组织主题外拍、作品点评、展览筹备和器材分享。无论是手机摄影新手，还是想系统提升构图与修图能力的同学，都能在这里找到适合自己的成长路径。'),('1760030000003','校园青年志愿者协会','2026-01-09',6,'1760010000005','1760020000003','校园青年志愿者协会以公益服务和社会实践为核心，长期开展敬老陪伴、环保倡议、校园文明宣传和大型活动志愿服务。社团注重责任感、沟通力和执行力培养，欢迎愿意持续参与公益行动的同学加入。'),('1775027273819','新媒体社','2026-04-01',3,'1760010000011','1775026450414','新媒体社围绕校园内容策划、公众号运营、短视频拍摄、海报设计与活动传播展开工作，适合对文案、采访、摄影、剪辑或品牌传播感兴趣的同学。社团鼓励跨岗位协作，让成员在真实项目中积累作品和运营经验。');
/*!40000 ALTER TABLE `teams` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` char(13) NOT NULL COMMENT '记录ID',
  `user_name` varchar(32) NOT NULL COMMENT '用户账号',
  `pass_word` varchar(32) NOT NULL COMMENT '用户密码',
  `name` varchar(20) DEFAULT NULL COMMENT '用户姓名',
  `gender` char(2) DEFAULT NULL COMMENT '用户性别',
  `age` int DEFAULT NULL COMMENT '用户年龄',
  `phone` char(11) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(64) DEFAULT NULL COMMENT '联系地址',
  `status` int NOT NULL COMMENT '状态',
  `create_time` char(19) NOT NULL COMMENT '创建时间',
  `type` int NOT NULL COMMENT '用户身份：0系统管理员，1社团管理员，2学生',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('1760010000001','sys_admin','123456','顾明哲','男',29,'13811230001','杭州市西湖区学院路 88 号',1,'2026-01-03 09:10:00',0,'avatar_h'),('1760010000002','club_media','123456','沈知夏','女',23,'13811230002','杭州市余杭区文一西路 269 号',1,'2026-01-05 10:12:00',2,'avatar_j'),('1760010000003','club_robot','123456','周砚洲','男',24,'13811230003','杭州市拱墅区莫干山路 689 号',1,'2026-01-05 10:18:00',1,'avatar_d'),('1760010000004','club_photo','123456','林嘉霏','女',22,'13811230004','杭州市滨江区江南大道 518 号',1,'2026-01-05 10:24:00',1,'avatar_i'),('1760010000005','club_volunteer','123456','程一帆','男',23,'13811230005','杭州市上城区解放东路 197 号',1,'2026-01-05 10:27:00',1,'avatar_g'),('1760010000011','student_wangyx','123456','王语棠','女',20,'13922180011','杭州师范大学仓前校区 5 号楼',1,'2026-02-10 08:30:00',1,'/association/uploads/avatars/1760010000011_1775019508741.png'),('1760010000012','student_chenzr','123456','陈知润','男',21,'13922180012','杭州电子科技大学下沙校区 7 号楼',1,'2026-02-10 08:33:00',2,'avatar_b'),('1760010000013','student_liym','123456','李聿绵','女',19,'13922180013','浙江工业大学朝晖校区 2 号楼',1,'2026-02-10 08:36:00',2,'avatar_c'),('1760010000014','student_heyc','123456','何奕辰','男',20,'13922180014','浙江工商大学下沙校区 4 号楼',1,'2026-02-10 08:40:00',2,'avatar_d'),('1760010000015','student_xumq','123456','徐沐晴','女',21,'13922180015','中国计量大学启明生活区 8 号楼',1,'2026-02-10 08:45:00',2,'avatar_e'),('1760010000016','student_pengxl','123456','彭星澜','男',20,'13922180016','浙江理工大学生活二区 3 号楼',1,'2026-02-10 08:49:00',2,'avatar_f'),('1760010000017','student_tangny','123456','唐宁语','女',19,'13922180017','浙江传媒学院钱塘校区 11 号楼',1,'2026-02-10 08:54:00',2,'avatar_j'),('1760010000018','student_luqf','123456','陆清帆','男',22,'13922180018','浙江大学紫金港校区丹阳青溪 17 舍',1,'2026-02-10 08:58:00',2,'avatar_h'),('1760010000019','student_linyr','123456','林悠然','女',20,'13922180019','杭州电子科技大学圣光机宿舍区',1,'2026-02-11 09:05:00',2,'avatar_i'),('1760010000020','student_sunhl','123456','孙和林','男',21,'13922180020','浙江工商大学金沙港生活园区',1,'2026-02-11 09:12:00',2,'avatar_b'),('1760010000021','student_qinmn','123456','秦沐宁','女',20,'13922180021','杭州师范大学诚园 9 号楼',1,'2026-02-11 09:18:00',2,'avatar_e'),('1760010000022','student_yuao','123456','袁安鸥','男',22,'13922180022','浙江工业大学屏峰校区东苑 6 号楼',1,'2026-02-11 09:24:00',2,'avatar_g'),('1760010000023','student_zhqf','123456','郑清芙','女',19,'13922180023','浙江财经大学学博园 2 幢',1,'2026-02-11 09:31:00',2,'avatar_c'),('1760010000024','student_guyl','123456','顾奕澜','男',20,'13922180024','中国美术学院象山校区学生公寓',0,'2026-02-11 09:36:00',2,'avatar_a');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-02 14:24:57
