package com.hd123.jyzh.flowable.quickstart;

import java.util.HashMap;
import java.util.List;

import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

/**
 * 命令行流程引擎（TODO 执行报错, 后续再看）
 * <p>
 * 核心类: {@link org.flowable.engine.ProcessEngineConfiguration} 配置与调整流程引擎的设置
 * {@link org.flowable.engine.ProcessEngine} 流程引擎
 * <p>
 * 流程定义 -> 流程部署 -> 流程启动
 *
 * @author ZhengYu
 * @version 1.0 2021/8/17 14:26
 **/
public class HolidayRequest {

  private static final ProcessEngine processEngine;

  private static final RepositoryService repositoryService;

  private static final RuntimeService runtimeService;

  private static final TaskService taskService;

  private static final HistoryService historyService;

  static {
    // 流程引擎的设置
    ProcessEngineConfiguration processEngineConfiguration = new StandaloneProcessEngineConfiguration()
        .setJdbcUrl("jdbc:mysql://127.0.0.1:3306/flowable?useSSL=false").setJdbcUsername("root")
        .setJdbcPassword("123456")
        // .setJdbcDriver("com.mysql.cj.jdbc.Driver")
        .setJdbcDriver("com.mysql.jdbc.Driver")
        // ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE 表结构不存在时，会创建相应的表结构
        .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

    // 流程引擎
    processEngine = processEngineConfiguration.buildProcessEngine();
    repositoryService = processEngine.getRepositoryService();
    runtimeService = processEngine.getRuntimeService();
    taskService = processEngine.getTaskService();
    historyService = processEngine.getHistoryService();
  }

  public static void main(String[] args) {
    // 流程定义相关API
    processDeploy();

    // 流程实例相关API
    processInstance();

    // 历史相关API
    processHistory();
  }

  /**
   * 操作： 1. 流程定义 - CUD TODO U 2. 查询定义 - Q TODO 按条件查 3. 运行时动态修改流程定义
   *
   * @author ZhengYu
   */
  private static void processDeploy() {
    // 根据 xml 文件添加流程定义
    Deployment deploy = repositoryService.createDeployment()
        .addClasspathResource("holiday-request.bpmn20.xml").deploy();
    // 添加一个不使用的流程定义（下面会删除）
    repositoryService.createDeployment().addClasspathResource("holiday-request.bpmn20.xml")
        .deploy();

    // 查询流程定义
    List<Deployment> deployments = repositoryService.createDeploymentQuery().list();
    deployments.forEach(deployment -> {
      System.out.printf("流程定义ID: %s, 流程定义名称: %s\n", deployment.getId(), deployment.getName());
      if (deployment.getId().equals(deploy.getId())) {
        return;
      }
      // 删除流程
      repositoryService.deleteDeployment(deployment.getId(), false);
    });
  }

  /**
   * 概念： 1. key? 2. deploymentId 3. TenantId 4. Message 5. Form 操作： 1.
   * 流程实例启动、审批、拒绝、指派、回退 2. 流程实例查询 3. 查询待处理流程列表（所有人/单人） 4. 查询已处理流程列表（所有人/单人）
   *
   * @author ZhengYu
   */
  private static void processInstance() {
    // 启动流程实例
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("holidayRequest",
        new HashMap<String, Object>() {
          {
            put("employee", "zy");
            put("nrOfHolidays", "1");
            put("description", "take a rest");
          }
        });

    // 获取 managers 的任务列表
    List<Task> taskList = taskService.createTaskQuery().taskCandidateGroup("managers").list();
    taskList.forEach(task -> {
      System.out.printf("managers节点：TaskId: %s, TaskName: %s\n", task.getId(), task.getName());

      // 完成当前节点，同时通过审批
      taskService.complete(task.getId(), new HashMap<String, Object>() {
        {
          put("approved", true);
        }
      });
    });

    // 审核通过 -> 回到提交人的节点 -> 提交人确认 -> 结束流程
    taskList = taskService.createTaskQuery().taskAssignee("zy").list();
    taskList.forEach(task -> {
      System.out.printf("提交人节点：TaskId: %s, TaskName: %s\n", task.getId(), task.getName());

      // 完成当前节点，同时通过审批
      taskService.complete(task.getId(), new HashMap<String, Object>() {
        {
          put("approved", true);
        }
      });
    });
  }

  private static void processHistory() {
    // 查询所有已完成的活动
    List<HistoricActivityInstance> historicActivityInstanceList = historyService
        .createHistoricActivityInstanceQuery().finished().orderByHistoricActivityInstanceEndTime()
        .asc().list();
    historicActivityInstanceList
        .forEach(historicActivityInstance -> System.out.printf("节点： %s, 耗时： %d\n",
            historicActivityInstance.getActivityId(),
            historicActivityInstance.getDurationInMillis()));
  }
}