package com.yaya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysDepartment;
import com.yaya.entity.SysPost;
import com.yaya.entity.SysUser;
import com.yaya.entity.SysUserPost;
import com.yaya.exception.GlobalCommonException;
import com.yaya.mapper.SysDepartmentMapper;
import com.yaya.mapper.SysPostMapper;
import com.yaya.mapper.SysUserMapper;
import com.yaya.mapper.SysUserPostMapper;
import com.yaya.model.form.SysPostForm;
import com.yaya.service.SysPostService;
import com.yaya.util.SecurityUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class SysPostServiceImpl implements SysPostService {

    @Resource
    private SysPostMapper sysPostMapper;
    @Resource
    private SysUserPostMapper  sysUserPostMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysDepartmentMapper sysDepartmentMapper;

    @Override
    public void addSysPost(SysPostForm sysPostForm) {
        SysPost sysPost = new SysPost();
        BeanUtils.copyProperties(sysPostForm, sysPost);
        sysPost.setCreateId(SecurityUtils.getUserId());
        sysPost.setUpdateId(SecurityUtils.getUserId());
        sysPostMapper.insert(sysPost);
    }

    @Override
    public void deleteSysPost(List<Long> postIds) {
        if(CollectionUtils.isEmpty(postIds)){
            throw new GlobalCommonException("请选择要删除的岗位");
        }
        postIds.forEach(postId -> {
            //判断当前岗位是否存在用户
            List<SysUserPost> sysUserPosts = sysUserPostMapper.selectList(new LambdaQueryWrapper<SysUserPost>()
                    .eq(SysUserPost::getPostId, postId)
            );
            if(CollectionUtils.isNotEmpty(sysUserPosts)){
                List<Long> userIds = sysUserPosts.stream().map(SysUserPost::getUserId).distinct().toList();
                List<SysUser> sysUsers = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getIsDeleted, 0)
                        .in(SysUser::getUserId, userIds)
                );
                if(CollectionUtils.isNotEmpty(sysUsers)){
                    throw new GlobalCommonException("当前岗位下存在用户,不能删除");
                }
            }
            //删除
            SysPost sysPost = new SysPost();
            sysPost.setPostId(postId);
            sysPost.setIsDeleted(1);//删除
            sysPost.setUpdateId(SecurityUtils.getUserId());
            sysPostMapper.updateById(sysPost);
        });
    }

    @Override
    public void updateSysPost(SysPostForm sysPostForm) {
        SysPost sysPost = new SysPost();
        BeanUtils.copyProperties(sysPostForm, sysPost);
        sysPost.setUpdateId(SecurityUtils.getUserId());
        sysPostMapper.updateById(sysPost);
    }

    @Override
    public List<SysPost> getSysPostList() {
        //是否是平台管理员
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        if(b){
            return sysPostMapper.selectList(new LambdaQueryWrapper<SysPost>()
                    .eq(SysPost::getStatus, 1)
                    .eq(SysPost::getIsDeleted,0)
            );
        }else {
            //当前用户部门ID
            Long deptId = SecurityUtils.getDeptId();
            //当前用户所在的租户信息
            SysDepartment department = sysDepartmentMapper.getTopDepartmentByDeptId(deptId);
            return sysPostMapper.selectList(new LambdaQueryWrapper<SysPost>()
                    .eq(SysPost::getStatus, 1)
                    .eq(SysPost::getIsDeleted,0)
                    .eq(SysPost::getDeptId, department.getDeptId()) //租户ID
            );
        }
    }

    @Override
    public IPage<SysPost> getSysPostPage(Page<SysPost> page,String postName,Long deptId) {
        //判断是否是平台管理员
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        if(!b){
            //不是平台管理员
            deptId = SecurityUtils.getDeptId();
            SysDepartment department = sysDepartmentMapper.getTopDepartmentByDeptId(deptId);
            deptId = department.getDeptId();
        }
        Page<SysPost> sysPostPage = sysPostMapper.selectPage(page, new LambdaQueryWrapper<SysPost>()
                .like(StringUtils.isNotEmpty(postName), SysPost::getPostName, postName)
                .eq(SysPost::getIsDeleted, 0)
                .eq(deptId!=null,SysPost::getDeptId,deptId)
                .orderByDesc(SysPost::getUpdateTime)
        );
        sysPostPage.getRecords().forEach(sysPost -> {
            sysPost.setDeptName(sysDepartmentMapper.selectById(sysPost.getDeptId()).getDeptName());
        });
        return sysPostPage;
    }
}
