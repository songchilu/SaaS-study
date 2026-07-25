package com.yaya.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysPost;
import com.yaya.model.form.SysPostForm;

import java.util.List;

/**
 * 岗位管理-业务逻辑层
 */
public interface SysPostService {

    /**
     * 添加岗位
     * @param sysPostForm 岗位信息
     */
    void addSysPost(SysPostForm sysPostForm);

    /**
     * 删除岗位
     * @param postIds 岗位IDs
     */
    void deleteSysPost(List<Long> postIds);

    /**
     * 更新岗位
     * @param sysPostForm 岗位信息
     */
    void updateSysPost(SysPostForm sysPostForm);

    /**
     * 岗位列表
     * @return  列表
     */
    List<SysPost> getSysPostList();

    /**
     * @param page          分页配置
     * @param postName      岗位名称
     * @param deptId        租户ID
     * @return 分页
     */
    IPage<SysPost> getSysPostPage(Page<SysPost> page, String postName,Long deptId);

}
