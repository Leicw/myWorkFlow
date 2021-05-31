package com.lcw.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lcw.domain.FormData;
import com.lcw.domain.UserInfo;
import com.lcw.mappper.FormDataMapper;
import com.lcw.service.FormDataService;
import com.lcw.util.R;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Order;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ManGo
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    @Resource
    private TaskRuntime taskRuntime;
    @Resource
    private ProcessRuntime processRuntime;
    @Resource
    private TaskService taskService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private FormDataService formDataService;

//    获取我的代办任务
    @GetMapping("/getTask")
    public R<List<Map<String, Object>>> getTask(){
        List<Task> content = taskRuntime.tasks(Pageable.of(0, 100, Order.by("createdDate",Order.Direction.DESC))).getContent();

        List<Map<String, Object>> collect = content.stream().map(e -> {
            Map<String, Object> taskMap = new HashMap<>();
            taskMap.put("id", e.getId());
            taskMap.put("name", e.getName());
            taskMap.put("status", e.getStatus());
            taskMap.put("assignee", (Objects.nonNull(e.getAssignee()) ? e.getAssignee():"无执行人"));
            taskMap.put("createdDate", DateUtil.formatDateTime(e.getCreatedDate()));

//            需要流程实例名称
            ProcessInstance processInstance = processRuntime.processInstance(e.getProcessInstanceId());
            taskMap.put("processInstanceName", processInstance.getName());

            return taskMap;
        }).sorted((x, y) -> {
            String xStartDate = (String) x.get("createdDate");
            String yStartDate = (String) y.get("createdDate");
            return yStartDate.compareTo(xStartDate);
        }).collect(Collectors.toList());

        return R.success(collect);
    }
//    完成任务
    @PostMapping("/complete")
    public R<Boolean> getTask(@RequestParam String taskId){
        Task task = taskRuntime.task(taskId);
        if (Objects.isNull(task.getAssignee())) {
            taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(taskId).build());
        }

        taskRuntime.complete(TaskPayloadBuilder.complete()
                .withTaskId(taskId)
                .build());
        return R.success(true);
    }
//    渲染动态表单
    @GetMapping("/formDataShow")
    public R formDataShow(@RequestParam String taskId){
//        获取任务
        org.activiti.engine.task.Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
//        获取历史表单控件填写的信息
        Map<String, String> historicControlData = new HashMap<>();
        List<FormData> historicFormData = formDataService.list(new QueryWrapper<FormData>().ge("PROC_INST_ID_", task.getProcessInstanceId()));
        historicFormData.forEach(e -> {
            historicControlData.put(e.getControlId(),e.getControlValue());
        });

//        获取用户任务
        UserTask userTask = (UserTask)repositoryService.getBpmnModel(task.getProcessDefinitionId()).getFlowElement(task.getTaskDefinitionKey());
//        获取表单信息
        List<FormProperty> formProperties = userTask.getFormProperties();
        if (Objects.isNull(formProperties)){
            return R.success("任务无表单");
        }
//        根据规则渲染表单  		任务节点的key-_!类型-!名称-!默认值-_!是否参数
        List<Map<String,Object>> formDatas = new ArrayList<>();
        formProperties.forEach(e -> {
            String[] splitFK = e.getId().split("-_!");
            Map<String,Object> formData = new HashMap<>();
            formData.put("id",splitFK[0]);
            formData.put("controlType",splitFK[1]);
            formData.put("controlLabel",splitFK[2]);
//            formData.put("controlDefValue",splitFK[3]);
//            如果默认值是FormProperty_开头，表示需要从之前的表单信息中获取默认值
            String controlDefValue = splitFK[3];
            if (controlDefValue.startsWith("FormProperty_") && historicControlData.containsKey(controlDefValue)){
                controlDefValue = historicControlData.get(controlDefValue);
            }
            formData.put("controlDefValue",controlDefValue);

//            为了保存动态表单取得这个参数
            formData.put("controlParam",splitFK[4]);

            formDatas.add(formData);
        });
        return R.success(formDatas);
    }
//    保存动态表单
    @PostMapping("/saveFormData")
    public R<Boolean> saveFormData(@RequestParam String taskId,@RequestParam String formDatas){
        Task task = taskRuntime.task(taskId);
        boolean notIsVariable = false;
        Map<String,Object> variableMap = new HashMap<>();
//        解析表单
        String[] formData = formDatas.split("!_!");
        List<FormData> formDataList = new ArrayList<>();
        for (String datas : formData){
            String[] data = datas.split("-_!");
            FormData entity = new FormData();
//            控件id-_!控件值-_!是否参数!_!控件id-_!控件值-_!是否参数…
            entity.setProcInstId(task.getProcessInstanceId())
                    .setProcDefId(task.getProcessDefinitionId())
                    .setFormKey(task.getFormKey())
                    .setControlId(data[0])
                    .setControlValue(data[1]);
            formDataList.add(entity);

//            处理需要保存的流程变量
            String variableType = data[2];

//              不是变量
            if (notIsVariable = "f".equals(variableType)) {
                System.out.println("not variables");
//                字符串变量
            } else if ("s".equals(variableType)) {
                variableMap.put(entity.getControlId(), data[1]);
//                时间变量
            } else if ("t".equals(variableType)) {
                variableMap.put(entity.getControlId(), DateUtil.parseLocalDateTime(data[1]));
//                布尔变量
            } else if ("b".equals(variableType)) {
                variableMap.put(entity.getControlId(), BooleanUtil.toBoolean(data[1]));
            } else {
                throw new RuntimeException("参数类型：" + variableType + "错误");
            }
        }

//         完成任务
        if (notIsVariable){
            taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(taskId).build());
        }else {
            taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(taskId).withVariables(variableMap).build());
        }

//        保存表数据
        formDataService.saveBatch(formDataList);
        return R.success(true);
    }

}
