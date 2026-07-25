package com.yaya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysDepartment;
import com.yaya.entity.SysRole;
import com.yaya.entity.SysRoleDept;
import com.yaya.entity.SysUser;
import com.yaya.exception.GlobalCommonException;
import com.yaya.mapper.SysDepartmentMapper;
import com.yaya.mapper.SysRoleDeptMapper;
import com.yaya.mapper.SysRoleMapper;
import com.yaya.mapper.SysUserMapper;
import com.yaya.model.form.SysRoleForm;
import com.yaya.service.SysRoleService;
import com.yaya.util.SecurityUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SysRoleServiceImpl implements SysRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysRoleDeptMapper  sysRoleDeptMapper;
    @Resource
    private SysDepartmentMapper  sysDepartmentMapper;

    @Override
    public void addSysRole(SysRoleForm sysRoleForm) {
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        if(!b){
            throw new GlobalCommonException("非平台管理员无操作角色权限");
        }
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(sysRoleForm, sysRole);
        sysRole.setCreateId(SecurityUtils.getUserId());
        sysRole.setUpdateId(SecurityUtils.getUserId());
        sysRoleMapper.insert(sysRole);
        //角色ID
        Long roleId = sysRole.getRoleId();
        Integer dataScope = sysRoleForm.getDataScope();
        if(dataScope == 5){//自定义数据范围
            String deptIds = sysRoleForm.getDeptIds();
            if(StringUtils.isNotBlank(deptIds)){
                String[] split = deptIds.split(",");
                if(ArrayUtils.isNotEmpty(split)){
                    for(String deptId : split){
                        SysRoleDept sysRoleDept = new SysRoleDept();
                        sysRoleDept.setRoleId(roleId);
                        sysRoleDept.setDeptId(Long.parseLong(deptId));
                        sysRoleDeptMapper.insert(sysRoleDept);
                    }
                }
            }
        }
    }

    @Override
    public void deleteSysRole(List<Long> roleIds) {
        if(CollectionUtils.isEmpty(roleIds)){
            throw new GlobalCommonException("请选择要删除的角色");
        }
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        if(!b){
            throw new GlobalCommonException("非平台管理员无操作角色权限");
        }
        roleIds.forEach(roleId -> {

            SysRole sysRole_ = sysRoleMapper.selectById(roleId);
            if(sysRole_!=null){
                String roleCode = sysRole_.getRoleCode();
                if("ROOT".equalsIgnoreCase(roleCode) || "ADMIN".equalsIgnoreCase(roleCode) || "OPERATION".equalsIgnoreCase(roleCode)){
                    throw new GlobalCommonException("平台角色不能被删除");
                }
            }

            //判断用户是否存在,用户不存在,可以删除,逻辑删除
            List<SysUser> sysUsers = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>().eq(SysUser::getRoleId, roleId).eq(SysUser::getIsDeleted, 0));
            if(CollectionUtils.isNotEmpty(sysUsers)){
                throw new GlobalCommonException("当前角色下存在有效用户,不能删除");
            }
            SysRole sysRole = new SysRole();
            sysRole.setRoleId(roleId);
            sysRole.setUpdateId(SecurityUtils.getUserId());
            sysRole.setIsDeleted(1);//1:删除
            sysRoleMapper.updateById(sysRole);
        });
    }

    @Override
    public void updateSysRole(SysRoleForm sysRoleForm) {
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        if(!b){
            throw new GlobalCommonException("非平台管理员无操作角色权限");
        }
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(sysRoleForm, sysRole);
        sysRole.setUpdateId(SecurityUtils.getUserId());//更新人
        //删除旧数据权限
        Long roleId = sysRoleForm.getRoleId();
        sysRoleDeptMapper.delete(new LambdaQueryWrapper<SysRoleDept>().eq(SysRoleDept::getRoleId, roleId));
        //添加新的数据权限
        String deptIds = sysRoleForm.getDeptIds();
        if(StringUtils.isNotBlank(deptIds)){
            String[] split = deptIds.split(",");
            if(ArrayUtils.isNotEmpty(split)){
                for(String deptId : split){
                    SysRoleDept sysRoleDept = new SysRoleDept();
                    sysRoleDept.setRoleId(roleId);
                    sysRoleDept.setDeptId(Long.parseLong(deptId));
                    sysRoleDeptMapper.insert(sysRoleDept);
                }
            }
        }
        sysRoleMapper.updateById(sysRole);
    }

    @Override
    public List<SysRole> getSysRoleList() {
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        if(b){
            return sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                    .eq(SysRole::getIsDeleted, 0)
                    .eq(SysRole::getStatus, 1)
                    .orderByDesc(SysRole::getUpdateTime)
            );
        }else {
            Long deptId = SecurityUtils.getDeptId();
            //租户
            SysDepartment department = sysDepartmentMapper.getTopDepartmentByDeptId(deptId);
            return sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                    .eq(SysRole::getIsDeleted, 0)
                    .eq(SysRole::getStatus, 1)
                    .eq(SysRole::getDeptId,department.getDeptId()) //租户ID
                    .orderByDesc(SysRole::getUpdateTime)
            );
        }
    }

    @Override
    public IPage<SysRole> getSysRolePage(Page<SysRole> page, Long deptId, String roleName, Integer roleType, Integer status) {
        //如果是非平台管理员角色
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        if(!b){
            deptId = SecurityUtils.getDeptId();
            SysDepartment department = sysDepartmentMapper.getTopDepartmentByDeptId(deptId);
            deptId = department.getDeptId();
        }
        Page<SysRole> sysRolePage = sysRoleMapper.getSysRolePage(page, deptId, roleName, roleType, status);
        if(sysRolePage!=null && CollectionUtils.isNotEmpty(sysRolePage.getRecords())){
            sysRolePage.getRecords().forEach(sysRole -> {
                Long roleId = sysRole.getRoleId();
                List<SysRoleDept> sysRoleDepts = sysRoleDeptMapper.selectList(new LambdaQueryWrapper<SysRoleDept>()
                        .eq(SysRoleDept::getRoleId, roleId)
                );
                if(CollectionUtils.isNotEmpty(sysRoleDepts)){
                    sysRole.setSysRoleDeptList(sysRoleDepts);
                }
            });
        }
        return sysRolePage;
    }

    @Override
    public List<Map<String, Object>> getSysRoleDataScope() {
        //1-所有数据 2-部门及子部门数据 3-本部门数据 4-本人数据 5-自定义部门数据
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("data_id",1);
        map1.put("data_scope","所有数据");
        list.add(map1);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("data_id",2);
        map2.put("data_scope","部门及子部门数据");
        list.add(map2);
        Map<String, Object> map3 = new HashMap<>();
        map3.put("data_id",3);
        map3.put("data_scope","本部门数据");
        list.add(map3);
        Map<String, Object> map4 = new HashMap<>();
        map4.put("data_id",4);
        map4.put("data_scope","本人数据");
        list.add(map4);
        Map<String, Object> map5 = new HashMap<>();
        map5.put("data_id",5);
        map5.put("data_scope","自定义部门数据");
        list.add(map5);
        return list;
    }
}
