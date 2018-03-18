package org.bionimbuzbox.model.ignition;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.ArrayList;
import java.util.List;
import org.bionimbuzbox.helper.FileHelper;

public class IgnitionBuilderTest {

  public IgnitionBuilderTest() {
    // TODO Auto-generated constructor stub
  }
  
  public static List<File> buildDockerFiles(String caContent, String certContent, String keyContent) {
    List<File> files = new ArrayList<>();
    File caFile = new File()
        .withFilesystem("root")
        .withPath("/etc/docker/ca.pem")
        .withMode(420)
        .withContents(new Contents().withSource(
            DataURIUtil.encodeDataURI(caContent).toString()
        ));
    
    File serverFile = new File()
        .withFilesystem("root")
        .withPath("/etc/docker/server.pem")
        .withMode(420)
        .withContents(new Contents().withSource(
            DataURIUtil.encodeDataURI(certContent).toString()
        ));
    
    File serverKeyFile = new File()
        .withFilesystem("root")
        .withPath("/etc/docker/server-key.pem")
        .withMode(420)
        .withContents(new Contents().withSource(
            DataURIUtil.encodeDataURI(keyContent).toString()
        ));
    
    files.add(caFile);
    files.add(serverFile);
    files.add(serverKeyFile);
    
    return files;
  }

  public static List<Unit> buildSystemdDockerUnits() {
    List<Unit> units = new ArrayList<>();
    
    String dockerUnitDropinContents = "[Service]\n"
        + "Environment=\"DOCKER_OPTS="
        + " --tlsverify "
        + " --tlscacert=/etc/docker/ca.pem "
        + " --tlscert=/etc/docker/server.pem "
        + " --tlskey=/etc/docker/server-key.pem "
        + "\"";
    
    Dropin dockerUnitDropin = new Dropin()
        .withName("20-clct-docker.conf")
        .withContents(dockerUnitDropinContents);
    
    List<Dropin> dockerUnitDropins = new ArrayList<>();
    dockerUnitDropins.add(dockerUnitDropin);
    
    Unit dockerUnit = new Unit()
        .withName("docker.service")
        .withEnable(true)
        .withDropins(dockerUnitDropins);
        
    String socketUnitContents = "[Unit]\n"
        + "Description=Docker Secured Socket for the API\n\n"
        + "[Socket]\n"
        + "ListenStream=2376\n"
        + "BindIPv6Only=both\n"
        + "Service=docker.service"
        + "\n\n"
        + "[Install]\n"
        + "WantedBy=sockets.target\n";
    
    Unit socketUnit = new Unit()
        .withName("docker-tls-tcp.socket")
        .withEnable(true)
        .withContents(socketUnitContents);
    
    units.add(dockerUnit);
    units.add(socketUnit);
    
    return units;
  }
 
  public static void main(String[] args) throws Exception {
    
    Ignition ignition = new Ignition()
        .withVersion("2.0.0");
    
    String ca = FileHelper.readFile("/Users/tiago/.docker/machine/certs/ca.pem");
    String cert = FileHelper.readFile("/Users/tiago/.docker/machine/certs/cert.pem");
    String key = FileHelper.readFile("/Users/tiago/.docker/machine/certs/key.pem");
    
    Storage storage = new Storage()
        .withFiles(buildDockerFiles(ca, cert, key));
    
    Systemd systemd = new Systemd()
        .withUnits(buildSystemdDockerUnits());
    
    
    IgnitionBuilder builder = new IgnitionBuilder()
        .withIgnition(ignition)
        .withStorage(storage)
        .withSystemd(systemd);
    
    
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.setSerializationInclusion(Include.NON_NULL);
    String result = mapper.writeValueAsString(builder);
    
    System.out.println(result);
  }
}

/*
 "filesystem": "root",
        "path": "/etc/docker/ca.pem",
        "contents": {
          "source": "data:,-----BEGIN%20CERTIFICATE-----%0AMIIFNDCCAx6gAwIBAgIBATALBgkqhkiG9w0BAQswLTEMMAoGA1UEBhMDVVNBMRAw%0ADgYDVQQKEwdldGNkLWNhMQswCQYDVQQLEwJDQTAeFw0xNTA5MDIxMDExMDhaFw0y%0ANTA5MDIxMDExMThaMC0xDDAKBgNVBAYTA1VTQTEQMA4GA1UEChMHZXRjZC1jYTEL%0A...%20...%20...%0A",
          "verification": {}
        },
 * */
