package xg.process.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.CollectionUtils;
import xg.auth.service.SysUserService;
import xg.model.process.Process;
import xg.model.process.ProcessRecord;
import xg.model.process.ProcessTemplate;
import xg.model.system.SysUser;
import xg.process.mapper.OaProcessMapper;
import xg.process.service.MessageService;
import xg.process.service.OaProcessRecordService;
import xg.process.service.OaProcessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xg.process.service.OaProcessTemplateService;
import xg.security.custom.LoginUserInfoHelper;
import xg.vo.process.ApprovalVo;
import xg.vo.process.ProcessFormVo;
import xg.vo.process.ProcessQueryVo;
import xg.vo.process.ProcessVo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author xg
 * @since 2023-04-10
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, Process> implements OaProcessService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private OaProcessRecordService processRecordService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private MessageService messageService;


    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
        IPage<ProcessVo> pageModel = baseMapper.selectPage(pageParam,processQueryVo);
        return pageModel;
    }

    @Override
    public void deployByZip(String deployPath) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(deployPath);

        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deploy = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();

        System.out.println(deploy.getId());
        System.out.println(deploy.getName());

    }

    @Override
    public void startUp(ProcessFormVo processFormVo) {
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        ProcessTemplate processTemplate= processTemplateService.getById(processFormVo.getProcessTemplateId());
        Process process = new Process();
        BeanUtils.copyProperties(processFormVo,process);
        process.setStatus(1);
        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setFormValues(processFormVo.getFormValues());
        process.setTitle(sysUser.getName() + "发起" + processTemplate.getName() + "申请");
        baseMapper.insert(process);
        //将processFormVo转成map
        String processDefinitionKey = processTemplate.getProcessDefinitionKey();
        String businessKey = String.valueOf(processTemplate.getId());
        String formValues = processFormVo.getFormValues();
        JSONObject jsonObject = JSON.parseObject(formValues);
        JSONObject formData = jsonObject.getJSONObject("formData");
        Map<String,Object> map = new HashMap<>();
        for(Map.Entry<String,Object> entry : formData.entrySet()) {
            map.put(entry.getKey(),entry.getValue());
        }

        Map<String,Object> variables = new HashMap<>();
        variables.put("data",map);
        //启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);

        //查询下一个审批人
        List<Task> taskList = this.getCurrentTaskList(processInstance.getId());
        List<String> nameList = new ArrayList<>();
        for(Task task : taskList) {
            String assigneeName = task.getAssignee();
            SysUser user = sysUserService.getByUsername(assigneeName);
            String name = user.getName();
            nameList.add(name);
            //TODO 推送
            messageService.pushPendingMessage(process.getId(),user.getId(),task.getId());

        }

        process.setProcessInstanceId(processInstance.getId());
        process.setDescription("等待" + StringUtils.join(nameList.toArray(), ",") + "审批");


        baseMapper.updateById(process);

        processRecordService.record(process.getId(),1,"发起申请");

    }

    @Override
    public IPage<ProcessVo> findPending(Page<Process> pageParam) {
        // 根据当前人的ID查询
        TaskQuery query = taskService.createTaskQuery().taskAssignee(LoginUserInfoHelper.getUsername()).orderByTaskCreateTime().desc();
        List<Task> list = query.listPage((int) ((pageParam.getCurrent() - 1) * pageParam.getSize()), (int) pageParam.getSize());
        long totalCount = query.count();

        List<ProcessVo> processList = new ArrayList<>();
        // 根据流程的业务ID查询实体并关联
        for (Task item : list) {
            String processInstanceId = item.getProcessInstanceId();
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (processInstance == null) {
                continue;
            }
            // 业务key
            String businessKey = processInstance.getBusinessKey();
            if (businessKey == null) {
                continue;
            }

            Process process = this.getById(Long.parseLong(businessKey));
            if(process == null) continue;
            System.out.println("businessKey : " + businessKey);
            System.out.println("process : " + process);
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId(item.getId());
            processList.add(processVo);
        }
        IPage<ProcessVo> page = new Page<ProcessVo>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
        page.setRecords(processList);
        return page;

    }

    @Override
    public Map<String, Object> show(Long id) {
        //根据流程id获取流程信息Process
        Process process = baseMapper.selectById(id);
        //查询流程记录信息
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessRecord::getProcessId,id);
        List<ProcessRecord> processRecordList = processRecordService.list(wrapper);
        //查询模板
        ProcessTemplate processTemplate = processTemplateService.getById(process);
        boolean isApprove = false;
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());

        for(Task task : taskList) {
            if(task.getAssignee().equals(LoginUserInfoHelper.getUsername())) {
                isApprove  = true;
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("process", process);
        map.put("processRecordList", processRecordList);
        map.put("processTemplate", processTemplate);
        map.put("isApprove", isApprove);
        return map;
    }

    @Override
    public void approve(ApprovalVo approvalVo) {
        //获取任务id
        String taskId = approvalVo.getTaskId();
        Map<String, Object> variables = taskService.getVariables(taskId);
        for(Map.Entry<String,Object> entry : variables.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }

        //判断审批状态 = 1通过 -1驳回，流程结束
        //记录相关信息
        //查询下一个审批人
        //更新流程记录表
        if(approvalVo.getStatus() == 1) {
            Map<String,Object> variable = new HashMap<>();
            taskService.complete(taskId,variable);
        }else {
            this.endTask(taskId);
        }

        String description = approvalVo.getStatus().intValue() == 1 ? "已通过" : "驳回";
        processRecordService.record(approvalVo.getProcessId(),approvalVo.getStatus(),description);

        Process process = baseMapper.selectById(approvalVo.getProcessId());
        List<Task> taskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if(!CollectionUtils.isEmpty(taskList)) {
            List<String> assignList = new ArrayList<>();
            for(Task task : taskList) {
                String assignee = task.getAssignee();
                SysUser sysUser = sysUserService.getByUsername(assignee);
                assignList.add(sysUser.getName());

                //TODO 公众号推送
                messageService.pushPendingMessage(process.getId(), sysUser.getId(), task.getId());

                process.setDescription("等待" + StringUtils.join(assignList.toArray(), ",") + "审批");
                process.setStatus(1);

            }
        }else {
            if(approvalVo.getStatus().intValue() == 1) {
                process.setDescription("审批完成（同意）");
                process.setStatus(2);
            } else {
                process.setDescription("审批完成（拒绝）");
                process.setStatus(-1);
            }
        }
        messageService.pushProcessedMessage(process.getId(), process.getUserId(), approvalVo.getStatus());
        baseMapper.updateById(process);

    }

    @Override
    public IPage<ProcessVo> findProcessed(Page<Process> pageParam) {
        //封装查询条件
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .finished().orderByTaskCreateTime().desc();
        //条件分页查询
        int begin = (int) ((pageParam.getCurrent() - 1) * pageParam.getSize());
        int size = (int) pageParam.getSize();
        List<HistoricTaskInstance> list = query.listPage(begin, size);
        long totalCount = query.count();
        //遍历list封装为ProcessVo
        List<ProcessVo> processVoList = new ArrayList<>();
        for(HistoricTaskInstance item : list) {
            String processInstanceId = item.getProcessInstanceId();
            LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Process::getProcessInstanceId,processInstanceId);
            Process process = baseMapper.selectOne(wrapper);
            ProcessVo processVo = new ProcessVo();
            if(process == null) continue;
            BeanUtils.copyProperties(process,processVo);
//            processVo.setTaskId("0");
            processVoList.add(processVo);
        }
        //Ipage返回
        IPage<ProcessVo> pageModel = new Page<>(pageParam.getCurrent(),pageParam.getSize(),totalCount);
        pageModel.setRecords(processVoList);

        return pageModel;


    }

    @Override
    public IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam) {
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        IPage<ProcessVo> pageModel = baseMapper.selectPage(pageParam, processQueryVo);
        return pageModel;
    }

    private void endTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        // 并行任务可能为null
        if(CollectionUtils.isEmpty(endEventList)) {
            return;
        }
        FlowNode endFlowNode = (FlowNode) endEventList.get(0);
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

        //  临时保存当前活动的原始方向
        List originalSequenceFlowList = new ArrayList<>();
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
        //  清理活动方向
        currentFlowNode.getOutgoingFlows().clear();

        //  建立新方向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);
        List newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);
        //  当前节点指向新的方向
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        //  完成当前任务
        taskService.complete(task.getId());
    }


    //当前任务列表
    private List<Task> getCurrentTaskList(String id) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(id).list();
        return taskList;
    }
}
