package com.yaya.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.config.YaYaConfig;
import com.yaya.entity.SysDepartment;
import com.yaya.entity.SysFile;
import com.yaya.entity.SysUser;
import com.yaya.exception.GlobalCommonException;
import com.yaya.mapper.SysDepartmentMapper;
import com.yaya.mapper.SysFileMapper;
import com.yaya.mapper.SysUserMapper;
import com.yaya.service.FileService;
import com.yaya.util.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Transactional
@Service
public class FileServiceImpl implements FileService {


    //本系统支持的文件格式
    private final List<String> extensions= Arrays.asList(".zip",".rar",".mp3",".mp4",".pdf",".docx",".doc",".xlsx",".xls",".pptx",".ppt",".txt");
    //头像格式
    private final List<String> picExtensions = Arrays.asList(".jpg",".jpeg",".png",".gif");
    //MIME格式
    private static final Set<String> ALLOWED_MIME = Set.of("image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp");
    //校验文件格式的工具
    private final Tika tika = new Tika();

    @Resource
    private SysFileMapper sysFileMapper;

    @Resource
    private SysDepartmentMapper sysDepartmentMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private YaYaConfig yaYaConfig;




    @Override
    public Map<String, String> uploadImage(MultipartFile file, Integer compress) throws IOException {
        //获取当前操作用户的部门信息
        Long deptId = SecurityUtils.getDeptId();
        //获取租户信息
        SysDepartment department = sysDepartmentMapper.getTopDepartmentByDeptId(deptId);
        //判断文件是否为空
        boolean empty = file.isEmpty();
        if(empty){
            throw new GlobalCommonException("文件内容为空");
        }
        //获取文件名
        String filename = file.getOriginalFilename();
        if(StringUtils.isEmpty(filename)){
            throw new GlobalCommonException("文件名称不能为空");
        }
        //判断文件是否存在后缀
        int lastIndexIfDot = filename.lastIndexOf(".");
        if(lastIndexIfDot==-1){
            throw new GlobalCommonException("不能识别的文件格式");
        }
        //判断文件是否是系统支持的文件格式
        InputStream in = file.getInputStream();
        String detectedMime = tika.detect(in).toLowerCase();
        if (!ALLOWED_MIME.contains(detectedMime)) {
            throw new GlobalCommonException("系统不支持这样的文件格式");
        }
        //文件格式截取
        String suffix = filename.substring(lastIndexIfDot);
        //变成小写
        String s = suffix.toLowerCase();
        //判断格式是否正确
        boolean contains = picExtensions.contains(s);
        //如果不是我们支持的格式抛出异常
        if(!contains){
            throw new GlobalCommonException("文件不存在或者格式错误");
        }
        //生成新文件名称
        String filename_new = IdUtil.getSnowflakeNextIdStr()+suffix;
        //文件保存的位置
        LocalDate now = LocalDate.now();
        //拼接文件保存位置的具体目录 file+用户租户ID+图片目录+年+月+日
        String pinJoin="file/"+department.getDeptId()+"/images/"+now.getYear()+"/"+now.getMonthValue()+"/"+now.getDayOfMonth();
        //文件保存到服务器的硬盘地址
        String localAddress =yaYaConfig.getLocalUrl()+"/"+pinJoin;
        //判断文件路径是否存在
        File f_ = new File(localAddress);
        if(!f_.exists()){
            boolean md = f_.mkdirs();//如果不存在逐层创建
            log.info("文件上传中...存储目录不存在...目录创建中...目录创建状态:{}",md?"成功":"失败");
        }

        //本地地址-完整
        String local_save_address=localAddress+"/"+filename_new;
        //用户访问地址-完整
        String local_server_url=pinJoin+"/"+filename_new;

        //图片是否压缩
        if(compress != null && compress == 1){
            //压缩图片
            long targetBytes = 200 * 1024L;//目标压缩大小 当前目标200K
            long originalSize = IOUtils.consume(in);//源文件大小
            //如果源文件比目标文件小,不需要压缩
            if (originalSize <= targetBytes) {
                Thumbnails.of(in).scale(1.0).outputQuality(1.0).toFile(new File(local_save_address));
                System.out.println("无需压缩");
            }else {
                System.out.println("原始大小: " + (originalSize / 1024) + " KB");
                //先将文件保存到指定位置
                file.transferTo(new File(local_save_address));
                //获取这个文件循环压缩
                File tempFile = new File(local_save_address);
                double quality = 0.95;                 //图片质量
                double scale = 1.0;                    // 当前缩放比例
                int maxAttempts = 20;                  //最多压缩次数,防止死循环
                int attempt = 0;
                //文件过大,要想达到目标大小,需要渐进式循环压缩
                while (attempt < maxAttempts) {
                    attempt++; //记录压缩次数
                    //压缩,并将压缩文件保存到临时目录
                    Thumbnails.of(tempFile)
                            .scale(scale)
                            .outputFormat("jpg")
                            .outputQuality(quality)
                            .toFile(tempFile);
                    tempFile =  new File(local_save_address+".jpg");
                    //压缩后的文件大小
                    long currentSize = tempFile.length();
                    System.out.printf("第%d次尝试 → 质量: %.2f, 缩放: %.2f → 大小: %d KB%n",attempt, quality, scale, currentSize / 1024);
                    //如果压缩后的文件已经比目标文件小,那么压缩完成直接输出
                    if (currentSize <= targetBytes) {
                        log.info("压缩✅....文件的压缩大小为:{}",currentSize);
                        break;
                    }
                }
                log.info("图片压缩已完成....");
            }
        }else {
            log.info("图片不压缩....直接保存....");
            //保存文件到本地
            file.transferTo(new File(local_save_address));
        }

        //数据库保存
        SysFile sysFile = new SysFile();
        sysFile.setFileName(filename);
        sysFile.setDeptId(department.getDeptId()); //租户ID
        sysFile.setCreateId(SecurityUtils.getUserId());
        sysFile.setFileLocalUrl(local_save_address);
        sysFile.setFileServerUrl(local_server_url);
        sysFileMapper.insert(sysFile);

        //返回服务器访问地址
        Map<String,String> map = new HashMap<>();
        map.put("image_url",local_server_url);
        map.put("image_compress_url",local_server_url.endsWith(".jpg")?local_server_url:local_server_url+".jpg");
        return map;
    }

    @Override
    public Map<String, String> uploadFile(MultipartFile file) throws IOException {
        //获取当前操作用户的租户信息
        SysDepartment department = sysDepartmentMapper.getTopDepartmentByDeptId(SecurityUtils.getDeptId());
        //判断文件是否为空
        boolean empty = file.isEmpty();
        if(empty){
            throw new GlobalCommonException("文件内容为空");
        }
        //获取文件名
        String filename = file.getOriginalFilename();
        if(StringUtils.isEmpty(filename)){
            throw new GlobalCommonException("文件名称不能为空");
        }
        //判断文件是否存在后缀
        int lastIndexIfDot = filename.lastIndexOf(".");
        if(lastIndexIfDot==-1){
            throw new GlobalCommonException("不能识别的文件格式");
        }
        //文件格式截取
        String suffix = filename.substring(lastIndexIfDot);
        //变成小写
        String s = suffix.toLowerCase();
        //判断格式是否正确
        boolean contains = extensions.contains(s);
        //如果不是我们支持的格式抛出异常
        if(!contains){
            if(picExtensions.contains(s)){
                throw new GlobalCommonException("图片上传请使用uploadImage接口");
            }
            throw new GlobalCommonException("支持的文件格式为:"+ List.of(extensions));
        }
        //生成新文件名称
        String filename_new = IdUtil.getSnowflakeNextIdStr()+suffix;
        //文件保存的位置
        LocalDate now = LocalDate.now();
        //拼接文件保存位置的具体目录 file+用户租户ID+目录+年+月+日
        String pinJoin="file/"+department.getDeptId()+"/other/"+now.getYear()+"/"+now.getMonthValue()+"/"+now.getDayOfMonth();
        //文件保存到服务器的硬盘地址
        String localUrl =yaYaConfig.getLocalUrl()+"/"+pinJoin;
        //判断文件路径是否存在
        File f_ = new File(localUrl);
        if(!f_.exists()){
            boolean md = f_.mkdirs();//如果不存在逐层创建
            log.info("文件上传中...存储目录不存在...目录创建中...目录创建状态:{}",(md?"成功":"失败"));
        }

        //本地地址-完整
        String local_save_address=localUrl+"/"+filename_new;
        //用户访问地址-完整
        String local_server_url=pinJoin+"/"+filename_new;

        //保存文件到本地
        file.transferTo(new File(local_save_address));

        //数据库保存
        SysFile sysFile = new SysFile();
        sysFile.setFileName(filename);
        sysFile.setDeptId(department.getDeptId());//最顶层，模拟租户
        sysFile.setCreateId(SecurityUtils.getUserId());
        sysFile.setFileLocalUrl(local_save_address);
        sysFile.setFileServerUrl(local_server_url);
        sysFileMapper.insert(sysFile);

        //返回服务器访问地址
        Map<String,String> map = new HashMap<>();
        map.put("file_url",local_server_url);
        return map;
    }

    @Override
    public IPage<SysFile> getFilePage(Page<SysFile> page, String fileServerUrl, LocalDateTime startTime, LocalDateTime endTime, Long deptId) {
        //判断是否是平台管理员,如果是平台管理员可以查看全部以及基于租户查询，如果不是平台管理员,只能看见自己租户下的文件
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        if(!b){
            Long deptId_ = SecurityUtils.getDeptId();
            SysDepartment department = sysDepartmentMapper.getTopDepartmentByDeptId(deptId_);
            deptId = department.getDeptId();
        }
        return sysFileMapper.getFilePage(page, fileServerUrl, startTime, endTime, deptId);
    }
}
