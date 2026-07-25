/**
 * 服务器端的核心配置
 */

/**
 * 服务代理地址
 */
const server_url='/api';
/**
 * 文件服务器地址
 */
const file_url="/api";

/**
 * 浏览器本地存储对象
 */ 
const yaya = layui.data('yaya');
/**
 * token令牌
 */
const token = (yaya && Object.keys(yaya).length>0)?yaya.token:'';
/**
 * 按钮权限
 */
const perms = (yaya && Object.keys(yaya).length>0)?yaya.perm:[];
/**
 * 用户信息
 */
const user = (yaya && Object.keys(yaya).length>0)?yaya.user:{};
/**
 * 当前登录的用户ID
 */
const userId = (user && Object.keys(user).length>0)?user.userId:'';
/**
 * 当前登录用户的部门ID
 */
const deptId = (user && Object.keys(user).length>0)?user.deptId:'';
/**
 * 是否是超级管理员 true=是,false=否
 */
const is_super = (user && Object.keys(user).length>0)?user.super:false;

/**
 * 定义一个全局或当前页面可见的权限判断函数
 */
window.hasPermission = function(authCode) {
    // 判断当前用户的权限列表中是否包含该权限码
    return perms.includes(authCode);
};