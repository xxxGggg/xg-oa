package xg.process.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import xg.auth.service.SysUserService;
import xg.model.process.ProcessRecord;
import xg.model.system.SysUser;
import xg.process.mapper.OaProcessRecordMapper;
import xg.process.service.OaProcessRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xg.security.custom.LoginUserInfoHelper;

/**
 * <p>
 * 审批记录 服务实现类
 * </p>
 *
 * @author xg
 * @since 2023-04-11
 */
@Service
public class OaProcessRecordServiceImpl extends ServiceImpl<OaProcessRecordMapper, ProcessRecord> implements OaProcessRecordService {

    @Autowired
    private SysUserService sysUserService;
    @Override
    public void record(Long processId, Integer status, String description) {

        Long userId = LoginUserInfoHelper.getUserId();
        SysUser sysUser = sysUserService.getById(userId);
        //String username = LoginUserInfoHelper.getUsername();

        ProcessRecord processRecord = new ProcessRecord();
        processRecord.setProcessId(processId);
        processRecord.setStatus(status);
        processRecord.setDescription(description);
        processRecord.setOperateUser(sysUser.getName());
        processRecord.setOperateUserId(userId);
        baseMapper.insert(processRecord);
    }

}
