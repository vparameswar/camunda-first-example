package com.example.workflow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class TaskDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        int count = 1;
        while(count < 1000){
            Thread.sleep(1000);
            System.out.println("Counter: "+count);
            count++;
        }
        System.out.println("Task Delegate Completed!!!");
    }
}
