package com.yaya.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.YaYaSaaSPlusApplicationTests;
import com.yaya.entity.SysFile;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

class SysFileMapperTest extends YaYaSaaSPlusApplicationTests {

    @Resource
    private SysFileMapper sysFileMapper;

    @Test
    void getFilePage() {
        Page<SysFile> page = sysFileMapper.getFilePage(new Page<SysFile>(1, 5), "2063209763026227200", null, null, null);
        page.getRecords().forEach(System.out::println);
    }
}