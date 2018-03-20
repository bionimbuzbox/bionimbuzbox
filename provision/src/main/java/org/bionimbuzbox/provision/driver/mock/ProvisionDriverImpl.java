package org.bionimbuzbox.provision.driver.mock;

import java.util.List;
import org.bionimbuzbox.model.Provider;
import org.bionimbuzbox.model.Server;
import org.bionimbuzbox.provision.driver.ProvisionDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProvisionDriverImpl implements ProvisionDriver {

  private static final String DRIVER_NAME = "mock";
  private static final Logger LOGGER = LoggerFactory.getLogger(ProvisionDriverImpl.class);
  
  public ProvisionDriverImpl() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public String getDriverName() {
    return DRIVER_NAME;
  }

  @Override
  public ProvisionDriver configure(Provider provider) {
    LOGGER.info("Configuring {} driver", DRIVER_NAME);
    return this;
  }

  @Override
  public Server createServer(Server server) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Server> createServers(List<Server> servers) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void deleteServer(Server server) {
    // TODO Auto-generated method stub

  }

  @Override
  public void deleteServers(List<Server> servers) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean isValid() {
    return true;
  }

}
