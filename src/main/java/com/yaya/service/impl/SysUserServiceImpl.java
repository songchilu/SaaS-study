package com.yaya.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.SysDepartment;
import com.yaya.entity.SysPost;
import com.yaya.entity.SysRole;
import com.yaya.entity.SysUser;
import com.yaya.entity.SysUserPost;
import com.yaya.exception.GlobalCommonException;
import com.yaya.mapper.SysDepartmentMapper;
import com.yaya.mapper.SysPostMapper;
import com.yaya.mapper.SysRoleMapper;
import com.yaya.mapper.SysUserMapper;
import com.yaya.mapper.SysUserPostMapper;
import com.yaya.model.excel.SysUserExcel;
import com.yaya.model.form.SysUserForm;
import com.yaya.model.form.SysUserResetPasswordForm;
import com.yaya.service.SysUserService;
import com.yaya.util.AccountAndPassWordMatchUtils;
import com.yaya.util.DesensitizeUtils;
import com.yaya.util.RedisClient;
import com.yaya.util.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Transactional
@Service
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysDepartmentMapper sysDepartmentMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private SysPostMapper sysPostMapper;
    @Resource
    private SysUserPostMapper sysUserPostMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private RedisClient redisClient;

    @Override
    public void addSysUser(SysUserForm sysUserForm) {
        //判断当前用户是否存在
        String username = sysUserForm.getUsername();
        if(StringUtils.isEmpty(username)){
            throw new GlobalCommonException("用户名不能为空");
        }
        List<SysUser> sysUsers = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getIsDeleted, 0)
        );
        if(CollectionUtils.isNotEmpty(sysUsers)){
            throw new GlobalCommonException("当前用户["+username+"]已存在");
        }
        //当前用户所在的部门
        Long deptId = sysUserForm.getDeptId();
        if (deptId == null) {
            throw new GlobalCommonException("请选择部门");
        }
        SysDepartment sysDepartment = sysDepartmentMapper.selectById(deptId);
        if (sysDepartment == null) {
            throw new GlobalCommonException("部门不存在");
        }
        Integer isDeleted = sysDepartment.getIsDeleted();
        if (isDeleted == 1) {//删除
            throw new GlobalCommonException("部门["+sysDepartment.getDeptName()+"]已被删除");
        }
        Integer status = sysDepartment.getStatus();
        if(status==0){ //禁用
            throw new GlobalCommonException("部门"+sysDepartment.getDeptName()+"已被禁用");
        }
        //角色ID
        Long roleId = sysUserForm.getRoleId();
        if (roleId == null) {
            throw new GlobalCommonException("请选择角色");
        }
        //查询角色信息
        SysRole sysRole = sysRoleMapper.selectById(roleId);
        if (sysRole == null) {
            throw new GlobalCommonException("选择的角色不存在");
        }
        Integer isDeleted_ = sysRole.getIsDeleted();
        if (isDeleted_ == 1) {
            throw new GlobalCommonException("角色["+sysRole.getRoleName()+"]已被删除");
        }
        Integer status_ = sysRole.getStatus();
        if(status_==0){
            throw new GlobalCommonException("角色"+sysRole.getRoleName()+"已被禁用");
        }
        //判断添加的是否是管理角色
        Integer roleType = sysRole.getRoleType();
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        if(roleType==1){//如果是管理角色,那么只有平台管理员才能添加
            if (!b){
                throw new GlobalCommonException("非平台管理员,不能添加管理角色的用户");
            }
        }
        //判断添加的用户所属角色是否属于当前部门(租户)或者子部门下的
        SysDepartment department = sysDepartmentMapper.getTopDepartmentByDeptId(deptId);
        List<SysDepartment> deptList = sysDepartmentMapper.getDepartmentAndSubDepartmentList(department.getDeptId(), null);
        if(CollectionUtils.isEmpty(deptList)){
            throw new GlobalCommonException("当前部门不存在");
        }
        List<Long> deptIds = deptList.stream().map(SysDepartment::getDeptId).distinct().toList();
        //是否可以查到角色
        List<SysRole> roles = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getDeptId, deptIds).eq(SysRole::getIsDeleted, 0));
        if(CollectionUtils.isEmpty(roles)){
            throw new GlobalCommonException("所选角色不属于当前部门");
        }
        //复制
        SysUser sysUser = new SysUser();
        //采用spring自带的bean工具进行POJO的复制
        BeanUtils.copyProperties(sysUserForm,sysUser);
        String postIdStr = sysUserForm.getPostIds();
        if(StringUtils.isNotEmpty(postIdStr)){
            String[] split = postIdStr.split(",");
            if(ArrayUtils.isNotEmpty(split)){
                List<String> postIds = Arrays.asList(split);
                postIds.forEach(postId -> {
                    SysPost sysPost = sysPostMapper.selectById(postId);
                    if (sysPost == null) {
                        throw new GlobalCommonException("岗位不存在");
                    }
                    Integer is_deleted_post = sysPost.getIsDeleted();
                    Integer status_post = sysPost.getStatus();
                    if(is_deleted_post==1){
                        throw new GlobalCommonException("岗位["+sysPost.getPostName()+"]已被删除");
                    }
                    if(status_post==0){
                        throw new GlobalCommonException("岗位["+sysPost.getPostName()+"]已被禁用");
                    }
                    //判断当前岗位是否属于当前部门或者子部门
                    List<SysPost> sysPosts = sysPostMapper.selectList(new LambdaQueryWrapper<SysPost>().in(SysPost::getPostId, postIds).eq(SysPost::getIsDeleted, 0));
                    if(CollectionUtils.isEmpty(sysPosts)){
                        throw new GlobalCommonException("所选岗位不属于当前部门");
                    }
                });
            }
        }
        sysUser.setCreateId(SecurityUtils.getUserId());
        sysUser.setUpdateId(SecurityUtils.getUserId());
        //密码
        String password = sysUser.getPassword();
        if(StringUtils.isEmpty(password)){
            throw new GlobalCommonException("密码不能为空");
        }
        boolean pwd = AccountAndPassWordMatchUtils.checkPassword(password);
        if(!pwd){
            throw new GlobalCommonException("必须由英文字母 + 数字组成 长度至少6位");
        }
        //加密
        String encode = passwordEncoder.encode(password);
        sysUser.setPassword(encode);
        sysUserMapper.insert(sysUser);
        //插入岗位信息
        if(StringUtils.isNotEmpty(postIdStr)){
            Long userId = sysUser.getUserId();
            String[] split = postIdStr.split(",");
            if(ArrayUtils.isNotEmpty(split)){
                List<String> postIds = Arrays.asList(split);
                postIds.forEach(postId -> {
                    SysUserPost sysUserPost = new SysUserPost();
                    sysUserPost.setUserId(userId);
                    sysUserPost.setPostId(Long.parseLong(postId));
                    sysUserPostMapper.insert(sysUserPost);
                });
            }
        }

    }

    @Override
    public Map<String, Object> importSysUser(MultipartFile file, Integer cover) throws IOException {
        //hutool工具类读取excel文件,解析里面的用户信息
        //文件输入流
        InputStream in = file.getInputStream();
        // hutool工具加载文件流
        ExcelReader reader = ExcelUtil.getReader(in);
        //读取文件内容
        List<SysUserExcel> userExcels = reader.readAll(SysUserExcel.class);
        if(CollectionUtils.isEmpty(userExcels)){
            throw new GlobalCommonException("文档内容为空");
        }
        Map<String, Object> map = new HashMap<>();
        /*
         * Excel导入完成后的信息
         * 1. 总条数
         * 2. 成功条数
         * 3. 失败条数
         * 4. 失败的账号提示消息 账号:原因
         */
        //提示消息
        String prompt = "";
        for (int i = 0; i < userExcels.size(); i++) {
            SysUserExcel userExcel = userExcels.get(i);
            SysUser sysUser = new SysUser();
            //复制
            BeanUtils.copyProperties(userExcel,sysUser);
            //用户名
            String username = sysUser.getUsername();
            //用户昵称
            String nickname = sysUser.getNickname();
            //部门编号
            String deptCode = userExcel.getDeptCode();
            //角色编号
            String roleCode = userExcel.getRoleCode();
            //岗位编号
            String postCodes = userExcel.getPostCodes();
            //过期时间
            LocalDateTime expiredTime = userExcel.getExpiredTime();
            if(StringUtils.isEmpty(username)){
                prompt+="<p>第["+(i+1)+"]行账号信息缺失</p><br/>";
                continue;//跳出本次循环
            }
            if(StringUtils.isEmpty(nickname)){
                prompt+="<p>账号["+username+"],昵称信息缺失</p><br/>";
                continue;//跳出本次循环
            }
            if(StringUtils.isEmpty(deptCode)){
                prompt+="<p>账号["+username+"],部门编号信息缺失</p><br/>";
                continue;//跳出本次循环
            }
            if(StringUtils.isEmpty(roleCode)){
                prompt+="<p>账号["+username+"],角色编号信息缺失</p><br/>";
                continue;//跳出本次循环
            }
            if(expiredTime==null){
                prompt+="<p>账号["+username+"],过期时间信息缺失</p><br/>";
                continue;//跳出本次循环
            }
            SysDepartment sysDepartment = sysDepartmentMapper.selectOne(new LambdaQueryWrapper<SysDepartment>()
                    .eq(SysDepartment::getDeptCode, deptCode)
                    .eq(SysDepartment::getIsDeleted, 0)
            );
            if(sysDepartment==null){
                prompt+="<p>账号["+username+"],部门编号错误</p><br/>";
                continue;//跳出本次循环
            }
            //当前部门ID
            Long deptId = sysDepartment.getDeptId();
            sysUser.setDeptId(deptId);
            SysRole sysRole = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, roleCode).eq(SysRole::getIsDeleted, 0));
            if(sysRole==null){
                prompt+="<p>账号["+username+"],角色编号错误</p><br/>";
                continue;//跳出本次循环
            }
            //角色类型
            Integer roleType = sysRole.getRoleType();
            if(roleType==null){
                throw new GlobalCommonException("当前角色编号丢失角色类型");
            }
            if(roleType==1){
                throw new GlobalCommonException("不能导入管理角色的用户");
            }
            sysUser.setRoleId(sysRole.getRoleId());
            List<Long> postIds = new ArrayList<>();
            if(StringUtils.isNotBlank(postCodes)){
                String[] postCodeStrs = postCodes.split(",");
                if(ArrayUtils.isNotEmpty(postCodeStrs)){
                    List<SysPost> sysPosts = sysPostMapper.selectList(new LambdaQueryWrapper<SysPost>()
                            .in(SysPost::getPostCode, Arrays.asList(postCodeStrs))
                            .eq(SysPost::getIsDeleted, 0)
                    );
                    if(CollectionUtils.isEmpty(sysPosts)){
                        prompt+="<p>账号["+username+"],岗位编号错误</p><br/>";
                        continue;//跳出本次循环
                    }else {
                        sysPosts.forEach(sysPost -> {
                            postIds.add(sysPost.getPostId());
                        });
                    }
                }
            }
            //判断账号是否存在
            SysUser selectOne = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUsername, username)
                    .eq(SysUser::getIsDeleted, 0)
            );
            if(selectOne!=null){
                //判断是否覆盖更新
                if(cover==1){//覆盖更新
                    Long userId = selectOne.getUserId();
                    sysUser.setUserId(userId);
                    sysUser.setUpdateId(SecurityUtils.getUserId());//更新人
                    sysUserMapper.updateById(sysUser);
                    if(CollectionUtils.isNotEmpty(postIds)){
                        //删除岗位
                        sysUserPostMapper.delete(new LambdaQueryWrapper<SysUserPost>().eq(SysUserPost::getUserId, userId));
                        //重新添加
                        postIds.forEach(postId -> {
                            SysUserPost sysUserPost = new SysUserPost();
                            sysUserPost.setUserId(userId);
                            sysUserPost.setPostId(postId);
                            sysUserPostMapper.insert(sysUserPost);
                        });
                    }
                }
            }else {//直接插入
                sysUser.setCreateId(SecurityUtils.getUserId());
                sysUser.setUpdateId(SecurityUtils.getUserId());
                //密码(初始密码和账号相同)
                sysUser.setPassword(passwordEncoder.encode(sysUser.getUsername()));
                System.out.println("sysUser:"+sysUser);
                sysUserMapper.insert(sysUser);
                if(CollectionUtils.isNotEmpty(postIds)){
                    Long userId = sysUser.getUserId();
                    postIds.forEach(postId -> {
                        SysUserPost sysUserPost = new SysUserPost();
                        sysUserPost.setUserId(userId);
                        sysUserPost.setPostId(postId);
                        sysUserPostMapper.insert(sysUserPost);
                    });
                }
            }
        }



        map.put("total", userExcels.size());//总条数
        map.put("success_count", userExcels.size());//成功条数
        map.put("failure_count", userExcels.size());//失败条数
        map.put("prompt", prompt);//错误提示消息
        return map;
    }

    @Override
    public void deleteSysUser(List<Long> userIds) {
        if(CollectionUtils.isEmpty(userIds)){
            throw new GlobalCommonException("请选择要删除的用户");
        }
        userIds.forEach(userId -> {
            SysUser sysUser_ = sysUserMapper.selectById(userId);
            if(sysUser_!=null){
                String username = sysUser_.getUsername();
                if("admin".equalsIgnoreCase(username) || "root".equalsIgnoreCase(username) || "operation".equalsIgnoreCase(username)){
                    throw new GlobalCommonException("平台账号【"+username+"】不能删除");
                }
            }
            SysUser sysUser = new SysUser();
            sysUser.setUserId(userId);
            sysUser.setUpdateId(SecurityUtils.getUserId());
            sysUser.setIsDeleted(1);
            sysUserMapper.updateById(sysUser);
        });
    }

    @Override
    public void batchDisSysUserStatus(List<Long> userIds) {
        if(CollectionUtils.isEmpty(userIds)) {
            throw new GlobalCommonException("请选择要封禁的用户");
        }
        //判断用户是否是平台管理账户,如果是不能被封禁
        List<SysUser> sysUsers = sysUserMapper.selectByIds(userIds);
        if(CollectionUtils.isEmpty(sysUsers)) {
            throw new GlobalCommonException("当前要封禁的用户不存在");
        }
        List<String> filter = sysUsers.stream().map(SysUser::getUsername).filter(x -> "root".equalsIgnoreCase(x) || "admin".equalsIgnoreCase(x) || "operation".equalsIgnoreCase(x)).toList();
        if(CollectionUtils.isNotEmpty(filter)) {
            throw new GlobalCommonException("不能封禁平台管理账户[root|admin|operation]");
        }
        userIds.forEach(userId -> {
            SysUser sysUser = new  SysUser();
            sysUser.setUserId(userId);
            sysUser.setIsEnabled(0);//封禁
            sysUser.setUpdateId(SecurityUtils.getUserId());
            sysUserMapper.updateById(sysUser);
        });
    }

    @Override
    public void batchEnableSysUserStatus(List<Long> userIds) {
        if(CollectionUtils.isEmpty(userIds)) {
            throw new GlobalCommonException("请选择要解封的用户");
        }
        userIds.forEach(userId -> {
            SysUser sysUser = new  SysUser();
            sysUser.setUserId(userId);
            sysUser.setIsEnabled(1);//解禁
            sysUser.setUpdateId(SecurityUtils.getUserId());
            sysUserMapper.updateById(sysUser);
        });
    }

    @Override
    public void resetSysUserPassword(SysUserResetPasswordForm sysUserResetPasswordForm) {
        if(CollectionUtils.isEmpty(sysUserResetPasswordForm.getUserIds())) {
            throw new GlobalCommonException("请选择要重置密码的用户");
        }
        String resetPassword = sysUserResetPasswordForm.getResetPassword();
        if(StringUtils.isEmpty(resetPassword)){
            throw new GlobalCommonException("重置的密码不能为空");
        }
        //判断密码是否合法
        boolean b = AccountAndPassWordMatchUtils.checkPassword(resetPassword);
        if(!b){
            throw new GlobalCommonException("密码英文加数字组成长度至少6位");
        }
        //判断用户是否是平台管理账户,如果是不能被重置密码
        List<SysUser> sysUsers = sysUserMapper.selectByIds(sysUserResetPasswordForm.getUserIds());
        if(CollectionUtils.isEmpty(sysUsers)) {
            throw new GlobalCommonException("当前要重置密码的用户不存在");
        }
        List<String> filter = sysUsers.stream().map(SysUser::getUsername).filter(x -> "root".equalsIgnoreCase(x) || "admin".equalsIgnoreCase(x) || "operation".equalsIgnoreCase(x)).toList();
        if(CollectionUtils.isNotEmpty(filter)) {
            throw new GlobalCommonException("不能重置平台管理账户[root|admin|operation]");
        }
        sysUserResetPasswordForm.getUserIds().forEach(userId -> {
            SysUser sysUser_ = sysUserMapper.selectById(userId);
            if(sysUser_==null){
                throw new GlobalCommonException("重置的用户不存在");
            }
            SysUser sysUser = new  SysUser();
            sysUser.setUserId(userId);
            sysUser.setPassword(passwordEncoder.encode(resetPassword));
            sysUser.setUpdateId(SecurityUtils.getUserId());
            sysUserMapper.updateById(sysUser);
        });
    }

    @Override
    public void changeSysUserPassword(Long userId, String oldPassword, String newPassword) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            throw new GlobalCommonException("用户不存在");
        }
        //校验原密码是否正确
        boolean matches = passwordEncoder.matches(oldPassword, sysUser.getPassword());
        if (!matches) {
            throw new GlobalCommonException("原密码输入错误");
        }
        //新密码和原密码相同,不需要修改
        if(oldPassword.equals(newPassword)) {
            throw new GlobalCommonException("原密码和新密码相同");
        }
        //密码格式
        boolean checkedPassword = AccountAndPassWordMatchUtils.checkPassword(newPassword);
        if (!checkedPassword) {
            throw new GlobalCommonException("密码不合法,正确的密码格式:必须由英文+数字组成,长度至少6位");
        }
        sysUser.setPassword(passwordEncoder.encode(newPassword));
        sysUser.setUpdateId(SecurityUtils.getUserId());
        sysUserMapper.updateById(sysUser);//更新
        //删除redis 用户信息
        String keyUser = "login_user:"+sysUser.getUsername();//使用账号作为key
        redisClient.del(keyUser);
    }

    @Override
    public void updateSysUser(SysUserForm sysUserForm) {
        /*
         * 账号只有平台管理员可以更新,租户管理不可以更新
         */
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        //用户的原账号
        Long userId = sysUserForm.getUserId();
        SysUser sysUser_ = sysUserMapper.selectById(userId);
        if(sysUser_ == null) {
            throw new GlobalCommonException("更新的用户不存在");
        }
        Integer isDeleted = sysUser_.getIsDeleted();
        if(isDeleted == 1){
            throw new GlobalCommonException("更新的用户已被删除");
        }
        String username = sysUserForm.getUsername();
        if(StringUtils.isNotEmpty(username)) {
            String username_ = sysUser_.getUsername();
            if(!username_.equals(username)) {
                if(!b){
                    throw new GlobalCommonException("用户账号只有平台管理员可以更新,请联系平台管理员");
                }
            }
        }else {
            sysUserForm.setUsername(null);
        }
        //用户角色只有平台管理可以更新
        Long roleId = sysUserForm.getRoleId();
        if(roleId != null && !roleId.equals(sysUser_.getRoleId())) {
            if(!b){
                throw new GlobalCommonException("用户角色只有平台管理员可以更新,请联系平台管理员");
            }
        }
        //密码不能从此入口更新,有专门的密码修改入口
        String password = sysUserForm.getPassword();
        if(StringUtils.isNotBlank(password)) {
            throw new GlobalCommonException("密码只能在个人中心或管理员通过重置的方式修改");
        }
        SysUser sysUser = new  SysUser();
        BeanUtils.copyProperties(sysUserForm,sysUser);
        sysUser.setUpdateId(SecurityUtils.getUserId());
        sysUserMapper.updateById(sysUser);

        //删除原有的岗位
        sysUserPostMapper.delete(new LambdaQueryWrapper<SysUserPost>()
                .eq(SysUserPost::getUserId,sysUserForm.getUserId())
        );
        //更新岗位
        String postIdStr = sysUserForm.getPostIds();
        if(StringUtils.isNotBlank(postIdStr)) {
            String[] split = postIdStr.split(",");
            if(ArrayUtils.isNotEmpty(split)){
                List<String> postIds = Arrays.asList(split);
                postIds.forEach(postId -> {
                    SysUserPost sysUserPost = new SysUserPost();
                    sysUserPost.setUserId(userId);
                    sysUserPost.setPostId(Long.parseLong(postId));
                    sysUserPostMapper.insert(sysUserPost);
                });
            }
        }

    }

    @Override
    public void updateSysUserAvatar(Long userId, String avatar) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setAvatar(avatar);
        sysUser.setUpdateId(SecurityUtils.getUserId());
        sysUserMapper.updateById(sysUser);
    }

    @Override
    public IPage<SysUser> getSysUserPage(Page<SysUser> page,Integer isEnabled, Long deptId,String roleName, String username, String nickname, LocalDateTime start, LocalDateTime end) {
        IPage<SysUser> sysUserPage = sysUserMapper.getSysUserPage(page, isEnabled, deptId, roleName, username, nickname, start, end);
        if(sysUserPage!=null) {
            List<SysUser> records = sysUserPage.getRecords();
            if(CollectionUtils.isNotEmpty(records)) {
                records.forEach(sysUser -> {
                    //账号脱敏
                    String username_ = sysUser.getUsername();
                    String desnusername = DesensitizeUtils.desensitizePhone(username_);
                    sysUser.setUsername(desnusername);
                    //岗位信息
                    Long userId = sysUser.getUserId();
                    List<SysUserPost> sysUserPosts = sysUserPostMapper.selectList(new LambdaQueryWrapper<SysUserPost>()
                            .eq(SysUserPost::getUserId, userId)
                    );
                    if(CollectionUtils.isNotEmpty(sysUserPosts)) {
                        List<Long> postIds = sysUserPosts.stream().map(SysUserPost::getPostId).distinct().toList();
                        List<SysPost> sysPosts = sysPostMapper.selectByIds(postIds);
                        sysUser.setSysPosts(sysPosts);
                    }
                    //用户的全部门路径
                    Long deptId_ = sysUser.getDeptId();
                    String path = sysDepartmentMapper.getFullDeptNamePathByDeptId(deptId_);
                    sysUser.setFullNamePath(path);
                });
            }
        }
        return sysUserPage;
    }

    @Override
    public SysUser getSysUserDetailByUserId(Long userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if(sysUser != null){
            sysUser.setPassword(null);
            Long deptId = sysUser.getDeptId();
            Long roleId = sysUser.getRoleId();
            SysDepartment sysDepartment = sysDepartmentMapper.selectById(deptId);
            sysUser.setSysDepartment(sysDepartment);
            SysRole sysRole = sysRoleMapper.selectById(roleId);
            sysUser.setSysRole(sysRole);
            List<SysUserPost> sysUserPosts = sysUserPostMapper.selectList(new LambdaQueryWrapper<SysUserPost>()
                    .eq(SysUserPost::getUserId, userId)
            );
            if(CollectionUtils.isNotEmpty(sysUserPosts)){
                List<Long> postIds = sysUserPosts.stream().map(SysUserPost::getPostId).distinct().toList();
                List<SysPost> sysPosts = sysPostMapper.selectByIds(postIds);
                if(CollectionUtils.isNotEmpty(sysPosts)){
                    sysUser.setSysPosts(sysPosts);
                }
            }
            Long createId = sysUser.getCreateId();
            Long updateId = sysUser.getUpdateId();
            SysUser create = sysUserMapper.selectById(createId);
            SysUser update = sysUserMapper.selectById(updateId);
            sysUser.setCreate(create);
            sysUser.setUpdate(update);
        }
        return sysUser;
    }

    @Override
    public Long getSysUserCount() {
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        if(b){
            //平台管理员
            return sysUserMapper.selectCount(null);
        }else {
            SysDepartment department = sysDepartmentMapper.getTopDepartmentByDeptId(SecurityUtils.getDeptId());
            return sysUserMapper.getSysUserCount(department.getDeptId());
        }
    }
}
