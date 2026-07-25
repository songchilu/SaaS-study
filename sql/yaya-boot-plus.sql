-- 创建数据库
CREATE DATABASE IF NOT EXISTS `yaya-saas-plus` CHARACTER SET utf8mb4;

-- 切换数据库
USE `yaya-saas-plus`;


-- 菜单表
CREATE TABLE `sys_menu` (
    `menu_id`       BIGINT		    PRIMARY KEY     AUTO_INCREMENT  COMMENT '菜单ID,主键',
    `menu_title`    VARCHAR(100)    NOT NULL                        COMMENT '菜单标题',
    `menu_icon`     VARCHAR(100)    DEFAULT NULL                    COMMENT '菜单图标',
    `menu_type`     INT             NOT NULL                        COMMENT '菜单类型(1-目录,2-菜单(链接),3-按钮)',
    `perms`         VARCHAR(100)    DEFAULT NULL                    COMMENT '菜单权限,当menu_type为3时有效,为按钮添加权限标识',
    `menu_url`      VARCHAR(521)    DEFAULT NULL                    COMMENT '菜单跳转地址',
    `parent_id`     BIGINT          NOT NULL DEFAULT 0              COMMENT '父ID,最顶层从0开始',
    `sort`    	    INT             DEFAULT 1                       COMMENT '排序序号',
    `status`        INT             NOT NULL DEFAULT 1              COMMENT '状态(1-正常 0-停用)',
    `create_id`     BIGINT                                          COMMENT '创建人ID,关联用户表(sys_user)主键',
    `update_id`     BIGINT                                          COMMENT '更新人ID,关联用户表(sys_user)主键',
    `create_time`   DATETIME        DEFAULT NOW()                   COMMENT '创建时间',
    `update_time`   TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '设置当前时间，并且自动更新时间',
    KEY `idx_menu_title` (`menu_title`),
    KEY `idx_menu_type` (`menu_type`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_sort` (`sort`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 菜单数据
INSERT INTO `sys_menu` VALUES (1, '系统管理', 'iconfont icon-CRMEB-shezhi-xianxing', 1, NULL, '', 0, 1, 1, 1, 1, '2026-06-20 23:15:11', '2026-06-20 23:28:02');
INSERT INTO `sys_menu` VALUES (2, '用户管理', 'iconfont icon-CRMEB-yonghuyunying-xianxing1', 2, NULL, 'views/user-list.html', 1, 1, 1, 1, 1, '2026-06-20 23:17:24', '2026-06-20 23:27:24');
INSERT INTO `sys_menu` VALUES (3, '部门管理', 'iconfont icon-zhinengmofangicon_bumenguanli', 2, NULL, 'views/dept-list.html', 1, 2, 1, 1, 1, '2026-06-20 23:19:14', '2026-06-20 23:19:14');
INSERT INTO `sys_menu` VALUES (4, '角色管理', 'iconfont icon-CRMEB-chengjiaoyonghushu-xianxing', 2, NULL, 'views/role-list.html', 1, 3, 1, 1, 1, '2026-06-20 23:21:38', '2026-06-20 23:23:02');
INSERT INTO `sys_menu` VALUES (5, '菜单管理', 'iconfont icon-tubiao', 2, '', 'views/menu-list.html', 1, 5, 1, 1, 1, '2026-06-20 23:24:09', '2026-06-21 17:41:39');
INSERT INTO `sys_menu` VALUES (6, '监控管理', 'layui-icon layui-icon-bot', 1, '', '', 0, 2, 1, 1, 1, '2026-06-20 23:25:07', '2026-06-20 23:25:07');
INSERT INTO `sys_menu` VALUES (7, '登录日志', 'iconfont icon-CRMEB-yuangong-xianxing', 2, '', 'views/log-login.html', 6, 1, 1, 1, 1, '2026-06-20 23:25:42', '2026-06-20 23:25:42');
INSERT INTO `sys_menu` VALUES (8, '系统日志', 'iconfont icon-CRMEB-caiwujilu-xianxing', 2, '', 'views/log-sys.html', 6, 2, 1, 1, 1, '2026-06-20 23:26:22', '2026-06-20 23:26:22');
INSERT INTO `sys_menu` VALUES (9, '缓存管理', 'iconfont icon-CRMEB-miaoshahuodong-xianxing', 2, '', 'views/cache-list.html', 6, 3, 1, 1, 1, '2026-06-20 23:26:54', '2026-06-20 23:26:54');
INSERT INTO `sys_menu` VALUES (10, '公告管理', 'iconfont icon-CRMEB-mendiandingdan-xianxing', 1, '', '', 0, 3, 1, 1, 1, '2026-06-20 23:28:34', '2026-07-02 15:57:06');
INSERT INTO `sys_menu` VALUES (11, '类型管理', 'iconfont icon-CRMEB-daishenhe-mianxing', 2, '', 'views/notice-type-list.html', 10, 1, 1, 1, 1, '2026-06-20 23:29:11', '2026-06-26 10:49:52');
INSERT INTO `sys_menu` VALUES (12, '消息管理', 'iconfont icon-CRMEB-daichuliyonghufankui-xianxing', 2, '', 'views/notice-list.html', 10, 2, 1, 1, 1, '2026-06-20 23:30:08', '2026-07-02 15:56:56');
INSERT INTO `sys_menu` VALUES (13, '岗位管理', 'iconfont icon-CRMEB-lianjie', 2, '', 'views/post-list.html', 1, 4, 1, 1, 1, '2026-06-21 17:42:06', '2026-06-21 17:42:06');
INSERT INTO `sys_menu` VALUES (14, '添加类型', 'iconfont icon-CRMEB-daichuliyonghufankui-xianxing', 3, 'notice-type-add', '', 11, 1, 1, 1, 1, '2026-07-02 15:46:44', '2026-07-02 15:46:44');
INSERT INTO `sys_menu` VALUES (15, '删除类型', 'iconfont icon-CRMEB-qiandao-xianxing', 3, 'notice-type-del', '', 11, 2, 1, 1, 1, '2026-07-02 15:47:11', '2026-07-02 15:47:11');
INSERT INTO `sys_menu` VALUES (16, '更新类型', '', 3, 'notice-type-edit', '', 11, 3, 1, 1, 1, '2026-07-02 15:47:52', '2026-07-02 15:47:52');
INSERT INTO `sys_menu` VALUES (17, '搜索类型', '', 3, 'notice-type-search', '', 11, 4, 1, 1, 1, '2026-07-02 15:48:29', '2026-07-02 15:48:29');
INSERT INTO `sys_menu` VALUES (18, '添加消息', '', 3, 'notice-add', '', 12, 1, 1, 1, 1, '2026-07-02 15:49:37', '2026-07-02 15:49:37');
INSERT INTO `sys_menu` VALUES (19, '删除消息', '', 3, 'notice-del', '', 12, 2, 1, 1, 1, '2026-07-02 15:49:53', '2026-07-02 15:49:53');
INSERT INTO `sys_menu` VALUES (20, '更新消息', '', 3, 'notice-edit', '', 12, 3, 1, 1, 1, '2026-07-02 15:50:22', '2026-07-02 15:56:31');
INSERT INTO `sys_menu` VALUES (21, '搜索消息', '', 3, 'notice-seach', '', 12, 4, 1, 1, 1, '2026-07-02 15:57:46', '2026-07-02 15:57:46');
INSERT INTO `sys_menu` VALUES (22, '消息详情', '', 3, 'notice-detail', '', 12, 5, 1, 1, 1, '2026-07-02 15:58:29', '2026-07-02 15:58:29');
INSERT INTO `sys_menu` VALUES (23, '消息分配', '', 3, 'notice-assign', '', 12, 6, 1, 1, 1, '2026-07-02 15:59:52', '2026-07-02 15:59:52');
INSERT INTO `sys_menu` VALUES (24, '添加用户', 'iconfont icon-tubiao', 3, 'user-add', '', 2, 1, 1, 1, 1, '2026-07-02 16:02:34', '2026-07-02 16:03:08');
INSERT INTO `sys_menu` VALUES (25, '导入用户', 'layui-icon layui-icon-success', 3, 'user-import', '', 2, 2, 1, 1, 1, '2026-07-02 16:02:57', '2026-07-02 16:03:17');
INSERT INTO `sys_menu` VALUES (26, '删除单个用户', 'iconfont icon-CRMEB-yinliang-mianxing', 3, 'user-del', '', 2, 3, 1, 1, 1, '2026-07-02 16:03:54', '2026-07-02 16:03:54');
INSERT INTO `sys_menu` VALUES (27, '删除批量用户', 'iconfont icon-CRMEB-miaoshahuodong-xianxing', 3, 'user-batch-del', '', 2, 4, 1, 1, 1, '2026-07-02 16:04:21', '2026-07-02 16:06:04');
INSERT INTO `sys_menu` VALUES (28, '更新用户', 'iconfont icon-CRMEB-miaoshahuodong-xianxing', 3, 'user-edit', '', 2, 5, 1, 1, 1, '2026-07-02 16:04:49', '2026-07-02 16:04:49');
INSERT INTO `sys_menu` VALUES (29, '搜索用户', 'iconfont icon-CRMEB-kucunguanli-xianxing', 3, 'user-search', '', 2, 6, 1, 1, 1, '2026-07-02 16:05:25', '2026-07-02 16:05:25');
INSERT INTO `sys_menu` VALUES (30, '重置密码', 'iconfont icon-tubiao', 3, 'reset-pwd', '', 2, 7, 1, 1, 1, '2026-07-02 16:06:33', '2026-07-02 16:09:27');
INSERT INTO `sys_menu` VALUES (31, '用户解封', 'iconfont icon-CRMEB-APP-xianxing1', 3, 'user-enable', '', 2, 8, 1, 1, 1, '2026-07-02 16:07:49', '2026-07-02 16:09:33');
INSERT INTO `sys_menu` VALUES (32, '用户封禁', 'iconfont icon-upload', 3, 'user-disable', '', 2, 9, 1, 1, 1, '2026-07-02 16:08:22', '2026-07-02 16:09:39');
INSERT INTO `sys_menu` VALUES (33, '添加部门', 'iconfont icon-zhinengmofangicon_bumenguanli', 3, 'dept-add', '', 3, 1, 1, 1, 1, '2026-07-02 16:12:27', '2026-07-02 16:12:27');
INSERT INTO `sys_menu` VALUES (34, '删除部门', 'iconfont icon-tubiao', 3, 'dept-del', '', 3, 2, 1, 1, 1, '2026-07-02 16:12:49', '2026-07-02 16:12:49');
INSERT INTO `sys_menu` VALUES (35, '更新部门', 'iconfont icon-zhinengmofangicon_bumenguanli', 3, 'dept-edit', '', 3, 3, 1, 1, 1, '2026-07-02 16:13:09', '2026-07-02 16:13:09');
INSERT INTO `sys_menu` VALUES (36, '搜索部门', 'iconfont icon-CRMEB-yinliang-mianxing', 3, 'dept-search', '', 3, 4, 1, 1, 1, '2026-07-02 16:13:30', '2026-07-02 16:13:54');
INSERT INTO `sys_menu` VALUES (37, '添加角色', 'iconfont icon-CRMEB-miaoshahuodong-xianxing', 3, 'role-add', '', 4, 1, 1, 1, 1, '2026-07-02 16:14:45', '2026-07-02 16:14:45');
INSERT INTO `sys_menu` VALUES (38, '删除角色', 'iconfont icon-tubiao', 3, 'role-del', '', 4, 2, 1, 1, 1, '2026-07-02 16:15:04', '2026-07-02 16:15:04');
INSERT INTO `sys_menu` VALUES (39, '更新角色', 'iconfont icon-a-1_huaban1', 3, 'role-edit', '', 4, 3, 1, 1, 1, '2026-07-02 16:15:30', '2026-07-02 16:15:30');
INSERT INTO `sys_menu` VALUES (40, '搜索角色', 'layui-icon layui-icon-lock', 3, 'role-search', '', 4, 4, 1, 1, 1, '2026-07-02 16:15:54', '2026-07-02 16:15:54');
INSERT INTO `sys_menu` VALUES (41, '菜单授权', 'iconfont icon-xitongzujian', 3, 'role-auth', '', 4, 5, 1, 1, 1, '2026-07-02 16:16:22', '2026-07-02 16:16:22');
INSERT INTO `sys_menu` VALUES (42, '添加岗位', 'layui-icon layui-icon-folder', 3, 'post-add', '', 13, 1, 1, 1, 1, '2026-07-02 16:18:36', '2026-07-02 16:18:36');
INSERT INTO `sys_menu` VALUES (43, '删除岗位', 'layui-icon layui-icon-gitee', 3, 'post-del', '', 13, 2, 1, 1, 1, '2026-07-02 16:18:57', '2026-07-02 16:18:57');
INSERT INTO `sys_menu` VALUES (44, '更新岗位', 'iconfont icon-zhinengmofangicon_bumenguanli', 3, 'post-edit', '', 13, 3, 1, 1, 1, '2026-07-02 16:19:18', '2026-07-02 16:19:18');
INSERT INTO `sys_menu` VALUES (45, '搜索岗位', 'iconfont icon-yibiaopan', 3, 'post-search', '', 13, 4, 1, 1, 1, '2026-07-02 16:19:39', '2026-07-02 16:19:39');
INSERT INTO `sys_menu` VALUES (46, '添加菜单', 'layui-icon layui-icon-success', 3, 'menu-add', '', 5, 1, 1, 1, 1, '2026-07-02 16:20:13', '2026-07-02 16:20:13');
INSERT INTO `sys_menu` VALUES (47, '删除菜单', 'iconfont icon-tubiao', 3, 'menu-del', '', 5, 2, 1, 1, 1, '2026-07-02 16:20:35', '2026-07-02 16:20:35');
INSERT INTO `sys_menu` VALUES (48, '更新菜单', 'iconfont icon-CRMEB-APP-xianxing1', 3, 'menu-edit', '', 5, 3, 1, 1, 1, '2026-07-02 16:21:00', '2026-07-02 16:21:00');
INSERT INTO `sys_menu` VALUES (49, '搜索菜单', 'iconfont icon-CRMEB-biaoshi-xianxing', 3, 'menu-search', '', 5, 4, 1, 1, 1, '2026-07-02 16:21:24', '2026-07-02 16:21:24');
INSERT INTO `sys_menu` VALUES (50, '文件管理', 'iconfont icon-CRMEB-daichuliyonghufankui-mianxing', 2, '', 'views/file-list.html', 1, 6, 1, 1, 1, '2026-07-03 15:39:25', '2026-07-03 15:39:25');
INSERT INTO `sys_menu` VALUES (51, '文件查询', 'iconfont icon-zhinengmofangicon_bumenguanli', 3, 'file-search', '', 50, 1, 1, 1, 1, '2026-07-03 16:01:43', '2026-07-03 16:01:43');
INSERT INTO `sys_menu` VALUES (52, '密钥管理', 'iconfont icon-a-1_huaban1', 2, '', 'views/key-manager-list.html', 1, 7, 1, 1, 1, '2026-07-07 13:56:36', '2026-07-07 16:27:54');
INSERT INTO `sys_menu` VALUES (53, '密钥添加', 'iconfont icon-CRMEB-kucunguanli-xianxing', 3, 'key-add', '', 52, 1, 1, 1, 1, '2026-07-08 10:01:09', '2026-07-08 10:01:09');
INSERT INTO `sys_menu` VALUES (54, '密钥查询', 'iconfont icon-tubiao', 3, 'key-search', '', 52, 2, 1, 1, 1, '2026-07-08 10:01:31', '2026-07-08 10:01:31');
INSERT INTO `sys_menu` VALUES (55, '密钥删除', 'iconfont icon-upload', 3, 'key-del', '', 52, 3, 1, 1, 1, '2026-07-11 10:14:04', '2026-07-11 10:14:04');

-- 部门表
CREATE TABLE `sys_department` (
    `dept_id`      BIGINT	PRIMARY KEY  AUTO_INCREMENT     COMMENT '菜单ID,主键',
    `dept_name`    VARCHAR(100)     NOT NULL                COMMENT '部门名称',
    `dept_code`    VARCHAR(100)     NOT NULL                COMMENT '部门编号',
    `dept_logo`    VARCHAR(255)                             COMMENT '部门LOGO,只有一级部门需要LOGO(一级部门模拟租户)',
    `tree_path`    VARCHAR(512)                             COMMENT '祖宗ID列表,用逗号(,)分割,所有部门的祖宗ID都从最顶部开始,从0开始',
    `parent_id`    BIGINT           NOT NULL DEFAULT 0      COMMENT '父ID,最顶层从0开始',
    `sort`    	   INT              DEFAULT 1               COMMENT '排序序号',
    `status`       INT              NOT NULL DEFAULT 1      COMMENT '状态(1-正常 0-停用)',
    `is_deleted`   INT 			    DEFAULT 0 			    COMMENT '逻辑删除标识(1-已删除 0-未删除)',
    `create_id`    BIGINT                                   COMMENT '创建人ID,关联用户表(sys_user)主键',
    `update_id`    BIGINT                                   COMMENT '更新人ID,关联用户表(sys_user)主键',
    `create_time`  DATETIME         DEFAULT NOW()           COMMENT '创建时间',
    `update_time`  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '设置当前时间，并且自动更新时间',
    KEY `idx_dept_name` (`dept_name`),
    KEY `idx_dept_code` (`dept_code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_sort` (`sort`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 部门数据
INSERT INTO `sys_department` VALUES (1, 'YaYa', 'YaYa', '','0', 0, 1, 1, 0, 1, 1, '2026-04-09 11:27:14', '2026-04-09 11:27:14');

-- 角色表
CREATE TABLE `sys_role` (
    `role_id`      BIGINT PRIMARY KEY AUTO_INCREMENT     COMMENT '角色ID,主键',
    `role_name`    VARCHAR(100)    NOT NULL              COMMENT '角色名称',
    `role_code`    VARCHAR(100)    NOT NULL              COMMENT '角色编号',
    `dept_id`      BIGINT                                COMMENT '部门ID,关联部门表(sys_department)主键',
    `role_type`    INT             NOT NULL DEFAULT 0    COMMENT '角色类型(0-普通角色 1-管理角色)',
    `data_scope`   INT             DEFAULT 4             COMMENT '数据权限(1-所有数据 2-部门及子部门数据 3-本部门数据 4-本人数据 5-自定义部门数据)',
    `status`       INT             NOT NULL DEFAULT 1    COMMENT '状态(1-正常 0-停用)',
    `is_deleted`   INT             DEFAULT 0             COMMENT '逻辑删除标识(1-已删除 0-未删除)',
    `create_id`    BIGINT                                COMMENT '创建人ID,关联用户表(sys_user)主键',
    `update_id`    BIGINT                                COMMENT '更新人ID,关联用户表(sys_user)主键',
    `create_time`  DATETIME        DEFAULT NOW()         COMMENT '创建时间',
    `update_time`  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '设置当前时间，并且自动更新时间',
    KEY `idx_role_name` (`role_name`),
    KEY `idx_role_code` (`role_code`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 角色数据
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'ROOT', 1, 1, 1,1, 0, 1, 1, '2026-04-09 11:27:14', '2026-04-09 11:27:14');
INSERT INTO `sys_role` VALUES (2, '系统管理员', 'ADMIN', 1, 1, 1,1, 0, 1, 1, '2026-04-09 11:27:14', '2026-04-09 11:27:14');
INSERT INTO `sys_role` VALUES (3, '运营管理员', 'OPERATION', 1, 1, 1,1, 0, 1, 1, '2026-04-09 11:27:14', '2026-04-09 11:27:14');


-- 角色菜单关联表
CREATE TABLE `sys_role_menu` (
    `role_id`         BIGINT          NOT NULL        COMMENT '角色ID',
    `menu_id`         BIGINT          NOT NULL        COMMENT '菜单ID',
    PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';


-- 角色部门关联表(自定义数据权限)
CREATE TABLE `sys_role_dept`  (
    `role_id`         BIGINT          NOT NULL        COMMENT '角色ID',
    `dept_id`         BIGINT          NOT NULL        COMMENT '部门ID',
    PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '角色部门关联表(自定义数据权限)';


-- 用户表
CREATE TABLE `sys_user`(
    `user_id`        BIGINT    PRIMARY KEY AUTO_INCREMENT            COMMENT '用户ID,主键',
    `dept_id`        BIGINT                                          COMMENT '部门ID,关联部门表(sys_department)主键',
    `role_id`        BIGINT                                          COMMENT '角色ID,关联角色表(sys_role)主键',
    `username`       VARCHAR(100)    NOT NULL                        COMMENT '用户名',
    `password`       VARCHAR(255)    NOT NULL                        COMMENT '密码',
    `nickname`       VARCHAR(255)    NOT NULL                        COMMENT '昵称',
    `phone`          VARCHAR(50)                                     COMMENT '手机号',
    `email`          VARCHAR(255)                                    COMMENT '邮箱',
    `sex`            TINYINT(1)      DEFAULT 1                       COMMENT '性别(1-男,0-女,2-保密)',
    `avatar`         VARCHAR(255)    DEFAULT 'file/avatar_1.png'     COMMENT '头像地址',
    `expired_time`   DATETIME        NOT NULL                        COMMENT '过期时间',
    `remark`         VARCHAR(255)    DEFAULT NULL                    COMMENT '备注',
    `is_locked`      TINYINT(1)      DEFAULT 0                       COMMENT '账号是否被锁定(1-锁定 0-未锁定)',
    `is_enabled`     TINYINT(1)      DEFAULT 1                       COMMENT '账号是否可用(1-可用 0-不可用)',
    `is_deleted`     TINYINT(1)      DEFAULT 0                       COMMENT '逻辑删除标识(0-未删除 1-已删除)',
    `create_id`      BIGINT                                          COMMENT '创建人ID,关联用户表(sys_user)主键',
    `update_id`      BIGINT                                          COMMENT '更新人ID,关联用户表(sys_user)主键',
    `create_time`    DATETIME        DEFAULT NOW()                   COMMENT '创建时间',
    `update_time`    TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '设置当前时间，并且自动更新时间',
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_create_id` (`create_id`),
    KEY `idx_username` (`username`),
    KEY `idx_nickname` (`nickname`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '用户表';

-- 用户数据
INSERT INTO `sys_user` VALUES (1, 1,1, 'root','$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', '超级管理员', '','',1,'file/avatar_1.png', '2026-12-31 23:59:59', '最高权限',0,1,0,  1, 1, '2026-04-09 11:27:14', '2026-04-09 11:27:14');
INSERT INTO `sys_user` VALUES (2, 1,2, 'admin','$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', '系统管理员', '','',1,'file/avatar_2.png', '2026-12-31 23:59:59', '系统权限',0,1,0,  1, 1, '2026-04-10 08:42:06', '2026-04-09 11:27:14');
INSERT INTO `sys_user` VALUES (3, 1,3, 'operation','$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', '运营管理员','','',1,'file/avatar_3.png', '2026-12-31 23:59:59', '运营权限',0,1,0,  1, 1, '2026-04-10 10:00:24', '2026-04-09 11:27:14');


-- 岗位表
CREATE TABLE `sys_post` (
    `post_id`      BIGINT  PRIMARY KEY AUTO_INCREMENT   COMMENT '岗位ID,主键',
    `post_name`    VARCHAR(100)    NOT NULL             COMMENT '岗位名称',
    `post_code`    VARCHAR(100)    NOT NULL             COMMENT '岗位编号',
    `dept_id`      BIGINT                               COMMENT '部门ID,关联部门表(sys_department)主键',
    `status`       INT             NOT NULL DEFAULT 0   COMMENT '状态(1-正常 0-停用)',
    `is_deleted`   INT             DEFAULT 0            COMMENT '逻辑删除标识(1-已删除 0-未删除)',
    `create_id`    BIGINT                               COMMENT '创建人ID,关联用户表(sys_user)主键',
    `update_id`    BIGINT                               COMMENT '更新人ID,关联用户表(sys_user)主键',
    `create_time`  DATETIME        DEFAULT NOW()        COMMENT '创建时间',
    `update_time`  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '设置当前时间，并且自动更新时间',
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_create_id` (`create_id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

-- 用户岗位关联表
CREATE TABLE `sys_user_post` (
    `post_id`     BIGINT          NOT NULL      COMMENT '岗位ID',
    `user_id`     BIGINT          NOT NULL      COMMENT '用户ID',
    PRIMARY KEY (`post_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户岗位关联表';

-- 文件管理表
CREATE TABLE `sys_file` (
    `file_id`           BIGINT  PRIMARY KEY AUTO_INCREMENT  COMMENT '文件ID,主键',
    `file_name`         VARCHAR(255)    NOT NULL            COMMENT '文件名称',
    `file_local_url`    text            NOT NULL            COMMENT '文件保存到服务器的物理地址',
    `file_server_url`   text            NOT NULL            COMMENT '访问文件的服务器地址',
    `dept_id`           BIGINT          NOT NULL    	    COMMENT '部门ID',
    `create_id`         BIGINT          NOT NULL            COMMENT '操作用户ID',
    `create_time`       DATETIME        DEFAULT NOW()       COMMENT '上传时间',
    `update_time`       TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '设置当前时间，并且自动更新时间',
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_create_id` (`create_id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COMMENT='文件管理表';

-- 公告类型表
CREATE TABLE `sys_notice_type` (
    `notice_type_id`    BIGINT    PRIMARY KEY AUTO_INCREMENT  COMMENT '公告类型ID,主键',
    `notice_type_name`  VARCHAR(100)    NOT NULL              COMMENT '公告类型名称',
    `dept_id`           BIGINT          NOT NULL              COMMENT '部门ID',
    `create_id`         BIGINT                                COMMENT '创建人ID,关联用户表(sys_user)主键',
    `update_id`         BIGINT                                COMMENT '更新人ID,关联用户表(sys_user)主键',
    `create_time`       DATETIME        DEFAULT NOW()         COMMENT '上传时间',
    `update_time`       TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '设置当前时间，并且自动更新时间',
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_create_id` (`create_id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告类型表';

-- 公告类型数据  系统升级 系统公告 假期通知 公司新闻 其它


-- 公告表
CREATE TABLE `sys_notice` (
    `notice_id`         BIGINT  PRIMARY KEY AUTO_INCREMENT      COMMENT '公告ID,主键',
    `notice_title`      VARCHAR(100)    NOT NULL                COMMENT '公告标题',
    `notice_content`    LONGTEXT        NOT NULL                COMMENT '公告内容,可以是文字，图片，视频，音频，表格等多媒体内容',
    `notice_level`      TINYINT(1)      NOT NULL                COMMENT '公告级别(0-普通, 1-重要, 2-紧急)',
    `notice_type_id`    INT             NOT NULL                COMMENT '公告类型ID关联(sys_notice_type)表主键',
    `is_top`            TINYINT(1)      NOT NULL    DEFAULT '0' COMMENT '是否置顶(0-否, 1-是)',
    `dept_id`           BIGINT          NOT NULL                COMMENT '部门ID',
    `create_id`         BIGINT                                  COMMENT '创建人ID,关联用户表(sys_user)主键',
    `update_id`         BIGINT                                  COMMENT '更新人ID,关联用户表(sys_user)主键',
    `create_time`       DATETIME        DEFAULT NOW()           COMMENT '上传时间',
    `update_time`       TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '设置当前时间，并且自动更新时间',
    KEY `idx_notice_title` (`notice_title`),
    KEY `idx_notice_level` (`notice_level`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_create_id` (`create_id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 公告用户关联表
CREATE TABLE `sys_notice_user` (
    `notice_id`         BIGINT          NOT NULL      COMMENT '公告ID',
    `user_id`           BIGINT          NOT NULL      COMMENT '用户ID',
    `dept_id`           BIGINT          NOT NULL      COMMENT '部门ID',
    `is_read`           TINYINT(1)      DEFAULT '0'   COMMENT '读取状态(0-未读, 1-已读)',
    `read_time`         DATETIME                      COMMENT '阅读时间',
    PRIMARY KEY (`notice_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告用户关联表';

-- 日志表
CREATE TABLE `sys_log` (
    `log_id`            VARCHAR(255)  PRIMARY KEY           COMMENT '操作日志ID,主键',
    `business_name`     VARCHAR(255)                        COMMENT '业务/模块名称（如：用户管理-新增用户）',
    `log_type`          INT             NOT NULL            COMMENT '日志类型(1-登陆日志 2-其它操作日志)',
    `dept_id`           BIGINT                              COMMENT '操作用户部门ID',
    `create_id`         BIGINT                              COMMENT '操作用户ID',
    `request_url`       VARCHAR(255)                        COMMENT '请求地址',
    `request_params`    TEXT                                COMMENT '请求参数（JSON字符串）',
    `response_result`   TEXT                                COMMENT '响应结果（JSON字符串）',
    `execution_time`    BIGINT          DEFAULT NULL        COMMENT '方法执行耗时（毫秒）',
    `status`            TINYINT(1)      DEFAULT 1           COMMENT '请求状态(1-成功 0-失败)',
    `error_msg`         TEXT                                COMMENT '错误堆栈信息',
    `ip`                VARCHAR(50)     DEFAULT NULL        COMMENT '操作人IP地址',
    `address`           VARCHAR(50)     DEFAULT NULL        COMMENT 'IP对应的地址',
    `browser`           VARCHAR(100)                        COMMENT '客户端浏览器',
    `track_id`          VARCHAR(255)                        COMMENT '日志链路ID',
    `oper_time`         DATETIME        DEFAULT NOW()       COMMENT '操作时间',
    KEY `idx_create_id` (`create_id`),
    KEY `idx_log_type` (`log_type`),
    KEY `idx_status` (`status`),
    KEY `idx_oper_time` (`oper_time`),
    KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日志记录表';

-- 密钥管理表
CREATE TABLE `key_management` (
    `key_id`                BIGINT      PRIMARY KEY     AUTO_INCREMENT      COMMENT '自增序号（主键）',
    `dept_id`               BIGINT      NOT NULL                            COMMENT '顶层部门ID(模拟租户ID)',
    `public_key_content`    LONGTEXT                                        COMMENT '公钥内容',
    `private_key_content`   LONGTEXT                                        COMMENT '私钥内容',
    `create_time`           DATETIME    DEFAULT NOW()                       COMMENT '密钥创建时间',
    `update_time`           TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '设置当前时间，并且自动更新时间',
    `create_id`             BIGINT      NOT NULL COMMENT '创建人ID',
    `update_id`             BIGINT      NOT NULL COMMENT '更新人ID',
    `remark`                VARCHAR(512) DEFAULT NULL COMMENT '备注信息',
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_create_id` (`create_id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='密钥管理表';