package org.bionimbuzbox.scheduling.scheduler;

import java.util.List;
import java.util.Random;
import org.bionimbuzbox.model.Job;
import org.bionimbuzbox.model.Resource;
import org.bionimbuzbox.orchestration.driver.OrchestrationDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomScheduler implements Scheduler {

  private static final Logger log = LoggerFactory.getLogger(AllTogetherScheduler.class);
  private final Random random = new Random();
  private final List<OrchestrationDriver> drivers;
  
  public RandomScheduler(List<OrchestrationDriver> drivers) {
    this.drivers = drivers;
  }
  
  private synchronized int randomInt(int bound) {
    return this.random.nextInt(bound);
  }
  
  protected OrchestrationDriver randomChoice(List<OrchestrationDriver> drivers) {
    OrchestrationDriver driver = drivers.get(this.randomInt(drivers.size()));
    log.info("Driver randomly choose: {} ({})", driver.cluster().getName(), driver.cluster().getId());
    return drivers.get(this.randomInt(drivers.size()));
  }

  @Override
  public OrchestrationDriver choose(Job job) {
    return randomChoice(drivers);
  }

  @Override
  public OrchestrationDriver choose(Resource resource) {
    return randomChoice(drivers);
  }
}
