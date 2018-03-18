package org.bionimbuzbox.orchestration.driver.swarm;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificates;
import com.spotify.docker.client.DockerCertificatesStore;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerStringCertificates;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.messages.RegistryAuth;
import com.spotify.docker.client.messages.swarm.JoinTokens;
import com.spotify.docker.client.messages.swarm.SwarmInit;
import com.spotify.docker.client.messages.swarm.SwarmJoin;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import org.bionimbuzbox.model.Server;

public class DockerClientHelper {
  
  public static final int DEFAULT_SWARM_ADVERTISE_PORT = 2377;
  public static final int DEFAULT_DOCKER_UNENCRYPTED_PORT = 2375;
  public static final int DEFAULT_DOCKER_ENCRYPTED_PORT = 2376;
  
  private static final Logger LOGGER = Logger.getLogger(DockerClientHelper.class.getName());

  private DockerClientHelper() {
    // TODO Auto-generated constructor stub
  }

  public static RegistryAuth buildRegistryAuth(String server, String username, String password, String email) {
    return RegistryAuth.builder()
        .email(email)
        .username(username)
        .password(password)
        .serverAddress(server)
        .build();
  }

  public static DockerCertificatesStore buildDockerCertificatesStore(String caContent, String certContent, String keyContent) throws DockerCertificateException {

    return DockerStringCertificates.builder()
        .caCertContent(caContent)
        .clientCertContent(certContent)
        .clientKeyContent(keyContent)
        .build().get();

  }
  
  public static DockerCertificatesStore buildDockerCertificatesStore(File caFile, File certFile, File keyFile) throws DockerCertificateException {

    return DockerCertificates.builder()
        .caCertPath(Paths.get(caFile.toURI()))
        .clientCertPath(Paths.get(certFile.toURI()))
        .clientKeyPath(Paths.get(keyFile.toURI()))
        .build().get();

  }

  public static DockerClient buildDockerClient(Server server) { 
    return buildDockerClient(server, true);
  }
  
  /**
   * Meta Info Needed:
   * 
   * server.meta.ssl-ca-cert
   * server.meta.ssl-client-cert
   * server.meta.ssl-client-key
   * server.meta.public-ip
   * 
   * @param server
   * @param secure
   * @return
   */
  public static DockerClient buildDockerClient(Server server, boolean secure) {
    String schema = "http";
    int port = DEFAULT_DOCKER_UNENCRYPTED_PORT;
    
    DefaultDockerClient.Builder dockerClientBuilder = DefaultDockerClient.builder();

    if (secure) {
      try {
        DockerCertificatesStore store = buildDockerCertificatesStore(
            server.getMetaInfo("ssl-ca-cert"),
            server.getMetaInfo("ssl-client-cert"),
            server.getMetaInfo("ssl-client-key")
        );
        
        schema = "https";
        port = DEFAULT_DOCKER_ENCRYPTED_PORT;
        
        dockerClientBuilder.dockerCertificates(store);
        
      } catch (Exception e) {
        LOGGER.warning(e.getMessage());
      }
    }

    String URI = String.format("%s://%s:%d", schema, server.getMetaInfo("fqdn"), port);
    LOGGER.info("DockerClient URI: " + URI);

    DockerClient client = dockerClientBuilder.uri(URI)
        .connectTimeoutMillis(15000)
        .readTimeoutMillis(30000).build();

    awaitReadiness(client, 30, 3000);

    return client;
  }

  private static void awaitReadiness(DockerClient client, int attempts, long attemptsIntervalMillis) {
    do {
      attempts--;
      try {
        client.version().apiVersion();
        break;
      } catch (Exception e) {
        LOGGER.warning(e.getMessage());
        try {
          Thread.sleep(attemptsIntervalMillis);
        } catch (Exception ex) {
          LOGGER.severe(ex.getMessage());
        }
      }
    } while(attempts > 0);
  }
  
  /**
   * Meta Info Needed:
   * 
   * server.meta.private-ip
   * 
   * @param client
   * @param server
   * @return
   * @throws Exception
   */
  public static JoinTokens initSwarm(DockerClient client, Server server, boolean force) throws Exception {
    String privateIp = server.getMetaInfo("private-ip") == null ?
        "0.0.0.0" : server.getMetaInfo("private-ip");
    
    SwarmInit swarmInit = SwarmInit.builder()
        .forceNewCluster(force)
        .advertiseAddr(String.format("%s:%d", privateIp, DEFAULT_SWARM_ADVERTISE_PORT))
        .listenAddr(String.format("%s:%d", privateIp, DEFAULT_SWARM_ADVERTISE_PORT))
        .build();

    client.initSwarm(swarmInit);

    return client.inspectSwarm().joinTokens();
  }
  
  /**
   * Meta Info Needed:
   * 
   * server.meta.private-ip
   * 
   * @param client
   * @param server
   * @return
   * @throws Exception
   */
  public static JoinTokens initSwarm(DockerClient client, Server server) throws Exception {
    return initSwarm(client, server, false);
  }
  
  /**
   * Meta Info Needed:
   * 
   * server.meta.private-ip
   * 
   * @param client
   * @param server
   * @return
   * @throws Exception
   */
  public static void joinSwarm(DockerClient client, Server server, String token, List<String> remoteAddrs) throws Exception {
    String privateIp = server.getMetaInfo("private-ip") == null ?
        "0.0.0.0" : server.getMetaInfo("private-ip");
    
    SwarmJoin swarmJoin = SwarmJoin.builder()
        .advertiseAddr(String.format("%s:%d", privateIp, DEFAULT_SWARM_ADVERTISE_PORT))
        .listenAddr(String.format("%s:%d", privateIp, DEFAULT_SWARM_ADVERTISE_PORT))
        .joinToken(token)
        .remoteAddrs(remoteAddrs)
        .build();

    client.joinSwarm(swarmJoin);
  }
}
