package com.yaya.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.KeyManagement;
import org.apache.ibatis.annotations.Param;

/**
 * 密钥管理 - 持久化管理
 */
public interface KeyManagementMapper extends BaseMapper<KeyManagement> {

    /**
     * 密钥分页
     * @param page 分页信息
     * @param deptName 部门名称
     * @param deptId    部门ID
     * @return 页
     */
    IPage<KeyManagement> getKeyManagementPage(Page<KeyManagement> page,@Param(value = "deptName") String deptName,@Param("deptId") Long deptId);

}
