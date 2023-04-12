package xg.auth.test.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author XG
 * @create 2023-04-09 20:30
 */
@SpringBootTest
public class ProcessTest {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    @Test
    public void suspendProcessInstanceAll() {
        ProcessDefinition qingjia = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("qingjia").list().get(0);
        boolean suspended = qingjia.isSuspended();
        if(suspended) {

            repositoryService.activateProcessDefinitionById(qingjia.getId(),true,null);
            System.out.println(qingjia.getId());
        }else {
            repositoryService.suspendProcessDefinitionById(qingjia.getId(),true,null);
            System.out.println(qingjia.getId());
        }
    }

    @Test
    public void startUpProcessAddBusinessKey() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia", "1001");
        System.out.println(processInstance.getBusinessKey());
    }

    @Test
    public void findCompleteTaskLis() {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee("lisi")
                .finished().list();
        list.forEach(historicTaskInstance -> {
            System.out.println("流程实例id：" + historicTaskInstance.getProcessInstanceId());
            System.out.println("任务id：" + historicTaskInstance.getId());
            System.out.println("任务负责人：" + historicTaskInstance.getAssignee());
            System.out.println("任务名称：" + historicTaskInstance.getName());
        });
    }


    @Test
    public void completeTask(){
        Task task = taskService.createTaskQuery().taskAssignee("zhangsan").singleResult();
        taskService.complete(task.getId());
    }


    @Test
    public void findTaskList() {
        String assign = "zhangsan";
        List<Task> list = taskService.createTaskQuery().taskAssignee(assign).list();

        list.forEach(item -> {
            System.out.println(item.getProcessInstanceId());
            System.out.println(item.getId());
            System.out.println(item.getAssignee());
            System.out.println(item.getName());
        });
    }




    @Test
    public void startProcess() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qingjia");
        System.out.println(processInstance.getProcessInstanceId());
        System.out.println(processInstance.getId());
        System.out.println(processInstance.getActivityId());
    }



    @Test
    public void deployProcess() {
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia.bpmn20.xml")
                .addClasspathResource("process/qingjia.png")
                .name("请假申请流程")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

}
