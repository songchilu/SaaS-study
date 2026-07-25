package com.yaya.mapper;

import com.yaya.YaYaSaaSPlusApplicationTests;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SysUserMapperTest extends YaYaSaaSPlusApplicationTests {

    @Resource
    private SysUserMapper sysUserMapper;

    @Test
    void getSysUserCountByDeptId() {
        long count = sysUserMapper.getSysUserCount(4L);
        System.out.println(count);
    }
}