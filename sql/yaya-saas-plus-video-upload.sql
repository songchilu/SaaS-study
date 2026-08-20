-- ============================================================
-- YaYa-SaaS-Plus 视频上传功能 - 菜单数据
-- 适用场景: 已部署数据库升级(全新安装已包含在 yaya-boot-plus.sql 中)
-- 前提: 菜单ID 56~59 在当前库中空闲(现有最大为55)
-- 授权说明: 默认给 系统管理员(role_id=2) 和 运营管理员(role_id=3) 授权,
--           如需调整可在 "角色管理 -> 菜单授权" 页面修改
-- ============================================================

-- 1. 视频管理菜单(挂载在 系统管理 parent_id=1 下, sort=8)
INSERT INTO `sys_menu` VALUES (56, '视频管理', 'iconfont icon-CRMEB-shipin-xianxing', 2, '', 'views/video-list.html', 1, 8, 1, 1, 1, NOW(), NOW());

-- 2. 视频管理按钮权限
INSERT INTO `sys_menu` VALUES (57, '视频上传', '', 3, 'video-upload', '', 56, 1, 1, 1, 1, NOW(), NOW());
INSERT INTO `sys_menu` VALUES (58, '视频删除', '', 3, 'video-del', '', 56, 2, 1, 1, 1, NOW(), NOW());
INSERT INTO `sys_menu` VALUES (59, '视频查询', '', 3, 'video-search', '', 56, 3, 1, 1, 1, NOW(), NOW());

-- 3. 默认授权: 系统管理员(2)、运营管理员(3)
INSERT INTO `sys_role_menu` (role_id, menu_id) VALUES (2, 56), (2, 57), (2, 58), (2, 59);
INSERT INTO `sys_role_menu` (role_id, menu_id) VALUES (3, 56), (3, 57), (3, 58), (3, 59);