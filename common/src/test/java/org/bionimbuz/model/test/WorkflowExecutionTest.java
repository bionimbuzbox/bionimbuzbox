package org.bionimbuz.model.test;

import static org.junit.Assert.assertEquals;

import org.bionimbuzbox.model.Model;
import org.bionimbuzbox.model.Workflow;
import org.bionimbuzbox.model.WorkflowExecution;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowExecutionTest {
  
  private static final Logger log = LoggerFactory.getLogger(WorkflowExecutionTest.class);

  public WorkflowExecutionTest() {
    // TODO Auto-generated constructor stub
  }
  
  @Test
  public void testJSONSerializationDeserialization() {
    
    Workflow w = new Workflow("w1");
    WorkflowExecution we1 = new WorkflowExecution(w);
    
    String json1 = we1.toJSONString();
    log.info(json1);
    WorkflowExecution we2 = Model.buildFromJSONString(json1, WorkflowExecution.class);
    String json2 = we2.toJSONString();
    log.info(json2);
    assertEquals(json1, json2);
  }

  
}
