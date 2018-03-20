package org.bionimbuzbox.provision.driver.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsRequest;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.ImportKeyPairRequest;
import com.amazonaws.services.ec2.model.ImportKeyPairResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.util.SecurityGroupUtils;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53ClientBuilder;
import com.amazonaws.services.route53.model.Change;
import com.amazonaws.services.route53.model.ChangeAction;
import com.amazonaws.services.route53.model.ChangeBatch;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.binary.Base64;
import org.bionimbuzbox.helper.SSHKeyHelper;
import org.bionimbuzbox.helper.SSHKeyHelper.SSHKeyPair;
import org.bionimbuzbox.model.Datacenter;
import org.bionimbuzbox.model.Provider;
import org.bionimbuzbox.model.Server;
import org.bionimbuzbox.provision.driver.ProvisionDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProvisionDriverImpl implements ProvisionDriver {
  public static final String DEFAULT_SECURITY_GROUP = "bionimbuzbox-security-group";

  private static final String DRIVER_NAME = "aws";
  private static final int INSTANCE_POOL_SIZE = 10;
  private static final Logger LOGGER = LoggerFactory.getLogger(ProvisionDriverImpl.class);


  private AmazonEC2 client;
  private final Properties config = new Properties();

  private AWSCredentials credentials;
  private List<Datacenter> datacenters;
  private final Properties defaults = new Properties();

  public ProvisionDriverImpl() {
    try {
      this.defaults.load(this.getClass().getResourceAsStream("/aws.defaults"));
      this.config.putAll(this.defaults);

    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  @Override
  public ProvisionDriver configure(Provider provider) {
    this.config.putAll(provider.getMeta());
    
    this.credentials = new BasicAWSCredentials(
        config.getProperty("access-key-id"),
        config.getProperty("secret-access-key")
        );
    this.client = AmazonEC2ClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(this.credentials))
        .withRegion(config.getProperty("region"))
        .build();
    LOGGER.info("AmazonEC2 client loaded!");
    return this;
  }

  @Override
  public Server createServer(Server server) {
    String region = server.getCluster().getDatacenter().getMetaInfo("region", config.getProperty("region"));

    createSecurityGroup(DEFAULT_SECURITY_GROUP);
    createKey(server.getMetaInfo("hostname"), server.getMetaInfo("ssh-public-key"));

    try {
      RunInstancesRequest request = new RunInstancesRequest()
          .withImageId(server.getMetaInfo("image", config.getProperty(region + ".image")))
          .withInstanceType(server.getMetaInfo("size", config.getProperty("size")))
          .withMinCount(1)
          .withMaxCount(1)
          .withUserData(Base64.encodeBase64String(
              server.getMetaInfo("user-data", config.getProperty("user-data")).getBytes()))
          .withKeyName(server.getMetaInfo("hostname"))
          .withSecurityGroups(DEFAULT_SECURITY_GROUP);

      RunInstancesResult result = this.client.runInstances(request);
      Instance instance = result.getReservation().getInstances().get(0);

      // Block
      awaitReadiness(instance);

      server.addMetaInfo("instance-id", instance.getInstanceId());
      server.addMetaInfo("private-ip", instance.getPrivateIpAddress());
      server.addMetaInfo("public-ip", instance.getPublicIpAddress());

      createDomainRecord(
          server.getCluster().getDatacenter().getProvider().getMetaInfo("domain"), 
          "A", 
          server.getMetaInfo("hostname"), 
          server.getMetaInfo("public-ip"));

    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }

    return server;
  }

  @Override
  public List<Server> createServers(List<Server> servers) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void deleteServer(Server server) {
    try {
      deleteKey(server.getMetaInfo("hostname"));
      deleteDomainRecord(
          server.getCluster().getDatacenter().getProvider().getMetaInfo("domain"),
          "A",
          server.getMetaInfo("hostname"),
          server.getMetaInfo("public-ip"));

      TerminateInstancesRequest request = new TerminateInstancesRequest()
          .withInstanceIds(server.getMetaInfo("instance-id"));

      this.client.terminateInstances(request);

      LOGGER.info("{} instance with id {} terminated!", server.getMetaInfo("hostname"), server.getMetaInfo("instance-id"));

    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  @Override
  public void deleteServers(List<Server> server) {
    //TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest(ids);

  }
  
  public List<Datacenter> getDatacenters() {
    if (this.datacenters != null) {
      return this.datacenters;
    }

    this.datacenters = new ArrayList<>();
    for (Regions region : Regions.values()) {
      Datacenter datacenter = new Datacenter();
      datacenter.setName(region.getName());
      this.datacenters.add(datacenter);
    }
    return this.datacenters;
  }
  
  @Override
  public String getDriverName() {
    return DRIVER_NAME;
  }

  private KeyPairInfo getKey(String keyName) {
    DescribeKeyPairsRequest request = new DescribeKeyPairsRequest()
        .withKeyNames(keyName);
    try {
      DescribeKeyPairsResult result = this.client.describeKeyPairs(request);
      for(KeyPairInfo kpInfo : result.getKeyPairs()) {
        LOGGER.info("recovering {} key with {} fingerprint.", keyName, kpInfo.getKeyFingerprint());
        return kpInfo;
      }
    } catch (Exception e) {
      LOGGER.warn("{} key not found.", keyName);
    }
    return null;  
  }


  private boolean keyExists(String keyName) {
    DescribeKeyPairsRequest request = new DescribeKeyPairsRequest()
        .withKeyNames(keyName);

    DescribeKeyPairsResult result = this.client.describeKeyPairs(request);

    return result.getKeyPairs().size() > 0 ? true : false;  
  }

  private void authorizeICMPTraffic(String groupName) {
    authorizePortTraffic(groupName, -1, -1, "icmp");
  }

  private void authorizeInternalTraffic(String groupName) {
    AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest = new
        AuthorizeSecurityGroupIngressRequest()
        .withGroupName(groupName)
        .withSourceSecurityGroupName(groupName);
    try {
      this.client.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);
      LOGGER.info("Internal traffic granted in security group {}", groupName);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }    
  }

  private void authorizePortTraffic(String groupName, int fromPort, int toPort, String ipProtocol) {
    String cidrIp = "0.0.0.0/0";
    AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest = new
        AuthorizeSecurityGroupIngressRequest()
        .withGroupName(groupName)
        .withCidrIp(cidrIp)
        .withFromPort(fromPort)
        .withToPort(toPort)
        .withIpProtocol(ipProtocol);

    try {
      this.client.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);
      LOGGER.info("{} traffic to {}-{} ports granted to {} in security group {}", 
          ipProtocol, fromPort, toPort, cidrIp, groupName);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }  
  }

  private void authorizePortTraffic(String groupName, int port, String ipProtocol) {
    authorizePortTraffic(groupName, port, port, ipProtocol);
  }

  private void awaitReadiness(Instance instance) {
    InstanceStateName desiredStatus = InstanceStateName.Running;
    InstanceStateName currentStatus = InstanceStateName.Pending;

    int ATTEMPTS = 10;
    int INTERVAL = 3000;

    try {
      do {
        ATTEMPTS--;

        final DescribeInstancesRequest request = new DescribeInstancesRequest()
            .withInstanceIds(instance.getInstanceId());

        final DescribeInstancesResult result = this.client.describeInstances(request);

        Instance i = result.getReservations().get(0).getInstances().get(0);
        currentStatus = InstanceStateName.fromValue(i.getState().getName());

        LOGGER.info("Instance {}, Status: {}, Attempts Left: {}", 
            i.getInstanceId(),
            currentStatus.name(),
            ATTEMPTS);

        if (desiredStatus.equals(currentStatus) && i.getPrivateIpAddress() != null && i.getPublicIpAddress() != null) {
          BeanUtils.copyProperties(instance, i);
          ATTEMPTS = 0;
        } 

        if (ATTEMPTS > 0) {
          Thread.sleep(INTERVAL);
        }

      } while (ATTEMPTS > 0 );
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void changeDomainRecord(ChangeAction action, String domainName, String type, String name, String data) {
    try{
      String hostedZoneId = "Z3NGPMATU2BKD2";

      final ResourceRecord resourceRecord = new ResourceRecord(data);
      final ResourceRecordSet resourceRecordSet = new ResourceRecordSet()
          .withResourceRecords(resourceRecord)
          .withName(name + "." + domainName)
          .withType(type)
          .withTTL(1800L);

      final Change change = new Change()
          .withAction(action)
          .withResourceRecordSet(resourceRecordSet);

      final ChangeBatch changeBatch = new ChangeBatch()
          .withChanges(change);

      final ChangeResourceRecordSetsRequest changeResourceRecordSetsRequest = new ChangeResourceRecordSetsRequest()
          .withHostedZoneId(hostedZoneId)
          .withChangeBatch(changeBatch);

      LOGGER.info("ABuilding Route53 Client within Region: {}", config.getProperty("region"));
      
      AmazonRoute53 route53client = AmazonRoute53ClientBuilder.standard()
          .withCredentials(new AWSStaticCredentialsProvider(this.credentials))
          .withRegion(config.getProperty("region"))
          .build();

      route53client.changeResourceRecordSets(changeResourceRecordSetsRequest);

      LOGGER.info("Action {} on {} domain for {} {} {}", action, domainName, name, type, data);
    }
    catch(Exception e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private void createDomainRecord(String domainName, String type, String name, String data) {
    changeDomainRecord(ChangeAction.UPSERT, domainName, type, name, data);
  }

  private KeyPairInfo createKey(String keyName, String publicKey) { 
    KeyPairInfo keyPairInfo = getKey(keyName);

    if (keyPairInfo != null) {
      LOGGER.info("{} key already exists.", keyName);
      return keyPairInfo;
    }

    ImportKeyPairRequest importKeyPairRequest = new ImportKeyPairRequest()
        .withPublicKeyMaterial(publicKey)
        .withKeyName(keyName);

    try {  
      ImportKeyPairResult result = this.client.importKeyPair(importKeyPairRequest);  
      keyPairInfo = new KeyPairInfo()
          .withKeyFingerprint(result.getKeyFingerprint())
          .withKeyName(keyName);
      LOGGER.info("{} key with {} fingerprint created.", keyName, keyPairInfo.getKeyFingerprint());
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    return keyPairInfo;
  }

  private void createSecurityGroup(String groupName) {
    this.createSecurityGroup(groupName, groupName);
  }

  private void createSecurityGroup(String groupName, String description) {
    this.createSecurityGroup(groupName, description, null);
  }

  private void createSecurityGroup(String groupName, String description, String vpcId) {
    if (doesSecurityGroupExist(groupName)) {
      LOGGER.warn("{} security group alredy exists in region {}.", 
          groupName, config.getProperty("region"));
      return;
    }

    CreateSecurityGroupRequest createSecurityGroupRequest = new
        CreateSecurityGroupRequest()
        .withGroupName(groupName)
        .withDescription(description)
        .withVpcId(vpcId);

    //CreateSecurityGroupResult createSecurityGroupResult  =
    this.client.createSecurityGroup(createSecurityGroupRequest);

    LOGGER.info("{} security group created!", groupName);

    authorizeInternalTraffic(DEFAULT_SECURITY_GROUP);
    authorizePortTraffic(DEFAULT_SECURITY_GROUP, 22, "tcp");
    authorizePortTraffic(DEFAULT_SECURITY_GROUP, 2376, "tcp");
    authorizePortTraffic(DEFAULT_SECURITY_GROUP, 80, "tcp");
    authorizePortTraffic(DEFAULT_SECURITY_GROUP, 8080, "tcp");
    authorizePortTraffic(DEFAULT_SECURITY_GROUP, 443, "tcp");
    authorizeICMPTraffic(DEFAULT_SECURITY_GROUP);
  }

  private void deleteDomainRecord(String domainName, String type, String name, String data) {
    changeDomainRecord(ChangeAction.DELETE, domainName, type, name, data);
  }

  private void deleteKey(String keyName) {
    DeleteKeyPairRequest request = new DeleteKeyPairRequest()
        .withKeyName(keyName);

    this.client.deleteKeyPair(request);
    LOGGER.info("{} key deleted.", keyName);
  }

  private boolean doesSecurityGroupExist(String groupName) {
    return SecurityGroupUtils.doesSecurityGroupExist(this.client, groupName);
  }


  private void instanceInfoPooling(Instance instance) throws InterruptedException, ExecutionException {
    List<Instance> instances = new ArrayList<Instance>();
    instances.add(instance);
    instanceInfoPooling(instances);
  }

  private void instanceInfoPooling(List<Instance> instances) throws InterruptedException, ExecutionException {
    ExecutorService instancePool = Executors.newFixedThreadPool(INSTANCE_POOL_SIZE);
    for (Instance i : instances) {
      instancePool.execute(new InstanceInfoRunner(this.client, i));
    }

    LOGGER.info("before shutdown ...");
    instancePool.shutdown();

    LOGGER.info("before awaitTermination ...");
    instancePool.awaitTermination(30, TimeUnit.SECONDS);
    LOGGER.info("after awaitTermination ...");
  }

  public static void main(String[] args) throws Exception {
    Provider provider = new Provider();
    provider.setId("aws");
    provider.setType("aws");
    provider.addMetaInfo("access-key-id", "");
    provider.addMetaInfo("secret-access-key", "");
    provider.addMetaInfo("region", "");

    ProvisionDriverImpl driver = (ProvisionDriverImpl) ProvisionDriver.initializeDriver(provider);

    test3(driver);
  }

  public static void test1(ProvisionDriverImpl driver) {
    driver.createSecurityGroup(DEFAULT_SECURITY_GROUP);
    driver.authorizeInternalTraffic(DEFAULT_SECURITY_GROUP);
    driver.authorizePortTraffic(DEFAULT_SECURITY_GROUP, 22, "tcp");
    driver.authorizePortTraffic(DEFAULT_SECURITY_GROUP, 2376, "tcp");
    driver.authorizePortTraffic(DEFAULT_SECURITY_GROUP, 80, "tcp");
    driver.authorizePortTraffic(DEFAULT_SECURITY_GROUP, 8080, "tcp");
    driver.authorizePortTraffic(DEFAULT_SECURITY_GROUP, 443, "tcp");
    driver.authorizeICMPTraffic(DEFAULT_SECURITY_GROUP);
  }

  public static void test2(ProvisionDriverImpl driver) {
    String keyName = "server-1.swarm.us-east-1.aws";
    SSHKeyPair sshKeyPair = SSHKeyHelper.generateSSHKeyPair(keyName);

    //driver.getKey(keyName);
    driver.createKey(keyName, sshKeyPair.getPublicKey());
    //driver.keyExists(keyName);
    //driver.getKey(keyName);
    driver.deleteKey(keyName);
  }

  public static void test3(ProvisionDriverImpl driver) {
    driver.createDomainRecord(
        "aws.io.etc.br",
        "A",
        "server-1.swarm.us-east-1",
        "200.147.67.142"
        );
    driver.deleteDomainRecord(
        "aws.io.etc.br",
        "A",
        "server-1.swarm.us-east-1",
        "200.147.67.142"
        );

  }

  @Override
  public boolean isValid() {
    return true;
  }

}
