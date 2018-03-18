package org.bionimbuzbox.orchestration.driver.swarm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.ServiceCreateResponse;
import com.spotify.docker.client.messages.Volume;
import com.spotify.docker.client.messages.mount.Mount;
import com.spotify.docker.client.messages.swarm.ContainerSpec;
import com.spotify.docker.client.messages.swarm.JoinTokens;
import com.spotify.docker.client.messages.swarm.RestartPolicy;
import com.spotify.docker.client.messages.swarm.ServiceMode;
import com.spotify.docker.client.messages.swarm.ServiceSpec;
import com.spotify.docker.client.messages.swarm.Swarm;
import com.spotify.docker.client.messages.swarm.Task;
import com.spotify.docker.client.messages.swarm.TaskSpec;
import com.spotify.docker.client.messages.swarm.TaskStatus;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;
import org.bionimbuzbox.helper.JSONHelper;
import org.bionimbuzbox.model.Cluster;
import org.bionimbuzbox.model.Job;
import org.bionimbuzbox.model.Resource;
import org.bionimbuzbox.model.Server;
import org.bionimbuzbox.orchestration.driver.OrchestrationDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrchestrationDriverImpl implements OrchestrationDriver {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrchestrationDriverImpl.class);

  private final Cluster cluster;
  private boolean initialized = false;
  private JoinTokens joinTokens;
  private final List<Server> managers = new ArrayList<>();

  private final ArrayList<String> remoteAddrs = new ArrayList<>();

  public OrchestrationDriverImpl(Cluster cluster) {
    this.cluster = cluster;
  }

  @Override
  public void build() {
    for(Server server : this.cluster.getServers()) {
      
      try (DockerClient client = DockerClientHelper.buildDockerClient(server)) {
      
        if ("true".equals(server.getMetaInfo("init"))) {
          Swarm swarm = client.inspectSwarm();
          joinTokens = swarm.joinTokens();
          remoteAddrs.add(String.format("%s:%d", server.getMetaInfo("private-ip", client.getHost()), DockerClientHelper.DEFAULT_SWARM_ADVERTISE_PORT));
          managers.add(server);
          initialized = true;
          LOGGER.info("Swarm cluster {} initialized.", cluster.getId());
          return;
        } else if ("manager".equals(server.getMetaInfo("manager"))) {
          remoteAddrs.add(String.format("%s:%d", server.getMetaInfo("private-ip", client.getHost()), DockerClientHelper.DEFAULT_SWARM_ADVERTISE_PORT));
          managers.add(server);
        }
      } catch (Exception e) {
        LOGGER.error("Swarm cluster {} not initialized!", cluster.getId());
        LOGGER.error(e.getMessage(), e);
      }
    }
  }

  @Override
  public boolean clean(Job job) {
    boolean result = false;
    try (DockerClient client = getManagerClient()) {
      LOGGER.info("Removing {} [{}] job...", job.getId(), job.getMetaInfo("swarm.job.id"));
      client.removeService(job.getMetaInfo("swarm.job.id"));
      LOGGER.info("Job {} [{}] removed.", job.getId(), job.getMetaInfo("swarm.job.id"));
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    return result;
  }

  @Override
  public boolean clean(Resource resource) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void installBioNimbuzBox() {

  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public void join(Server server) {

    try (DockerClient client = DockerClientHelper.buildDockerClient(server)) {
      
      if (!initialized) {
        LOGGER.info("Swarm cluster {} not initialized. Executing auto init...", cluster.getId());
        server.addMetaInfo("role", "manager");
        server.addMetaInfo("init", "true");
        joinTokens = DockerClientHelper.initSwarm(client, server, true);
        remoteAddrs.add(String.format("%s:%d", server.getMetaInfo("private-ip", client.getHost()), DockerClientHelper.DEFAULT_SWARM_ADVERTISE_PORT));
        managers.add(server);
        initialized = true;
        LOGGER.info("Swarm cluster {} initialized.", cluster.getId());
        return;
      }

      if (client.info().swarm().localNodeState().equals("inactive")) {
        if ("manager".equals(server.getMetaInfo("role"))) { 
          LOGGER.info("Joining {} server to {} swarm cluster  as manager", server.getId(), cluster.getId());
          DockerClientHelper.joinSwarm(client, server, joinTokens.manager(), remoteAddrs);
          remoteAddrs.add(String.format("%s:%d", server.getMetaInfo("private-ip", client.getHost()), DockerClientHelper.DEFAULT_SWARM_ADVERTISE_PORT));
        } else {
          LOGGER.info("Joining {} server to {} swarm cluster as worker", server.getId(), cluster.getId());
          DockerClientHelper.joinSwarm(client, server, joinTokens.worker(), remoteAddrs);
          server.addMetaInfo("role", "worker");
        }
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  @Override
  public String logs(String id) {
    return "OK";
  }

  @Override
  public Job start(Job job) {
    LOGGER.info("Starting job {} ...", job.getId());
    
    String serviceName = String.format("%s-%s", 
        job.getWorkflow().getId(), 
        job.getId());
    
    Map<String, String> labels = new HashMap<>();
    labels.put("job.id", job.getId());
    
    ContainerSpec containerSpec = buildContainerSpec(job.getMeta());
    TaskSpec taskSpec = buildTaskSpec(containerSpec);
    ServiceSpec serviceSpec = buildServiceSpec(serviceName, taskSpec, labels);
    ServiceCreateResponse response = createService(serviceSpec); //TODO: Error handling
    
    job.addMetaInfo("swarm.job.id", response.id());
    job.addMetaInfo("swarm.job.name", serviceName);
    LOGGER.info("Job {} [{}] started.", job.getId(), job.getMetaInfo("swarm.job.id"));
    return job;
  }

  @Override
  public Resource start(Resource resource) {
    LOGGER.info("Starting resource {} ...", resource.getId());
    
    String uniqueName = resource.getId();
    
    Map<String, String> driverOpts = new HashMap<>();
    driverOpts.put("endpoint", "http://192.168.99.100:9000");
    driverOpts.put("access-key", "AKIAIOSFODNN7EXAMPLE");
    driverOpts.put("secret-key", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");
    driverOpts.put("bucket", uniqueName);
    driverOpts.put("opts=cache", "/tmp/" + uniqueName);
    
    Volume volume = Volume.builder()
        .driver("minio/minfs")
        .driverOpts(driverOpts)
        .name(uniqueName)
        .build();
    
    try (DockerClient client = getManagerClient()){
      volume = client.createVolume(volume);
      resource.addMetaInfo("swarm.resource.id", uniqueName);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    
    LOGGER.info("Resource {} [{}] started.", resource.getId(), uniqueName);
    return resource;
  }

  public Job.Status status(Job job) {
    try (DockerClient client = getManagerClient()){
      Task.Criteria criteria = Task.Criteria.builder()
          .serviceName(job.getMetaInfo("swarm.job.id"))
          .build();

      List<Task> tasks = client.listTasks(criteria);
      for (Task task : tasks) {
        LOGGER.info("Job: {}, Task: {}, Status: {}", job.getId(), task.id(), task.status().state());

        switch (task.status().state()) {
          case TaskStatus.TASK_STATE_COMPLETE:
            return Job.Status.FINISHED;
          case TaskStatus.TASK_STATE_FAILED:
          case TaskStatus.TASK_STATE_REJECTED:
            LOGGER.warn("Error Job running");
            break;
          default:
            return Job.Status.RUNNING;
        }
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    return Job.Status.WAITING;
  }
  
  @Override
  public Resource.Status status(Resource resource) {
    Resource.Status status = Resource.Status.NEW;
    try (DockerClient client = getManagerClient()){
      client.inspectVolume(resource.getMetaInfo("swarm.resource.id"));
      status = Resource.Status.READY;
    } catch (Exception e) {
      status = Resource.Status.PENDING;
    }   
    return status;
  }
  
  private ServiceCreateResponse createService(ServiceSpec serviceSpec) {
    ServiceCreateResponse response = null;
    try (DockerClient client = getManagerClient()){
      response = client.createService(serviceSpec);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    return response;
  }
  
  private ServiceSpec buildServiceSpec(String name, TaskSpec taskSpec, Map<String, String> labels) {
    long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    
    String serviceName = String.format("%s-%s", 
        timestamp, 
        name);
    
    ServiceSpec.Builder builder = ServiceSpec.builder()
        .mode(ServiceMode.withReplicas(1))
        .name(serviceName)
        .taskTemplate(taskSpec);

    for(Entry<String,String> entry : labels.entrySet()) {
      builder.addLabel(entry.getKey(), entry.getValue());
    }

     return builder.build();
  }
  
  private ContainerSpec buildContainerSpec(Map<String, String> options) {
    ContainerSpec.Builder containerSpecBuilder = ContainerSpec.builder();
    containerSpecBuilder.image(options.get("swarm.image"));
    containerSpecBuilder.dir(options.getOrDefault("environment.work-dir", "/root"));
    containerSpecBuilder.env(StringUtils.split(options.getOrDefault("environment.vars", "HOME=/root"), ';'));

    List<Mount> mounts = buildMounts(options);
    if (!mounts.isEmpty()) {
      containerSpecBuilder.mounts(mounts);
    }

    List<String> commands = buildCommands(options);
    if (!commands.isEmpty()) {
      containerSpecBuilder.command(commands);
    }
    
    return containerSpecBuilder.build();
  }
  
  private TaskSpec buildTaskSpec(ContainerSpec containerSpec) {
    return TaskSpec.builder()
    .containerSpec(containerSpec)
    .restartPolicy(RestartPolicy.builder()
        .condition(RestartPolicy.RESTART_POLICY_ON_FAILURE)
        .maxAttempts(3)
        .delay(10000000000L) // 10 segundos
        .build())
    .build();
  }

  @SuppressWarnings("unchecked")
  private List<String> buildCommands(Map<String, String> options) {

    if (!options.containsKey("swarm.commands")) {
      return new ArrayList<String>();
    }

    return JSONHelper.fromJSON(options.get("swarm.commands"), ArrayList.class);
  }

  private List<Mount> buildMounts(Map<String, String> options) {
    List<Mount> list = new ArrayList<>();

    if (!options.containsKey("swarm.mounts")) {
      return list;
    }
    
    List<Map<String, String>> mounts = JSONHelper.fromJSON(options.get("swarm.mounts"), new TypeReference<List<Map<String,String>>>() {});

    for (Map<String, String> meta : mounts) {
      list.add(
          Mount.builder()
          .source(meta.get("source"))
          .target(meta.get("target"))
          .readOnly(Boolean.parseBoolean(meta.get("readonly")))
          .type(meta.get("type"))
          .build()
          );
    }
    return list;
  }

  private DockerClient getManagerClient() {
    DockerClient client = null;
    for (Server manager : managers) {
      try {
        client = DockerClientHelper.buildDockerClient(manager);
        break;
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
      }
    }
    return client;
  }

  @Override
  public Cluster cluster() {
    return this.cluster;
  }
}
