package xg.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import xg.model.process.ProcessTemplate;
import xg.model.process.ProcessType;
import xg.process.mapper.OaProcessTypeMapper;
import xg.process.service.OaProcessTemplateService;
import xg.process.service.OaProcessTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author xg
 * @since 2023-04-10
 */
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {

    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Override
    public List<ProcessType> findProcessType() {
        List<ProcessType> processTypeList = baseMapper.selectList(null);

        for(ProcessType processType : processTypeList) {
            Long typeId = processType.getId();
            LambdaQueryWrapper<ProcessTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessTemplate::getProcessTypeId,typeId);
            List<ProcessTemplate> processTemplateList = processTemplateService.list(wrapper);

            processType.setProcessTemplateList(processTemplateList);
        }

        return processTypeList;
    }
}
