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
     * 文件分页
     * @param page              分页信息
     * @param fileServerUrl     访问地址
     * @param startTime         开始时间
     * @param endTime           结束时间
     * @param deptId         部门ID
     * @return  分页
     */
    IPage<SysFile> getFilePage(Page<SysFile> page,String fileServerUrl,LocalDateTime startTime, LocalDateTime endTime, Long deptId);
}
