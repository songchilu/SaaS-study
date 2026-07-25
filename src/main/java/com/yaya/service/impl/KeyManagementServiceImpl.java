package com.yaya.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaya.entity.KeyManagement;
import com.yaya.entity.SysDepartment;
import com.yaya.mapper.KeyManagementMapper;
import com.yaya.mapper.SysDepartmentMapper;
import com.yaya.service.KeyManagementService;
import com.yaya.util.CryptoUtils;
import com.yaya.util.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Transactional
@Service
public class KeyManagementServiceImpl implements KeyManagementService {

    @Resource
    private KeyManagementMapper keyManagementMapper;
    @Resource
    private SysDepartmentMapper sysDepartmentMapper;

    @Override
    public void createKey(Long deptId,String remark) {
        if(deptId == null){
            Long deptId_ = SecurityUtils.getDeptId();
            SysDepartment department = sysDepartmentMapper.getTopDepartmentByDeptId(deptId_);
            deptId = department.getDeptId();
        }
        KeyManagement keyManagement = new KeyManagement();
        keyManagement.setDeptId(deptId);
        Map<String, String> rsaKey = CryptoUtils.createRsaKey();
        String publicKey = rsaKey.get("publicKey");//获取公钥
        String privateKey = rsaKey.get("privateKey");//获取私钥
        keyManagement.setCreateId(SecurityUtils.getUserId());//创建人
        keyManagement.setUpdateId(SecurityUtils.getUserId());//更新人
        keyManagement.setPublicKeyContent(publicKey);//公钥
        keyManagement.setPrivateKeyContent(privateKey);//私钥
        keyManagement.setRemark(remark);//备注
        keyManagementMapper.insert(keyManagement);
    }

    @Override
    public IPage<KeyManagement> getKeyManagementPage(Page<KeyManagement> page, String deptName) {
        Long deptId=null;
        Boolean b = SecurityUtils.isRootOrAdminOrOperation();
        if(!b){
            deptId = SecurityUtils.getDeptId();
            SysDepartment department = sysDepartmentMapper.getTopDepartmentByDeptId(deptId);
            deptId = department.getDeptId();
        }
        return keyManagementMapper.getKeyManagementPage(page, deptName,deptId);
    }

    @Override
    public void deleteKeyManagement(Long keyId) {
        keyManagementMapper.deleteById(keyId);
    }
}
