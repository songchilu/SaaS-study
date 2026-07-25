package com.yaya.mapper;

import com.yaya.YaYaSaaSPlusApplicationTests;
import com.yaya.model.resp.SysUserUvPvResp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SysLogMapperTest extends YaYaSaaSPlusApplicationTests {

    @Resource
    private SysLogMapper sysLogMapper;

    @Test
    void getChartSysUserUvPv() {
        List<SysUserUvPvResp> chartSysUserUvPv = sysLogMapper.getChartSysUserUvPv(1L);
        chartSysUserUvPv.forEach(System.out::println);
    }
}