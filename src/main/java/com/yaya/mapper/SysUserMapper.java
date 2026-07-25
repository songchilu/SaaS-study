package com.yaya.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.annotation.DataPermission;
import com.yaya.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 用户持久化层
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 用户分页
     */
    @DataPermission(userAlias = "u",deptAlias = "d",userIdColumnName = "user_id",deptIdColumnName = "dept_id")
    IPage<SysUser> getSysUserPage(Page<SysUser> page,@Param("isEnabled") Integer isEnabled,@Param("deptId") Long deptId,@Param("roleName") String roleName,@Param("username") String username,@Param("nickname") String nickname,@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 租户用户数
     */
    long getSysUserCount(@Param("deptId") Long deptId);
}
