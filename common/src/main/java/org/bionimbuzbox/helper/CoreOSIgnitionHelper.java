package org.bionimbuzbox.helper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.bionimbuzbox.model.ignition.Contents;
import org.bionimbuzbox.model.ignition.DataURIUtil;
import org.bionimbuzbox.model.ignition.Dropin;
import org.bionimbuzbox.model.ignition.File;
import org.bionimbuzbox.model.ignition.Ignition;
import org.bionimbuzbox.model.ignition.IgnitionBuilder;
import org.bionimbuzbox.model.ignition.Passwd;
import org.bionimbuzbox.model.ignition.Storage;
import org.bionimbuzbox.model.ignition.Systemd;
import org.bionimbuzbox.model.ignition.Unit;
import org.bionimbuzbox.model.ignition.User;

public class CoreOSIgnitionHelper {
  
  private static final Logger LOGGER = Logger.getLogger(CoreOSIgnitionHelper.class.getName());
  
  private CoreOSIgnitionHelper() {
    
  }
  
  public static List<File> createDockerTLSFiles(String caContent, String certContent, String keyContent) {
    List<File> files = new ArrayList<>();
    
    files.add(createDockerTLSCAFile(caContent));
    files.add(createDockerTLSCertFile(certContent));
    files.add(createDockerTLSKeyFile(keyContent));
    
    return files;
  }
  
  public static File createDockerTLSCAFile(String caContent) {
    return new File()
    .withFilesystem("root")
    .withPath("/etc/docker/ca.pem")
    .withMode(420)
    .withContents(new Contents().withSource(
        DataURIUtil.encodeDataURI(caContent).toString()
    ));
  }
  
  public static File createDockerTLSCertFile(String certContent) {
    return new File()
    .withFilesystem("root")
    .withPath("/etc/docker/server.pem")
    .withMode(420)
    .withContents(new Contents().withSource(
        DataURIUtil.encodeDataURI(certContent).toString()
    ));
  }
  
  public static File createDockerTLSKeyFile(String keyContent) {
    return new File()
    .withFilesystem("root")
    .withPath("/etc/docker/server-key.pem")
    .withMode(420)
    .withContents(new Contents().withSource(
        DataURIUtil.encodeDataURI(keyContent).toString()
    ));
  }
  
  public static File createNFSExportsFile(String exportsContent) {
    return new File()
        .withFilesystem("root")
        .withPath("/etc/exports")
        .withMode(420)
        .withContents(new Contents().withSource(
            DataURIUtil.encodeDataURI(exportsContent).toString()
        ));
  }
 
  private static ObjectMapper mapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.setSerializationInclusion(Include.NON_NULL);
    return mapper;
  }
  
  public static IgnitionBuilder createIgnitionBuilder(){
    return new IgnitionBuilder()
        .withIgnition(new Ignition().withVersion("2.0.0"));
  }
   
  public static Unit createDockerTLSServiceUnit() {
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
    
    return new Unit()
        .withName("docker.service")
        .withEnable(true)
        .withDropins(dockerUnitDropins);
  }
  
  
  public static Unit createDockerSecureSocketUnit() {
    String socketUnitContents = "[Unit]\n"
        + "Description=Docker Secured Socket for the API\n\n"
        + "[Socket]\n"
        + "ListenStream=2376\n"
        + "BindIPv6Only=both\n"
        + "Service=docker.service"
        + "\n\n"
        + "[Install]\n"
        + "WantedBy=sockets.target\n";
    
    return new Unit()
        .withName("docker-tls-tcp.socket")
        .withEnable(true)
        .withContents(socketUnitContents);
  }
    

  public static Unit createRPCStatdServiceUnit() {
    return new Unit()
        .withName("rpc-statd.service")
        .withEnable(true);
  }
  
  public static Unit createNFSMountUnit(String unitName, String what, String where) {
    return createMountUnit(unitName, what, where, "nfs");
  }
  
  public static Unit createMountUnit(String unitName, String what, String where, String type) {
    return new Unit()
        .withName(unitName + ".mount")
        .withEnable(true)
        .withContents("[Mount]\n"
            + String.format("What=%s\n", what)
            + String.format("Where=%s\n", where)
            + String.format("Type=%s\n\n", type)
            + "[Install]\n"
            + "WantedBy=multi-user.target\n"); 
  }
  
  public static Unit createBindMountUnit(String unitName, String what, String where) {
    return new Unit()
        .withName(unitName + ".mount")
        .withEnable(true)
        .withContents("[Mount]\n"
            + String.format("What=%s\n", what)
            + String.format("Where=%s\n", where)
            + "Type=none\n"
            + "Options=bind\n\n"
            + "[Install]\n"
            + "WantedBy=multi-user.target\n"); 
  }
  
  public static Unit createNFSServerServiceUnit() {
    return new Unit()
        .withName("nfs-server.service")
        .withEnable(true); 
  }
  
  
  public static Unit createMaskedUpdateEngineServiceUnit() {
    return new Unit()
        .withName("update-engine.service")
        .withMask(true); 
  }
  
  
  public static Unit createMaskedLocksmithdServiceUnit() {
    return new Unit()
        .withName("locksmithd.service")
        .withMask(true); 
  }
  
  
  public static List<Unit> createDockerTLSUnits() {
    List<Unit> units = new ArrayList<>();
    
    units.add(createDockerTLSServiceUnit());
    units.add(createDockerSecureSocketUnit());
    
    return units;
  }
  
  public static User createUser(String name, String ... sshAuthorizedKeys ) {
    return new User()
      .withName(name)
      .withSshAuthorizedKeys(Arrays.asList(sshAuthorizedKeys));
  }

  public static String buildDockerTLSIgnitionJSON(String caContent, String certContent, String keyContent) {
    return buildIgnitionJSON(
        createDockerTLSFiles(caContent, certContent, keyContent),
        createDockerTLSUnits()
        );
  }
  
  
  public static String buildDockerTLSNFSServerIgnitionJSON(String caContent, String certContent, String keyContent) {
    
    String exportsContent = "/media *(rw,async,no_subtree_check,root_squash,fsid=0)";
   
    List<File> files = createDockerTLSFiles(caContent, certContent, keyContent);
    files.add(createNFSExportsFile(exportsContent));
    
    List<Unit> units = createDockerTLSUnits();
    units.add(createMaskedUpdateEngineServiceUnit());
    units.add(createMaskedLocksmithdServiceUnit());
    units.add(createRPCStatdServiceUnit());
    units.add(createNFSServerServiceUnit());
    
    return buildIgnitionJSON(files, units);
  }
  
  public static String buildDockerTLSNFSMountIgnitionJSON(
    String caContent, String certContent, String keyContent,
    String mountName, String what, String where) {
       
    List<File> files = createDockerTLSFiles(caContent, certContent, keyContent);
    
    List<Unit> units = createDockerTLSUnits();
    units.add(createMaskedUpdateEngineServiceUnit());
    units.add(createMaskedLocksmithdServiceUnit());
    units.add(createRPCStatdServiceUnit());
    units.add(createNFSMountUnit(mountName, what, where));
    
    return buildIgnitionJSON(files, units);
  }
  
  public static String buildIgnitionJSON(List<File> files, List<Unit> units) {
           
    Storage storage = new Storage()
        .addFiles(files);
    
    Systemd systemd = new Systemd()
        .addUnits(units);
    
    IgnitionBuilder builder = createIgnitionBuilder()
        .withStorage(storage)
        .withSystemd(systemd);
    
    return buildIgnitionBuilderJSON(builder);
  }
  
  public static String buildIgnitionJSON(List<File> files, List<Unit> units, List<User> users) {
    
    Storage storage = new Storage()
        .addFiles(files);
    
    Systemd systemd = new Systemd()
        .addUnits(units);
    
    Passwd passwd = new Passwd()
        .addUsers(users);
    
    IgnitionBuilder builder = createIgnitionBuilder()
        .withStorage(storage)
        .withSystemd(systemd)
        .withPasswd(passwd);
    
    return buildIgnitionBuilderJSON(builder);
  }
  
  public static String buildIgnitionBuilderJSON(IgnitionBuilder builder) {
    String result = null;
    try {
      result = mapper().writeValueAsString(builder);
    } catch (Exception e) {
      LOGGER.severe(e.getMessage());;
    }
    return result;
  }
  
  public static void main(String[] args) {
    String out = "";
    
    out = buildDockerTLSNFSMountIgnitionJSON(
        "ca","certificate","key",
        "mnt", "172.17.8.1:/Volumes/HDD", "/mnt");
    
    out = buildDockerTLSNFSServerIgnitionJSON(
        "ca","certificate","key");
    LOGGER.info(out);
  }
}
