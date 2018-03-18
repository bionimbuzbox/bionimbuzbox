package org.bionimbuzbox.test;

import java.util.Properties;
import org.bionimbuzbox.dao.DAODriver;
import org.bionimbuzbox.dao.DAOFactory;
import org.bionimbuzbox.dao.JobDAO;
import org.bionimbuzbox.dao.ResourceDAO;
import org.bionimbuzbox.dao.WorkflowDAO;
import org.bionimbuzbox.dao.elasticsearch.ElasticSearchDriver;
import org.bionimbuzbox.helper.PropertiesHelper;
import org.bionimbuzbox.model.Job;
import org.bionimbuzbox.model.Resource;
import org.bionimbuzbox.model.Workflow;

public class SchedulingDAOFactoryTest {

  private DAOFactory factory;
  
  public SchedulingDAOFactoryTest() {
    // TODO Auto-generated constructor stub
  }
  
  public void initialize() throws Exception {
    Properties config = new Properties();
    config.load(DAOFactory.class.getResourceAsStream("/config.properties"));
    Properties driverConfig = PropertiesHelper.matchingSubset(config, "scheduling.dao.driver.elasticsearch.", false);
    
    DAODriver driver = new ElasticSearchDriver(driverConfig);
    driver.initialize();
    
    factory = DAOFactory.create(driver);
  }
  
  public void test() {
    WorkflowDAO workflowDAO = factory.getWorkflowDAO();
    ResourceDAO resourceDAO = factory.getResourceDAO();
    JobDAO jobDAO = factory.getJobDAO();
    
    Workflow w = new Workflow("XYZ");
    
    Resource r1 = new Resource("Resource-1");
    r1.setWorkflow(w);
    resourceDAO.save(r1);
    w.addResource(r1);
    
    Resource r2 = new Resource("Resource-2");
    r2.setWorkflow(w);
    resourceDAO.save(r2);
    w.addResource(r2);
    
    Job j1 = new Job("Job-1");
    j1.setWorkflow(w);
    jobDAO.save(j1);
    w.addJob(j1);
    
    Job j2 = new Job("Job-2");
    j2.setWorkflow(w);
    j2.addDependency(j1);
    jobDAO.save(j2);
    w.addJob(j2);
    
    Job j3 = new Job("Job-3");
    j3.setWorkflow(w);
    j3.addDependency(j2);
    jobDAO.save(j3);
    w.addJob(j3);
    
    Job j4 = new Job("Job-4");
    j4.setWorkflow(w);
    j4.addDependency(j2);
    jobDAO.save(j4);
    w.addJob(j4);
    
    Job j5 = new Job("Job-5");
    j5.setWorkflow(w);
    j5.addDependency(j3);
    jobDAO.save(j5);
    w.addJob(j5);
    
    Job j6 = new Job("Job-6");
    j6.setWorkflow(w);
    j6.addDependency(j5);
    j6.addDependency(j4);
    jobDAO.save(j6);
    w.addJob(j6);
    
    workflowDAO.save(w);
  }

  public static void main(String[] args) throws Exception {
    SchedulingDAOFactoryTest test = new SchedulingDAOFactoryTest();
    test.initialize();
    test.test();
  }

}
