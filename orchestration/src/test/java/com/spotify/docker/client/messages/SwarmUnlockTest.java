package com.spotify.docker.client.messages;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificates;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.swarm.SwarmInit;
import com.spotify.docker.client.messages.swarm.SwarmSpec;
import java.nio.file.Paths;

public class SwarmUnlockTest {

  public SwarmUnlockTest() {
    // TODO Auto-generated constructor stub
  }
  
  public void test() throws Exception {
    // docker-machine must be installed
    // docker client must be installed
    Process p1 = Runtime.getRuntime().exec("docker-machine create -d virtualbox m1");
    Process p2 = Runtime.getRuntime().exec("docker-machine ip m1");
    byte[] ip = new byte[32];
    p2.getInputStream().read(ip);
    
    
    DockerClient client = DefaultDockerClient.builder()
        .uri("")
        .dockerCertificates(new DockerCertificates(Paths.get("/Users/tiago/.docker/machine/cert")))
        .build();
    
    //client.initSwarm(swarmInit);
    
    Process p3 = Runtime.getRuntime().exec("docker $(docker-machine config m1) swarm init --autolock --advertise-addr eth1");
    
    Process p4 = Runtime.getRuntime().exec("docker-machine ssh m1 sudo /etc/init.d/docker restart");
    // Build DockerClient ...
    
    
    Runtime.getRuntime().exec("docker-machine rm m1");
  }

}
