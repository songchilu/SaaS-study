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
            @Parameter(name = "deptId",description = "部门ID"),
            @Parameter(name = "fileServerUrl",description = "文件服务地址"),
            @Parameter(name = "startTime",description = "开始时间"),
            @Parameter(name = "endTime",description = "结束时间")
    })
    @PostMapping(value = "/getFilePage")
    public Result<IPage<SysFile>> getFilePage(@RequestParam(value = "pageNo") Integer pageNo,
                                              @RequestParam(value = "pageSize") Integer pageSize,
                                              @RequestParam(value = "fileServerUrl",required = false) String fileServerUrl,
                                              @RequestParam(value = "startTime",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                              @RequestParam(value = "endTime",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
                                              @RequestParam(value = "deptId",required = false) Long deptId){
        return Result.ok(fileService.getFilePage(new Page<>(pageNo, pageSize),fileServerUrl,startTime,endTime,deptId));
    }
}
