package com.yaya.mapper;

import com.yaya.YaYaSaaSPlusApplicationTests;
import com.yaya.entity.SysMenu;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import java.util.List;

class SysMenuMapperTest extends YaYaSaaSPlusApplicationTests {

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Test
    void getSysMenuTree() {
        List<SysMenu> sysMenuTree = sysMenuMapper.getSysMenuTree(null,1);
        sysMenuTree.forEach(System.out::println);
    }

    @Test
    void getSubSysMenuTree() {
        List<SysMenu> sysMenuTree = sysMenuMapper.getSubSysMenuTree(1L,null);
        sysMenuTree.forEach(System.out::println);
    }

    @Test
    void getSysMenuTreeExcludeButton(){
        List<SysMenu> button = sysMenuMapper.getSysMenuTreeExcludeButton(null);
        button.forEach(System.out::println);
    }
}