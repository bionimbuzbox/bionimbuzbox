package org.bionimbuzbox.provision.driver.digitalocean;

import com.myjeeva.digitalocean.DigitalOcean;
import com.myjeeva.digitalocean.common.ActionType;
import com.myjeeva.digitalocean.common.DropletStatus;
import com.myjeeva.digitalocean.impl.DigitalOceanClient;
import com.myjeeva.digitalocean.pojo.DomainRecord;
import com.myjeeva.digitalocean.pojo.DomainRecords;
import com.myjeeva.digitalocean.pojo.Droplet;
import com.myjeeva.digitalocean.pojo.Droplets;
import com.myjeeva.digitalocean.pojo.Image;
import com.myjeeva.digitalocean.pojo.Images;
import com.myjeeva.digitalocean.pojo.Key;
import com.myjeeva.digitalocean.pojo.Network;
import com.myjeeva.digitalocean.pojo.Region;
import com.myjeeva.digitalocean.pojo.Regions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.apache.commons.beanutils.BeanUtils;
import org.bionimbuzbox.model.Provider;
import org.bionimbuzbox.model.Server;
import org.bionimbuzbox.provision.driver.ProvisionDriver;

public class ProvisionDriverImpl implements ProvisionDriver {

	private static final Logger LOGGER = Logger.getLogger(ProvisionDriverImpl.class.getName());
	
	private static final String DRIVER_NAME = "digitalocean";
	private static final int DROPLET_POOL_SIZE = 10;
	
	private final Properties config = new Properties();
	private final Properties defaults = new Properties();
	private DigitalOcean client;
	
	private final List<String> datacentersList = new ArrayList<>();
	private final List<String> imagesList = new ArrayList<>();
	
	public ProvisionDriverImpl() {
	  try {
	    this.defaults.load(this.getClass().getResourceAsStream("/digitalocean.defaults"));
	    this.config.putAll(this.defaults);
	  } catch (Exception e) {
      LOGGER.severe(e.getMessage());
    }
	}	
	
	public List<String> listDatacenters() {
		if (this.datacentersList.size() != 0) {
			return this.datacentersList;
		}
		
		try {
			Regions regions = this.client.getAvailableRegions(0);
			for (Iterator<Region> iterator = regions.getRegions().iterator(); iterator.hasNext();) {
				this.datacentersList.add(iterator.next().getSlug());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return this.datacentersList;
	}
	
	public List<String> listImages() {
		if (this.imagesList.size() != 0) {
			return this.imagesList;
		}
	
		try {
			Images images = this.client.getAvailableImages(1, 200, ActionType.DISTRIBUTION);
			for (Iterator<Image> iterator = images.getImages().iterator(); iterator.hasNext();) {
				imagesList.add(iterator.next().getSlug());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return imagesList;
	}
	
	 private Key createKey(String name, String publicKey) {
	   Key key = new Key(name, publicKey);
     try {
       key = this.client.createKey(key);

     } catch (Exception e) {
       LOGGER.severe(e.getMessage());
     }
     return key;
	 }
	 
	 private void deleteKey(String fingerprint) {
     try {
       this.client.deleteKey(fingerprint);
     } catch (Exception e) {
       LOGGER.severe(e.getMessage());
     }
   }
	 
	 private DomainRecord createDomainRecord(String domainName, String type, String name, String data) {
	   try {
	     DomainRecord record = new DomainRecord(name, data, type);
	     return this.client.createDomainRecord(domainName, record);
	   } catch (Exception e) {
	     LOGGER.severe(e.getMessage());
	   }
	   return null;
	 }
	 
	 private int getDomainRecordId(String domainName, String type, String name, String data) {
	   try {
       DomainRecords records = this.client.getDomainRecords(domainName, 1, 500);
       for (DomainRecord record : records.getDomainRecords()) {
         if(type.equals(record.getType()) && 
             data.equals(record.getData()) &&
             name.equals(record.getName())) {
           return record.getId();
         }
       }
     } catch (Exception e) {
       LOGGER.severe(e.getMessage());
     }
	   return 0;
	 }
	 
	 private void deleteDomainRecord(String domainName, String type, String name, String data) {
     try {
       int recordId = getDomainRecordId(domainName, type, name, data);
       this.client.deleteDomainRecord(domainName, recordId);
     } catch (Exception e) {
       LOGGER.severe(e.getMessage());
     }
   }
	 
	 public void listServers() {
	   try {
	     Droplets droplets = this.client.getAvailableDroplets(1, 100);
	     LOGGER.info(droplets.toString());
	   } catch (Exception e) {
      e.printStackTrace();
    }
	 }

	
	@Override
	public String getDriverName() {
		return DRIVER_NAME;
	}

	
	@Override
	public ProvisionDriver configure(Provider provider) {
	  this.config.putAll(provider.getMeta());
	  this.client = new DigitalOceanClient("v2", config.getProperty("auth-token"));
    return this;
	}
	
	private void dropletInfoPooling(Droplet droplet) throws InterruptedException, ExecutionException {
	  List<Droplet> droplets = new ArrayList<Droplet>();
	  droplets.add(droplet);
	  dropletInfoPooling(droplets);
	}
	
	private void dropletInfoPooling(List<Droplet> droplets) throws InterruptedException, ExecutionException {
	  ExecutorService dropletPool = Executors.newFixedThreadPool(DROPLET_POOL_SIZE);
    for (Droplet d : droplets) {
      dropletPool.execute(new DropletInfoRunner(this.client, d));
    }
    
    LOGGER.info("before shutdown ...");
    dropletPool.shutdown();
    
    LOGGER.info("before awaitTermination ...");
    dropletPool.awaitTermination(30, TimeUnit.SECONDS);
    LOGGER.info("after awaitTermination ...");
	}

	private Droplet createDropletFromServer(Server server) {
    Droplet droplet = new Droplet();
    
    droplet.setRegion(
        new Region(
            server.getCluster().getDatacenter().getMetaInfo("region", config.getProperty("region"))
        )
    );
    
    droplet.setImage(
        new Image(
            server.getMetaInfo("image", config.getProperty("image"))
        )
    );
    
    droplet.setSize(server.getMetaInfo("size", config.getProperty("size")));
    droplet.setEnableBackup(Boolean.valueOf(server.getMetaInfo("enable-backup", config.getProperty("enable-backup"))));
    droplet.setEnableIpv6(Boolean.valueOf(server.getMetaInfo("enable-ipv6", config.getProperty("enable-ipv6"))));
    droplet.setEnablePrivateNetworking(Boolean.valueOf(server.getMetaInfo("enable-private-networking", config.getProperty("enable-private-networking"))));
    droplet.setUserData(server.getMetaInfo("user-data", config.getProperty("user-data")));
    
    return droplet;
	}
	
	@Override
	public List<Server> createServers(List<Server> servers) {
		
	  List<String> dropletNames = new ArrayList<>();
    for (Server server : servers) {
      dropletNames.add(server.getName());
    }
	  
		Droplet dropletRequest = createDropletFromServer(servers.get(0));
		dropletRequest.setNames(dropletNames);
		
		try { 
			Droplets dropletsResponse = this.client.createDroplets(dropletRequest);
			List<Droplet> droplets = dropletsResponse.getDroplets();
			
			// Block
			this.dropletInfoPooling(droplets);
			
			for(Server server : servers) {
			  for (Droplet droplet : droplets) {
			    if (server.getName().equals(droplet.getName())) {
			      this.mergeDropletToServer(droplet, server);
			    }
        }
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return servers;
	}
	
	@Override
	public Server createServer(Server server) {
    Droplet dropletRequest = createDropletFromServer(server);
    dropletRequest.setName(server.getMetaInfo("fqdn"));
    
    List<String> tags = new ArrayList<>();
    tags.add("bionimbuzbox");
    tags.add(server.getMetaInfo("hostname").replaceAll("\\.", "_"));
    dropletRequest.setTags(tags);
    
    List<Key> keys = new ArrayList<>();
    Key key = createKey(server.getMetaInfo("hostname"), server.getMetaInfo("ssh-public-key"));
    keys.add(key);
    dropletRequest.setKeys(keys);
    
    try { 
      Droplet droplet = this.client.createDroplet(dropletRequest);
      
      // Block
      awaitReadiness(droplet);
      
      mergeDropletToServer(droplet, server);
      
      createDomainRecord(
          server.getCluster().getDatacenter().getProvider().getMetaInfo("domain"),
          "A",
          server.getMetaInfo("hostname"), 
          server.getMetaInfo("public-ip"));
      
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return server;
  }
	
	private void awaitReadiness(Droplet droplet) {
	  DropletStatus desiredStatus = DropletStatus.ACTIVE;
    DropletStatus currentStatus = null;
    int ATTEMPTS = 10;
    int INTERVAL = 3000;
    try {
      do {
        ATTEMPTS--;
        
        Droplet dropletInfo = this.client.getDropletInfo(droplet.getId());
        currentStatus = dropletInfo.getStatus();
        
        LOGGER.info(
            String.format("Droplet %s, Status: %s, Attempts Left: %d", 
                dropletInfo.getName(),
                dropletInfo.getStatus().toString(),
                ATTEMPTS));
        
        if (desiredStatus.equals(currentStatus)) {
          BeanUtils.copyProperties(droplet, dropletInfo);
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

	@Override
	public void deleteServers(List<Server> servers) {
	  for (Server server : servers) {
	    try {
	      deleteServer(server);
	    } catch (Exception e) {
        LOGGER.severe(e.getMessage());
      }
	  }
	} 	
	
	@Override
  public void deleteServer(Server server) {
    try {
      deleteKey(server.getMetaInfo("ssh-fingerprint"));
      deleteDomainRecord(
          server.getCluster().getDatacenter().getProvider().getMetaInfo("domain"),
          "A",
          server.getMetaInfo("hostname"),
          server.getMetaInfo("public-ip"));
      this.client.deleteDroplet(Integer.valueOf(server.getMetaInfo("droplet-id")));
    } catch (Exception e) {
      LOGGER.severe(e.getMessage());
    }
  }
	
	private void mergeDropletToServer(Droplet d, Server s) {
	  LOGGER.info("Merging Droplet to Server: " + d.getName());
	  for (Network network : d.getNetworks().getVersion4Networks() ) {
      String ip = network.getIpAddress();
      if ("private".equals(network.getType())) {
        s.addMetaInfo("private-ip", ip);
      } else {
        s.addMetaInfo("public-ip", ip);
      }
    }
	  
    s.addMetaInfo("disk", d.getDiskSize().toString());
    s.addMetaInfo("droplet-id", d.getId().toString());
  }

  @Override
  public boolean isValid() {
    return true;
  }
}
