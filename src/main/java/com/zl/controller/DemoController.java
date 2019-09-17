package com.zl.controller;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName DemoController
 * @Description TODO
 * @Date 2019/9/17 16:59
 * @Author albertzh
 **/
@Controller
public class DemoController {

    Logger logger = LoggerFactory.getLogger(DemoController.class);


    /**
     * 流程定义和部署相关的存储服务
     */
    @Autowired
    private RepositoryService repositoryService;

    /**
     * 流程运行时相关的服务
     */
    @Autowired
    private RuntimeService runtimeService;

    /**
     * 节点任务相关操作接口
     */
    @Autowired
    private TaskService taskService;

    /**
     * 流程图生成器
     */
    @Autowired
    private ProcessDiagramGenerator processDiagramGenerator;

    /**
     * 历史记录相关服务接口
     */
    @Autowired
    private HistoryService historyService;

    //跳转到上级审核页面
    @RequestMapping(value = "/toLeave")
    public String employeeLeave() {
        return "/employeeLeave";
    }


    //流程启动
    @RequestMapping(value = "/start")
    public String start() {
        // xml中定义的ID
        String instanceKey = "myProcess_1";
        logger.info("开启请假流程...");

        // 设置流程参数，开启流程
        Map<String, Object> map = new HashMap<>();
        map.put("jobNumber", "A1001");
        map.put("busData", "bus data");
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(instanceKey, map);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动

        logger.info("启动流程实例成功:{}", instance);
        logger.info("流程实例ID:{}", instance.getId());
        logger.info("流程定义ID:{}", instance.getProcessDefinitionId());


        //验证是否启动成功
        //通过查询正在运行的流程实例来判断
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        //根据流程实例ID来查询
        List<ProcessInstance> runningList = processInstanceQuery.processInstanceId(instance.getProcessInstanceId()).list();
        logger.info("根据流程ID查询条数:{}", runningList.size());

        // 返回流程ID
        return instance.getId();
    }


    /**
     * <p>员工提交申请</p>
     *
     * @param request 请求
     * @return String 申请受理结果
     * @author FRH
     * @time 2018年12月10日上午11:15:09
     * @version 1.0
     */
    @RequestMapping(value = "/employeeApply")
    @ResponseBody
    public String employeeApply(HttpServletRequest request) {
        /*
         * 获取请求参数
         */
        String taskId = request.getParameter("taskId"); // 任务ID
        String jobNumber = request.getParameter("jobNumber"); // 工号
        String leaveDays = request.getParameter("leaveDays"); // 请假天数
        String leaveReason = request.getParameter("leaveReason"); // 请假原因


        /*
         *  查询任务
         */
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            logger.info("任务ID:{}查询到任务为空！", taskId);
            return "fail";
        }


        /*
         * 参数传递并提交申请
         */
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("days", leaveDays);
        map.put("date", new Date());
        map.put("reason", leaveReason);
        map.put("jobNumber", jobNumber);
        taskService.complete(task.getId(), map);
        logger.info("执行【员工申请】环节，流程推动到【上级审核】环节");

        return "success";
    }


    /**
     * <p>查看任务</p>
     *
     * @param request 请求
     * @return String  任务展示页面
     * @author FRH
     * @time 2018年12月10日上午11:21:33
     * @version 1.0
     */
    @RequestMapping(value = "/toShowTask")
    public Object toShowTask(HttpServletRequest request) {
        /*
         * 获取请求参数
         */
        List<Task> taskList = taskService.createTaskQuery().list();
        if (taskList == null || taskList.size() == 0) {
            logger.info("查询任务列表为空！");
            return "/task";
        }
        /*
         * 查询所有任务，并封装
         */
        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        for (Task task : taskList) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("taskId", task.getId());
            map.put("name", task.getName());
            map.put("createTime", task.getCreateTime().toString());
            map.put("assignee", task.getAssignee());
            map.put("instanceId", task.getProcessInstanceId());
            map.put("executionId", task.getExecutionId());
            map.put("definitionId", task.getProcessDefinitionId());
            resultList.add(map);
        }
        return resultList;
    }


}
