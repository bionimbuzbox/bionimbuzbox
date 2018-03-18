package org.bionimbuzbox.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Workflow extends GenericModel implements Model {

  private static final long serialVersionUID = 8501048714154483954L;

  private String name;

  @JsonProperty(value="jobs")
  @JsonDeserialize(contentUsing = JobMapDeserializer.class)
  private ConcurrentMap<String, Job> jobs = new ConcurrentHashMap<String, Job>();

  @JsonProperty(value="resources")
  @JsonDeserialize(contentUsing = ResourceMapDeserializer.class)
  private ConcurrentMap<String, Resource> resources = new ConcurrentHashMap<String, Resource>();

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer("Workflow{ ");
    buffer.append( String.format("%s=%s, ", "ID", this.getId()) );
    buffer.append( String.format("%s=%s, ", "Meta", this.getMeta()) );
    buffer.append( String.format("%s=%s", "Jobs", this.jobs) );
    buffer.append( String.format("%s=%s", "Resources", this.resources) );

    buffer.append(" }");
    
    return buffer.toString();
    
  }

  public Workflow() {

  }

  public Workflow(String id) {
    this.setId(id);
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void loadJobsFromCollection(Collection<Job> jobs) {
    for(Job job : jobs) {
      this.addJob(job);
    }
  }

  public void loadResourcesFromCollection(Collection<Resource> resources) {
    for(Resource resource : resources) {
      this.addResource(resource);
    }
  }

  public void addJob(Job job) {
    this.jobs.put(job.getId(), job);
  }
  
  public Job getJob(String id) {
    return this.jobs.get(id);
  }

  public void removeJob(Job job) {
    this.jobs.remove(job.getId());
  }

  public void removeJob(String id) {
    this.jobs.remove(id);
  }
  
  @JsonIgnore
  public Collection<Job> getJobs() {
    return this.jobs.values();
  }
  /*
  @JsonIdentityReference(alwaysAsId = true)
  private void setJobs(Collection<String> ids) {
    for (String id : ids) {
      Job j = new Job(id);
      this.addJob(j);
    }
  }
  */


  public void addResource(Resource resource) {
    this.resources.put(resource.getId(), resource);
  }
  
  public Resource getResource(String id) {
    return this.resources.get(id);
  }

  public void removeResource(Resource resource) {
    this.jobs.remove(resource.getId());
  }

  public void removeResource(String id) {
    this.resources.remove(id);
  }
  
  @JsonIgnore
  public Collection<Resource> getResources() {
    return this.resources.values();
  }
  
  /*
  @JsonIdentityReference(alwaysAsId = true)
  private void setResources(Collection<String> ids) {
    for (String id : ids) {
      Resource r = new Resource(id);
      this.addResource(r);
    }
  }
*/
  public static class JobMapDeserializer extends JsonDeserializer<Job> {
    @Override
    public Job deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      Object workflow = p.getParsingContext()
          .getParent()
          .getParent()
          .getCurrentValue();
      
      String id = ctxt.getParser().getCurrentName();

      Job job = p.readValueAs(Job.class);
      job.setId(id);
      job.setWorkflow((Workflow) workflow);

      return job;
    }
  }
  
  public static class ResourceMapDeserializer extends JsonDeserializer<Resource> {

    @Override
    public Resource deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      String name = ctxt.getParser().getCurrentName();

      Resource resource = p.readValueAs(Resource.class);

      resource.setId(name);   // Fills the key in the value object!

      return resource;
    }
  }
  
  public static class IdentifiableMapDeserializer<T extends Identifiable> extends JsonDeserializer<T> {

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      String name = ctxt.getParser().getCurrentName();

      T identifiable = p.readValueAs(new TypeReference<T>() {
      });

      identifiable.setId(name);   // Fills the key in the value object!

      return identifiable;
    }
  }
}
