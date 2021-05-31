package com.lcw.controller;

import cn.hutool.core.date.DateUtil;
import com.lcw.util.R;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

/**
 * @author ManGo
 */
@RestController
@RequestMapping("/processDefinition")
public class ProcessDefinitionController {
    @Resource
    private ProcessRuntime processRuntime;

    @Resource
    private RepositoryService repositoryService;

//    上传流程定义文件
    @PostMapping("/uploadStreamAndDeployment")
    public R<String> uploadStreamAndDeployment(@RequestParam MultipartFile processFile,@RequestParam String deploymentName){
        String fileName = processFile.getOriginalFilename();
        String name = processFile.getName();
        String extension = FilenameUtils.getExtension(fileName);

        InputStream inputStream = null;
        Deployment deployment = null;
        try {
            inputStream = processFile.getInputStream();
            //        如果文件是zip
            if ("zip".equals(extension)){
                ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                deployment = repositoryService.createDeployment()
                        .addZipInputStream(zipInputStream)
                        .name(deploymentName)
                        .deploy();
            }else{
                deployment = repositoryService.createDeployment()
                        .addInputStream(fileName,inputStream)
                        .name(deploymentName)
                        .deploy();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return R.success("部署失败：" + e.getMessage());
        }

        return R.success("部署成功，部署id：" + deployment.getId() + " 流程实例名称" + fileName);
    }

    /*在线绘制流程定义，并部署*/
    @PostMapping("/addDeploymentByString")
    public R<String> addDeploymentByString(@RequestParam String stringWithBPMN,@RequestParam String deploymentName){
        Deployment deployment = repositoryService.createDeployment()
                .addString("stringWithBPMNjs.bpmn", stringWithBPMN)
                .name(deploymentName)
                .deploy();

        return R.success("部署成功：" + deployment.getId());

    }

//    @PostMapping("addProcDef")

//    获取流程定义列表
    @GetMapping("/getDefinitions")
    public R<List<Map<String, Object>>> getDefinitions(){

        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().listPage(0,100);

        List<Map<String, Object>> collect = processDefinitions.stream().map(e -> {
            Map<String, Object> processDefinitionMap = new HashMap<>();
            processDefinitionMap.put("id", e.getId());
            processDefinitionMap.put("name", e.getName());
            processDefinitionMap.put("key", e.getKey());
            processDefinitionMap.put("resourcesName", e.getResourceName());
            processDefinitionMap.put("deploymentId", e.getDeploymentId());
            processDefinitionMap.put("version", e.getVersion());
            return processDefinitionMap;
        }).collect(Collectors.toList());

        return R.success(collect);
    }
//    获取流程定义的xml
    @GetMapping("/getDefinitionXMl")
    public R<Boolean> getDefinitionXMl(HttpServletResponse response, @RequestParam String deploymentId, @RequestParam String resourceName) throws IOException {
        InputStream in = repositoryService.getResourceAsStream(deploymentId, resourceName);

        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(resourceName, "utf-8"));

        byte[] bytes = new byte[2048];
        ServletOutputStream out = response.getOutputStream();

        int len = 0;
        while((len = in.read(bytes)) != -1){
            out.write(bytes,0,len);
        }
        out.flush();
        out.close();
        in.close();

        return R.success(true);
    }
//    获取流程部署列表
    @GetMapping("/getDeployments")
    public R<List<Map<String, Object>>> getDeployments() {

        List<Deployment> deployments = repositoryService.createDeploymentQuery().orderByDeploymenTime().desc().listPage(0, 100);

        List<Map<String, Object>> collect = deployments.stream().map(e -> {
            Map<String, Object> processDefinitionMap = new HashMap<>();
            processDefinitionMap.put("id", e.getId());
            processDefinitionMap.put("name", e.getName());
            processDefinitionMap.put("key", e.getKey());
            processDefinitionMap.put("deploymentTime", DateUtil.formatDateTime(e.getDeploymentTime()));
            processDefinitionMap.put("version", e.getVersion());
            return processDefinitionMap;
        }).collect(Collectors.toList());

        return R.success(collect);
    }


//    删除流程定义
    @PostMapping("/delDeployment")
    public R<Boolean> delDeployment(@RequestParam String deploymentId) {
        repositoryService.deleteDeployment(deploymentId);
        return R.success(true);
    }

}
