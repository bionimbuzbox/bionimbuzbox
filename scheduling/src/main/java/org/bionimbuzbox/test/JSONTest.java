package org.bionimbuzbox.test;

import org.bionimbuzbox.model.Job;
import org.bionimbuzbox.model.Model;
import org.bionimbuzbox.model.Resource;
import org.bionimbuzbox.model.Workflow;

public class JSONTest {

  public JSONTest() {
    // TODO Auto-generated constructor stub
  }
  
  public static void test1() {
    Workflow w = new Workflow("XYZ");
    
    Resource r1 = new Resource("Resource-1");
    r1.setWorkflow(w);
    w.addResource(r1);
    
    Resource r2 = new Resource("Resource-2");
    r2.setWorkflow(w);
    w.addResource(r2);
    
    Job j1 = new Job("Job-1");
    j1.setWorkflow(w);
    w.addJob(j1);
    
    Job j2 = new Job("Job-2");
    j2.setWorkflow(w);
    j2.addDependency(j1);
    w.addJob(j2);
    
    Job j3 = new Job("Job-3");
    j3.setWorkflow(w);
    j3.addDependency(j2);
    w.addJob(j3);
    
    Job j4 = new Job("Job-4");
    j4.setWorkflow(w);
    j4.addDependency(j2);
    w.addJob(j4);
    
    Job j5 = new Job("Job-5");
    j5.setWorkflow(w);
    j5.addDependency(j3);
    w.addJob(j5);
    
    Job j6 = new Job("Job-6");
    j6.setWorkflow(w);
    j6.addDependency(j5);
    j6.addDependency(j4);
    w.addJob(j6);
    
    String json = w.toJSONString();
    System.out.println(json);
    
    Workflow w1 = Model.buildFromJSONString(json, Workflow.class);
    String json1 = w1.toJSONString();
    System.out.println(json1);
  }
  
  public static void test2() {
 Workflow w = new Workflow("123");
    
    Job j1 = new Job("j1");
    Job j2 = new Job("j2");
    Job j3 = new Job("j3");
    Job j4 = new Job("j4");
    
    j1.setWorkflow(w);
    j2.setWorkflow(w);
    j3.setWorkflow(w);
    j4.setWorkflow(w);
    
    w.addJob(j1);
    w.addJob(j2);
    w.addJob(j3);
    w.addJob(j4);
    
    Resource r1 = new Resource("r1");
    Resource r2 = new Resource("r2");
    Resource r3 = new Resource("r3");
    Resource r4 = new Resource("r4");
    
    r1.setWorkflow(w);
    r2.setWorkflow(w);
    r3.setWorkflow(w);
    r4.setWorkflow(w);
    
    w.addResource(r1);
    w.addResource(r2);
    w.addResource(r3);
    w.addResource(r4);
    
    String json = w.toJSONString();
    System.out.println(json);
    
    Workflow w1 = Model.buildFromJSONString(json, Workflow.class);
    String json1 = w1.toJSONString();
    System.out.println(json1);
  }
  
  public static void test3() {
    Workflow w = new Workflow("XYZ");
    
    Resource r1 = new Resource("Resource-1");
    r1.setWorkflow(w);
    w.addResource(r1);
    
    Resource r2 = new Resource("Resource-2");
    r2.setWorkflow(w);
    w.addResource(r2);
    
    Job j1 = new Job("Job-1");
    j1.setWorkflow(w);
    w.addJob(j1);
    
    Job j2 = new Job("Job-2");
    j2.setWorkflow(w);
    j2.addDependency(j1);
    w.addJob(j2);
    
    Job j3 = new Job("Job-3");
    j3.setWorkflow(w);
    j3.addDependency(j2);
    w.addJob(j3);
    
    Job j4 = new Job("Job-4");
    j4.setWorkflow(w);
    j4.addDependency(j2);
    w.addJob(j4);
    
    Job j5 = new Job("Job-5");
    j5.setWorkflow(w);
    j5.addDependency(j3);
    w.addJob(j5);
    
    Job j6 = new Job("Job-6");
    j6.setWorkflow(w);
    j6.addDependency(j5);
    j6.addDependency(j4);
    w.addJob(j6);
    
    String json = j6.toJSONString();
    System.out.println(json);
    
    Job job = Model.buildFromJSONString(json, Job.class);
    String json1 = job.toJSONString();
    System.out.println(json1);
  }

  public static void main(String[] args) {
   test3();

  }

}
