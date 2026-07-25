package com.yaya.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysUser;
import com.yaya.model.form.SysUserForm;
import com.yaya.model.form.SysUserResetPasswordForm;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户管理-业务逻辑层
 */
public interface SysUserService {

    /**
     * 添加用户
     */
    void addSysUser(SysUserForm sysUserForm);
    /**
     * 批量导入
     */
    Map<String,Object> importSysUser(MultipartFile file,Integer cover) throws IOException;
    /**
     * 删除用户
     */
    void deleteSysUser(List<Long> userIds);

    /**
     * 用户批量封禁
     * @param userIds 用户ID列表
     */
    void batchDisSysUserStatus(List<Long> userIds);

    /**
     * 用户批量解封
     * @param userIds 用户ID列表
     */
    void batchEnableSysUserStatus(List<Long> userIds);

    /**
     * 重置密码
     * @param sysUserResetPasswordForm 提交信息
     */
    void resetSysUserPassword(SysUserResetPasswordForm sysUserResetPasswordForm);

    /**
     * 密码修改
     * @param userId        用户ID
     * @param oldPassword   原密码
     * @param newPassword   新密码
     */
    void changeSysUserPassword(Long userId,String oldPassword,String newPassword);

    /**
     * 更新用户
     */
    void updateSysUser(SysUserForm sysUserForm);

    /**
     * 更新用户头像
     * @param userId    用户ID
     * @param avatar    头像地址
     */
    void updateSysUserAvatar(Long userId,String avatar);

    /**
     * 用户-分页
     */
    IPage<SysUser> getSysUserPage(Page<SysUser> page,Integer isEnabled,Long deptId, String roleName, String username, String nickname, LocalDateTime start,LocalDateTime end);
    /**
     * 用户详情
     */
    SysUser getSysUserDetailByUserId(Long userId);

    /**
     * 租户用户数
     */
    Long getSysUserCount();
}
