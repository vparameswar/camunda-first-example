package com.example.workflow;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendMessageDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        log.info("Sending Message: rollback to processInstanceId: "+delegateExecution.getProcessInstanceId());
        delegateExecution.getProcessEngineServices().getRuntimeService().createMessageCorrelation("rollback").processInstanceId(delegateExecution.getProcessInstanceId()).correlate();
        log.info("Sending Message Completed!!");
    }
}
