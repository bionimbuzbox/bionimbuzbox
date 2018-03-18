package org.bionimbuzbox.scheduling.scheduler;

import org.bionimbuzbox.model.Job;
import org.bionimbuzbox.model.Resource;
import org.bionimbuzbox.orchestration.driver.OrchestrationDriver;

public interface Scheduler {
  public OrchestrationDriver choose(Job job);
  public OrchestrationDriver choose(Resource resource);
}
