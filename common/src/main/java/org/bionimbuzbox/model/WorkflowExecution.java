package org.bionimbuzbox.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WorkflowExecution extends GenericModel {

  private static final long serialVersionUID = 3569786841679753446L;

  private Workflow workflow;
  
  
  public WorkflowExecution(Workflow workflow) {
    this.workflow = workflow;
  }
  
  @JsonCreator
  private WorkflowExecution(@JsonProperty("workflow") String id) {
    this.workflow = new Workflow(id);
  }
  
  public Workflow getWorkflow() {
    return workflow;
  }
  
  @JsonIdentityReference(alwaysAsId = true)
  @JsonProperty("workflow")
  private String workflow() {
    return this.workflow.getId();
  }
}
