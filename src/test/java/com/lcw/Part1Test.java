package com.lcw;

import com.lcw.sercurity.SecurityUtil;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class Part1Test {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    @Resource
    private SecurityUtil securityUtil;


    @Test
    public void DeployTest() throws FileNotFoundException {
        repositoryService
                .createDeployment()
                .addClasspathResource("bpmn/Part4_Task_claim.bpmn")
                .name("部署name")
                .key("部署key")
                .deploy();
//        System.out.println(System.getProperty("user.dir"));

        /*ZipInputStream zipInputStream = new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream("bpmn/Part1_DeploymentV2.zip"));

        Deployment deploy = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .addClasspathResource("bpmn/Part1_Deployment2.bpmn")
                .name("name")
                .key("key")
                .deploy();*/
//        System.out.println(deploy.getName());

    }

    @Test
    public void getDeploymentTest(){
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        list.forEach(e -> System.out.println(e.getName()+e.getId()+e.getKey()));

    }

    /*删除所有流程定义*/
    @Test
    public void delAllDeployment(){
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        list.forEach(e -> {
            repositoryService.deleteDeployment(e.getId());
        });

    }

    @Test
    public void timeTest(){
//        System.out.println(new LocalDateTime().plusWeeks(2).toDate());
        System.out.println(DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS"));
    }

    @Test
    public void startProc(){
//        ProcessInstance myProcess_part2 = runtimeService.startProcessInstanceByKey("myProcess_Part2");
        ProcessInstance myProcess_part2 = runtimeService.startProcessInstanceById("myProcess_claim:1:4a940231-bc3e-11eb-b8e3-00ff3102abf3");
    }

    @Test
    public void startProcByBuilder(){
        /*securityUtil.logInAs("l"); //登录*/
        runtimeService.createProcessInstanceBuilder().businessKey("测试builder").name("测试builder").processDefinitionId("myProcess_claim:1:4a940231-bc3e-11eb-b8e3-00ff3102abf3").start();
    }

    @Test
    public void testProcessInstance(){
//        查询流程
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        list.forEach(e -> {
            System.out.println(e.getName());
            System.out.println(e.getProcessDefinitionKey());
            System.out.println(e.getProcessDefinitionId());
            System.out.println(e.getProcessInstanceId());
            System.out.println(e.isSuspended());
            System.out.println(e.isEnded());

        });

    }

    @Test
    public void suspendProc(){
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        list.forEach(e -> {
            runtimeService.suspendProcessInstanceById(e.getProcessInstanceId());
        });

    }

    @Test
    public void activeProc(){
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        list.forEach(e -> {
            runtimeService.activateProcessInstanceById(e.getProcessInstanceId());
        });
    }

    @Test
    public void delProc(){
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        list.forEach(e -> {
            runtimeService.deleteProcessInstance(e.getProcessInstanceId(),"删除原因");
        });
    }


    @Test
    public void TaskQuery(){
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("bajie").list();
        tasks.forEach(e -> {
            System.out.println(e.getName());
            System.out.println(e.getProcessInstanceId());
            System.out.println(e.getAssignee());
            System.out.println(e.getDueDate());
        });

    }

    @Test
    public void setTaskDueDate(){
        taskService.setDueDate("61bba5fd-bc38-11eb-880d-00ff3102abf3",new Date());
    }

    @Test
    public void completeTask(){
        Map<String,Object> map = new HashMap<>();
        map.put("hhh","123");

        taskService.complete("61bba5fd-bc38-11eb-880d-00ff3102abf3",map);

    }

    @Test
    public void claimTask(){
//        taskService.claim("6e07ef5e-bc3e-11eb-90df-00ff3102abf3","123");

        taskService.setAssignee("6e07ef5e-bc3e-11eb-90df-00ff3102abf3",null);
    }


    @Test
    public void historic(){
//        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId("6e046cea-bc3e-11eb-90df-00ff3102abf3").list();
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId("6e046cea-bc3e-11eb-90df-00ff3102abf3").list();

        System.out.println(list.size());


    }
}
