package xg.auth.test.activiti;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @author XG
 * @create 2023-04-10 10:43
 */
public class MyTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask task) {
        if(task.getName().equals("经理审批")) {
            task.setAssignee("jack");
        }else if(task.getName().equals("人事审批")) {
            task.setAssignee("tom");
        }
    }
}
