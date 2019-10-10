package com.zl;

import com.google.common.collect.Maps;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName: DemoMain
 * @Description: 启动类
 * @Author: zl
 * @Date: 2019/10/9 22:27
 * @Version: 1.0
 **/
public class DemoMain {
    private static final Logger logger = LoggerFactory.getLogger(DemoMain.class);

    public static void main(String[] args) throws ParseException {
        logger.info("启动我们的程序");
        //创建流程引擎
        ProcessEngine processEngine = getProcessEngine();
        //部署流程定义文件
        ProcessDefinition processDefinition = getProcessDefinition(processEngine);
        //启动运行流程
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        logger.info("启动流程{}", processInstance.getProcessDefinitionKey());
        //处理流程任务
        Scanner scanner = new Scanner(System.in);
        while (processInstance != null && !processInstance.isEnded()) {
            TaskService taskService = processEngine.getTaskService();
            List<Task> list = taskService.createTaskQuery().list();
            logger.info("待处理任务数量:【{}】", list.size());
            for (Task task : list) {
                logger.info("待处理任务：【{}】", task.getName());
                FormService formService = processEngine.getFormService();
                TaskFormData taskFormData = formService.getTaskFormData(task.getId());
                List<FormProperty> formProperties = taskFormData.getFormProperties();
                Map<String, Object> variables = Maps.newHashMap();
                for (FormProperty formProperty : formProperties) {
                    String s = null;
                    if (StringFormType.class.isInstance(formProperty.getType())) {
                        logger.info("请输入{}?", formProperty.getName());
                        s = scanner.nextLine();
                        variables.put(formProperty.getId(), s);
                    } else if (DateFormType.class.isInstance(formProperty.getType())) {
                        logger.info("请输入{},格式(yyyy-MM-dd)", formProperty.getName());
                        s = scanner.nextLine();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date parse = simpleDateFormat.parse(s);
                        variables.put(formProperty.getId(), parse);
                    } else {
                        logger.info("类型暂不支持{}", formProperty.getType());
                    }
                    logger.info("您输入的内容是[{}]", s);
                }
                taskService.complete(task.getId(), variables);
                processInstance = processEngine.getRuntimeService().createProcessInstanceQuery().processDefinitionId(processDefinition.getId()).singleResult();
            }


        }
        logger.info("结束我们的程序");
    }

    private static ProcessDefinition getProcessDefinition(ProcessEngine processEngine) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder deployment = repositoryService.createDeployment();
        deployment.addClasspathResource("second_approve.bpmn20.xml");
        Deployment deploy = deployment.deploy();
        String id = deploy.getId();
        String name1 = deploy.getName();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(id).singleResult();
        logger.info("流程定义文件{},流程id:{}", processDefinition.getName(), processDefinition.getId());
        return processDefinition;
    }

    private static ProcessEngine getProcessEngine() {
        ProcessEngineConfiguration cfg = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
        ProcessEngine processEngine = cfg.buildProcessEngine();
        String name = processEngine.getName();
        String version = processEngine.VERSION;
        logger.info("流程引擎名称{}，版本信息{}", name, version);
        return processEngine;
    }
}
