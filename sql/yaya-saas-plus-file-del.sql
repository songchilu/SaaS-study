-- ============================================================
-- YaYa-SaaS-Plus 文件管理 - 删除按钮权限菜单
-- 适用场景: 已部署数据库升级(全新安装已包含在 yaya-boot-plus.sql 中)
-- 前提: 菜单ID 61 在当前库中空闲(现有最大为60)
-- 说明: 文件管理页(file-list.html)的删除按钮使用 file-del 权限控制
-- 授权说明: 默认给 系统管理员(role_id=2) 和 运营管理员(role_id=3) 授权,
--           如需调整可在 "角色管理 -> 菜单授权" 页面修改
-- ============================================================

-- 1. 文件删除按钮(挂载在 文件管理 menu_id=50 下, sort=3)
INSERT INTO `sys_menu` VALUES (61, '文件删除', '', 3, 'file-del', '', 50, 3, 1, 1, 1, NOW(), NOW());

-- 2. 默认授权: 系统管理员(2)、运营管理员(3)
INSERT INTO `sys_role_menu` (role_id, menu_id) VALUES (2, 61);
INSERT INTO `sys_role_menu` (role_id, menu_id) VALUES (3, 61);