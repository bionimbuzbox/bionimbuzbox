package org.bionimbuzbox.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.logging.Level;
import java.util.logging.Logger;

@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Resource extends GenericModel implements Model {

  public enum Status {NEW, PENDING, READY}

  private static final long serialVersionUID = -5798616872162114368L;
  private static final Logger LOGGER = Logger.getLogger(Resource.class.getName());
  private Workflow workflow;
  private Status status = Status.NEW;
  
  public Resource() {
    
  }
  
  public Resource(String id) {
    this.setId(id);
  }
  
  public Resource(String id, Workflow w) {
    this.setId(id);
    this.setWorkflow(w);
  }
  
  public Resource(String resourceId, String workflowId) {
    this.setId(resourceId);
    this.setWorkflow(workflowId);
  }

  @JsonIdentityReference(alwaysAsId=true)
  private void setWorkflow(String id){
    Workflow workflow = new Workflow(id);
    this.workflow = workflow;
  }

  public void setWorkflow(Workflow workflow){
    this.workflow = workflow;
  }

  public Workflow getWorkflow() {
    return this.workflow;
  }

  public Status getStatus() {
    return this.status;
  }

  public void setStatus(Status status) {
    this.status = status;
    LOGGER.info(
        String.format("Resource '%s' status: %s", this.getId(), status)
        );
  }

  @JsonIgnore
  public boolean isPending() {
    if (this.status == Status.PENDING) {
      return true;
    }
    return false;
  }

  @JsonIgnore
  public boolean isReady() {
    if (this.status == Status.READY) {
      return true;
    }
    return false;
  }

  public void execute() {
    this.setStatus(Status.PENDING);
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
    }
    this.setStatus(Status.READY);
  }
}
