package com.yaya.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.annotation.LogCollect;
import com.yaya.entity.SysFile;
import com.yaya.model.Result;
import com.yaya.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "文件管理")
@RestController
public class FileController {

    @Resource
    private FileService fileService;

    @LogCollect(module = "文件管理-上传图片",logResponse = true)
    @Operation(summary = "图片上传")
    @PostMapping(value = "/uploadImage")
    public Result<Map<String,String>> uploadImage(@Parameter(description = "上传的文件",required = true) @RequestParam(value = "file") MultipartFile file,
                                                  @Parameter(description = "是否压缩 0:不压缩 1:压缩") @RequestParam(value = "compress",defaultValue = "0",required = false) Integer compress) throws IOException {
        return Result.ok(fileService.uploadImage(file,compress));
    }


    @LogCollect(module = "文件管理-文件上传",logResponse = true)
    @Operation(summary = "文件上传")
    @PostMapping(value = "/uploadFile")
    public Result<Map<String,String>> uploadFile(@Parameter(description = "上传的文件",required = true) @RequestParam(value = "file") MultipartFile file) throws IOException {
        return Result.ok(fileService.uploadFile(file));
    }


    @Operation(summary = "文件分页")
    @Parameters(value = {
            @Parameter(name = "pageNo",description = "当前页",required = true),
            @Parameter(name = "pageSize",description = "当前页",required = true),
            @Parameter(name = "fileName",description = "文件名"),
            @Parameter(name = "fileType",description = "文件类型(image-图片 video-视频 file-文件)"),
            @Parameter(name = "nickname",description = "上传人昵称"),
            @Parameter(name = "deptId",description = "部门ID"),
            @Parameter(name = "fileServerUrl",description = "文件服务地址"),
            @Parameter(name = "startTime",description = "开始时间"),
            @Parameter(name = "endTime",description = "结束时间")
    })
    @PostMapping(value = "/getFilePage")
    public Result<IPage<SysFile>> getFilePage(@RequestParam(value = "pageNo") Integer pageNo,
                                              @RequestParam(value = "pageSize") Integer pageSize,
                                              @RequestParam(value = "fileName",required = false) String fileName,
                                              @RequestParam(value = "fileType",required = false) String fileType,
                                              @RequestParam(value = "nickname",required = false) String nickname,
                                              @RequestParam(value = "fileServerUrl",required = false) String fileServerUrl,
                                              @RequestParam(value = "startTime",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                              @RequestParam(value = "endTime",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
                                              @RequestParam(value = "deptId",required = false) Long deptId){
        return Result.ok(fileService.getFilePage(new Page<>(pageNo, pageSize),fileServerUrl,fileName,fileType,nickname,startTime,endTime,deptId));
    }

    @LogCollect(module = "文件管理-删除文件",logRequest = true)
    @Operation(summary = "删除文件")
    @PostMapping(value = "/deleteFile")
    public Result<Object> deleteFile(@Parameter(name = "fileId",description = "文件ID",required = true) @RequestParam(value = "fileId") Long fileId){
        fileService.deleteFile(fileId);
        return Result.ok();
    }

    @LogCollect(module = "视频管理-上传视频",logRequest = true,logResponse = true)
    @Operation(summary = "视频上传")
    @PostMapping(value = "/uploadVideo")
    public Result<Map<String,String>> uploadVideo(@Parameter(description = "上传的视频文件",required = true) @RequestParam(value = "file") MultipartFile file) throws IOException {
        return Result.ok(fileService.uploadVideo(file));
    }

    @Operation(summary = "视频分页")
    @Parameters(value = {
            @Parameter(name = "pageNo",description = "当前页",required = true),
            @Parameter(name = "pageSize",description = "页容量",required = true),
            @Parameter(name = "fileName",description = "文件名"),
            @Parameter(name = "startTime",description = "开始时间"),
            @Parameter(name = "endTime",description = "结束时间"),
            @Parameter(name = "deptId",description = "部门ID")
    })
    @PostMapping(value = "/getVideoPage")
    public Result<IPage<SysFile>> getVideoPage(@RequestParam(value = "pageNo") Integer pageNo,
                                               @RequestParam(value = "pageSize") Integer pageSize,
                                               @RequestParam(value = "fileName",required = false) String fileName,
                                               @RequestParam(value = "startTime",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                               @RequestParam(value = "endTime",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
                                               @RequestParam(value = "deptId",required = false) Long deptId){
        return Result.ok(fileService.getVideoPage(new Page<>(pageNo, pageSize),fileName,startTime,endTime,deptId));
    }

    @LogCollect(module = "视频管理-删除视频",logRequest = true)
    @Operation(summary = "删除视频")
    @PostMapping(value = "/deleteVideo")
    public Result<Object> deleteVideo(@Parameter(name = "fileId",description = "视频ID",required = true) @RequestParam(value = "fileId") Long fileId){
        fileService.deleteVideo(fileId);
        return Result.ok();
    }
}
