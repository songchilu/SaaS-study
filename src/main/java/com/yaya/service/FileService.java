package com.yaya.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 文件管理业务逻辑层
 */
public interface FileService {

    /**
     * 图片上传
     * @param file      图片文件
     * @param compress  是否压缩 0:不压缩 1:压缩
     * @return  返回图片在服务器的基本信息
     */
    Map<String,String> uploadImage(MultipartFile file, Integer compress) throws IOException;

    /**
     * 除了图片外的其他文件上传
     * @param file          文件
     * @return              文件信息
     * @throws IOException  异常
     */
    Map<String,String> uploadFile(MultipartFile file) throws IOException;

    /**
     * 视频上传(仅支持MP4)
     * @param file          视频文件
     * @return              视频访问地址
     * @throws IOException  异常
     */
    Map<String,String> uploadVideo(MultipartFile file) throws IOException;

    /**
     * 文件分页
     * @param page              分页信息
     * @param fileServerUrl     访问地址
     * @param startTime         开始时间
     * @param endTime           结束时间
     * @param deptId         部门ID
     * @return  分页
     */
    IPage<SysFile> getFilePage(Page<SysFile> page,String fileServerUrl,String fileName,String fileType,String nickname,LocalDateTime startTime, LocalDateTime endTime, Long deptId);

    /**
     * 视频分页
     * @param page          分页信息
     * @param fileName      文件名
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @param deptId        部门ID(平台管理员可指定,非平台管理员强制本人租户)
     * @return  分页
     */
    IPage<SysFile> getVideoPage(Page<SysFile> page,String fileName,LocalDateTime startTime, LocalDateTime endTime, Long deptId);

    /**
     * 删除视频
     * @param fileId  视频ID
     */
    void deleteVideo(Long fileId);

    /**
     * 删除文件
     * @param fileId  文件ID
     */
    void deleteFile(Long fileId);
}