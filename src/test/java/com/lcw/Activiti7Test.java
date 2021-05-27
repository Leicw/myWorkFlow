package com.lcw;

import com.lcw.sercurity.SecurityUtil;
import org.activiti.api.model.shared.model.VariableInstance;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SpringBootTest
public class Activiti7Test {

    @Resource
    private ProcessRuntime processRuntime;
    @Resource
    private TaskRuntime taskRuntime;

    @Resource
    private SecurityUtil securityUtil;

    /*查询*/
    @Test
    public void queryProcInst(){
        securityUtil.logInAs("l"); //登录
        Page<ProcessInstance> page = processRuntime.processInstances(Pageable.of(0, 100));

        List<ProcessInstance> content = page.getContent();
        content.forEach(e -> {
            System.out.println("======" + e.getId());
            System.out.println("======" + e.getStatus());
            System.out.println("======" + e.getName());
            System.out.println("======" + e.getProcessDefinitionKey());
            System.out.println("======" + e.getProcessDefinitionId());
        });
    }
    /*创建实例*/
    @Test
    public void startProcInst(){

        securityUtil.logInAs("l"); //登录

        Map<String,Object> map = new HashMap<>();
        map.put("1","2");

        processRuntime.start(ProcessPayloadBuilder
                .start()
                .withProcessDefinitionKey("myProcess_Part1")
                .withName("流程实例名")
                .withBusinessKey("流程实例业务键")
                .withVariables(map)
                .build()
        );

    }


    /*暂停流程*/
    @Test
    public void suspendProcInst(){
        securityUtil.logInAs("l"); //登录
        processRuntime.suspend(ProcessPayloadBuilder.suspend("5a6d40d7-bd1e-11eb-9a46-00ff3102abf3"));
    }
    /*恢复流程实例*/
    @Test
    public void activeProcInst(){
        securityUtil.logInAs("l"); //登录
        processRuntime.resume(ProcessPayloadBuilder.resume("5a6d40d7-bd1e-11eb-9a46-00ff3102abf3"));


    }
    /*删除流程*/
    @Test
    public void delProcInst(){
        securityUtil.logInAs("l"); //登录
        processRuntime.delete(ProcessPayloadBuilder.delete("5a6d40d7-bd1e-11eb-9a46-00ff3102abf3"));
    }

    /*查询流程参数*/
    @Test
    public void queryVariables(){
        securityUtil.logInAs("l"); //登录
        List<VariableInstance> variables = processRuntime.variables(ProcessPayloadBuilder
                .variables()
                .withProcessInstanceId("83b264fa-bd28-11eb-bd8a-00ff3102abf3")
                .build());

        variables.forEach(e -> {
            System.out.println(e.getTaskId());
            System.out.println(e.getName());
            System.out.println(e.getValue().toString());

        });
    }

    /*查询任务*/
    @Test
    public void queryTasks(){
        securityUtil.logInAs("bajie"); //登录
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 100));
//        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 100), TaskPayloadBuilder.tasks().build());
        List<Task> content = tasks.getContent();
        content.forEach(e -> {
            System.out.println(e.getAssignee());
            System.out.println(e.getName());
            System.out.println(e.getId());
        });
    }

    /*完成任务*/
    @Test
    public void completeTaskTest(){
        securityUtil.logInAs("l"); //登录
        Task task = taskRuntime.task("3bb230e0-bd32-11eb-b754-00ff3102abf3");
        if(Objects.isNull(task.getAssignee())){
            taskRuntime.claim(TaskPayloadBuilder
                    .claim()
                    .withTaskId(task.getId())
                    .build()
            );
        }
        taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId("3bb230e0-bd32-11eb-b754-00ff3102abf3").build());

    }
}
