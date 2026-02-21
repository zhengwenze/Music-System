/*
 Navicat Premium Dump SQL

 Source Server         : MySQL 8.0.18
 Source Server Type    : MySQL
 Source Server Version : 80018 (8.0.18)
 Source Host           : localhost:3306
 Source Schema         : music_dreamer

 Target Server Type    : MySQL
 Target Server Version : 80018 (8.0.18)
 File Encoding         : 65001

 Date: 30/12/2025 04:17:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for likelist
-- ----------------------------
DROP TABLE IF EXISTS `likelist`;
CREATE TABLE `likelist`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '收藏id',
  `userId` int(11) NULL DEFAULT NULL COMMENT '用户id',
  `listId` int(11) NULL DEFAULT NULL COMMENT '歌单id',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_list`(`userId` ASC, `listId` ASC) USING BTREE,
  INDEX `idx_userId`(`userId` ASC) USING BTREE,
  INDEX `idx_listId`(`listId` ASC) USING BTREE,
  CONSTRAINT `fk_likelist_song_list` FOREIGN KEY (`listId`) REFERENCES `song_list` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_likelist_user` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户歌单收藏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of likelist
-- ----------------------------

-- ----------------------------
-- Table structure for list_music
-- ----------------------------
DROP TABLE IF EXISTS `list_music`;
CREATE TABLE `list_music`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '关联id',
  `music` int(11) NOT NULL COMMENT '歌曲id',
  `listid` int(11) NOT NULL COMMENT '歌单id',
  `add_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_list_music`(`listid` ASC, `music` ASC) USING BTREE,
  INDEX `idx_music`(`music` ASC) USING BTREE,
  INDEX `idx_listid`(`listid` ASC) USING BTREE,
  CONSTRAINT `fk_list_music_music` FOREIGN KEY (`music`) REFERENCES `music` (`music_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_list_music_song_list` FOREIGN KEY (`listid`) REFERENCES `song_list` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '歌单歌曲关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of list_music
-- ----------------------------
INSERT INTO `list_music` VALUES (11, 192, 11, '2025-12-29 03:49:35');
INSERT INTO `list_music` VALUES (12, 193, 11, '2025-12-29 04:48:01');
INSERT INTO `list_music` VALUES (13, 194, 11, '2025-12-29 04:48:01');

-- ----------------------------
-- Table structure for log
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '日志id',
  `userName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `do_some` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作记录',
  `MusicName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作歌曲记录',
  `create_date` date NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_username`(`userName` ASC) USING BTREE,
  INDEX `idx_create_date`(`create_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of log
-- ----------------------------

-- ----------------------------
-- Table structure for msg
-- ----------------------------
DROP TABLE IF EXISTS `msg`;
CREATE TABLE `msg`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '消息id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息标题',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '接收用户id',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime NULL DEFAULT NULL,
  `isread` int(11) NULL DEFAULT 0,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_msg_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 355 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of msg
-- ----------------------------
INSERT INTO `msg` VALUES (226, '用户', 23, '可以实现已读消息和未读消息展示颜色不一样吗？？已读是灰色，未读是绿色。', '2025-12-27 12:59:58', 1, NULL);
INSERT INTO `msg` VALUES (227, '用户', 31, '可以实现已读消息和未读消息展示颜色不一样吗？？已读是灰色，未读是绿色。', '2025-12-27 12:59:58', 0, NULL);
INSERT INTO `msg` VALUES (228, '用户', 32, '可以实现已读消息和未读消息展示颜色不一样吗？？已读是灰色，未读是绿色。', '2025-12-27 12:59:58', 0, NULL);
INSERT INTO `msg` VALUES (236, '用户', 40, '可以实现已读消息和未读消息展示颜色不一样吗？？已读是灰色，未读是绿色。', '2025-12-27 12:59:58', 0, NULL);
INSERT INTO `msg` VALUES (237, '歌手收到', 23, '歌手收到', '2025-12-27 13:01:25', 1, NULL);
INSERT INTO `msg` VALUES (238, '歌手收到', 31, '歌手收到', '2025-12-27 13:01:25', 0, NULL);
INSERT INTO `msg` VALUES (239, '歌手收到', 32, '歌手收到', '2025-12-27 13:01:25', 0, NULL);
INSERT INTO `msg` VALUES (247, '歌手收到', 40, '歌手收到', '2025-12-27 13:01:25', 0, NULL);
INSERT INTO `msg` VALUES (248, '用户user收到', 41, '用户user收到', '2025-12-27 13:01:54', 0, NULL);
INSERT INTO `msg` VALUES (249, '用户user收到', 42, '用户user收到', '2025-12-27 13:01:54', 0, NULL);
INSERT INTO `msg` VALUES (250, '用户user收到', 43, '用户user收到', '2025-12-27 13:01:54', 0, NULL);
INSERT INTO `msg` VALUES (258, '用户user收到', 51, '用户user收到', '2025-12-27 13:01:54', 0, NULL);
INSERT INTO `msg` VALUES (259, 'zwz收到', 2, 'zwz收到zwz收到zwz收到zwz收到', '2025-12-27 13:02:48', 1, NULL);
INSERT INTO `msg` VALUES (260, 'hhh', 2, 'hhh', '2025-12-27 13:11:43', 1, NULL);
INSERT INTO `msg` VALUES (261, '111', 2, '111', '2025-12-27 13:12:31', 1, NULL);
INSERT INTO `msg` VALUES (262, '你好zwz', 2, '你好zwz', '2025-12-27 13:15:30', 1, NULL);
INSERT INTO `msg` VALUES (263, '你好zwz', 2, '你好zwz你好zwz你好zwz你好zwz你好zwz你好zwz', '2025-12-27 13:15:51', 1, NULL);
INSERT INTO `msg` VALUES (264, 'zwz050418', 2, '测试zwz050418', '2025-12-27 13:20:12', 1, NULL);
INSERT INTO `msg` VALUES (265, 'zzwwzz', 1, 'zzwwzz', '2025-12-27 13:22:22', 0, NULL);
INSERT INTO `msg` VALUES (266, 'zzwwzz', 2, 'zzwwzz', '2025-12-27 13:22:59', 1, NULL);
INSERT INTO `msg` VALUES (267, 'zzwwzzzzwwzz', 2, 'zzwwzzzzwwzzzzwwzz', '2025-12-27 13:23:13', 1, NULL);
INSERT INTO `msg` VALUES (268, '全体用户收到user', 22, '全体用户收到', '2025-12-27 14:41:11', 1, NULL);
INSERT INTO `msg` VALUES (269, '全体用户收到user', 41, '全体用户收到', '2025-12-27 14:41:11', 0, NULL);
INSERT INTO `msg` VALUES (270, '全体用户收到user', 42, '全体用户收到', '2025-12-27 14:41:11', 0, NULL);
INSERT INTO `msg` VALUES (271, '全体用户收到user', 43, '全体用户收到', '2025-12-27 14:41:11', 0, NULL);
INSERT INTO `msg` VALUES (279, '全体用户收到user', 51, '全体用户收到', '2025-12-27 14:41:11', 0, NULL);
INSERT INTO `msg` VALUES (280, 'hhhjhh', 22, '哈哈哈哈', '2025-12-27 14:44:10', 1, NULL);
INSERT INTO `msg` VALUES (281, 'hhhjhh', 41, '哈哈哈哈', '2025-12-27 14:44:10', 0, NULL);
INSERT INTO `msg` VALUES (282, 'hhhjhh', 42, '哈哈哈哈', '2025-12-27 14:44:10', 0, NULL);
INSERT INTO `msg` VALUES (283, 'hhhjhh', 43, '哈哈哈哈', '2025-12-27 14:44:10', 0, NULL);
INSERT INTO `msg` VALUES (291, 'hhhjhh', 51, '哈哈哈哈', '2025-12-27 14:44:10', 0, NULL);
INSERT INTO `msg` VALUES (292, '你是谁', 52, '你是谁admin1', '2025-12-27 14:45:39', 0, NULL);
INSERT INTO `msg` VALUES (293, '特定', 22, '111', '2025-12-27 14:46:43', 1, NULL);
INSERT INTO `msg` VALUES (294, '已读', 22, '已读', '2025-12-27 14:47:16', 1, NULL);
INSERT INTO `msg` VALUES (295, '112213', 22, '123123', '2025-12-28 19:19:29', 1, NULL);
INSERT INTO `msg` VALUES (296, '112213', 41, '123123', '2025-12-28 19:19:29', 0, NULL);
INSERT INTO `msg` VALUES (297, '112213', 42, '123123', '2025-12-28 19:19:29', 0, NULL);
INSERT INTO `msg` VALUES (298, '112213', 43, '123123', '2025-12-28 19:19:29', 0, NULL);
INSERT INTO `msg` VALUES (306, '112213', 51, '123123', '2025-12-28 19:19:29', 0, NULL);
INSERT INTO `msg` VALUES (307, '所有人收到', 22, '所有人收到', '2025-12-28 19:22:11', 1, NULL);
INSERT INTO `msg` VALUES (308, '所有人收到', 41, '所有人收到', '2025-12-28 19:22:11', 0, NULL);
INSERT INTO `msg` VALUES (309, '所有人收到', 42, '所有人收到', '2025-12-28 19:22:11', 0, NULL);
INSERT INTO `msg` VALUES (310, '所有人收到', 43, '所有人收到', '2025-12-28 19:22:11', 0, NULL);
INSERT INTO `msg` VALUES (318, '所有人收到', 51, '所有人收到', '2025-12-28 19:22:11', 0, NULL);
INSERT INTO `msg` VALUES (319, '全体歌手111', 23, '全体歌手111', '2025-12-28 19:51:25', 1, NULL);
INSERT INTO `msg` VALUES (320, '全体歌手111', 31, '全体歌手111', '2025-12-28 19:51:25', 0, NULL);
INSERT INTO `msg` VALUES (321, '全体歌手111', 32, '全体歌手111', '2025-12-28 19:51:25', 0, NULL);
INSERT INTO `msg` VALUES (322, '全体歌手111', 40, '全体歌手111', '2025-12-28 19:51:25', 0, NULL);
INSERT INTO `msg` VALUES (323, '向管理员111发送', 164, '向管理员111发送向管理员111发送向管理员111发送向管理员111发送', '2025-12-28 19:53:11', 1, NULL);
INSERT INTO `msg` VALUES (324, '全体用户，你们好', 22, '全体用户，你们好', '2025-12-28 21:04:42', 1, NULL);
INSERT INTO `msg` VALUES (325, '全体用户，你们好', 41, '全体用户，你们好', '2025-12-28 21:04:42', 0, NULL);
INSERT INTO `msg` VALUES (326, '全体用户，你们好', 42, '全体用户，你们好', '2025-12-28 21:04:42', 0, NULL);
INSERT INTO `msg` VALUES (327, '全体用户，你们好', 43, '全体用户，你们好', '2025-12-28 21:04:42', 0, NULL);
INSERT INTO `msg` VALUES (328, '全体用户，你们好', 51, '全体用户，你们好', '2025-12-28 21:04:42', 0, NULL);
INSERT INTO `msg` VALUES (329, '全体用户，你们好', 165, '全体用户，你们好', '2025-12-28 21:04:42', 0, NULL);
INSERT INTO `msg` VALUES (330, '全体用户，你们好', 166, '全体用户，你们好', '2025-12-28 21:04:42', 0, NULL);
INSERT INTO `msg` VALUES (331, '全体用户，你们好', 167, '全体用户，你们好', '2025-12-28 21:04:42', 0, NULL);
INSERT INTO `msg` VALUES (332, '全体用户，你们好', 169, '全体用户，你们好', '2025-12-28 21:04:42', 0, NULL);
INSERT INTO `msg` VALUES (333, '111', 22, 'neirou', '2025-12-28 23:31:27', 1, NULL);
INSERT INTO `msg` VALUES (334, '111', 41, 'neirou', '2025-12-28 23:31:27', 0, NULL);
INSERT INTO `msg` VALUES (335, '111', 42, 'neirou', '2025-12-28 23:31:27', 0, NULL);
INSERT INTO `msg` VALUES (336, '111', 43, 'neirou', '2025-12-28 23:31:27', 0, NULL);
INSERT INTO `msg` VALUES (337, '111', 51, 'neirou', '2025-12-28 23:31:27', 0, NULL);
INSERT INTO `msg` VALUES (338, '111', 165, 'neirou', '2025-12-28 23:31:27', 0, NULL);
INSERT INTO `msg` VALUES (339, '111', 166, 'neirou', '2025-12-28 23:31:27', 0, NULL);
INSERT INTO `msg` VALUES (340, '111', 167, 'neirou', '2025-12-28 23:31:27', 0, NULL);
INSERT INTO `msg` VALUES (341, '111', 169, 'neirou', '2025-12-28 23:31:27', 0, NULL);
INSERT INTO `msg` VALUES (342, '你好admin、111', 23, '你好admin、111你好admin、111你好admin、111你好admin、111', '2025-12-28 23:33:22', 1, NULL);
INSERT INTO `msg` VALUES (343, 'admin111', 164, 'admin111', '2025-12-29 01:57:23', 0, NULL);
INSERT INTO `msg` VALUES (344, 'admin', 164, 'admin', '2025-12-29 03:36:08', 0, NULL);
INSERT INTO `msg` VALUES (345, '你好', 23, '发送消息给singer', '2025-12-29 11:00:57', 1, NULL);
INSERT INTO `msg` VALUES (346, '发布新歌', 22, '《新歌首发》', '2025-12-29 11:03:11', 1, NULL);
INSERT INTO `msg` VALUES (347, '发布新歌', 41, '《新歌首发》', '2025-12-29 11:03:11', 0, NULL);
INSERT INTO `msg` VALUES (348, '发布新歌', 42, '《新歌首发》', '2025-12-29 11:03:11', 0, NULL);
INSERT INTO `msg` VALUES (349, '发布新歌', 43, '《新歌首发》', '2025-12-29 11:03:11', 0, NULL);
INSERT INTO `msg` VALUES (350, '发布新歌', 51, '《新歌首发》', '2025-12-29 11:03:11', 0, NULL);
INSERT INTO `msg` VALUES (351, '发布新歌', 165, '《新歌首发》', '2025-12-29 11:03:11', 0, NULL);
INSERT INTO `msg` VALUES (352, '发布新歌', 166, '《新歌首发》', '2025-12-29 11:03:11', 0, NULL);
INSERT INTO `msg` VALUES (353, '发布新歌', 167, '《新歌首发》', '2025-12-29 11:03:11', 0, NULL);
INSERT INTO `msg` VALUES (354, '发布新歌', 169, '《新歌首发》', '2025-12-29 11:03:11', 0, NULL);

-- ----------------------------
-- Table structure for music
-- ----------------------------
DROP TABLE IF EXISTS `music`;
CREATE TABLE `music`  (
  `music_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '歌曲id',
  `from_singer` int(11) NOT NULL COMMENT '对应的用户',
  `music_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '歌曲名',
  `music_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '地址（对应UID名）',
  `activation` int(11) NOT NULL DEFAULT 0 COMMENT '是否激活(0激活,1用户锁定,2管理员锁定)',
  `listen_numb` int(11) NOT NULL DEFAULT 0 COMMENT '播放量',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '歌曲封面url',
  `timelength` int(11) NULL DEFAULT NULL COMMENT '歌曲时长（秒）',
  `create_time` date NULL DEFAULT NULL COMMENT '创建时间',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签',
  `lyric` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '歌词文件地址',
  `singer_username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`music_id`) USING BTREE,
  INDEX `idx_music_name`(`music_name` ASC) USING BTREE,
  INDEX `idx_from_singer`(`from_singer` ASC) USING BTREE,
  CONSTRAINT `fk_music_user` FOREIGN KEY (`from_singer`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 196 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '歌曲表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of music
-- ----------------------------
INSERT INTO `music` VALUES (181, 23, '梦想启航', '/music/singer111_song1.mp3', 1, 1280, 'http://localhost:8089/file/image/9d30a4b1-27a3-4bde-a2f3-ff4efe494386.png', 235, '2024-05-12', '流行,励志,正能量', '/lyrics/singer111_song1.lrc', 'singer111');
INSERT INTO `music` VALUES (182, 23, '静谧时光', '/music/singer111_song2.mp3', 1, 920, 'http://localhost:8089/file/image/501475ca-ca56-4e5e-8a8e-027d08cee59b.png', 188, '2024-06-18', '民谣,安静,治愈', '/lyrics/singer111_song2.lrc', 'singer111');
INSERT INTO `music` VALUES (190, 23, 'Baby', 'http://localhost:8089/file/music/0acb79fd-13a3-4d3b-932d-1c3c6b4206fd.mp3', 1, 12, 'http://localhost:8089/file/image/f6f4a7c4-c47e-426d-90de-4a80f2f392e0.png', 240, '2025-12-28', '民谣,安静,治愈', NULL, 'singer111');
INSERT INTO `music` VALUES (191, 23, 'ABA', 'http://localhost:8089/file/music/ecd9a86e-d90c-465b-b788-816876b5a0b0.mp3', 1, 0, 'http://localhost:8089/file/image/01b233f3-ae03-47ed-a202-dad33f48b33a.png', 240, '2025-12-28', NULL, NULL, 'singer111');
INSERT INTO `music` VALUES (192, 172, 'Hello', 'http://localhost:8089/file/music/70176781-df7f-4e54-aed6-55cd33c1fe13.mp3', 0, 0, 'http://localhost:8089/file/image/6aadeab8-2a76-4b13-b4a6-4e9c3b24a65c.png', 240, '2025-12-29', NULL, NULL, 'Adele');
INSERT INTO `music` VALUES (193, 172, 'Rolling in the Deep', 'http://localhost:8089/file/music/5bd735b5-2fe1-47e1-9788-c557ba1e46d0.mp3', 1, 0, 'http://localhost:8089/file/image/1b94f477-d8ed-4869-ab63-0ac8ed626f5c.png', NULL, '2025-12-29', NULL, NULL, 'Adele');
INSERT INTO `music` VALUES (194, 172, 'Someone Like You', 'http://localhost:8089/file/music/241e56d3-735f-4a48-b0db-521f81e7ae95.mp3', 1, 0, 'http://localhost:8089/file/image/d4e0d9c1-34b8-4e35-a2f1-3a9f82ee4f1b.png', NULL, '2025-12-29', NULL, NULL, 'Adele');
INSERT INTO `music` VALUES (195, 23, '111', 'http://localhost:8089/file/music/0c5626fa-1ddf-408f-92db-080714a786b4.mp3', 0, 0, 'http://localhost:8089/file/image/79c8137b-52e7-4290-912b-54d0345c90d8.png', NULL, '2025-12-29', NULL, NULL, 'singer111');

-- ----------------------------
-- Table structure for mylike
-- ----------------------------
DROP TABLE IF EXISTS `mylike`;
CREATE TABLE `mylike`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '收藏id',
  `music` int(11) NOT NULL COMMENT '歌曲id',
  `user` int(11) NOT NULL COMMENT '用户id',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_music`(`user` ASC, `music` ASC) USING BTREE,
  INDEX `idx_music`(`music` ASC) USING BTREE,
  INDEX `idx_user`(`user` ASC) USING BTREE,
  CONSTRAINT `fk_mylike_music` FOREIGN KEY (`music`) REFERENCES `music` (`music_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_mylike_user` FOREIGN KEY (`user`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户歌曲收藏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of mylike
-- ----------------------------
INSERT INTO `mylike` VALUES (17, 190, 22, '2025-12-29 04:14:36');

-- ----------------------------
-- Table structure for song_list
-- ----------------------------
DROP TABLE IF EXISTS `song_list`;
CREATE TABLE `song_list`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '歌单id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '歌单名称',
  `user` int(11) NULL DEFAULT NULL COMMENT '创建用户id',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '歌单封面',
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '歌单描述',
  `create_date` date NULL DEFAULT NULL COMMENT '创建时间',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user` ASC) USING BTREE,
  CONSTRAINT `fk_song_list_user` FOREIGN KEY (`user`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '歌单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of song_list
-- ----------------------------
INSERT INTO `song_list` VALUES (1, '歌单A01', 2, 'http://localhost:8089/file/image/9d30a4b1-27a3-4bde-a2f3-ff4efe494386.png', '这是简介这是简介这是简介', '2022-03-03', '这里可以添加标签 随便写写就行');
INSERT INTO `song_list` VALUES (2, '摇滚金曲002', 2, 'https://gxhoss.oss-cn-beijing.aliyuncs.com/image/u%3D1234567890%2C9876543210%26fm%3D193%26fmt%3DJPEG.jpg', '经典摇滚歌曲合集，让你尽情释放', '2022-04-15', '摇滚,经典,热血');
INSERT INTO `song_list` VALUES (3, '流行榜单003', 2, 'https://gxhoss.oss-cn-beijing.aliyuncs.com/image/u%3D5555555555%2C6666666666%26fm%3D193%26fmt%3DJPEG.jpg', '最新流行歌曲排行榜，紧跟音乐潮流', '2022-05-20', '流行,热门,榜单');
INSERT INTO `song_list` VALUES (4, '轻音乐合集004', 2, 'https://gxhoss.oss-cn-beijing.aliyuncs.com/image/u%3D7777777777%2C8888888888%26fm%3D193%26fmt%3DJPEG.jpg', '舒缓轻音乐，放松身心的最佳选择', '2022-06-08', '轻音乐,放松,治愈');
INSERT INTO `song_list` VALUES (7, '欧美流行', 1, 'http://localhost:8089/file/image/6aadeab8-2a76-4b13-b4a6-4e9c3b24a65c.png', '欧美流行音乐', '2025-12-28', '欧美流行音乐');
INSERT INTO `song_list` VALUES (8, '歌单66', 1, 'http://localhost:8089/file/image/8f98ee26-b8ce-4835-8c32-5743c079fc29.png', NULL, '2025-12-28', NULL);
INSERT INTO `song_list` VALUES (11, '我的歌单2', 22, 'http://localhost:8089/file/image/dfe72a33-da8e-4805-a37a-885bc87ab6cb.png', '用户user111的歌单2号', '2025-12-28', '歌单2号');
INSERT INTO `song_list` VALUES (12, '港乐', 22, 'http://localhost:8089/file/image/0afb0f7f-f482-4032-9b71-240a4b48c8bd.png', 'hongkong', '2025-12-28', 'good');
INSERT INTO `song_list` VALUES (13, 'hello', 22, 'http://localhost:8089/file/image/0f424a0c-057e-429e-9dd3-85388cdd0736.png', 'ada', '2025-12-28', 'sadsa');
INSERT INTO `song_list` VALUES (15, '1234', 22, 'http://localhost:8089/file/image/d40d4e7e-1fd2-41af-9fa5-112afad54ab4.png', NULL, '2025-12-29', NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱（不重）',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号（不重复）',
  `role` int(11) NULL DEFAULT 2 COMMENT '角色（0为管理员1为歌手2为普通用户）',
  `activation` int(11) NULL DEFAULT 0 COMMENT '是否激活（针对歌手）',
  `create_time` date NULL DEFAULT NULL COMMENT '创建时间',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像url',
  `about` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户简介',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `idx_email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `idx_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 174 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'a866ffb45a93efe748dcd7156cb04366', '123@qq.com', '17811111133', 0, 1, '2025-08-09', 'http://localhost:8089/file/image/9d30a4b1-27a3-4bde-a2f3-ff4efe494386.png', '我是简介');
INSERT INTO `user` VALUES (2, 'zwz050418', '9fd383f12dca20d59414369c48b607ad', '17590522997@qq.com', '17590522997', 0, 1, '2025-12-21', 'http://localhost:8089/file/image/a76c3089-a5ce-4d30-b034-edd150991238.png', NULL);
INSERT INTO `user` VALUES (22, 'user111', '0c534ed3fff7d2bfd32ee19d84644e3f', 'user666@qq.comm', '181111111234', 2, 1, '2025-12-25', 'http://localhost:8089/file/image/9d30a4b1-27a3-4bde-a2f3-ff4efe494386.png', '我是用户111哈哈哈哈哈哈');
INSERT INTO `user` VALUES (23, 'singer111', 'd951d4f8b350f1f74e53e8dbe68c6cc3', 'singer115516@qq.com', '18111227121', 1, 1, '2025-12-25', 'http://localhost:8089/file/image/e80fd04d-4d51-4ca7-9c0b-808d59994d80.png', '我是歌手singer111歌手');
INSERT INTO `user` VALUES (31, 'singer0242', 'b4d0472939a306d29d5615b1373d39d0', 'singer01324231111@example.com', '18100000088', 1, 0, '2025-12-25', 'http://localhost:8089/file/image/9d30a4b1-27a3-4bde-a2f3-ff4efe494386.png', '歌手简介001');
INSERT INTO `user` VALUES (32, 'singer002', 'a87ff679a2f3e71d9181a67b7542122c', 'updated_singer002@example.com', '13800138002', 1, 1, '2025-12-25', 'http://localhost:8089/file/image/501475ca-ca56-4e5e-8a8e-027d08cee59b.png', '歌手简介002');
INSERT INTO `user` VALUES (40, 'singer010', '3d2172418ce3c60bba369f28e399c30a', 'singer010@example.com', '18100000010', 1, 1, '2025-12-25', 'http://localhost:8089/file/image/f6f4a7c4-c47e-426d-90de-4a80f2f392e0.png', '歌手简介010');
INSERT INTO `user` VALUES (41, 'user041', 'e10adc3949ba59abbe56e057f20f883e', 'user011@example.com', '18100000041', 2, 1, '2025-12-25', 'http://localhost:8089/file/image/01b233f3-ae03-47ed-a202-dad33f48b33a.png', '普通用户041');
INSERT INTO `user` VALUES (42, 'user042', 'c33367701511b4f6020ec61ded352059', 'user042@example.com', '18100000042', 2, 0, '2025-12-25', 'http://localhost:8089/file/image/9d30a4b1-27a3-4bde-a2f3-ff4efe494386.png', '普通用户042');
INSERT INTO `user` VALUES (43, 'user043', '698d51a19d8a121ce581499d7b701668', 'user043@example.com', '18100000043', 2, 0, '2025-12-25', 'http://localhost:8089/file/image/501475ca-ca56-4e5e-8a8e-027d08cee59b.png', '普通用户043');
INSERT INTO `user` VALUES (51, 'user222', '0fe4305f97b115279f779548b7c68657', 'user222@qq.com', '18910302222', 2, 1, '2025-12-25', 'http://localhost:8089/file/image/f6f4a7c4-c47e-426d-90de-4a80f2f392e0.png', NULL);
INSERT INTO `user` VALUES (52, 'admin11', 'e020590f0e18cd6053d7ae0e0a507609', 'admin11@qq.com', '17566668888', 0, 1, '2025-12-26', 'http://localhost:8089/file/image/01b233f3-ae03-47ed-a202-dad33f48b33a.png', NULL);
INSERT INTO `user` VALUES (53, 'admin1', 'e00cf25ad42683b3df678c61f42c6bda', 'admin1@qq.com', '18911112222', 0, 1, '2025-12-27', 'http://localhost:8089/file/image/a76c3089-a5ce-4d30-b034-edd150991238.png', NULL);
INSERT INTO `user` VALUES (164, 'admin111', 'bbad8d72c1fac1d081727158807a8798', 'admin111@qq.com', '19111588111', 0, 1, '2025-12-28', 'http://localhost:8089/file/image/a76c3089-a5ce-4d30-b034-edd150991238.png', '我是admin111');
INSERT INTO `user` VALUES (165, 'user11', '03aa1a0b0375b0461c1b8f35b234e67a', 'user1111@qq.com', '18111112341', 2, 1, '2025-12-28', NULL, NULL);
INSERT INTO `user` VALUES (166, 'user66', 'ee8c50f66027b5867b029f14d8cc6566', 'user66@qq.com', '18611115555', 2, 1, '2025-12-28', NULL, NULL);
INSERT INTO `user` VALUES (167, 'user115', 'f978328d425bb3100da67c3328e63cc1', 'user1455@qq.com', '18111111115', 2, 1, '2025-12-28', NULL, NULL);
INSERT INTO `user` VALUES (168, 'singer155', 'de157de31cf89ada2e03f8cde865cc17', 'singer155@qq.com', '18111227166', 1, 1, '2025-12-28', 'http://localhost:8089/file/image/f6f4a7c4-c47e-426d-90de-4a80f2f392e0.png', NULL);
INSERT INTO `user` VALUES (169, 'user117', '7f6ce5fb3e7de92d09f6b00a6b4d0c62', 'user117@aa.com', '18726351425', 2, 1, '2025-12-28', NULL, NULL);
INSERT INTO `user` VALUES (170, 'singer166', '37c0463330c2001015600fe4a02b2367', 'singer11551@qq.com', '18111227169', 1, 1, '2025-12-28', NULL, NULL);
INSERT INTO `user` VALUES (171, 'singer888', '3e6ba0ccaba71cabf5d759bb5b0ef1d8', 'singer11988@qq.com', '18111227188', 1, 1, '2025-12-28', NULL, NULL);
INSERT INTO `user` VALUES (172, 'Adele', 'e10adc3949ba59abbe56e057f20f883e', 'Adele123456@qq.com', '18910207899', 1, 1, '2025-12-29', 'http://localhost:8089/file/image/26cd3469-ef40-4aa1-9eab-b276ee7a99cb.png', '阿黛尔·阿德金斯 Adele');
INSERT INTO `user` VALUES (173, 'yyp1', 'f1c26da535b01de6a01a5cefc166eb5e', 'yypAdele123456@qq.com', '13939043195', 2, 0, '2025-12-30', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
