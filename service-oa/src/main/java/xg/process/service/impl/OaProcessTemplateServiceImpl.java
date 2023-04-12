package xg.process.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import xg.model.process.ProcessTemplate;
import xg.model.process.ProcessType;
import xg.process.mapper.OaProcessTemplateMapper;
import xg.process.service.OaProcessService;
import xg.process.service.OaProcessTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xg.process.service.OaProcessTypeService;

import java.util.List;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author xg
 * @since 2023-04-10
 */
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {

    @Autowired
    private OaProcessTypeService processTypeService;

    @Autowired
    private OaProcessService processService;


    @Override
    public IPage<ProcessTemplate> selectPageProcessTemplate(Page<ProcessTemplate> pageParam) {
        Page<ProcessTemplate> processTemplatePage = baseMapper.selectPage(pageParam, null);
        List<ProcessTemplate> processTemplateList = processTemplatePage.getRecords();
        for(ProcessTemplate processTemplate : processTemplateList) {
            Long processTypeId = processTemplate.getProcessTypeId();

            LambdaQueryWrapper<ProcessType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessType::getId,processTypeId);
            ProcessType processType = processTypeService.getOne(wrapper);
            if(processType == null) continue;
            processTemplate.setProcessTypeName(processType.getName());

        }
        return processTemplatePage;
    }

    @Override
    public void publish(Long id) {
        ProcessTemplate processTemplate = baseMapper.selectById(id);
        processTemplate.setStatus(1);
        baseMapper.updateById(processTemplate);
        String processDefinitionPath = processTemplate.getProcessDefinitionPath();
        if(!StringUtils.isEmpty(processDefinitionPath)) {

            processService.deployByZip(processDefinitionPath);
        }
    }


}
