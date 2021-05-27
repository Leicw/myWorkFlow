package com.lcw.controller;

import cn.hutool.core.date.DateUtil;
import com.lcw.domain.UserInfo;
import com.lcw.util.R;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ManGo
 */
@RestController
@RequestMapping("/history")
public class HistoryController {
    @Resource
    private HistoryService historyService;
//    查询用户历史任务
    @GetMapping("/getHistoricTaskByUserName")
    public R<List<HistoricTaskInstance>> getHistoricTaskByUserName(@AuthenticationPrincipal UserInfo userInfo) {

        List<HistoricTaskInstance> content = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(userInfo.getName())
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .finished()
                .list();

        /*List<Map<String, Object>> collect = content.stream().map(e -> {
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
        }).collect(Collectors.toList());*/

        return R.success(content);
    }
//    根据实例id查询任务

    @GetMapping("/getHistoricTaskByPiId")
    public R<List<HistoricTaskInstance>> getHistoricTaskByPiId(@RequestParam String processInstanceId) {

        List<HistoricTaskInstance> content = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .finished()
                .list();

            /*List<Map<String, Object>> collect = content.stream().map(e -> {
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
            }).collect(Collectors.toList());*/

        return R.success(content);
    }

//    高亮渲染流程历史
}
