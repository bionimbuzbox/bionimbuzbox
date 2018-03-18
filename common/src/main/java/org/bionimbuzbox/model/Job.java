package org.bionimbuzbox.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@JsonIgnoreProperties(ignoreUnknown=true, value={"id"})
//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
//@JsonPropertyOrder({ "id", "workflow", "status", "meta", "dependencies" })
public class Job extends GenericModel implements Model {
	  
  public static class DependencySerializer extends JsonSerializer<Job> {
    @Override
    public void serialize(Job value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException, JsonProcessingException {
      gen.writeString(value.getId());
    }
  }
  
  public enum Status { FINISHED, NEW, RUNNING, WAITING }
  
  private static final Logger LOGGER = Logger.getLogger(Job.class.getName());

  private static final long serialVersionUID = -3026902568820443733L;
  
  @JsonSerialize(contentUsing = DependencySerializer.class)
  private Map<String, Job> dependencies = new HashMap<String, Job>();
  
  private Status status = Status.NEW;
  
  private Workflow workflow;
  
  public Job() {
    
  }
  
  public Job(String id) {
    this.setId(id);
  }
  
  public void addDependency(Job dependency) {
    if (!this.equals(dependency)) {
      this.dependencies.put(dependency.getId(), dependency);
    } else {
      throw new RuntimeException("You can not add a dependency of itself.");
    }
  }
  
  @JsonIdentityReference(alwaysAsId = true)
  public Collection<Job> getDependencies() {
    return this.dependencies.values();
  }
  
  public Status getStatus() {
    return this.status;
  }
  
  @JsonIgnore
  public Workflow getWorkflow() {
    return this.workflow;
  }

  public boolean hasDependencies() {
    if (this.dependencies.size() > 0) {
      return true;
    }
    return false;
  }

  @JsonIgnore
  public boolean isFinished() {
    if (this.status == Status.FINISHED) {
      return true;
    }
    return false;
  }
  
  @JsonIgnore
  public boolean isReady() {
    return this.isFinished();
  }
  
  @JsonIgnore
  public boolean isRunning() {
    if (this.status == Status.RUNNING) {
      return true;
    }
    return false;
  }
  
  @JsonIgnore
  public boolean isWaiting() {
    if (this.status == Status.WAITING) {
      return true;
    }
    return false;
  }

  public void removeDependency(Job dependency) {
    this.dependencies.remove(dependency.getId());
  }

  public void setStatus(Status status) {
    this.status = status;
    LOGGER.info(
        String.format("Job '%s' status: %s", this.getId(), status)
    );
  }

  //@JsonIgnore
  public void setWorkflow(Workflow workflow){
    this.workflow = workflow;
  }
  
  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer("Job{ ");
    buffer.append( String.format("%s=%s, ", "ID", this.getId()) );
    buffer.append( String.format("%s=%s, ", "Status", this.getStatus()) );
    buffer.append( String.format("%s=%s, ", "Meta", this.getMeta()) );
    buffer.append( String.format("%s=%s", "Dependencies", this.dependencies) );
    buffer.append(" }");
    
    return buffer.toString();
  }

  @JsonIdentityReference(alwaysAsId = true)
  private void setDependencies(Collection<String> ids) {
    for (String id : ids) {
      Job j = new Job(id);
      this.addDependency(j);
    }
  }
  
  //@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
  //@JsonIdentityReference(alwaysAsId=true)
  @JsonIgnore
  private void setWorkflow(String id){
    Workflow workflow = new Workflow(id);
    this.workflow = workflow;
  }

}
