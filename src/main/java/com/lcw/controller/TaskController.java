package com.lcw.controller;

import cn.hutool.core.date.DateUtil;
import com.lcw.domain.UserInfo;
import com.lcw.util.R;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Order;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
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
//    保存动态表单
}
