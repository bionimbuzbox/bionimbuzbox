package org.bionimbuzbox.scheduling.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bionimbuzbox.model.Job;
import org.bionimbuzbox.model.Resource;
import org.bionimbuzbox.model.Workflow;
import org.bionimbuzbox.orchestration.driver.OrchestrationDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllTogetherScheduler extends RandomScheduler implements Scheduler {
  
  private static final Logger log = LoggerFactory.getLogger(AllTogetherScheduler.class);
  private static final Map<String, OrchestrationDriver> affinity = new HashMap<>();
  private final List<OrchestrationDriver> drivers;

  public AllTogetherScheduler(List<OrchestrationDriver> drivers) {
    super(drivers);
    this.drivers = drivers;
  }
  
  protected OrchestrationDriver sameChoice(Workflow workflow) {
    OrchestrationDriver driver;
    String key = workflow.getId();
    
    if (affinity.containsKey(key)) {
      driver = affinity.get(key);
      log.info("Getting the previous choice [{}] for the workflow {} ...", 
          driver.cluster().getId(), workflow.getId());
      
      for (OrchestrationDriver d : drivers) {
        if (d.cluster().getId().equals(driver.cluster().getId())){
          log.info("The previous choice [{}] for the workflow {} has been picked.",
              driver.cluster().getId(), workflow.getId());
          return driver;
        }
      }
      log.warn("Could not pick the previous choice [{}] for the workflow {}. Choosing a random one ...",
          driver.cluster().getId(),
          workflow.getId());
    } else {
      log.info("First placement for workflow {}.", workflow.getId());
    }
    
    driver = randomChoice(drivers);
    affinity.put(key, driver);
    
    log.info("Cluster {} has been randomically choose for the workflow {}.", 
        workflow.getId(), driver.cluster().getId(), workflow.getId());
    
    return driver;
  }
  

  @Override
  public OrchestrationDriver choose(Job job) {
    return sameChoice(job.getWorkflow());
  }

  @Override
  public OrchestrationDriver choose(Resource resource) {
    return sameChoice(resource.getWorkflow());
  }
}
