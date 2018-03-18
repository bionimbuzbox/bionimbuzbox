package org.bionimbuzbox.scheduling.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.bionimbuzbox.dao.DAOFactory;
import org.bionimbuzbox.dao.WorkflowDAO;
import org.bionimbuzbox.model.Cluster;
import org.bionimbuzbox.model.Job;
import org.bionimbuzbox.model.Workflow;
import org.bionimbuzbox.orchestration.driver.OrchestrationDriver;
import org.bionimbuzbox.scheduling.SchedulingService;
import org.bionimbuzbox.scheduling.runner.WorkflowRunner;
import org.bionimbuzbox.scheduling.scheduler.AllTogetherScheduler;
import org.bionimbuzbox.scheduling.scheduler.Scheduler;

@Path("/")
public class SchedulingAPI {
	
	@Inject
	private DAOFactory daoFactory;
	
	@Inject
	private SchedulingService schedulingService;
	
	private static Logger log = Logger.getLogger(SchedulingAPI.class.getName());

	@GET
	@Path("workflows")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Workflow> listAll() {
		log.info("Getting all Workflows ...");
		final List<Workflow> workflows = new ArrayList<>();
		try {
		  workflows.addAll(daoFactory.getWorkflowDAO().getAll());
		} catch (Exception e) {
		  log.severe(e.getMessage());
		  e.printStackTrace();
    }
		log.info("Workflows: " + workflows);
		return workflows;
	}
	
	@GET
	@Path("workflow/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Workflow get(@PathParam("id") String id) {
		return daoFactory.getWorkflowDAO().get(id);
	}
	
	@POST
  @Path("workflow")
  @Produces(MediaType.APPLICATION_JSON)
  public Workflow save(Workflow workflow) {
	  daoFactory.getWorkflowDAO().save(workflow);
	  return workflow;
  }
	
	@POST
  @Path("workflow/{id}/run")
  @Produces(MediaType.APPLICATION_JSON)
  public void run(@NotNull @PathParam("id") String id) {
    Workflow workflow = daoFactory.getWorkflowDAO().get(id);
    correctJobDependenciesReferences(workflow);
    
    List<Cluster> clusters = daoFactory.getClusterDAO().getAll(true); // TODO: API CALL
	  schedulingService.execute(workflow, clusters);
  }
	
	private Workflow correctJobDependenciesReferences(Workflow workflow) {
	  for (Iterator<Job> jobsIterator = workflow.getJobs().iterator(); jobsIterator.hasNext();) {
	    Job job = jobsIterator.next();
	    for (Iterator<Job> dependenciesIterator = job.getDependencies().iterator(); dependenciesIterator.hasNext();) {
	      Job dependency = dependenciesIterator.next();
	      job.addDependency(workflow.getJob(dependency.getId()));
      }
	  }
	  
	  return null;
	}
}
