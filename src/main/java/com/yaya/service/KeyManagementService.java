package com.yaya.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.KeyManagement;

/**
 * 密钥管理-业务逻辑层
 */
public interface KeyManagementService {

    /**
     * 创建密钥对
     * @param deptId 顶层部门ID(租户ID)
     * @param remark 备注
     */
    void createKey(Long deptId,String remark);

    /**
     * 密钥分页
     * @param page 分页
     * @param deptName 租户名称
     * @return 密钥
     */
    IPage<KeyManagement> getKeyManagementPage(Page<KeyManagement> page, String deptName);

    /**
     * 删除密钥
     * @param keyId 密钥ID
     */
    void deleteKeyManagement(Long keyId);
}
