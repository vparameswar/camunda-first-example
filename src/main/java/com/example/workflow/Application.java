package com.example.workflow;

import org.camunda.bpm.engine.migration.MigrationPlan;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.camunda.bpm.spring.boot.starter.event.PreUndeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableProcessApplication("ProcessInstanceMigrationApplication")
public class Application {
@Autowired
  ApplicationContext applicationContext;
  public static void main(String... args) {
    SpringApplication.run(Application.class, args);
  }

  //@EventListener
  public void applicationStartedEvent(ApplicationStartingEvent applicationEvent) throws InterruptedException {
    System.out.println("Event Name -->"+applicationEvent.toString());
    Thread.sleep(10000000);
  }
  //@EventListener
  public void onPostDeploy(PostDeployEvent event) throws InterruptedException {

    ProcessDefinition latest = event.getProcessEngine().getRepositoryService().createProcessDefinitionQuery()
                    .latestVersion().singleResult();
    if(latest.getVersion()<=1){
      System.out.println("Only one version is available. Skipping the migration!!!");
      return;
    }
    ProcessDefinition previous = event.getProcessEngine().getRepositoryService().createProcessDefinitionQuery()
            .processDefinitionVersion(latest.getVersion()-1).singleResult();
    MigrationPlan migrationPlan = event.getProcessEngine().getRuntimeService()
            .createMigrationPlan(previous.getId(), latest.getId())
            .mapEqualActivities()
            .build();

    ProcessInstanceQuery processInstanceQuery = event.getProcessEngine().getRuntimeService()
            .createProcessInstanceQuery()
            .processDefinitionId(migrationPlan.getSourceProcessDefinitionId());
    List<String> processInstanceIds = processInstanceQuery.list().stream().map(p->p.getProcessInstanceId()).collect(Collectors.toList());
    Thread.sleep(100000);
    System.out.println("done!");
    if(CollectionUtils.isEmpty(processInstanceIds)){
      System.out.println("Process Instance Ids to migrate: "+processInstanceIds.size());
      return;
    }
    event.getProcessEngine().getRuntimeService().newMigration(migrationPlan)
            .processInstanceIds(processInstanceIds)
            .skipCustomListeners()
            .skipIoMappings()
            .execute();
System.out.println("Total Process Instances Migrated: "+processInstanceIds.size());


  }

  //@EventListener
  public void onPreUndeployEvent(PreUndeployEvent event) {
    event.getProcessEngine().getRepositoryService().suspendProcessDefinitionByKey("main",true, new Date());
    System.out.println("suspended....");
  }

}