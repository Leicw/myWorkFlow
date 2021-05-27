# Activiti7整合BPMN-JS说明

### 安装Node.js
* 下载地址https://nodejs.org/en/download/

### BPMN-JS地址
* 下载源码https://github.com/bpmn-io/bpmn-js-examples/
 ```
基于有属性面板的例子去修改
https://github.com/bpmn-io/bpmn-js-examples/tree/master/properties-panel 属性面板
https://github.com/bpmn-io/bpmn-js-examples/tree/master/i18n 汉化说明
 ```

### 在resources文件夹下再创建一个resources文件夹，
 ```
实际路径为resources/resources/
 ```

### 把从github下载的bpmn-js-examples-master.zip压缩包解压
### 拷贝properties-panel到resources/resources/并改文件夹名字为bpmnjs
 ```
实际路径为resources/resources/bpmnjs
 ```

### 解压慕课网git仓库根目录下bpmnjs汉化包，并资源拷贝到以下路径
 ```
https://git.imooc.com/coding-454/activiti7_workflow
bpmnjs初始化.zip
resources/resources/bpmnjs/resources
 ```

### 打开resources/resources/bpmnjs/app/index.js
### 注释以下内容
 ```
import propertiesProviderModule from 'bpmn-js-properties-panel/lib/provider/camunda';
import camundaModdleDescriptor from 'camunda-bpmn-moddle/resources/camunda.json';
 ```

### 注释以下内容
 ```
var bpmnModeler = new BpmnModeler({
  container: canvas,
  propertiesPanel: {
    parent: '#js-properties-panel'
  },
  additionalModules: [
    propertiesPanelModule,
    propertiesProviderModule
  ],
  moddleExtensions: {
    camunda: camundaModdleDescriptor
  }
});
 ```

### 添加以下内容
 ```
import propertiesProviderModule from '../resources/properties-panel/provider/activiti';
import activitiModdleDescriptor from '../resources/activiti.json';
import customTranslate from '../resources/customTranslate/customTranslate';
import customControlsModule from '../resources/customControls';

// 添加翻译组件
var customTranslateModule = {
    translate: ['value', customTranslate]
};


var bpmnModeler = new BpmnModeler({
    container: canvas,
    propertiesPanel: {
        parent: '#js-properties-panel'
    },
    additionalModules: [
        propertiesPanelModule,
        propertiesProviderModule,
        customControlsModule,
        customTranslateModule
    ],
    moddleExtensions: {
        activiti:activitiModdleDescriptor
    }
});

 ```

### 使用命令行工具打开resources/resources/bpmnjs/并执行命令
 ```
npm install

npm run dev
 ```

### 增加BPMNJS可执行选框默认勾选，打开resources/newDiagram.bpmn
### 属性修改为isExecutable="true
 ```
<bpmn2:process id="Process_1" isExecutable="true">
 ```


