package com.yaya.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaya.entity.SysDepartment;
import com.yaya.entity.SysUser;
import com.yaya.exception.GlobalCommonException;
import com.yaya.mapper.SysDepartmentMapper;
import com.yaya.mapper.SysUserMapper;
import com.yaya.model.form.SysDepartmentForm;
import com.yaya.service.SysDepartmentService;
import com.yaya.util.SecurityUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Transactional
@Service
public class SysDepartmentServiceImpl implements SysDepartmentService {

    @Resource
    private SysDepartmentMapper sysDepartmentMapper;
    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public void addSysDepartment(SysDepartmentForm sysDepartmentForm) {
        Long userId = SecurityUtils.getUserId();
        SysDepartment sysDepartment = new SysDepartment();
        BeanUtils.copyProperties(sysDepartmentForm, sysDepartment);
        sysDepartment.setCreateId(userId);//创建人
        sysDepartment.setUpdateId(userId);//更新人
        Long parentId = sysDepartment.getParentId();
        if (parentId == null || parentId == 0L) { //一级部门
            sysDepartment.setParentId(0L);//顶级父ID为0
            sysDepartment.setTreePath("0");//顶级祖宗列表为0
        }else { //不是一级部门
            //构建祖宗列
            //上级部门
            SysDepartment parentDepartment = sysDepartmentMapper.selectById(parentId);
            //上级部门的祖宗列
            String parentTreePath = parentDepartment.getTreePath();
            //拼接当前部门的parentId
            String treePath = parentTreePath+","+parentId;
            sysDepartment.setTreePath(treePath);
        }
        sysDepartmentMapper.insert(sysDepartment);
    }

    @Override
    public void deleteSysDepartment(List<Long> departmentIds) {
        if (CollectionUtils.isEmpty(departmentIds)) {
            throw new GlobalCommonException("请选择要删除的部门");
        }
        //判断是否存在用户
        departmentIds.forEach(departmentId -> {
            List<SysUser> sysUsers = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getDeptId, departmentId)
                    .eq(SysUser::getIsDeleted, 0)//未删除
            );
            if (CollectionUtils.isNotEmpty(sysUsers)) {
                throw new GlobalCommonException("部门在存在用户,不能直接被删除");
            }
            //判断当前部门在是否存在子部门,如果存在不能删除
            List<SysDepartment> sysDepartments = sysDepartmentMapper.selectList(new LambdaQueryWrapper<SysDepartment>()
                    .eq(SysDepartment::getParentId, departmentId)
                    .eq(SysDepartment::getIsDeleted, 0)
            );
            if (CollectionUtils.isNotEmpty(sysDepartments)) {
                throw new GlobalCommonException("存在子部门,不能删除");
            }
            SysDepartment sysDepartment = new SysDepartment();
            sysDepartment.setDeptId(departmentId);
            sysDepartment.setIsDeleted(1);//删除
            sysDepartment.setUpdateId(SecurityUtils.getUserId());//更新人
            sysDepartmentMapper.updateById(sysDepartment);
        });
    }

    @Override
    public void updateSysDepartment(SysDepartmentForm sysDepartmentForm) {
        //不允许跨父节点更新
        Long deptId = sysDepartmentForm.getDeptId();//部门
        SysDepartment sysDepartment_ = sysDepartmentMapper.selectById(deptId);
        //原parentId
        Long parentId = sysDepartment_.getParentId();
        //当前的parentId
        Long parentId_ = sysDepartmentForm.getParentId();
        if(!parentId_.equals(parentId)){
            throw new GlobalCommonException("不能跨部门更新");
        }
        SysDepartment sysDepartment = new SysDepartment();
        BeanUtils.copyProperties(sysDepartmentForm, sysDepartment);
        sysDepartment.setUpdateId(SecurityUtils.getUserId());//更新人
        sysDepartmentMapper.updateById(sysDepartment);
    }

    @Override
    public List<SysDepartment> getOneLevelSysDepartment() {
        return sysDepartmentMapper.selectList(new LambdaQueryWrapper<SysDepartment>()
                .eq(SysDepartment::getIsDeleted, 0) //未删除
                .eq(SysDepartment::getParentId, 0) //顶层
                .eq(SysDepartment::getStatus, 1) //可用
                .orderByAsc(SysDepartment::getSort) //排序
                .orderByDesc(SysDepartment::getUpdateTime) //排序
        );
    }

    @Override
    public List<SysDepartment> getSysDepartmentTree(String deptName,Integer status) {
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        if(b){
            //查询出所有的部门(顶层节点+后代节点)
            List<SysDepartment> sysDepartments = sysDepartmentMapper.selectList(new LambdaQueryWrapper<SysDepartment>()
                    .eq(SysDepartment::getIsDeleted, 0)
                    .eq(status != null, SysDepartment::getStatus, status)
                    .like(StringUtils.isNotEmpty(deptName), SysDepartment::getDeptName, deptName)
            );
            if(CollectionUtils.isNotEmpty(sysDepartments)){
                sysDepartments.forEach(sysDepartment -> {
                    sysDepartment.setSpread(true);
                });
            }
            return convertToTree(sysDepartments);
        }else {
            List<SysDepartment> list = sysDepartmentMapper.getDepartmentAndSubDepartmentList(SecurityUtils.getDeptId(), status);
            if(CollectionUtils.isNotEmpty(list)){
                if(CollectionUtils.isNotEmpty(list)){
                    list.forEach(sysDepartment -> {
                        sysDepartment.setSpread(true);
                    });
                }
                return convertToTree_(SecurityUtils.getDeptId(),list);
            }
            return Collections.emptyList();
        }
    }

    /**
     * 将普通列表数据转成tree树形结构数据
     */
    public List<SysDepartment> convertToTree(List<SysDepartment> allNodes) {
        // 1. 找出所有顶级部门（parentId 为 0 的节点）
        return allNodes.stream()
                .filter(node -> node.getParentId() == 0)
                // 2. 递归寻找每个顶级部门的子部门
                .peek(node -> node.setChildren(getChildrenNodes(node.getDeptId(), allNodes)))
                .toList();
    }

    /**
     * 转换-带有当前部门的结构化数据
     */
    public List<SysDepartment> convertToTree_(Long deptId,List<SysDepartment> allNodes) {
        // 1. 找出所有顶级部门（parentId 为 0 的节点）
        return allNodes.stream()
                .filter(node -> node.getDeptId().equals(deptId))
                // 2. 递归寻找每个顶级部门的子部门
                .peek(node -> node.setChildren(getChildrenNodes(node.getDeptId(), allNodes)))
                .toList();
    }

    /**
     *
     * @param parentId 部门ID
     * @param allNodes 子节点
     * @return  叶子节点
     */
    private List<SysDepartment> getChildrenNodes(Long parentId, List<SysDepartment> allNodes) {
        return allNodes.stream()
                .filter(node -> node.getParentId().equals(parentId))
                // 嵌套递归，支持无限级子部门
                .peek(node -> node.setChildren(getChildrenNodes(node.getDeptId(), allNodes)))
                .toList();
    }
}
