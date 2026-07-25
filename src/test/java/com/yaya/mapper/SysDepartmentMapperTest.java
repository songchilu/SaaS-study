package com.yaya.mapper;

import com.yaya.YaYaSaaSPlusApplicationTests;
import com.yaya.entity.SysDepartment;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import java.util.List;

class SysDepartmentMapperTest extends YaYaSaaSPlusApplicationTests {

    @Resource
    private SysDepartmentMapper sysDepartmentMapper;

    @Test
    void getTopDepartmentByDeptId(){
        SysDepartment department = sysDepartmentMapper.getTopDepartmentByDeptId(2L);
        System.out.println(department);
    }

    @Test
    void getDepartmentAndSubDepartmentListByDeptId(){
        List<SysDepartment> list = sysDepartmentMapper.getDepartmentAndSubDepartmentList(2L,1);
        list.forEach(System.out::println);
    }
}