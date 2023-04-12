package xg.auth.test.activiti;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XG
 * @create 2023-04-10 11:16
 */
@SpringBootTest
public class ProcessTestDemo2 {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Test
    public void deployProcess() {
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("process/jiaban.bpmn20.xml")
                .addClasspathResource("process/jiaban.png")
                .name("加班申请流程")
                .deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    @Test
    public void startProcessInstance() {
        Map<String,Object> map = new HashMap<>();
        map.put("assign1","lucy3");
        //map.put("assign2","mary3");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban", map);

        System.out.println(processInstance.getProcessDefinitionId());
        System.out.println(processInstance.getId());

    }

    @Test
    public void completTask() {
        Task task = taskService.createTaskQuery()
                .taskAssignee("lucy3")  //要查询的负责人
                .singleResult();//返回一条

        Map<String, Object> variables = new HashMap<>();
        variables.put("assign2", "zhao");
        //完成任务,参数：任务id
        taskService.complete(task.getId(), variables);
    }

    @Test
    public void findTaskList() {
        String assign = "zhao";
        List<Task> list = taskService.createTaskQuery().taskAssignee(assign).list();
        list.forEach(task -> {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        });
    }

}
