package org.bionimbuzbox.scheduling.runner;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bionimbuzbox.model.Resource;
import org.bionimbuzbox.orchestration.driver.OrchestrationDriver;

public class ResourceRunner implements Runnable {
	private static final Logger LOGGER = Logger.getLogger( ResourceRunner.class.getName() );

	private Resource resource;
	private OrchestrationDriver driver;
	
	public ResourceRunner(Resource resource, OrchestrationDriver driver) {
		LOGGER.info("Resource started for: " + resource.getId());
		this.resource = resource;
		this.driver = driver;
	}
	
	@Override
	public void run() {
	  resource.setStatus(Resource.Status.PENDING);
	  driver.start(resource);
	  
	  while(!resource.isReady()) {
	    resource.setStatus(driver.status(resource));
	    try {
	      Thread.sleep(1000);
	    } catch (InterruptedException e) {
	      LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
	    }
	  }
	  
	  LOGGER.info("Resource finished for: " + resource.getId());
	}

}
