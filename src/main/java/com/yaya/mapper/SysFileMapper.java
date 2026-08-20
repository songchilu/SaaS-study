package com.yaya.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysFile;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 文件操作持久化层
 */
public interface SysFileMapper extends BaseMapper<SysFile> {

    /**
     * 文件分页
     * @param page              分页信息
     * @param fileServerUrl     访问地址
     * @param startTime         开始时间
     * @param endTime           结束时间
     * @param deptId         部门ID
     * @return  分页
     */
    Page<SysFile> getFilePage(Page<SysFile> page, @Param("fileServerUrl") String fileServerUrl, @Param("fileName") String fileName, @Param("fileType") String fileType, @Param("nickname") String nickname, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("deptId") Long deptId);
    /**
     * 视频分页(只查询 /videos/ 目录下的记录)
     * @param page          分页信息
     * @param fileName      文件名
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @param deptId        部门ID
     * @return  分页
     */
    Page<SysFile> getVideoPage(Page<SysFile> page, @Param("fileName") String fileName, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("deptId") Long deptId);

}
