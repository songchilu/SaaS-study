package com.yaya.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import com.yaya.annotation.LogCollect;
import com.yaya.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
@Slf4j
@Tag(name = "缓存管理")
@RestController
public class RedisAdminController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "获取缓存信息")
    @PostMapping("/getRedisSysInfo")
    public Result<Map<String, Object>> getRedisSysInfo() {
        Map<String, Object> status = new HashMap<>();

        // 1. 获取 Key 总数 (DBSIZE 命令)
        Long dbSize = stringRedisTemplate.execute(RedisServerCommands::dbSize);
        status.put("key_count", dbSize != null ? dbSize : 0);

        // 2. 获取 Redis INFO 信息
        Properties info = stringRedisTemplate.execute((RedisCallback<Properties>) RedisServerCommands::info);

        if (info != null) {
            // used_memory_human 会自动转化为易读格式，如 "11.23M"
            status.put("used_memory", info.getProperty("used_memory_human"));
            // 客户端连接数
            status.put("connected_clients", info.getProperty("connected_clients"));
            //运行时间
            status.put("uptime", Long.parseLong(info.getProperty("uptime_in_seconds", "0")));
        } else {
            status.put("used_memory", "unknown");
            status.put("connected_clients", 0);
            //运行时间 单位:秒
            status.put("uptime", 0);
        }
        //获取内存总量
        long total = OshiUtil.getMemory().getTotal();
        status.put("total_memory", FileUtil.readableFileSize(total));
        //获取CPU信息
        CpuInfo cpuInfo = OshiUtil.getCpuInfo();
        Integer cpuNum = cpuInfo.getCpuNum();
        status.put("cpu_num", cpuNum);
        return Result.ok(status);
    }

    @Operation(summary = "删除单个key")
    @LogCollect(module = "缓存管理-删除单个key",logRequest = true,logResponse = true)
    @PostMapping("/deleteKey")
    public Result<Object> deleteKey(@Parameter(name = "key",description = "缓存的精确key,例如 user:info:1") @RequestParam(value = "key") String key) {
        try {
            Boolean deleted = stringRedisTemplate.delete(key);
            System.out.println(key);
            System.out.println(deleted);
            if (Boolean.TRUE.equals(deleted)) {
                return Result.ok();
            }else {
                return Result.error("删除失败,key不存在或已过期");
            }
        } catch (Exception e) {
            log.error("删除单个缓存失败:",e);
            return  Result.error(e.getMessage());
        }
    }


    /**
     * 前缀删除
     */
    @Operation(summary = "删除指定前缀的key")
    @LogCollect(module = "缓存管理-删除制定前缀的key",logRequest = true,logResponse = true)
    @PostMapping("/deleteByPrefix")
    public Result<Object> deleteByPrefix(@Parameter(name = "prefix",description = "缓存的精确key,例如 user:session:") @RequestParam(value = "prefix")  String prefix) {
        if (!StringUtils.hasText(prefix)) {
            return Result.error("prefix不能为空");
        }
        if (prefix.contains("*")) {
            return Result.error("prefix不能包含通配符");
        }
        String pattern = prefix + "*";
        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(1000)
                .build();
        long deleted = 0;
        List<String> batch = new ArrayList<>(500);
        try (Cursor<String> cursor = stringRedisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                batch.add(cursor.next());
                if (batch.size() == 500) {
                    deleted += stringRedisTemplate.delete(batch);
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) {
                deleted += stringRedisTemplate.delete(batch);
            }
        }
        return Result.ok(deleted);
    }
}
