package com.spotify.docker.client.messages;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificates;
import com.spotify.docker.client.DockerClient;
import java.nio.file.Paths;

public class SwarmInfoTest {

  public SwarmInfoTest() {
    // TODO Auto-generated constructor stub
  }
  
  public static void main(String[] args) throws Exception {
    DockerClient client = DefaultDockerClient.builder()
        .uri("https://192.168.99.100:2376")
        .dockerCertificates(new DockerCertificates(Paths.get("/Users/tiago/.docker/machine/certs/")))
        .build();
    
    Info info = client.info();
    System.out.println(info.swarm());
  }

}
