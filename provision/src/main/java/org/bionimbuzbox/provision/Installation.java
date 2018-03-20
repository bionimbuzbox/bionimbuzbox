package org.bionimbuzbox.provision;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bionimbuzbox.helper.FileHelper;
import org.bionimbuzbox.helper.PropertiesHelper;
import org.bionimbuzbox.helper.SSHKeyHelper;
import org.bionimbuzbox.helper.SSHKeyHelper.SSHKeyPair;
import org.bionimbuzbox.model.Cluster;
import org.bionimbuzbox.model.Datacenter;
import org.bionimbuzbox.model.GenericModel;
import org.bionimbuzbox.model.Key;
import org.bionimbuzbox.model.Provider;
import org.bionimbuzbox.model.Server;
import org.bionimbuzbox.orchestration.driver.OrchestrationDriver;
import org.bionimbuzbox.provision.driver.ProvisionDriver;

public class Installation {
	
	private static final Logger LOGGER = Logger.getLogger(Installation.class.getName());
	
	private final Properties config = new Properties();
	
	public Installation() throws Exception {
		this.config.load(getClass().getResourceAsStream("/installation-default.properties"));
	}
	
	public Installation(Properties config) throws Exception {
		this();
		this.config.putAll(config);
	}
	
	public void run() throws Exception {
		ProvisionDriver driver = this.buildProvisionDriver();
		
		Datacenter datacenter = buildDatacenter();
		
		Cluster cluster = buildCluster(datacenter);
		
		Key key = buildKey();
		//driver.createKey(key);
		
		int count = Integer.valueOf(config.getProperty("provision.cluster.size"));
		
		List<Server> servers = this.buildServers(cluster, key, count);
		driver.createServers(servers);
		
		cluster.setServers(servers);
		//driver.listServers();
		
		//LOGGER.info("Datacenters: " + driver.listDatacenters());
		//LOGGER.info("Images: " + driver.listImages());
		
		configureClusterOrchestration(cluster);
	}
	
	private void configureClusterOrchestration(Cluster cluster) throws Exception {		
		OrchestrationDriver driver = OrchestrationDriver
		    .initializeDriver(cluster);
	}
	
	private ProvisionDriver buildProvisionDriver() throws Exception {
		String driverName = config.getProperty("provision.driver");
		
		Properties driverConfig = PropertiesHelper.matchingSubset(
				config, 
				"provision." + driverName, 
				false);
		Provider provider = new Provider();
		provider.setType(driverName);
		provider.setMeta((Map)driverConfig);
		
		return ProvisionDriver.initializeDriver(provider);
	}
	
	private Datacenter buildDatacenter() {
		Datacenter datacenter = this.build(Datacenter.class);
		datacenter.setName(config.getProperty("provision.datacenter.name"));
		return datacenter;
	}
	
	private Cluster buildCluster(Datacenter datacenter) {
		Cluster cluster = this.build(Cluster.class);
		cluster.setName(config.getProperty("provision.cluster.name"));
		cluster.setType(config.getProperty("provision.cluster.type"));
		cluster.setDatacenter(datacenter);
		return cluster;
	}
	
	private Key buildKey() {
		Key key; 
		if (Boolean.valueOf(config.getProperty("provision.key.auto-generate"))) {
			key = new Key();
			try {
				
				SSHKeyPair sshKeyPair = SSHKeyHelper.generateSSHKeyPair();
				
				key.addMetaInfo("private-key", sshKeyPair.getPrivateKey());
				key.addMetaInfo("public-key", sshKeyPair.getPublicKey());
				key.addMetaInfo("fingerprint", sshKeyPair.getFingerPrint());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			key = this.build(Key.class);
		}
		key.setName(config.getProperty("provision.key.name"));
		
		return key;
	}
	
	private List<Server> buildServers(Cluster cluster, Key key, int count) throws Exception {
	  String discoveryURL = this.generateDiscoveryURL(count);
		List<Server> servers = new ArrayList<>(count);
		for(int i=1; i<=count; i++){
		  Server server = this.build(Server.class);
	    server.setName(String.format("%s-%d", cluster.getName(), i));
	    server.setCluster(cluster);
	    server.setKey(key);
	    server.addMetaInfo("user-data", 
	        server.getMetaInfo("user-data").replaceAll("\\$discovery_url", discoveryURL)
	    );
			servers.add(server);
		}
		
		return servers;
	} 
	
	private <T extends GenericModel> T build(Class<T> model) {
		String modelName = model.getSimpleName().toLowerCase();
		
		Properties metaConfig = PropertiesHelper.matchingSubset(
				config, 
				String.format("provision.%s.meta", modelName), 
				false);
		Map<String, String> meta = this.buildMeta(metaConfig);
		
		T builded = null;
		try {
			builded = model.newInstance();
			builded.setMeta(meta);
		} catch (InstantiationException | IllegalAccessException e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		
		return builded;
	}
	
	private Map<String, String> buildMeta(Properties metaProperties) {
		Map<String, String> meta = new HashMap<>();
		
		Set<String> propertyNames = metaProperties.stringPropertyNames();
		
		for (String propertyName : propertyNames) {
			String propertyValue = metaProperties.getProperty(propertyName);
			meta.put(propertyName, propertyValue);
			
			if (propertyName.endsWith("-file")) {
				propertyName = propertyName.replaceAll("-file", "");
				try {
					propertyValue = FileHelper.readFile(propertyValue);
				} catch (Exception e) {
					propertyValue = null;
					LOGGER.severe(e.getMessage());
				}
			}
			
			meta.put(propertyName, propertyValue);
		}
		
		return meta;
	}
	
	private String generateDiscoveryURL(int size) throws Exception {
		String url = String.format("https://discovery.etcd.io/new?size=%d", size);

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);

		HttpResponse response = client.execute(request);
	
		return EntityUtils.toString(response.getEntity());
	}
	
	private static Options getCommandLineOptions() {
		Options options = new Options();
		options.addOption("c", "config", true, "Config file");
		return options;
	}

	public static void main(String[] args) {
		Properties config = new Properties();
	    CommandLineParser parser = new DefaultParser();
	    
	    try {
	    	InputStream inStream;
	        // parse the command line arguments
	        CommandLine line = parser.parse( Installation.getCommandLineOptions(), args );
	        if (line.hasOption("config")) {
	        	LOGGER.info(new File(".").getAbsolutePath());
	        	inStream = new FileInputStream(line.getOptionValue("config"));
	        } else {
	        	inStream = Installation.class.getResourceAsStream("/installation-default.properties");
	        }
    		config.load(inStream);
    		
    		new Installation(config).run();
    		
	    } catch( ParseException ex ) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + ex.getMessage() );
	    } catch( FileNotFoundException ex ) {
	        // oops, something went wrong
	        System.err.println( "File not found.  Reason: " + ex.getMessage() );
	    } catch( Exception ex ) {
	        ex.printStackTrace();
	        System.err.println( "Unhandled exception.  Reason: " + ex.getMessage() );
	    }
	}
}
