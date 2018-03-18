package org.bionimbuzbox.scheduling.runner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.bionimbuzbox.model.Job;
import org.bionimbuzbox.model.Resource;
import org.bionimbuzbox.model.Workflow;
import org.bionimbuzbox.scheduling.scheduler.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowRunner implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger( WorkflowRunner.class );
	
	private final Workflow workflow;
	private final Scheduler scheduler;
	
	private int DEPENDENCIES_THREAD_POOL_SIZE = 5;
	private int JOBS_THREAD_POOL_SIZE = 5;
	private int RESOURCES_THREAD_POOL_SIZE = 5;
	
	private ConcurrentMap<String, Job> waitingJobs = new ConcurrentHashMap<>(); 
	private ConcurrentMap<String, Job> runningJobs = new ConcurrentHashMap<>(); 
	private ConcurrentMap<String, Job> finishedJobs = new ConcurrentHashMap<>();
	
	private ConcurrentMap<String, Resource> pendingResources = new ConcurrentHashMap<>();
	private ConcurrentMap<String, Resource> readyResources = new ConcurrentHashMap<>();
	
	public WorkflowRunner(Workflow w, Scheduler scheduler) {
		this.workflow = w;
		this.scheduler = scheduler;
	}
	
	@Override
	public void run() {
	  LocalDateTime initialDateTime = LocalDateTime.now();
	  
		ExecutorService resourcesPool = Executors.newFixedThreadPool(RESOURCES_THREAD_POOL_SIZE);
		ExecutorService jobsPool = Executors.newFixedThreadPool(JOBS_THREAD_POOL_SIZE);
		ExecutorService dependenciesPool = Executors.newFixedThreadPool(DEPENDENCIES_THREAD_POOL_SIZE);//creating a pool of 5 threads 
        
		LOGGER.info("Resources size:" + this.workflow.getResources().size());
		
		// Initiating each Resource, adding to Pending (ResourceRunner) Pools
		for (Iterator<Resource> iterator = this.workflow.getResources().iterator(); iterator.hasNext();) {
			Resource resource = iterator.next();
			resourcesPool.execute(new ResourceRunner(resource, scheduler.choose(resource)));
			this.pendingResources.put(resource.getId(), resource);
		}
		
		LOGGER.info("Pending Resources size: {}", this.pendingResources.size());
		LOGGER.info("Ready Resources size: {}", this.readyResources.size());
		
		// While pending resources ...
		while (this.workflow.getResources().size() != this.readyResources.size()) {
			
			// PENDING RESOURCES
			for (Iterator<Resource> iterator = this.pendingResources.values().iterator(); iterator.hasNext();) {
				Resource resource = iterator.next();
				if (resource.isReady()) {
					this.pendingResources.remove(resource.getId());
					this.readyResources.put(resource.getId(), resource);
				}
			}
		}
		
		LOGGER.info("Jobs size: {}", this.workflow.getJobs().size());
       	
		// Initiating each Jobs, adding to Running (JobRunner) or Waiting (DependenciesRunner) Pools
    for (Iterator<Job> iterator = this.workflow.getJobs().iterator(); iterator.hasNext();) {
			Job job = iterator.next();
			
			if (!job.hasDependencies()) {
				jobsPool.execute(new JobRunner(job, scheduler.choose(job)));
				this.runningJobs.put(job.getId(), job);
			} else {
				dependenciesPool.execute(new DependenciesRunner(job));
				this.waitingJobs.put(job.getId(), job);
			}
		}
        
    LOGGER.info("Running Jobs size: {}", this.runningJobs.size());
		LOGGER.info("Waitinig Jobs size: {}", this.waitingJobs.size());
		LOGGER.info("Finished Jobs size: {}", this.finishedJobs.size());
        
        // While unfinished jobs ...
		while (this.workflow.getJobs().size() != this.finishedJobs.size()) {
			
			// WAITING JOBS
			for (Iterator<Job> iterator = this.waitingJobs.values().iterator(); iterator.hasNext();) {
				Job job = iterator.next();
				
				if (!job.hasDependencies()) {
					this.waitingJobs.remove(job.getId());
					jobsPool.execute(new JobRunner(job, scheduler.choose(job)));
					this.runningJobs.put(job.getId(), job);
				} 
			}
			
			// RUNNING JOBS
			for (Iterator<Job> iterator = this.runningJobs.values().iterator(); iterator.hasNext();) {
				Job job = iterator.next();
				
				if (job.isFinished()) {
					this.runningJobs.remove(job.getId());
					this.finishedJobs.put(job.getId(), job);
				} 
			}
		}
		
		LOGGER.info("Duration of Workflow {}: {} seconds", this.workflow.getId(), Duration.between(initialDateTime, LocalDateTime.now()).getSeconds());
		
		dependenciesPool.shutdown();
		jobsPool.shutdown();
		resourcesPool.shutdown();
	}

}
