package org.bionimbuzbox.provision.api;

import static org.bionimbuzbox.ca.CA.dn;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.bionimbuzbox.ca.CA;
import org.bionimbuzbox.ca.CertificateWithPrivateKey;
import org.bionimbuzbox.ca.CsrWithPrivateKey;
import org.bionimbuzbox.ca.RootCertificate;
import org.bionimbuzbox.ca.ext.ExtKeyUsageExtension;
import org.bionimbuzbox.ca.ext.KeyUsageExtension;
import org.bionimbuzbox.ca.ext.KeyUsageExtension.KeyUsage;
import org.bionimbuzbox.dao.ClusterDAO;
import org.bionimbuzbox.dao.DAOFactory;
import org.bionimbuzbox.dao.ServerDAO;
import org.bionimbuzbox.helper.CoreOSIgnitionHelper;
import org.bionimbuzbox.helper.SSHKeyHelper;
import org.bionimbuzbox.helper.SSHKeyHelper.SSHKeyPair;
import org.bionimbuzbox.model.Cluster;
import org.bionimbuzbox.model.Server;
import org.bionimbuzbox.model.ignition.File;
import org.bionimbuzbox.model.ignition.Unit;
import org.bionimbuzbox.model.ignition.User;
import org.bionimbuzbox.orchestration.driver.OrchestrationDriver;
import org.bionimbuzbox.provision.driver.ProvisionDriver;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class ServerAPI {
	
	@Inject
	private DAOFactory factory;
	private static Logger LOGGER = LoggerFactory.getLogger(ServerAPI.class);

	@GET
	@Path("servers")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Server> listAll() {
		ServerDAO dao = factory.getServerDAO();
		return dao.getAll();
	}
	
	@GET
	@Path("server/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Server get(@PathParam("id") String id) {
		ServerDAO dao = factory.getServerDAO();
		return dao.get(id);
	}
	
	@POST
	@Path("server/{id}/{action}")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean doAction(@PathParam("id") String id, @PathParam("action") String action) {
		return false;
	}
	
	@POST
	@Path("server")
	@Produces(MediaType.APPLICATION_JSON)
	public Server save(Server server) throws Exception {
	  ClusterDAO clusterDAO = factory.getClusterDAO();
	  ServerDAO serverDAO = factory.getServerDAO();
	  
	  Cluster cluster = clusterDAO.get(server.getCluster().getId(), true);
	  cluster.addServer(server);
	  server.setCluster(cluster);
    
	  server.addMetaInfo("hostname", 
	      String.format("%s.%s.%s.%s", 
	          server.getName(), 
	          cluster.getName(),
	          cluster.getDatacenter().getName(),
	          cluster.getDatacenter().getProvider().getName())
	      );
	  server.addMetaInfo("fqdn", 
  	  String.format("%s.%s", 
          server.getMetaInfo("hostname"),
          server.getCluster().getDatacenter().getProvider().getMetaInfo("domain"))
  	);
	  
	  addSSHKeys(server);
	  addDockerCertificates(server);
	  addCoreOSIgnitionUserData(server);
	  
	  ProvisionDriver provision = ProvisionDriver.initializeDriver(cluster.getDatacenter().getProvider());
	  provision.createServer(server);
	  
	  LOGGER.info(String.format("Server %s provisionado!", server.getName()));
	  
	  serverDAO.save(server);
	  LOGGER.info("{} server's config saved!", server.getMetaInfo("fqdn"));
	  
	  OrchestrationDriver orchestration = OrchestrationDriver.initializeDriver(cluster);
    orchestration.join(server);
    LOGGER.info(
        String.format("Server %s added to Cluster %s!", 
            server.getName(), cluster.getName()));
	  
	  serverDAO.save(server);
	  LOGGER.info("{} server's config saved!", server.getMetaInfo("fqdn"));
	  return server;
	}
	
	@DELETE
	@Path("server/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public void remove(@PathParam("id") String id) throws Exception {
	  ServerDAO dao = factory.getServerDAO();
	  Server server = dao.get(id, true);
	  
	  ProvisionDriver provision = ProvisionDriver.initializeDriver(server.getCluster().getDatacenter().getProvider());
    provision.deleteServer(server);
	  
		dao.remove(id);
	}
	
	private void addSSHKeys(Server server) {
	  SSHKeyPair sshKeyPair = SSHKeyHelper.generateSSHKeyPair(server.getMetaInfo("hostname"));
    
    server.addMetaInfo("ssh-private-key", sshKeyPair.getPrivateKey());
    server.addMetaInfo("ssh-public-key", sshKeyPair.getPublicKey());
    server.addMetaInfo("ssh-fingerprint", sshKeyPair.getFingerPrint());
	}
	
	private void addDockerCertificates(Server server) {
	  server.addMetaInfo("ssl-ca-cert", server.getCluster().getMetaInfo("ssl-ca-cert") );
	  
	  LOGGER.debug("CA Cert:\n" + server.getMetaInfo("ssl-ca-cert") );
	  
	  RootCertificate rootCertificate = CA.loadRootCertificate(
	      server.getCluster().getMetaInfo("ssl-ca-cert"), 
	      server.getCluster().getMetaInfo("ssl-ca-key"));
	  
	  buildClientCertificate(server, rootCertificate);
	  buildServerCertificate(server, rootCertificate);
	  
	  LOGGER.info("DockerCertificates loaded!");
	}
	
	private void buildClientCertificate(Server server, RootCertificate rootCertificate) {
	  CsrWithPrivateKey clientCSR = CA.createCsr().generateRequest(CA.dn("CN=BioNimbuzBoxClient"));
    CertificateWithPrivateKey clientCertificate = rootCertificate.signCsr(clientCSR)
        .setSerialNumber(CA.generateRandomSerialNumber())
        .setNotBefore(ZonedDateTime.now())
        .validDuringYears(1)
        .addExtension(Extension.basicConstraints, true, new BasicConstraints(false))
        .addExtension(KeyUsageExtension.create(true, KeyUsage.DIGITAL_SIGNATURE, KeyUsage.KEY_ENCIPHERMENT, KeyUsage.KEY_AGREEMENT))
        .addExtension(ExtKeyUsageExtension.create(KeyPurposeId.id_kp_clientAuth))
        .sign()
        .attachPrivateKey(clientCSR.getPrivateKey());
    
    server.addMetaInfo("ssl-client-cert", clientCertificate.print());
    server.addMetaInfo("ssl-client-key", clientCertificate.printKey());
    
    LOGGER.debug("Client Cert:\n" + server.getMetaInfo("ssl-client-cert"));
    LOGGER.debug("Client Key:\n" + server.getMetaInfo("ssl-client-key"));
	}
	
	private void buildServerCertificate(Server server, RootCertificate rootCertificate) {
	  List<GeneralName> names = new ArrayList<>();
    GeneralName g1 = new GeneralName(GeneralName.dNSName, server.getName());
    names.add(g1);
    
    GeneralName g2 = new GeneralName(GeneralName.dNSName, server.getMetaInfo("hostname"));
    names.add(g2);
    
    GeneralName g3 = new GeneralName(GeneralName.dNSName, server.getMetaInfo("fqdn"));
    names.add(g3);
    
    GeneralName g4 = new GeneralName(GeneralName.iPAddress, "127.0.0.1");
    names.add(g4);
    
    //GeneralName g5 = new GeneralName(GeneralName.iPAddress, "192.168.99.101");
    //names.add(g5);
    
    GeneralNames subjectAltName = new GeneralNames(names.toArray(new GeneralName[0]));
	  
	  CsrWithPrivateKey serverCSR = CA.createCsr().generateRequest(dn("CN=*"));
    CertificateWithPrivateKey serverCertificate = rootCertificate.signCsr(serverCSR)
        .setSerialNumber(CA.generateRandomSerialNumber())
        .setNotBefore(ZonedDateTime.now())
        .validDuringYears(1)
        .addExtension(Extension.basicConstraints, true, new BasicConstraints(false))
        .addExtension(KeyUsageExtension.create(true, KeyUsage.DIGITAL_SIGNATURE, KeyUsage.KEY_ENCIPHERMENT, KeyUsage.KEY_AGREEMENT))
        .addExtension(ExtKeyUsageExtension.create(KeyPurposeId.id_kp_serverAuth))
        .addExtension(Extension.subjectAlternativeName, false, subjectAltName)
        .sign()
        .attachPrivateKey(serverCSR.getPrivateKey());
     
    server.addMetaInfo("ssl-server-cert", serverCertificate.print());
    server.addMetaInfo("ssl-server-key", serverCertificate.printKey());
    
    LOGGER.debug("Server Cert:\n" + server.getMetaInfo("ssl-server-cert"));
    LOGGER.debug("Server Key:\n" + server.getMetaInfo("ssl-server-key"));
	}
	
	private void addCoreOSIgnitionUserData(Server server) {
	  LOGGER.info("CoresOS Ignition: building user-data ...");
	  
	  String ca = server.getMetaInfo("ssl-ca-cert");
	  String cert = server.getMetaInfo("ssl-server-cert");
	  String key = server.getMetaInfo("ssl-server-key");
	  String sshAuthorizedKeys = server.getMetaInfo("ssh-public-key");
	  
	  String exportsContent = "/media *(rw,async,no_subtree_check,no_root_squash,fsid=0)";
	  
	  List<User> users = new ArrayList<>();
	  users.add(CoreOSIgnitionHelper.createUser("core", sshAuthorizedKeys));
	  
    List<File> files = CoreOSIgnitionHelper.createDockerTLSFiles(ca, cert, key);
    
    List<Unit> units = CoreOSIgnitionHelper.createDockerTLSUnits();
    units.add(CoreOSIgnitionHelper.createMaskedUpdateEngineServiceUnit());
    units.add(CoreOSIgnitionHelper.createMaskedLocksmithdServiceUnit());
    units.add(CoreOSIgnitionHelper.createRPCStatdServiceUnit());
    
	  if (server.getCluster().getServers().size() == 1) {
	  
	    LOGGER.info("CoresOS Ignition: adding NFS Server config ...");
	    files.add(CoreOSIgnitionHelper.createNFSExportsFile(exportsContent));
	    units.add(CoreOSIgnitionHelper.createNFSServerServiceUnit());
	    units.add(CoreOSIgnitionHelper.createBindMountUnit("mnt", "/media", "/mnt"));
	  
	  } else {
	    LOGGER.info("CoresOS Ignition: adding NFS Mount config ...");
	    
	    for (Server s : server.getCluster().getServers()) {
	      if ("manager".equals(s.getMetaInfo("role")) ) {
	        
	        String what = String.format("%s:/media", s.getMetaInfo("private-ip"));
	        String where = "/mnt";
	        
	        units.add(CoreOSIgnitionHelper.createNFSMountUnit("mnt", what,  where));
	        LOGGER.info("CoresOS Ignition: NFS Mount {} added to {}", what, where);
	      }
	    }
	  }
	    
	  server.addMetaInfo("user-data", CoreOSIgnitionHelper.buildIgnitionJSON(files, units, users));
	}
	
	
}
