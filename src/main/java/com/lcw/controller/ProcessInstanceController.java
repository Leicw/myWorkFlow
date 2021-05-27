package com.lcw.controller;

import cn.hutool.core.date.DateUtil;
import com.lcw.domain.UserInfo;
import com.lcw.sercurity.SecurityUtil;
import com.lcw.util.R;
import org.activiti.api.model.shared.model.VariableInstance;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Order;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ManGo
 */
@RestController
@RequestMapping("/processinstance")
public class ProcessInstanceController {
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private ProcessRuntime processRuntime;
    @Resource
    private SecurityUtil securityUtil;

//    查询流程实例
    @GetMapping("/getProcessInstances")
    public R<List<Map<String, Object>>> getProcessInstances() {

//        List<ProcessInstance> content = processRuntime.processInstances(Pageable.of(0, 100, Order.by("startDate", Order.Direction.ASC))).getContent();  order不起作用
        List<ProcessInstance> content = processRuntime.processInstances(Pageable.of(0, 100)).getContent();

        List<Map<String, Object>> collect = content.stream().map(e -> {
            Map<String, Object> processDefinitionMap = new HashMap<>();
            processDefinitionMap.put("id", e.getId());
            processDefinitionMap.put("name", e.getName());
            processDefinitionMap.put("status", e.getStatus());
            processDefinitionMap.put("processDefinitionId", e.getProcessDefinitionId());
            processDefinitionMap.put("processDefinitionKey", e.getProcessDefinitionKey());
            processDefinitionMap.put("processDefinitionVersion", e.getProcessDefinitionVersion());
            processDefinitionMap.put("startDate", DateUtil.formatDateTime(e.getStartDate()));

//            查询流程定义，获取更详细信息
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(e.getProcessDefinitionId()).singleResult();
            processDefinitionMap.put("deploymentId", processDefinition.getDeploymentId());
            processDefinitionMap.put("resourceName", processDefinition.getResourceName());

            return processDefinitionMap;
        }).sorted((x, y) -> {
            String xStartDate = (String) x.get("startDate");
            String yStartDate = (String) y.get("startDate");
            return yStartDate.compareTo(xStartDate);
        }).collect(Collectors.toList());

        return R.success(collect);
    }
//    启动流程实例
    @PostMapping("/startProcessInstance")
    public R<String> startProcessInstance(
            @RequestParam String name,
            @RequestParam String processDefinitionKey) {

        ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder
                .start()
                .withProcessDefinitionKey(processDefinitionKey)
                .withName(name)
                .build());

        return R.success(processInstance.getId());
    }

//    删除流程实例
    @PostMapping("/delProcessInstance")
    public R<Boolean> delProcessInstance(@RequestParam String processInstanceId){
        processRuntime.delete(ProcessPayloadBuilder.delete(processInstanceId));
        return R.success(true);
    }

//    挂起流程实例
    @PostMapping("/suspendProcessInstance")
    public R<Boolean> suspendProcessInstance(@RequestParam String processInstanceId){
        processRuntime.suspend(ProcessPayloadBuilder.suspend(processInstanceId));
        return R.success(true);
    }
//    激活流程实例
    @PostMapping("/resumeProcessInstance")
    public R<Boolean> resumeProcessInstance(@RequestParam String processInstanceId){
        processRuntime.resume(ProcessPayloadBuilder.resume(processInstanceId));
        return R.success(true);
    }
//    查询流程实例参数
    @GetMapping("/getVariables")
    public R<List<VariableInstance>> getVariables(@RequestParam String processInstanceId){
        List<VariableInstance> variables = processRuntime.variables(ProcessPayloadBuilder.variables().withProcessInstanceId(processInstanceId).build());
        return R.success(variables);
    }
}
