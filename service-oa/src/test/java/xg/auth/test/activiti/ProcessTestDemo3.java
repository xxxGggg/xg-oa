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
 * @create 2023-04-10 15:02
 */
@SpringBootTest
public class ProcessTestDemo3 {
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
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("process/jiaban05.bpmn20.xml")
                .addClasspathResource("process/jiaban.png")
                .name("加班申请流程")
                .deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("jiaban05");
        System.out.println(processInstance.getId());
    }

    @Test
    public void findGroupTaskList() {
        List<Task> list = taskService.createTaskQuery()
                .taskCandidateUser("tom01")
                .list();
        for(Task task : list) {
            System.out.println("----------------------------");
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }
    }

    @Test
    public void claimTask(){
        //拾取任务,即使该用户不是候选人也能拾取(建议拾取时校验是否有资格)
        //校验该用户有没有拾取任务的资格
        Task task = taskService.createTaskQuery()
                .taskCandidateUser("tom01")//根据候选人查询
                .singleResult();
        if(task!=null){
            //拾取任务
            taskService.claim(task.getId(), "tom01");
            System.out.println("任务拾取成功");
        }
    }


    @Test
    public void findTaskList() {
        String assign = "tom01";
        List<Task> list = taskService.createTaskQuery().taskAssignee(assign).list();
        list.forEach(task -> {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        });
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




}
