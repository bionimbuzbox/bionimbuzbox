package org.bionimbuzbox.orchestration.driver;

import static org.junit.Assert.fail;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.mount.Mount;
import com.spotify.docker.client.messages.swarm.ContainerSpec;
import com.spotify.docker.client.messages.swarm.ServiceMode;
import com.spotify.docker.client.messages.swarm.ServiceSpec;
import com.spotify.docker.client.messages.swarm.TaskSpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.bionimbuzbox.helper.JSONHelper;
import org.bionimbuzbox.model.Cluster;
import org.bionimbuzbox.model.Job;
import org.bionimbuzbox.model.Server;
import org.bionimbuzbox.model.Workflow;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class OrchestratioDriverTest {

  private static final Logger LOGGER = Logger.getLogger(OrchestratioDriverTest.class.getName());
  
  private static OrchestrationDriver driver;
/*
  public void test55(DockerClient client) {
    List<Mount> mounts = new ArrayList<>();
    mounts.add(
        Mount.builder()
        .source("/")
        .target("/rootfs")
        .readOnly(true)
        .type("bind")
        .build()
        );
    mounts.add(
        Mount.builder()
        .source("/var/run")
        .target("/var/run")
        .readOnly(false)
        .type("bind")
        .build()
        );
    mounts.add(
        Mount.builder()
        .source("/sys")
        .target("/sys")
        .readOnly(true)
        .type("bind")
        .build()
        );
    mounts.add(
        Mount.builder()
        .source("/var/lib/docker/")
        .target("/var/lib/docker/")
        .readOnly(true)
        .type("bind")
        .build()
        );

    ContainerSpec containerSpec = ContainerSpec.builder()
        .image("google/cadvisor:v.0.24.1")
        .mounts(mounts)
        .build();

    TaskSpec taskSpec = TaskSpec.builder()
        .containerSpec(containerSpec)
        .build();

    ServiceSpec spec = ServiceSpec.builder()
        .mode(ServiceMode.withGlobal())
        .name("cadvisor")
        .taskTemplate(taskSpec)
        .build();

    try {
      client.createService(spec);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @BeforeClass
  public static void setup() throws Exception {

    Cluster cluster = new Cluster();
    cluster.setId("cluster-1");
    cluster.setName("cluster-1");
    cluster.setType("swarm");
    
    driver = OrchestrationDriver.initializeDriver(cluster);
  }
  
  @Test
  public void testUniqueDriver() {
    Cluster c1 = new Cluster();
    c1.setId("cluster-1");
    c1.setName("cluster-1");
    c1.setType("swarm");

    Cluster c2 = new Cluster();
    c2.setId("cluster-1");
    c2.setName("cluster-1");
    c2.setType("swarm");

    OrchestrationDriver d1 = OrchestrationDriver.initializeDriver(c1);
    OrchestrationDriver d2 = OrchestrationDriver.initializeDriver(c1);

    Assert.assertEquals(d1, d2);
  }
  
  @Test
  public void testJoinServers() {
    try {
      String ca = new String(Files.readAllBytes(
          Paths.get("/Users/tiago/.docker/machine/certs/ca.pem")), 
          StandardCharsets.UTF_8);
      String cert = new String(Files.readAllBytes(
          Paths.get("/Users/tiago/.docker/machine/certs/cert.pem")), 
          StandardCharsets.UTF_8);
      String key = new String(Files.readAllBytes(
          Paths.get("/Users/tiago/.docker/machine/certs/key.pem")), 
          StandardCharsets.UTF_8);

      Server s1 = new Server();
      s1.setId("s1");
      s1.addMetaInfo("private-ip", "192.168.99.100");
      s1.addMetaInfo("public-ip", "192.168.99.100");
      s1.addMetaInfo("fqdn", "192.168.99.100");
      s1.addMetaInfo("ssl-ca-cert", ca);
      s1.addMetaInfo("ssl-client-cert", cert);
      s1.addMetaInfo("ssl-client-key", key);


      Server s2 = new Server();
      s2.setId("s2");
      s2.addMetaInfo("private-ip", "192.168.99.101");
      s2.addMetaInfo("public-ip", "192.168.99.101");
      s2.addMetaInfo("fqdn", "192.168.99.101");
      s2.addMetaInfo("ssl-ca-cert", ca);
      s2.addMetaInfo("ssl-client-cert", cert);
      s2.addMetaInfo("ssl-client-key", key);
      
      driver.join(s1);
      driver.join(s2);
      
    } catch (IOException e) {
      fail();
    }
  }
  
  @Test
  public void testJobExecution() {
    
    testJoinServers();
    
    Job job = new Job("Job-1");
    job.addMetaInfo("swarm.image", "httpd:alpine");
    job.addMetaInfo("swarm.limit-cpu", "1");
    job.addMetaInfo("swarm.limit-memory", "");
    job.addMetaInfo("swarm.reserve-cpu", "0");
    job.addMetaInfo("swarm.reserve-memory", "0");

    Workflow w = new Workflow("W-1");
    job.setWorkflow(w);

    List<Map<String,String>> mounts = new ArrayList<>();
    Map<String,String> mount1 = new HashMap<>();
    mount1.put("source", "/var/log");
    mount1.put("target", "/mnt");
    mount1.put("readonly", "false");
    mount1.put("type", "bind");
    mounts.add(mount1);

    job.addMetaInfo("swarm.mounts", JSONHelper.toJSON(mounts));

    LOGGER.info(job.getMetaInfo("swarm.mounts"));
    
    try {
      driver.start(job);
      Thread.sleep(5000);
      driver.clean(job);
    } catch (Exception e) {
      fail();
    }
    
    
  }
  */
}
