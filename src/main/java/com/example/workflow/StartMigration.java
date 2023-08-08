package com.example.workflow;

import com.example.workflow.processmigration.UpgradeMainFromV1ToV2;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StartMigration {

  @Autowired
  private UpgradeMainFromV1ToV2 upgradeMainFromV1ToV2;

  RuntimeService runtimeService;

  @GetMapping("/startMigration")
  public String start() {
    upgradeMainFromV1ToV2.run();
    return "started";
  }
}
