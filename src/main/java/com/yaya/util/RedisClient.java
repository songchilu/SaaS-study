package com.yaya.util;

import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis客户端
 */
@Component
public class RedisClient {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 保存数据
     */
    public void set (String key,String value){
        stringRedisTemplate.opsForValue().set(key,value);
    }
    /**
     * 保存数据-过期时间
     * @param key    键
     * @param value  值
     * @param time   过期时间,单位是 秒
     */
    public void set (String key,String value,Long time){
        stringRedisTemplate.opsForValue().set(key,value,time, TimeUnit.SECONDS);
    }

    /**
     * 保存数据-过期时间
     * @param key    键
     * @param value  值
     * @param time   过期时间,单位是 分钟
     */
    public void set (String key,String value,Integer time){
        stringRedisTemplate.opsForValue().set(key,value,time, TimeUnit.MINUTES);
    }

    /**
     * 通过键获取对应的值
     * @param key 键
     * @return    值
     */
    public String get(String key){
        return stringRedisTemplate.opsForValue().get(key);
    }
    /**
     * 删除单个缓存
     * @param key 键
     */
    public void del(String key){
        stringRedisTemplate.delete(key);
    }

    /**
     * 删除批量缓存
     */
    public void del(List<String> keys) {
        if (CollectionUtils.isNotEmpty(keys)) {
            stringRedisTemplate.delete(keys);
        }
    }

    /**
     * 判断key是否存在
     */
    public Boolean exists(String key){
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 判断锁是否存在
     */
    public Boolean setIfAbsent(String key,String value,int expireTime,TimeUnit timeUnit){
        return stringRedisTemplate.opsForValue().setIfAbsent(key,value,expireTime,timeUnit);
    }

    /**
     * 模糊获取key列表
     */
    public List<String> findKeys(String pattern) {
        List<String> result = new ArrayList<>();
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(pattern)
                .count(500)
                .build();
        // 循环迭代游标，把所有分页数据读完
        try (Cursor<String> cursor = stringRedisTemplate.scan(scanOptions)) {
            while (cursor.hasNext()) {
                result.add(cursor.next());
            }
        }
        return result;
    }
}
