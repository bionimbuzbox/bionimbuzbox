package org.bionimbuzbox.provision.driver;

import java.util.List;
import org.bionimbuzbox.model.Provider;
import org.bionimbuzbox.model.Server;

public interface ProvisionDriver {
  
	public String getDriverName();
	public ProvisionDriver configure(Provider provider);
	
	public Server createServer(Server server);
	public List<Server> createServers(List<Server> servers);
	
	public void deleteServer(Server server);
	public void deleteServers(List<Server> servers);
	
	// public void listServers();
	
	// public List<String> listImages();
	// public List<String> listDatacenters();
	
	//public List<Key> createKeys(List<Key> keys);
	//public Key createKey(Key key);
	
	public boolean isValid();
	
	public static ProvisionDriver initializeDriver(Provider provider) throws Exception {
	  return AbstractProvisionDriver.initializeDriver(provider, false);
	}
	
	public static ProvisionDriver initializeDriver(Provider provider, boolean recreate) throws Exception {
	  return AbstractProvisionDriver.initializeDriver(provider, recreate);
	}
}
