package org.bionimbuzbox.scheduling.runner;

import java.util.Iterator;
import org.bionimbuzbox.model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DependenciesRunner implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger( DependenciesRunner.class);
	private Job job;
	
	public DependenciesRunner(Job job) {
		LOGGER.info("DependenciesRunner started for: {}", job.getId());
		this.job = job;
	}
	

	public void run() {		
	  LOGGER.info("Job {} has {} dependencies.", job.getId(), this.job.getDependencies().size());
	  
		while (this.job.getDependencies().size() > 0) {
		  
		  try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        LOGGER.error(e.getMessage(), e);
      }
		  
			for (Iterator<Job> iterator = this.job.getDependencies().iterator(); iterator.hasNext();) {
				Job dependency = iterator.next();
				/*
				LOGGER.info("Status of dependency {} for Job {} is {}.", 
				    dependency.getId(), 
				    this.job.getId(),
				    dependency.getStatus().toString() );
				*/
				if (dependency.isReady()) {
					iterator.remove();
					LOGGER.info("Dependency {} for Job {} removed.", 
	            dependency.getId(), 
	            this.job.getId(),
	            dependency.getStatus().toString() );
				}
			}
		
		}
		LOGGER.info("DependenciesRunner finished for: {}", job.getId());
	}
}
