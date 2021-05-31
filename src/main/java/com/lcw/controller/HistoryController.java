package com.lcw.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import com.lcw.domain.UserInfo;
import com.lcw.util.R;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.catalina.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ManGo
 */
@RestController
@RequestMapping("/history")
public class HistoryController {
    @Resource
    private HistoryService historyService;
    @Resource
    private RepositoryService repositoryService;

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
    @GetMapping("/historicHighlight")
    public R<Map<String,Object>> historicHighlight(@RequestParam("procInstId") String procInstId, @AuthenticationPrincipal UserInfo userInfo){
//        获取所有节点信息
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        Collection<FlowElement> flowElements = repositoryService
                .getBpmnModel(historicProcessInstance.getProcessDefinitionId())
                .getProcesses()
                .get(0)
                .getFlowElements();

//        获取所有连线
        Map<String,String> sequenceFlowMap = new HashMap<>();
        flowElements.forEach(e -> {
            if (e instanceof SequenceFlow){
                SequenceFlow sequenceFlow = (SequenceFlow)e;
                sequenceFlowMap.put(sequenceFlow.getSourceRef() + sequenceFlow.getTargetRef(),sequenceFlow.getId());
            }
        });

//        获取完成的节点
        Set<String>  activityInstanceSet = new HashSet<>();
        List<HistoricActivityInstance> activityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(procInstId).list();

        /*for (HistoricActivityInstance x : activityInstanceList){
            for (HistoricActivityInstance y : activityInstanceList){
                if (x != y){
                    activityInstanceSet.add(x.getActivityId() + y.getActivityId());
                }
            }
        }*/

//      如果list不按顺序，以下遍历会出现问题
        for (int i = 0;i < activityInstanceList.size() - 1;i++){
            for (int j = i + 1;j<activityInstanceList.size();j++){
                activityInstanceSet.add(activityInstanceList.get(i).getActivityId() + activityInstanceList.get(j).getActivityId());
            }
        }

//        获取高亮连线
        Set<String> highlightLine = new HashSet<>();
        activityInstanceSet.forEach(e -> {
            String sequenceFlowId;
            if ((sequenceFlowId = sequenceFlowMap.get(e)) != null){
                highlightLine.add(sequenceFlowId);
            }
        });

//        获取高亮节点(完成的任务
        /*List<HistoricActivityInstance> finishedList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(procInstId)
                .finished()
                .list();
        Set<String> finishedTask = new HashSet<>();
        finishedList.forEach(e -> finishedTask.add(e.getActivityId()));*/
        Set<String> finishedTask = activityInstanceList.stream()
                .filter(e -> Objects.nonNull(e.getEndTime()))
                .map(HistoricActivityInstance::getActivityId)
                .collect(Collectors.toSet());

//        获取节点高亮（未完成
        /*List<HistoricActivityInstance> unfinishedList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(procInstId)
                .unfinished()
                .list();
        Set<String> waitingToDO = new HashSet<>();
        unfinishedList.forEach(e -> waitingToDO.add(e.getActivityId()));*/
        Set<String> waitingToDO = activityInstanceList.stream()
                .filter(e -> Objects.isNull(e.getEndTime()))
                .map(HistoricActivityInstance::getActivityId)
                .collect(Collectors.toSet());


//        获取当前用户完成的
        String username = userInfo.getUsername();

        /*List<HistoricTaskInstance> finishedUserTaskList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(procInstId)
                .taskAssignee(username)
                .finished()
                .list();*/
        /*Set<String> myFinishedTask = new HashSet<>();
        finishedUserTaskList.forEach(e -> myFinishedTask.add(e.getTaskDefinitionKey()));*/

        Set<String> myFinishedTask = activityInstanceList.stream()
                .filter(e -> username.equals(e.getAssignee()))
                .map(HistoricActivityInstance::getActivityId)
                .collect(Collectors.toSet());


        Map<String,Object> result = new HashMap<>();
        result.put("highlightLine",highlightLine);
        result.put("finishedTask",finishedTask);
        result.put("waitingToDO",waitingToDO);
        result.put("myFinishedTask",myFinishedTask);

        return R.success(result);
    }
}
