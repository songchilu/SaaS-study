package com.yaya.controller;

import cn.hutool.core.io.resource.ResourceUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.annotation.LogCollect;
import com.yaya.annotation.RepeatSubmit;
import com.yaya.entity.SysUser;
import com.yaya.model.Result;
import com.yaya.model.form.SysUserForm;
import com.yaya.model.form.SysUserResetPasswordForm;
import com.yaya.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "用户管理")
@RestController
public class SysUserController {

    @Resource
    private SysUserService sysUserService;


    @Operation(summary = "模板下载")
    @PostMapping(value = "/downLoadSysUserTemplate",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downLoadSysUserTemplate() throws IOException {
        //读取输入流
        InputStream in = ResourceUtil.getStream("classpath:excel/sys_user_excel.xlsx");
        //创建下载缓冲区
        byte[] body = new byte[in.available()];
        //将输入流数据读入缓冲区
        int read = in.read(body);
        System.out.println("文件读到缓存区域的总字节数..."+read);
        //创建响应头
        HttpHeaders headers = new HttpHeaders();
        //构建文件名称
        String fileName="sys_user_excel_"+System.currentTimeMillis()+".xlsx";
        headers.add("Content-Disposition", "attachment;filename="+fileName);
        //创建响应状态码
        HttpStatus ok = HttpStatus.OK;
        return new ResponseEntity<>(body,headers,ok);
    }

    @LogCollect(module = "用户管理-添加用户",logRequest = true)
    @RepeatSubmit(message = "网络延时,请勿重复点击添加")//避免重复提交,默认值2秒
    @Operation(summary = "添加用户")
    @PostMapping(value = "/addSysUser")
    public Result<Object> addSysUser(@RequestBody SysUserForm sysUserForm){
        sysUserService.addSysUser(sysUserForm);
        return Result.ok();
    }

    @LogCollect(module = "用户管理-删除用户")
    @RepeatSubmit(message = "网络延时,请勿重复点击删除")//避免重复提交,默认值2秒
    @Operation(summary = "删除用户")
    @PostMapping(value = "/deleteSysUser")
    public Result<Object> deleteSysUser(@RequestBody List<Long> userIds){
        sysUserService.deleteSysUser(userIds);
        return Result.ok();
    }

    @LogCollect(module = "用户管理-重置密码",logRequest = true)
    @RepeatSubmit(message = "网络延时,请勿重复点击重置")//避免重复提交,默认值2秒
    @Operation(summary = "重置密码")
    @PostMapping(value = "/resetSysUserPassword")
    public Result<Object> resetSysUserPassword(@RequestBody SysUserResetPasswordForm sysUserResetPasswordForm){
        sysUserService.resetSysUserPassword(sysUserResetPasswordForm);
        return Result.ok();
    }

    @LogCollect(module = "用户管理-密码修改")
    @RepeatSubmit(message = "网络延时,请勿重复点击修改")//避免重复提交,默认值2秒
    @Operation(summary = "密码修改")
    @Parameters(value = {
            @Parameter(name = "userId",description = "用户ID"),
            @Parameter(name = "oldPassword",description = "原密码"),
            @Parameter(name = "newPassword",description = "新密码")
    })
    @PostMapping(value = "/changeSysUserPassword")
    public Result<Object> changeSysUserPassword(@RequestParam(value = "userId") Long userId,
                                                @RequestParam(value = "oldPassword") String oldPassword,
                                                @RequestParam(value = "newPassword") String newPassword){
        sysUserService.changeSysUserPassword(userId,oldPassword,newPassword);
        return Result.ok();
    }

    @LogCollect(module = "用户管理-用户封禁")
    @RepeatSubmit(message = "网络延时,请勿重复点击封禁")//避免重复提交,默认值2秒
    @Operation(summary = "用户封禁")
    @PostMapping(value = "/batchDisSysUserStatus")
    public Result<Object> batchDisSysUserStatus(@RequestBody List<Long> userIds){
        sysUserService.batchDisSysUserStatus(userIds);
        return Result.ok();
    }

    @LogCollect(module = "用户管理-用户解封")
    @RepeatSubmit(message = "网络延时,请勿重复点击解封")//避免重复提交,默认值2秒
    @Operation(summary = "用户解封")
    @PostMapping(value = "/batchEnableSysUserStatus")
    public Result<Object> batchEnableSysUserStatus(@RequestBody List<Long> userIds){
        sysUserService.batchEnableSysUserStatus(userIds);
        return Result.ok();
    }



    @LogCollect(module = "用户管理-批量导入",logResponse = true)
    @RepeatSubmit(message = "网络延时,请勿重复点击导入")//避免重复提交,默认值2秒
    @Operation(summary = "批量导入")
    @Parameters(value = {
        @Parameter(name = "cover",description = "是否覆盖 1:覆盖 0:不覆盖")
    })
    @PostMapping(value = "/importSysUser")
    public Result<Map<String,Object>> importSysUser(@RequestPart(value = "file") MultipartFile file,
                                                    @RequestParam(value = "cover",required = false,defaultValue = "0") Integer cover) throws IOException {
        return Result.ok(sysUserService.importSysUser(file,cover));
    }

    @LogCollect(module = "用户管理-更新用户")
    @RepeatSubmit(message = "网络延时,请勿重复点击更新")//避免重复提交,默认值2秒
    @Operation(summary = "更新用户")
    @PostMapping(value = "/updateSysUser")
    public Result<Object> updateSysUser(@RequestBody SysUserForm sysUserForm){
        sysUserService.updateSysUser(sysUserForm);
        return Result.ok();
    }

    @LogCollect(module = "用户管理-更新头像")
    @RepeatSubmit(message = "网络延时,请勿重复点击更新")//避免重复提交,默认值2秒
    @Operation(summary = "更新头像")
    @Parameters(value = {
        @Parameter(name = "userId",description = "用户ID",required = true),
        @Parameter(name = "avatar",description = "头像",required = true)
    })
    @PostMapping(value = "/updateSysUserAvatar")
    public Result<Object> updateSysUserAvatar(@RequestParam(value = "userId") Long userId,
                                              @RequestParam(value = "avatar") String avatar){
        sysUserService.updateSysUserAvatar(userId,avatar);
        return Result.ok();
    }

    @Operation(summary = "用户分页")
    @Parameters(value = {
            @Parameter(name = "pageNo",description = "当前页",required = true),
            @Parameter(name = "pageSize",description = "页容量",required = true),
            @Parameter(name = "isEnabled",description = "账号是否可用(1-可用 0-不可用)"),
            @Parameter(name = "deptId",description = "部门ID"),
            @Parameter(name = "roleName",description = "角色名称"),
            @Parameter(name = "username",description = "用户账号"),
            @Parameter(name = "nickname",description = "用户名称"),
            @Parameter(name = "start",description = "开始时间"),
            @Parameter(name = "end",description = "结束时间")
    })
    @PostMapping(value = "/getSysUserPage")
    public Result<IPage<SysUser>> getSysUserPage(@RequestParam(value = "pageNo") Integer pageNo,
                                                 @RequestParam(value = "pageSize") Integer pageSize,
                                                 @RequestParam(value = "isEnabled",required = false) Integer isEnabled,
                                                 @RequestParam(value = "deptId",required = false) Long deptId,
                                                 @RequestParam(value = "roleName",required = false) String roleName,
                                                 @RequestParam(value = "username",required = false) String username,
                                                 @RequestParam(value = "nickname",required = false) String nickname,
                                                 @RequestParam(value = "start",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                 @RequestParam(value = "end",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  LocalDateTime end){
        return Result.ok(sysUserService.getSysUserPage(new Page<>(pageNo,pageSize),isEnabled,deptId,roleName,username,nickname,start,end));
    }


    @Operation(summary = "用户详情")
    @PostMapping(value = "/getSysUserDetailByUserId")
    public Result<SysUser> getSysUserDetailByUserId(@Parameter(name = "userId",description = "用户ID",required = true) @RequestParam(value = "userId") Long userId){
        return Result.ok(sysUserService.getSysUserDetailByUserId(userId));
    }
}
